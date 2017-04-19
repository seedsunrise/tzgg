package cn.com.flaginfo.utils;

import cn.com.flaginfo.commons.utils.config.ConfigCenterUtils;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class SystemConfig {
	private static final String BUNDLE_NAME = "system"; 

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private SystemConfig() {
	}

	/**
	 * get the value from the properties file
	 * 
	 * @param key
	 *            the key in the properties file
	 * @return
	 */
	public static String getString(String key) {
		try {
			if(isConfigCenter()){
				return ConfigCenterUtils.getString(key);
			}
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	public static boolean isConfigCenter() {
		return ! "1".equals(RESOURCE_BUNDLE.getString("configs_local"));
	}
	
	
	public static String getApiKey(){
		
		return getString(MutiSiteConstants.API_USER_KEY);
	}
	
	public static String getApiVersion(){
		
		return getString(MutiSiteConstants.API_USER_VERSION);
	}
}

