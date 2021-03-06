package ch.elexis.omnivore.model.service;

import ch.elexis.core.jpa.entities.DocHandle;
import ch.elexis.core.jpa.model.adapter.AbstractModelAdapterFactory;
import ch.elexis.core.jpa.model.adapter.MappingEntry;
import ch.elexis.omnivore.model.IDocumentHandle;

public class OmnivoreModelAdapterFactory extends AbstractModelAdapterFactory {

	private static OmnivoreModelAdapterFactory INSTANCE;

	public static synchronized OmnivoreModelAdapterFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new OmnivoreModelAdapterFactory();
		}
		return INSTANCE;
	}

	private OmnivoreModelAdapterFactory() {
		super();
	}

	@Override
	protected void initializeMappings() {
		addMapping(new MappingEntry(IDocumentHandle.class, ch.elexis.omnivore.model.DocumentDocHandle.class,
				DocHandle.class));
	}
}
