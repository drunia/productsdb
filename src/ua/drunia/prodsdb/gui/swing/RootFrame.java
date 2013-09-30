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
import java.io.File;
import java.sql.ResultSet;

import javax.swing.*;
import java.awt.*;

public class RootFrame extends JFrame implements IUserUI {
	private static Logger log = Logger.getAnonymousLogger();
	private Database db;
	
	//default constructor of main JFrame
	public RootFrame(Database db) {
		super("Главная форма программы");		;
		this.db = db;
		
		//load settings
		Settings s = Settings.get();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500, 500);
		setLocationRelativeTo(null);
		
		//create tab - view categories
		CategoryView cw = new CategoryView(this);
		
		//create JTabbedPane and add our tabs on
		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("Категории", cw);
		
		//add JTabbedPane on main JFrame
		add(tabs, BorderLayout.CENTER);
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

}