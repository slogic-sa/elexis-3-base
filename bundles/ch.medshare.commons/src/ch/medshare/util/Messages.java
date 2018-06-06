package ch.medshare.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;


import org.eclipse.osgi.util.NLS;
public class Messages extends NLS {
  public static final String BUNDLE_NAME = "ch.medshare.commons.messages";

	private static final String BUNDLE_NAME = "ch.medshare.util.messages"; //$NON-NLS-1$
	
	private static final ResourceBundle RESOURCE_BUNDLE(BUNDLE_NAME);
	
	private Messages(){  static { // load message values from bundle file
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }

  private Messages() {
  }
}

	
	public static String getString(String key){
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
