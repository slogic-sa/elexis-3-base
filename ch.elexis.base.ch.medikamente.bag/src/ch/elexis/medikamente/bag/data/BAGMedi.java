/*******************************************************************************
 * Copyright (c) 2007-2011, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *******************************************************************************/
package ch.elexis.medikamente.bag.data;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import ch.elexis.core.data.interfaces.IOptifier;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.data.Artikel;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Organisation;
import ch.elexis.data.Query;
import ch.elexis.data.Xid;
import ch.rgw.tools.Money;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.VersionInfo;

/**
 * This Article is a medicament taken from the BAG (Swiss federal dep. of health)
 * 
 * @author Gerry
 * 
 */
public class BAGMedi extends Artikel implements Comparable<BAGMedi> {
	public static final String EXTTABLE = "CH_ELEXIS_MEDIKAMENTE_BAG_EXT";
	public static final String JOINTTABLE = "CH_ELEXIS_MEDIKAMENTE_BAG_JOINT";
	static final String VERSION = "0.1.2";
	public static final String IMG_GENERIKUM = "ch.elexis.medikamente.bag.generikum";
	public static final String IMG_HAS_GENERIKA = "ch.elexis.medikamente.bag.has_generika";
	public static final String IMG_ORIGINAL = "ch.elexis.medikamente.bag.original";
	
	public static final String FLD_GENERIC_PRODUCT = "Generikum";
	public static final String FLD_PRODUCT = "product";
	public static final String FLD_SUBSTANCE = "substance";
	public static final String FLD_KEYWORDS = "keywords";
	public static final String FLD_PRESCRIPTION = "prescription";
	public static final String FLD_KOMPENDIUM_TXT = "KompendiumText";
	public static final String FLD_GROUP = "Gruppe";
	
	public static final String FLD_EXTINFO = "ExtInfo";
	public static final String FLD_EXT_PRODUCER_ID = "HerstellerID";
	public static final String FLD_EXT_BAG_DOSSIER = "BAG-Dossier";
	public static final String FLD_EXT_SWISSMEDIC_NR = "Swissmedic-Nr.";
	public static final String FLD_EXT_SWISSMEDIC_LIST = "Swissmedic-Liste";
	public static final String FLD_EXT_HEALTHINSURANCE_TYPE = "Kassentyp";
	public static final String FLD_EXT_LIMITATION = "Limitatio";
	public static final String FLD_EXT_LIMITATION_PTS = "LimitatioPts";
	
	static final IOptifier bagOptifier = new BAGOptifier();
	
	// @formatter:off
	static final String extDB = 
			"CREATE TABLE " 	+ EXTTABLE + " (" + 
			"ID					VARCHAR(25) primary key," + 
			"lastupdate 		BIGINT," + 
			"deleted			CHAR(1) default '0'," + 
			FLD_KEYWORDS 		+ " VARCHAR(80)," + 
			FLD_PRESCRIPTION 	+ " TEXT," + 
			FLD_KOMPENDIUM_TXT 	+ " TEXT" + ");";
	
	static final String jointDB = 
			"CREATE TABLE " 	+ JOINTTABLE + "(" + 
			"ID					VARCHAR(25) primary key," + 
			FLD_PRODUCT 		+" VARCHAR(25)," + 
			FLD_SUBSTANCE		+ " VARCHAR(25)" + ");" + 
			"CREATE INDEX CHEMBJ1 ON " + JOINTTABLE + " ("+FLD_PRODUCT+");" + 
			"CREATE INDEX CHEMBJ2 ON " + JOINTTABLE + " ("+FLD_SUBSTANCE+");" + 
			"INSERT INTO " + JOINTTABLE + " (ID,"+FLD_SUBSTANCE+") VALUES('VERSION','" + VERSION + "');";
	
	// @formatter:on
	
	public static final String CODESYSTEMNAME = "Medikament";
	public static final String DOMAIN_PHARMACODE = "www.xid.ch/id/pk";
	
