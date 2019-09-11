package at.medevit.elexis.emediplan.ui.handler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import at.medevit.elexis.emediplan.core.EMediplanServiceHolder;
import at.medevit.elexis.emediplan.core.model.chmed16a.Medicament;
import at.medevit.elexis.emediplan.core.model.chmed16a.Medication;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.service.ContextServiceHolder;
import ch.elexis.core.data.service.CoreModelServiceHolder;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.builder.IPrescriptionBuilder;
import ch.elexis.core.model.prescription.EntryType;
import ch.elexis.data.Prescription;
import ch.rgw.tools.TimeTool;

public class DirectImportHandler extends AbstractHandler implements IHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		String emediplan =
			event.getParameter("at.medevit.elexis.emediplan.ui.directImport.parameter.emediplan");
		String patientid =
			event.getParameter("at.medevit.elexis.emediplan.ui.directImport.parameter.patientid");
		String stopreason =
			event.getParameter("at.medevit.elexis.emediplan.ui.directImport.parameter.stopreason");
		String medicationType =
			event.getParameter("at.medevit.elexis.emediplan.ui.directImport.parameter.medication"); //$NON-NLS-1$
		// if not set use all
		if (medicationType == null || medicationType.isEmpty()) {
			medicationType = "all";
		}
		
		if (StringUtils.isNotEmpty(patientid) && StringUtils.isNotEmpty(emediplan)) {
			Medication medication =
				EMediplanServiceHolder.getService().createModelFromChunk(emediplan);
			
			EMediplanServiceHolder.getService().addExistingArticlesToMedication(medication);
			
			IPatient patient =
				CoreModelServiceHolder.get().load(patientid, IPatient.class).orElse(null);
			if (patient != null) {
				ContextServiceHolder.get().setActivePatient(patient);
				
				List<IPrescription> currentMedication = getPrescriptions(patient, medicationType);
				for (IPrescription prescription : currentMedication) {
					prescription.setDateTo(LocalDateTime.now());
					prescription.setStopReason(stopreason != null ? stopreason : "Direct Import");
					ElexisEventDispatcher.getInstance().fire(new ElexisEvent(prescription,
						Prescription.class, ElexisEvent.EVENT_UPDATE));
				}
				List<Medicament> notFoundMedicament = new ArrayList<>();
				for (Medicament medicament : medication.Medicaments) {
					if (medicament.artikelstammItem != null) {
						createPrescription(medicament, patient);
					} else {
						notFoundMedicament.add(medicament);
					}
				}
				if (!notFoundMedicament.isEmpty()) {
					StringBuilder sb = new StringBuilder();
					sb.append(
						"Folgende Medikamente konnte im Artikelstamm nicht gefunden werden\n\n");
					notFoundMedicament
						.forEach(m -> sb.append(" - " + m.Id + " " + m.AppInstr + " " + m.TkgRsn));
					MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warnung",
						sb.toString());
				}
			}
		}
		return null;
	}
	
	private List<IPrescription> getPrescriptions(IPatient patient, String medicationType){
		if ("all".equals(medicationType)) {
			return patient.getMedication(Arrays.asList(EntryType.FIXED_MEDICATION,
				EntryType.RESERVE_MEDICATION, EntryType.SYMPTOMATIC_MEDICATION));
		} else if ("fix".equals(medicationType)) {
			return patient.getMedication(Arrays.asList(EntryType.FIXED_MEDICATION));
		} else if ("reserve".equals(medicationType)) {
			return patient.getMedication(Arrays.asList(EntryType.RESERVE_MEDICATION));
		} else if ("symptomatic".equals(medicationType)) {
			return patient.getMedication(Arrays.asList(EntryType.SYMPTOMATIC_MEDICATION));
		}
		return Collections.emptyList();
	}
	
	private IPrescription createPrescription(Medicament medicament, IPatient patient){
		medicament.entryType = EntryType.FIXED_MEDICATION;
		
		IPrescription prescription = new IPrescriptionBuilder(CoreModelServiceHolder.get(),
			medicament.artikelstammItem, patient, medicament.dosis).build();
		
		getLocalDateTime(medicament.dateFrom).ifPresent(ldt -> prescription.setDateFrom(ldt));
		getLocalDateTime(medicament.dateTo).ifPresent(ldt -> prescription.setDateTo(ldt));
		
		prescription.setRemark(medicament.AppInstr);
		prescription.setEntryType(medicament.entryType);
		prescription.setDisposalComment(medicament.TkgRsn);
		
		CoreModelServiceHolder.get().save(prescription);
		return prescription;
	}
	
	private java.util.Optional<LocalDateTime> getLocalDateTime(String dateString){
		if (dateString != null && !dateString.isEmpty()) {
			return java.util.Optional.of(new TimeTool(dateString).toLocalDateTime());
		}
		return java.util.Optional.empty();
	}
}