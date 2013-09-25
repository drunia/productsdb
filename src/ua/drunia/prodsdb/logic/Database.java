package ua.drunia.prodsdb.logic;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.sqlite.JDBC;

import ua.drunia.prodsdb.gui.IUserUI;

public class Database {
	public static final int DB_VER = 1; 
	private IUserUI ui;
	public boolean initialized;
	private Connection c;
	private String dbFileName;
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
	 * Initialize database (create tables, if not exists)
	 * @author drunia
	 */
	private boolean initDatabase() {
		/*
		 * Try connect with db
		 */
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			ui.error(e);
			return false;
		}
		/*
		 * Initialize tables
		 */
		try {
			c = DriverManager.getConnection("jdbc:sqlite:" + dbFileName);
			Statement st = c.createStatement();
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
		    st.close();			
		} catch (SQLException e) {
			ui.error(e);
			return false;
		}
		return true;
	}
	
	/**
	 * Proxy method update/insert action to db
	 * @author drunia
	 */
	public int executeUpdate(String sql) {
		int res = 0;
		try {
			Statement st = c.createStatement();
			res = st.executeUpdate(sql);
			st.close();
		} catch (SQLException e) {
			ui.error(e);
		}
		return res; 
	}
	/**
	 * Proxy method update/insert action to db
	 * @param preparedSql - prepared sql query like
	 * "select * from a where a.b = ? and a.c = ?"
	 * @param parameters - array of value of "?" in queue
	 * @author drunia
	 */
	public int executeUpdate(String preparedSql, Object[] parameters) {
		int res = 0;
		try {		
			PreparedStatement pst = c.prepareStatement(preparedSql);
			for (int i = 0; i < parameters.length; i++) 
				pst.setObject(i + 1, parameters[i]);
			res = pst.executeUpdate();
			pst.close();
		} catch (SQLException e) {
			ui.error(e);
		} 
		return res; 
	}
	/**
	 * Proxy method select action to db
	 * @author drunia
	 */
	public ResultSet executeQuery(String sql) {
		ResultSet rs = null;
		try {
			Statement st = c.createStatement();
			rs = st.executeQuery(sql);
			st.close();
		} catch (SQLException e) {
			ui.error(e);
		} 
		return rs;
	}
	/**
	 * Proxy method select action to db
	 * @param preparedSql - prepared sql query like
	 * "select * from a where a.b = ? and a.c = ?"
	 * @param parameters - array of value of "?" in queue
	 * @author drunia
	 */
	public ResultSet executeQuery(String preparedSql, Object[] parameters) {
		ResultSet rs = null;
		try {
			PreparedStatement pst = c.prepareStatement(preparedSql);
			for (int i = 0; i < parameters.length; i++) 
				pst.setObject(i + 1, parameters[i]);
			rs = pst.executeQuery();
			pst.close();
		} catch (SQLException e) {
			ui.error(e);
		} 
		return rs;
	}
}