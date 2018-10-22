package ch.elexis.base.ch.arzttarife.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.jpa.entities.EntityWithId;
import ch.elexis.core.jpa.entities.TarmedLeistung;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.services.ICodeElementService.CodeElementTyp;
import ch.elexis.core.services.ICodeElementServiceContribution;
import ch.elexis.core.services.IElexisEntityManager;
import ch.elexis.core.services.IStoreToStringContribution;

@Component
public class TarmedLeistungCodeElementService
		implements ICodeElementServiceContribution, IStoreToStringContribution {
	
	@Reference
	private IElexisEntityManager entityManager;
	
	@Override
	public String getSystem(){
		return TarmedLeistung.CODESYSTEM_NAME;
	}
	
	@Override
	public CodeElementTyp getTyp(){
		return CodeElementTyp.SERVICE;
	}
	
	@Override
	public Optional<ICodeElement> loadFromCode(String code, HashMap<Object, Object> context){
		EntityManager em = (EntityManager) entityManager.getEntityManager();
		TypedQuery<TarmedLeistung> gtinQuery =
			em.createNamedQuery("TarmedLeistung.code", TarmedLeistung.class);
		gtinQuery.setParameter("code", code);
		List<TarmedLeistung> resultList = gtinQuery.getResultList();
		if (resultList.size() > 0) {
			Optional<Identifiable> element = ArzttarifeModelAdapterFactory.getInstance()
				.getModelAdapter(resultList.get(0), ITarmedLeistung.class, false);
			if (element.isPresent()) {
				return Optional.of((ICodeElement) element.get());
			}
		}
		return Optional.empty();
	}
	
	@Override
	public List<ICodeElement> getElements(HashMap<Object, Object> context){
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Optional<String> storeToString(Identifiable identifiable){
		if (identifiable instanceof TarmedLeistung) {
			return Optional.of(ch.elexis.base.ch.arzttarife.tarmed.model.TarmedLeistung.STS_CLASS
				+ StringConstants.DOUBLECOLON + identifiable.getId());
		}
		return Optional.empty();
	}
	
	@Override
	public Optional<Identifiable> loadFromString(String storeToString){
		if (storeToString
			.startsWith(ch.elexis.base.ch.arzttarife.tarmed.model.TarmedLeistung.STS_CLASS
				+ StringConstants.DOUBLECOLON)) {
			String[] split = splitIntoTypeAndId(storeToString);
			String id = split[1];
			EntityManager em = (EntityManager) entityManager.getEntityManager();
			EntityWithId dbObject = em.find(TarmedLeistung.class, id);
			return Optional.ofNullable(ArzttarifeModelAdapterFactory.getInstance()
				.getModelAdapter(dbObject, null, false).orElse(null));
		}
		return Optional.empty();
	}
	
}
