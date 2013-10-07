/**
 * Settings class (Singleton pattern)
 * @since 24.09.2013
 * @author: drunia
 */

package ua.drunia.prodsdb.logic;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import ua.drunia.prodsdb.util.LogUtil;
 
public class Settings {
	private Logger log = Logger.getAnonymousLogger();
	private static final String CONF_FILE = "productsdb.conf";
	private static final String LANG_FILE = "lang/lang_";
	private static Settings instance;
	private Properties settings;
	private Properties langRes;
	
	/*
	 * Hide public constructor
	 */
	private Settings() {
		log.addHandler(LogUtil.getFileHandler());
		settings = new Properties();
		langRes = new Properties();;
		read();
	}
	
	/**
	 * Reading bundle with language 
	 * encoding: utf-8
	 * @param locale specific language locale
	 * @author drunia
	 */
	public void initLangResources(Locale locale) {
		FileReader fr;
		try {			
			String langPath = LANG_FILE + locale.getLanguage();
			InputStream is = null;
				try {
					is = new FileInputStream(langPath);
				} catch (FileNotFoundException e) {
					//if current language not exits chose default EN 
					log.warning("For locale " + locale.getLanguage() +
						" not find language, try default EN.");
					langPath = "./lang/lang_en";
					is = new FileInputStream(langPath);
				}
			log.info("Current language file: " + langPath); 
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			langRes.load(isr);
		} catch (Exception e) {
			log.warning("Fatal erorr: " + e.toString());
			System.exit(1);
		}
	}
	
	/**
	 * Get langRes object
	 * @return ResorceBundle  
	 * @author drunia
	 */
	public Properties getLangResources() {
		return langRes;
	}
	
	/**
	 * @author drunia
	 * Returned instance of Settings
	 */
	public static synchronized Settings get() {
		if (instance == null) {
			instance = new Settings();
		}
		return instance;
	}
	
	/**
	 * Read/Re-read settings from file
	 * @author drunia
	 */
	public void read() {
		FileReader settingsReader;
		try {
			settingsReader = new FileReader(CONF_FILE);
		} catch (FileNotFoundException e) {
			log.warning("Configuration error: " + e.toString() +
				"\nCreate new config \"" + CONF_FILE + "\" file.");
			settings.setProperty("db.file", "products.db");
			settings.setProperty("db.timeout", "30");
			settings.setProperty("locale.lang", Locale.getDefault().getLanguage());
			return;
		}
		try {
			settings.load(settingsReader);
		} catch (IOException e) {
			log.warning(e.toString());
		}
	}
	
	/**
	 * Save settings 
	 * @author drunia
	 */
	public void save() {
		try {
			FileWriter fw = new FileWriter(CONF_FILE);
			settings.store(fw, "ProductsDB configuration file");
		} catch (IOException e) {
			log.warning(e.toString());
		}
	}
	
	/**
	 * Gettings parameter value
	 * @author drunia
	 */
	public String getParam(String paramName) {
		return settings.getProperty(paramName);
	}
	
	/**
	 * Set parameter
	 * @author drunia
	 */
	public void setParam(String paramName, String paramValue) {	
		settings.setProperty(paramName, paramValue);
	}
}