/**
 * Settings class (Singleton pattern)
 * @since 24.09.2013
 * @author: drunia
 */

package ua.drunia.prodsdb.logic;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.Properties;
import java.util.logging.*;

import ua.drunia.prodsdb.util.LogUtil;
 
public class Settings {
	private Logger log = Logger.getAnonymousLogger();
	private static final String CONF_FILE = "productsdb.conf";
	private static Settings instance;
	private Properties settings;
	
	/*
	 * Hide public constructor
	 */
	private Settings() {
		log.addHandler(LogUtil.getFileHandler());
		settings = new Properties();
		read();
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
			log.warning(new FileNotFoundException("Configuration file \"" +
				CONF_FILE + "\" not found, initialize default settings").toString());
			settings.setProperty("db.file", "products.db");
			settings.setProperty("db.timeout", "30");
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