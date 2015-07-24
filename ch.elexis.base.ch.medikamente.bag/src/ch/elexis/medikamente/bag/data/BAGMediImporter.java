/*******************************************************************************
 * Copyright (c) 2007-2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *******************************************************************************/

package ch.elexis.medikamente.bag.data;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;

import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.core.ui.importer.div.importers.ExcelWrapper;
import ch.elexis.core.ui.util.ImporterPage;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Artikel;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.StringTool;

public class BAGMediImporter extends ImporterPage {
	static Query<Artikel> qbe = new Query<Artikel>(Artikel.class);
	static Logger log = Logger.getLogger(BAGMediImporter.class.getName());
	
	public static final int PRODUCER = 0;
	public static final int GENERIC = 1;
	public static final int PHARMACODE = 2;
	public static final int BAG_DOSSIER = 3;
	public static final int SWISSMEDIC_NR = 4;
	public static final int SWISSMEDIC_LIST = 5;
	public static final int NAME = 7;
	public static final int PURCHASE_PRICE = 8;
	public static final int SELLING_PRICE = 9;
	public static final int LIMITATION = 10;
	public static final int LIMITATION_PTS = 11;
	public static final int GROUP = 12;
	public static final int SUBSTANCE = 13;
	public static final int GTIN = 16;
	
	private static String pharmacode;
	
	public BAGMediImporter(){}
	
	@Override
	public Composite createPage(final Composite parent){
		FileBasedImporter fbi = new FileBasedImporter(parent, this);
		fbi.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		return fbi;
	}
	
	@Override
	public IStatus doImport(final IProgressMonitor monitor) throws Exception{
		FileInputStream is = new FileInputStream(results[0]);
		return doImport(is, monitor);
	}
	
