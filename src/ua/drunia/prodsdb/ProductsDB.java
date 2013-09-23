package ua.drunia.prodsdb;

import ua.drunia.prodsdb.logic.Database;
import ua.drunia.prodsdb.gui.IUserUI;

public class ProductsDB implements IUserUI {
	public static void main(String[] args) {
		/*
		 * Init database
		 */
		ProductsDB prodsdb = new ProductsDB();
		Database db = new Database(prodsdb, "products.db");
		if (db.initialized) 
			System.out.println("Database initialized OK.");
		else
			System.out.println("Database initialized FAIL.");
	}
	
	public void showData(String[] fields, String[] rows){
	}
	
	public void showError(Exception e) {
		System.err.println("Runtime error(s):\n" + e);
	}
	
	public boolean confirm(String msg) {
		return false;
	}

}