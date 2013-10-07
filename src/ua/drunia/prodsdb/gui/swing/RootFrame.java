/**
 * Main class JFrame of application
 * @author drunia
 * @since 30.09.2013
 */

package ua.drunia.prodsdb.gui.swing;

import ua.drunia.prodsdb.logic.*;
import ua.drunia.prodsdb.gui.*;
import ua.drunia.prodsdb.util.*;

import java.util.logging.Logger;
import java.util.logging.Level; 
import java.io.*;
import java.sql.ResultSet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class RootFrame extends JFrame implements IUserUI {
	private static Logger log = Logger.getAnonymousLogger();
	private Database db;
	private JTabbedPane tabs;
	private Settings settings = Settings.get();
	private MenuBar menu; 
	
	/**
	 * MainMenu class
	 * @author drunia
	 */
	private class MenuBar extends JMenuBar {
		private JMenu settingsMenu;
		private JMenu langMenu;
		
		//private inner-inner class action-handler
		class ChangeLangListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				JMenuItem item = (JMenuItem) e.getSource();
				Locale l = item.getLocale();
				settings.setParam("locale.lang", l.getLanguage());
				//call localizeUI() for localize UI by selected language
				localizeUI(l, tabs);
			}
		}
		
		/**
		 * Default constructor
		 * @author drunia
		 */
		public MenuBar() {
			super();
			settingsMenu = new JMenu();
			add(settingsMenu);
			langMenu = new JMenu();
			settingsMenu.add(langMenu);
			
			//find languages and add them to menu
			ChangeLangListener changeLangListener = new ChangeLangListener();
			ButtonGroup langGroup = new ButtonGroup();
			for (String lang : getAvailableLanguages()) {
				Locale langLocale = Locale.forLanguageTag(lang);
				Locale rootLocale = RootFrame.this.getLocale();
				JRadioButtonMenuItem langMenuItem = 
					new JRadioButtonMenuItem(langLocale.getDisplayLanguage());
				langGroup.add(langMenuItem);
				langMenuItem.setLocale(langLocale);
				langMenuItem.addActionListener(changeLangListener);
				langMenuItem.setSelected(
					langLocale.getLanguage().equals(rootLocale.getLanguage()));
				langMenu.add(langMenuItem);
			}
		}
		
		/**
		 * Localize menu
		 * @author drunia
		 */
		public void localize() {
			Properties langRes = Settings.get().getLangResources();
			settingsMenu.setText(langRes.getProperty("ROOT_MENU_SETTINGS"));
			langMenu.setText(langRes.getProperty("ROOT_MENU_LANGS"));
		}
		
	} 
	
	private class RootFrameListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			settings.save();
		}
	}
	
	//default constructor of main JFrame
	public RootFrame(Database db) {
		super();
		this.db = db;
		addWindowListener(new RootFrameListener());
	
		log.addHandler(LogUtil.getFileHandler());	
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500, 500);
		setLocationRelativeTo(null);
		
		//set locale from settings file
		setLocale(new Locale(settings.getParam("locale.lang")));
		
		//create and add MainMenu
		menu = new MenuBar();
		setJMenuBar(menu);
		
		//create tab - view categories
		CategoryView cw = new CategoryView(this);
		
		//create JTabbedPane and add our tabs on
		tabs = new JTabbedPane();
		tabs.addTab("Категории товаров", cw);
		
		//add JTabbedPane on main JFrame
		add(tabs, BorderLayout.CENTER);
		
		//localize UI
		localizeUI(getLocale(), tabs);
		
	}
	
	/**
	 * Returns available languages are placed in ./lang/ directory
	 * @return String[] languages
	 * @author drunia
	 */
	private String[] getAvailableLanguages() {
		String[] langs = null;
		try {
			FilenameFilter filter = new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return (name.startsWith("lang_"));
				}
			};
			File langDir = new File("./lang");
			langs = new String[langDir.list(filter).length];
			int i = 0;
			for (String lang : langDir.list(filter)) {
				langs[i] = lang.replaceFirst("lang_", "");
				i++;
			}
		} catch (Exception e) {
			log.warning("Error get available languages: " + e.toString());
			System.exit(1);
		}
		return langs;
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
		JOptionPane.showMessageDialog(null, "Ошибка выполнения:\n" + e, "Ошибка", JOptionPane.ERROR_MESSAGE);
	}
	
	//called when model has want something confirm
	public boolean confirm(String msg) {
		return false;
	}
	
	//called when model has want show message
	public void message(String msg){
		JOptionPane.showMessageDialog(null,
			"Сообщение от модели/контроллера:\n" + msg, "Простое сообщение", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public void localize(Properties langRes) {
		menu.localize();
		setTitle(langRes.getProperty("ROOT_TITLE"));
		tabs.setTitleAt(0, langRes.getProperty("CAT_VIEW_TITTLE"));
	}

}