	public IStatus doImport(final InputStream inputStream, final IProgressMonitor monitor)
		throws ElexisException{
		ExcelWrapper ew = new ExcelWrapper();
		if (ew.load(inputStream, 0)) {
			int f = ew.getFirstRow() + 1;
			int l = ew.getLastRow();
			monitor.beginTask("Import BAG-Medikamente", l - f);
			int counter = 0;
			ew.setFieldTypes(new Class[] {
				String.class, Character.class, Integer.class, Integer.class, Integer.class,
				Character.class, String.class, String.class, Double.class, Double.class,
				String.class, Integer.class, Integer.class, String.class, Integer.class,
				String.class, Integer.class
			});
			for (int i = f; i < l; i++) {
				List<String> row = ew.getRow(i);
				monitor.subTask(row.get(7));
				importUpdate(row.toArray(new String[0]));
				if (counter++ > 200) {
					PersistentObject.clearCache();
					counter = 0;
				}
				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}
				monitor.worked(1);
				row = null;
			}
			monitor.done();
			return Status.OK_STATUS;
		}
		return Status.CANCEL_STATUS;
	}
	
	/**
	 * Import a medicament from one row of the BAG-Medi file
	 * 
	 * @param row
	 *            contains relevant information about the article to include
	 * 
	 *            <pre>
	 *  [0]  {@link BAGMediImporter#PRODUCER}
	 *  [1]  {@link BAGMediImporter#GENERIC}
	 *  [2]  {@link BAGMediImporter#PHARMACODE}
	 *  [3]  {@link BAGMediImporter#BAG_DOSSIER}
	 *  [4]  {@link BAGMediImporter#SWISSMEDIC_NR}
	 *  [5]  {@link BAGMediImporter#SWISSMEDIC_LIST}
	 *  [7]  {@link BAGMediImporter#NAME}
	 *  [8]  {@link BAGMediImporter#PURCHASE_PRICE}
	 *  [9]  {@link BAGMediImporter#SELLING_PRICE}
	 *  [10] {@link BAGMediImporter#LIMITATION}
	 *  [11] {@link BAGMediImporter#LIMITATION_PTS}
	 *  [12] {@link BAGMediImporter#GROUP}
	 *  [13] {@link BAGMediImporter#SUBSTANCE}
	 *  [16] {@link BAGMediImporter#GTIN}
	 * </pre>
	 * @return true in case of a successfull import, false otherwise
	 */
	public static boolean importUpdate(final String[] row) throws ElexisException{
		String gtin = "";
		BAGMedi imp = null;
		
		// no GTIN and no pharmacode given, try resolution by NAME
		if (StringTool.isNothing(row[GTIN].trim()) && StringTool.isNothing(row[PHARMACODE].trim())) {
			String mid = qbe.findSingle(Artikel.FLD_NAME, Query.EQUALS, row[NAME]);
			if (mid != null) {
				imp = BAGMedi.load(mid);
			}
		} else {
			pharmacode = "0";
			if (!StringTool.isNothing(row[PHARMACODE])) {
				initPharmacode(row[PHARMACODE]);
			}
			gtin = row[GTIN].trim();
			
			qbe.clear(true);
			qbe.add(Artikel.FLD_EAN, Query.EQUALS, gtin);
			List<Artikel> lArt = qbe.execute();
			if (lArt == null) {
				// try resolution via pharmacode -> question whether to keep or remove this code part
				lArt = resolveByPharmacode(row[PHARMACODE]);
				if (lArt == null) {
					throw new ElexisException(BAGMediImporter.class,
						"Article list was null while scanning for " + gtin,
						ElexisException.EE_UNEXPECTED_RESPONSE, true);
				}
			}
			if (lArt.size() > 1) {
				// Duplikate entfernen, genau einen gültigen und existierenden Artikel behalten
				Iterator<Artikel> it = lArt.iterator();
				boolean hasValid = false;
				Artikel res = null;
				while (it.hasNext()) {
					Artikel ax = it.next();
					if (hasValid || (!ax.isValid())) {
						if (res == null) {
							res = ax;
						}
						it.remove();
					} else {
						hasValid = true;
					}
				}
				if (!hasValid) {
					if (res != null) {
						if (res.isDeleted()) {
							res.undelete();
							lArt.add(res);
						}
					}
				}
			}
			imp = lArt.size() > 0 ? BAGMedi.load(lArt.get(0).getId()) : null;
		}
		if (imp == null || (!imp.isValid())) {
			imp = new BAGMedi(row[NAME], pharmacode);
			
			String sql =
				new StringBuilder().append("INSERT INTO ").append(BAGMedi.EXTTABLE)
					.append(" (ID) VALUES (").append(imp.getWrappedId()).append(");").toString();
			PersistentObject.getConnection().exec(sql);
			
		} else {
			
			String sql =
				new StringBuilder().append("SELECT ID FROM ").append(BAGMedi.EXTTABLE)
					.append(" WHERE ID=").append(imp.getWrappedId()).toString();
			String extid = PersistentObject.getConnection().queryString(sql);
			if (extid == null) {
				sql =
					new StringBuilder().append("INSERT INTO ").append(BAGMedi.EXTTABLE)
						.append(" (ID) VALUES (").append(imp.getWrappedId()).append(");")
						.toString();
				PersistentObject.getConnection().exec(sql);
			}
			
		}
		imp.update(row);
		return true;
	}
	
	private static List<Artikel> resolveByPharmacode(String pharmaString) throws ElexisException{
		qbe.clear(true);
		qbe.add(Artikel.FLD_SUB_ID, "=", pharmacode);
		qbe.or();
		qbe.add(Artikel.FLD_SUB_ID, Query.EQUALS, pharmaString.trim());
		return qbe.execute();
	}
	
	private static void initPharmacode(String pString){
		try {
			// strip leading zeroes
			int pcode = Integer.parseInt(pString.trim());
			pharmacode = Integer.toString(pcode);
		} catch (Exception ex) {
			ExHandler.handle(ex);
			log.log(Level.WARNING, "Pharmacode falsch: " + pString);
		}
	}
	
	@Override
	public String getDescription(){
		return "Import Medikamentenliste BAG";
	}
	
	@Override
	public String getTitle(){
		return "Medi-BAG";
	}
	
}