	static {
		addMapping(Artikel.TABLENAME, FLD_GROUP + "=ExtId", FLD_GENERIC_PRODUCT + "=Codeclass",
			"inhalt=JOINT:" + FLD_SUBSTANCE + ":" + FLD_PRODUCT + ":" + JOINTTABLE, FLD_KEYWORDS
				+ "=EXT:" + EXTTABLE + ":" + FLD_KEYWORDS, FLD_PRESCRIPTION + "=EXT:" + EXTTABLE
				+ ":" + FLD_PRESCRIPTION, FLD_KOMPENDIUM_TXT + "=EXT:" + EXTTABLE + ":"
				+ FLD_KOMPENDIUM_TXT);
		
		Xid.localRegisterXIDDomainIfNotExists(DOMAIN_PHARMACODE, "Pharmacode",
			Xid.ASSIGNMENT_REGIONAL);
		
		if (!tableExists(JOINTTABLE)) {
			createOrModifyTable(jointDB);
			createOrModifyTable(extDB);
		} else {
			String v =
				getConnection().queryString(
					"SELECT " + FLD_SUBSTANCE + " FROM " + JOINTTABLE + " WHERE ID='VERSION';");
			VersionInfo vi = new VersionInfo(v);
			if (vi.isOlder(VERSION)) {
				if (vi.isOlder("0.1.1")) {
					createOrModifyTable(extDB);
				}
				if (vi.isOlder("0.1.2")) {
					createOrModifyTable("ALTER TABLE " + EXTTABLE + " add lastupdate BIGINT;");
				}
				getConnection().exec(
					"UPDATE " + JOINTTABLE + " SET " + FLD_SUBSTANCE + "='" + VERSION
						+ "' WHERE ID='VERSION';");
			}
		}
		// make sure, the substances table is created
		Substance.load("VERSION");
		String imgroot = "icons" + File.separator;
		UiDesk.getImageRegistry().put(IMG_GENERIKUM,
			BAGMediFactory.loadImageDescriptor(imgroot + "ggruen.png"));
		UiDesk.getImageRegistry().put(IMG_HAS_GENERIKA,
			BAGMediFactory.loadImageDescriptor(imgroot + "orot.png"));
		UiDesk.getImageRegistry().put(IMG_ORIGINAL,
			BAGMediFactory.loadImageDescriptor(imgroot + "oblau.ico"));
	}
	
	/**
	 * Create a BAGMEdi from a line of the BAG file
	 * 
	 * @param row
	 *            the line
	 */
	public BAGMedi(final String name, final String code){
		super(name, CODESYSTEMNAME, code);
		set("Klasse", getClass().getName());
	}
	
	public boolean isGenericum(){
		return checkNull(get(FLD_GENERIC_PRODUCT)).startsWith("G");
	}
	
	public boolean hasGenerica(){
		return get(FLD_GENERIC_PRODUCT).startsWith("O");
	}
	
	public List<Substance> getSubstances(){
		List<String[]> cnt = getList("inhalt", new String[0]);
		ArrayList<Substance> ret = new ArrayList<Substance>(cnt.size());
		for (String[] s : cnt) {
			ret.add(Substance.load(s[0]));
		}
		return ret;
	}
	
	public SortedSet<Interaction> getInteraktionen(){
		List<Substance> substances = getSubstances();
		SortedSet<Interaction> ret = new TreeSet<Interaction>();
		for (Substance s : substances) {
			List<Interaction> interactions = s.getInteractions();
			ret.addAll(interactions);
		}
		return ret;
	}
	
	public SortedSet<Interaction> getInteraktionenMit(final BAGMedi other){
		List<Substance> ls1 = getSubstances();
		List<Substance> ls2 = other.getSubstances();
		SortedSet<Interaction> ret = new TreeSet<Interaction>();
		for (Substance s1 : ls1) {
			if (ls2.contains(s1)) {
				continue;
			}
			for (Substance s2 : ls2) {
				ret = (SortedSet<Interaction>) s1.getInteractionsWith(s2, ret);
			}
		}
		return ret;
	}
	
	public Kontakt getHersteller(){
		return Kontakt.load(getExt(FLD_EXT_PRODUCER_ID));
	}
	
	public String getKompendiumText(){
		return checkNull(get(FLD_KOMPENDIUM_TXT));
	}
	
	public void setKompendiumText(String text){
		set(FLD_KOMPENDIUM_TXT, text);
	}
	
	public String getKeywords(){
		return checkNull(get(FLD_KEYWORDS));
	}
	
	public void setKeywords(String keywords){
		set(FLD_KEYWORDS, keywords);
	}
	
