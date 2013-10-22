/**
 * Main class of application
 * @author drunia
 * @since 30.09.2013
 */

package ua.drunia.prodsdb;

import javax.swing.*;
import java.util.logging.*;
import java.util.*;

import ua.drunia.prodsdb.gui.swing.RootFrame;
import ua.drunia.prodsdb.logic.Database;
import ua.drunia.prodsdb.logic.Settings;
import ua.drunia.prodsdb.util.LogUtil;


public class ProductsDB {
	private static Logger log = Logger.getAnonymousLogger();
	
	/**
	 * Main method - start point app
	 * @author drunia
	 */
	public static void main(String[] args) {
		log.addHandler(LogUtil.getFileHandler());
		
		//launch main JFrame in other graphical thread
		SwingUtilities.invokeLater(new Runnable() {
			public void run() { createGUI(); }
		});
	}	
	
	public static void createGUI() {
		//load settings
		Settings s = Settings.get();
		//initialize database
		Database db = new Database(s.getParam("db.file"));
		if (!db.initialized) {
			log.warning("Database initialization error!");
			System.exit(1);
		}
		//create swing GUI
		RootFrame rf = new RootFrame(db);
		rf.setVisible(true);
	}
}