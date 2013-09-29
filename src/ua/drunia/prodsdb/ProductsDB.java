package ua.drunia.prodsdb;

import ua.drunia.prodsdb.logic.Database;
import ua.drunia.prodsdb.gui.IUserUI;
import ua.drunia.prodsdb.logic.Settings;
import ua.drunia.prodsdb.util.LogUtil;
import ua.drunia.prodsdb.logic.CategoryController;
import ua.drunia.prodsdb.logic.Controller;

import java.util.logging.Logger;
import java.util.logging.Level; 

import java.io.File;

import java.sql.ResultSet;

import javax.swing.JOptionPane;

public class ProductsDB implements IUserUI, CategoryController.ISqlResultListener {
	private static Logger log = Logger.getLogger(ProductsDB.class.getName());

	public static void main(String[] args) {
		log.addHandler(LogUtil.getFileHandler());

		//Initiale
		final ProductsDB prodsdb = new ProductsDB();
		
		Settings s = Settings.get(prodsdb);
		System.out.println("Database filename = " + s.getParam("db.file"));
		System.out.println("Database operations timeout = " + s.getParam("db.timeout"));
		s.save();
		
		Database db = new Database(prodsdb, s.getParam("db.file"));
		if (db.initialized) {
			System.out.println("Database initialized OK.");
		}
		else
			System.out.println("Database initialized FAIL.");
			
		
		//test
		javax.swing.JFrame f = new javax.swing.JFrame();
		f.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		f.setSize(500, 500);
		javax.swing.JButton[] b = new javax.swing.JButton[4]; 
		for (int i = 0; i < 4; i++) {
			b[i] = new javax.swing.JButton("Button" + i);
			b[i].setPreferredSize(new java.awt.Dimension(200, 200));
		}
		f.add(b[0], java.awt.BorderLayout.PAGE_START);
		f.add(b[1], java.awt.BorderLayout.LINE_END);
		f.add(b[2], java.awt.BorderLayout.PAGE_END);
		//f.setVisible(true);
		
		
		String dbVer = null;
		if (db.beginTransaction()) {
			dbVer = String.valueOf(db.getVersion());
			db.commit();
		} 
		//prodsdb.message("Current local version db: " + dbVer);
		
		//Testing controller
		CategoryController cc = new CategoryController(db, prodsdb);
		cc.editCategory(2, 0, "Электронника", "Все электронное барахло");
		cc.setSqlResultListener(new CategoryController.ISqlResultListener() {
			public boolean sqlQueryReady(ResultSet rs, int callerId) {
				try {
					prodsdb.message("SQL: category name = " + rs.getString(3));
				} catch (java.sql.SQLException e) {
					log.info("callerId = " + callerId + e.toString());
				}
				return true;
			}
		});
		cc.getCategories(2);
		
		//cc.addCategory(0, "TestCategory1", "This description");
		//cc.removeCategory(1);
		
		//cc.sql("SELECT * FROM categories;", 1);
		//cc.getCategories(2);

	}
	
	
	
	
	public void updateUI(Object source) {
	
	}
	
	public boolean sqlQueryReady(ResultSet rs, int callerId) {
		System.out.println("updateUI");
		switch (callerId) {
			//custom sql
			case 1: 
				try {
					message("SQL: " + rs.getInt(1));
				} catch (java.sql.SQLException e) {
					log.info("callerId = " + callerId + e.toString());
				}
			break;
			
			//get categories
			case 2: 
				try {
					message("SQL: category name = " + rs.getString(3));
				} catch (java.sql.SQLException e) {
					log.info("callerId = " + callerId + e.toString());
				}
			break;
		}
		
		return false;
	}
	
	public void error(Exception e) {
		JOptionPane.showMessageDialog(null, "Runtime error(s):\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	public boolean confirm(String msg) {
		return false;
	}
	
	public void message(String msg){
		JOptionPane.showMessageDialog(null, "Message:\n" + msg, "Message", JOptionPane.INFORMATION_MESSAGE);
	}

	public File chooseFile(String promptMsg, DialogType type) {
		if (type == DialogType.SAVE) message("Saving");
		return null;
	}
}