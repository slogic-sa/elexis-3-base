/*******************************************************************************
 * Copyright (c) 2006-2011, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *******************************************************************************/

package ch.elexis.omnivore.ui.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.omnivore.ui.preferences.messages";
	
	public static String FileImportDialog_categoryLabel;
	public static String FileImportDialog_newCategoryText;
	public static String FileImportDialog_newCategoryCaption;
	public static String FileImportDialog_dateLabel;
	public static String FileImportDialog_importCaption;
	public static String FileImportDialog_importFileCaption;
	public static String FileImportDialog_importFileText;
	public static String FileImportDialog_keywordsLabel;
	public static String FileImportDialog_titleLabel;
	public static String OmnivoreView_categoryColumn;
	public static String OmnivoreView_configErrorCaption;
	public static String OmnivoreView_configErrorText;
	public static String OmnivoreView_dataSources;
	public static String OmnivoreView_dateColumn;
	public static String OmnivoreView_deleteActionCaption;
	public static String OmnivoreView_deleteActionToolTip;
	public static String OmnivoreView_editActionCaption;
	public static String OmnivoreView_editActionTooltip;
	public static String OmnivoreView_exportActionCaption;
	public static String OmnivoreView_exportActionTooltip;
	public static String OmnivoreView_flatActionCaption;
	public static String OmnivoreView_flatActionTooltip;
	public static String OmnivoreView_importActionCaption;
	public static String OmnivoreView_importActionToolTip;
	public static String OmnivoreView_keywordsColumn;
	public static String OmnivoreView_reallyDeleteCaption;
	public static String OmnivoreView_reallyDeleteContents;
	public static String OmnivoreView_searchKeywordsLabel;
	public static String OmnivoreView_searchTitleLabel;
	public static String OmnivoreView_titleColumn;
	public static String Omnivore_ErrNoActivator;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
}
