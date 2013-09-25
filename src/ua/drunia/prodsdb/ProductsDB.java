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
			db.executeUpdate("delete from dbconf");
		}
		else
			System.out.println("Database initialized FAIL.");
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
		System.out.println("Message: " + msg);
	}

	public File chooseFile(String promptMsg, int dialogMode) {
		return null;
	}
}