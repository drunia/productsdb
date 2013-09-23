package ua.drunia.prodsdb.logic;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.sqlite.JDBC;

import java.io.File;
import java.io.FileNotFoundException;

import ua.drunia.prodsdb.gui.IUserUI;

public class Database {
	public static final int DB_VER = 1; 
	private IUserUI ui;
	public boolean initialized;
	private String dbFileName; 
	private Connection c;
	private Statement st;
	/**
	 * Initialize sqllite database
	 * @param dbFileName - Path to database file
	 */
	public Database(IUserUI ui, String dbFileName) {
		this.ui = ui;
		this.dbFileName = dbFileName;
		initialized = initDatabase();
	}
	/**
	 * 
	 */
	private boolean initDatabase() {
		/**
		 * Check database file
		 */
		if (!new File(dbFileName).exists()) 
			if (!this.ui.confirm("DB file + " + dbFileName + " not found, would you like chose db file?")) {
				this.ui.showError(new FileNotFoundException("Database file not found"));
				return false;
		} else {/* chose database file */}
		/*
		 * Try connect with db
		 */
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			ui.showError(e);
			return false;
		}
		/*
		 * Initialize tables
		 */
		try {
			c = DriverManager.getConnection("jdbc:sqlite:" + dbFileName);
			st = c.createStatement();
			String sql = null;		
			/*
			 * dbconf table
			 */ 
			sql = "CREATE TABLE IF NOT EXISTS dbconf (db_ver INTEGER NOT NULL);";
			st.executeUpdate(sql);
			/*
			 * products table
			 */ 
			sql = "CREATE TABLE IF NOT EXISTS products (" + 
				"id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + 
				"product_name TEXT NOT NULL);";
			st.executeUpdate(sql);
			/*
			 * clients table
			 */ 
			sql = "CREATE TABLE IF NOT EXISTS clients (" + 
				"id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + 
				"client_name TEXT NOT NULL);";
			st.executeUpdate(sql);
			/*
			 * shoppinng table
			 */ 
			sql = "CREATE TABLE IF NOT EXISTS shopping (" + 
				"id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + 
				"clients_id INTEGER NOT NULL, products_id INTEGER NOT NULL);";
			st.executeUpdate(sql);
			
			if (!c.isClosed()) c.close();			
		} catch (SQLException e) {
			ui.showError(e);
			return false;
		}
		return true;
	}
}