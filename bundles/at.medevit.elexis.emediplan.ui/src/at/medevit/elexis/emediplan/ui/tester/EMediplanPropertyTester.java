package at.medevit.elexis.emediplan.ui.tester;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jface.viewers.StructuredSelection;

import at.medevit.elexis.emediplan.core.BlueMedicationServiceHolder;
import ch.elexis.omnivore.data.DocHandle;

public class EMediplanPropertyTester extends PropertyTester {
	
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue){
		if ("isUploadable".equals(property)) { //$NON-NLS-1$
			if (receiver instanceof StructuredSelection) {
				StructuredSelection selection = (StructuredSelection) receiver;
				if (!selection.isEmpty()) {
					Object object = selection.getFirstElement();
					if (object instanceof DocHandle) {
						DocHandle docHandle = (DocHandle) object;
						if (docHandle.getMimeType().toLowerCase().endsWith("pdf")
							|| docHandle.getTitle().toLowerCase().endsWith(".pdf")) {
							return true;
						}
					}
				}
			}
		} else if ("isDownloadable".equals(property)) { //$NON-NLS-1$
			if (receiver instanceof StructuredSelection) {
				StructuredSelection selection = (StructuredSelection) receiver;
				if (!selection.isEmpty()) {
					Object object = selection.getFirstElement();
					if (object instanceof DocHandle) {
						return BlueMedicationServiceHolder.getService()
							.getPendingUploadResult(object).isPresent();
					}
				}
			}
		}
		return false;
	}
}