	@SuppressWarnings("unchecked")
	public void update(final String[] row){
		Query<Organisation> qo = new Query<Organisation>(Organisation.class);
		String id = qo.findSingle("Name", "=", row[BAGMediImporter.PRODUCER]);
		if (id == null) {
			Organisation o = new Organisation(row[BAGMediImporter.PRODUCER], "Pharma");
			id = o.getId();
		}
		Map exi = getMap("ExtInfo");
		exi.put(FLD_EXT_PRODUCER_ID, id);
		set(FLD_GENERIC_PRODUCT, row[BAGMediImporter.GENERIC]);
		exi.put("Pharmacode", row[BAGMediImporter.PHARMACODE]);
		exi.put(FLD_EXT_BAG_DOSSIER, row[BAGMediImporter.BAG_DOSSIER]);
		exi.put(FLD_EXT_SWISSMEDIC_NR, row[BAGMediImporter.SWISSMEDIC_NR]);
		exi.put(FLD_EXT_SWISSMEDIC_LIST, row[BAGMediImporter.SWISSMEDIC_LIST]);
		exi.put(FLD_EXT_HEALTHINSURANCE_TYPE, "1");
		try {
			setEKPreis(new Money(Double.parseDouble(row[BAGMediImporter.PURCHASE_PRICE])));
		} catch (NumberFormatException nex) {
			setEKPreis(new Money());
			log.warn("Parse error preis " + row[BAGMediImporter.NAME] + ": "
				+ row[BAGMediImporter.PURCHASE_PRICE] + "/" + row[BAGMediImporter.SELLING_PRICE]);
			
		}
		try {
			setVKPreis(new Money(Double.parseDouble(row[BAGMediImporter.SELLING_PRICE])));
		} catch (NumberFormatException ex) {
			setVKPreis(new Money());
			log.warn("Parse error preis " + row[BAGMediImporter.NAME] + ": "
				+ row[BAGMediImporter.PURCHASE_PRICE] + "/" + row[BAGMediImporter.SELLING_PRICE]);
			
		}
		
		if (row[BAGMediImporter.LIMITATION].equals("Y")) {
			exi.put(FLD_EXT_LIMITATION, "Y");
			exi.put(FLD_EXT_LIMITATION_PTS, row[BAGMediImporter.LIMITATION_PTS]);
		} else {
			exi.remove("Limitation");
		}
		
		if (row.length > 12) {
			set(FLD_GROUP, row[BAGMediImporter.GROUP]);
		}
		
		if (row.length > 13) {
			if (!StringTool.isNothing(row[BAGMediImporter.SUBSTANCE])) {
				String[] substName = row[BAGMediImporter.SUBSTANCE].split("\\|");
				LinkedList<Substance> substances = new LinkedList<Substance>();
				for (String n : substName) {
					Substance s = Substance.find(n);
					if (s == null) {
						s = new Substance(n, row[BAGMediImporter.GROUP]);
					}
					substances.add(s);
				}
				deleteList("inhalt");
				for (Substance s : substances) {
					addToList("inhalt", s.getId(), new String[0]);
					s = null;
				}
				substances = null;
				
			}
			
		}
		
		if (row.length > 16) {
			setEAN(row[BAGMediImporter.GTIN]);
		}
		setMap(FLD_EXTINFO, exi);
	}
	
	@Override
	protected String getConstraint(){
		return "Typ='Medikament'";
	}
	
	@Override
	protected void setConstraint(){
		set("Typ", "Medikament");
	}
	
	@Override
	public String getCodeSystemName(){
		return CODESYSTEMNAME;
	}
	
	@Override
	public String getCodeSystemCode(){
		return CODESYSTEM_CODE_GTIN;
	}
	
	@Override
	public String getCode(){
		return getEAN();
	}
	
	public static BAGMedi load(final String id){
		return new BAGMedi(id);
	}
	
	protected BAGMedi(final String id){
		super(id);
	}
	
	protected BAGMedi(){}
	
	public int compareTo(final BAGMedi arg0){
		return (getLabel().compareTo(arg0.getLabel()));
	}
	
	@Override
	public boolean isDragOK(){
		return true;
	}
	
	@Override
	public boolean delete(){
		String sql = "UPDATE " + EXTTABLE + " SET deleted='1' WHERE ID=" + getWrappedId();
		getConnection().exec(sql);
		return super.delete();
	}
	
	@Override
	public IOptifier getOptifier(){
		return bagOptifier;
	}
	
}
