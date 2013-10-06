/**
 * Main class JFrame of application
 * @author drunia
 * @since 30.09.2013
 */

package ua.drunia.prodsdb.gui.swing;

import ua.drunia.prodsdb.logic.*;
import ua.drunia.prodsdb.gui.*;

import java.util.logging.Logger;
import java.util.logging.Level; 
import java.io.*;
import java.sql.ResultSet;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class RootFrame extends JFrame implements IUserUI {
	private static Logger log = Logger.getAnonymousLogger();
	private Database db;
	private Settings settings = Settings.get();
	
	//default constructor of main JFrame
	public RootFrame(Database db) {
		super("������� ����� ���������");		;
		this.db = db;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500, 500);
		setLocationRelativeTo(null);
		
		//create tab - view categories
		CategoryView cw = new CategoryView(this);
		
		//create JTabbedPane and add our tabs on
		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("��������� �������", cw);
		
		//add JTabbedPane on main JFrame
		add(tabs, BorderLayout.CENTER);
		
		//localize UI
		localizeUI(new Locale(settings.getParam("lang.locale")), tabs);
	}
	
	/**
	 * Localize all views by initialized Locale
	 * @param locale selected locale
	 * @tabs views on JTabbedPane
	 * @author drunia
	 */
	private void localizeUI(Locale locale, JTabbedPane tabs) {
		settings.initLangResources(locale);
		Properties langRes = settings.getLangResources();
		//localize self
		localize(langRes);
		//localize all views (JPanels)
		for (int i = 0; i < tabs.getTabCount(); i++) 
			((IUserUI) tabs.getComponentAt(i)).localize(langRes);
	}
	
	//return initialized instance of database
	public Database getDatabase() {
		return db;
	}
	
	//called when model has want update GUI
	public void updateUI(Object source) {
		log.info(this + " updateUI()");
	}
	
	//called when model has want show error
	public void error(Exception e) {
		JOptionPane.showMessageDialog(null, "������ ����������:\n" + e, "������", JOptionPane.ERROR_MESSAGE);
	}
	
	//called when model has want something confirm
	public boolean confirm(String msg) {
		return false;
	}
	
	//called when model has want show message
	public void message(String msg){
		JOptionPane.showMessageDialog(null,
			"��������� �� ������/�����������:\n" + msg, "������� ���������", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public void localize(Properties langRes) {
		setTitle(langRes.getProperty("ROOT_TITLE"));
	}

}