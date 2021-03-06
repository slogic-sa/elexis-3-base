/*******************************************************************************
 * Copyright (c) 2017, J. Sigle, Niklaus Giger and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    J. Sigle - Initial implementation in a private branch of Elexis 2.1
 *    N. Giger - Reworked for Elexis 3.4 including unit tests
 *    
 *******************************************************************************/

package ch.elexis.omnivore.model.util;

import java.util.List;

import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.omnivore.model.IDocumentHandle;
import ch.elexis.omnivore.model.service.OmnivoreModelServiceHolder;

public class Utils {

	public static List<IDocumentHandle> getMembers(IDocumentHandle dh, IPatient pat) {
		IQuery<IDocumentHandle> query = OmnivoreModelServiceHolder.get().getQuery(IDocumentHandle.class);
		query.and("category", COMPARATOR.EQUALS, dh.getTitle());
		query.and("kontakt", COMPARATOR.EQUALS, pat);
		return query.execute();
	}
}
