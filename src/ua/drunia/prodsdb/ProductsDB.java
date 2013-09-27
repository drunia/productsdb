package ua.drunia.prodsdb;

import ua.drunia.prodsdb.logic.Database;
import ua.drunia.prodsdb.gui.IUserUI;
import ua.drunia.prodsdb.logic.Settings;

import java.io.File;

import java.sql.ResultSet;

import javax.swing.JOptionPane;

public class ProductsDB implements IUserUI {
	public static void main(String[] args) {
		//Init
		ProductsDB prodsdb = new ProductsDB();
		
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
		prodsdb.message("Current local version db: " + dbVer);
		
	}
	
	
	
	
	public void updateUI(ResultSet rs){
		System.out.println("updateUI");
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