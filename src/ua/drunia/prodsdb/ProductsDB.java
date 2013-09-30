/**
 * Main class JFrame of application
 * @author drunia
 * @since 30.09.2013
 */

package ua.drunia.prodsdb;

import ua.drunia.prodsdb.logic.*;
import ua.drunia.prodsdb.gui.*;
import ua.drunia.prodsdb.util.*;

import java.util.logging.Logger;
import java.util.logging.Level; 
import java.io.File;
import java.sql.ResultSet;

import javax.swing.*;
import java.awt.*;

public class ProductsDB extends JFrame implements IUserUI {
	private static Logger log = Logger.getLogger(ProductsDB.class.getName());
	private Database db;
	
	/**
	 * Main method - start point app
	 * @author drunia
	 */
	public static void main(String[] args) {
		log.addHandler(LogUtil.getFileHandler());
		
		//launch main JFrame in other graphical thread
		SwingUtilities.invokeLater(new Runnable() {
			public void run() { new ProductsDB().setVisible(true); }
		});
	}
	
	//default constructor of main JFrame
	public ProductsDB() {
		super("Главная форма программы");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500, 500);
		setLocationRelativeTo(null);
		
		//load settings
		Settings s = Settings.get(this);
		
		//initialize database
		db = new Database(this, s.getParam("db.file"));
		if (!db.initialized) {
			log.warning("Database initialization error!");
			System.exit(1);
		}
		
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