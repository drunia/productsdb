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
	private Statement st;
	private String dbFileName;
	
	/**
	 * Initialize sqllite database
	 * @param dbFileName - Path to database file
	 */
	public Database(IUserUI ui, String dbFileName) {
		this.ui = ui;
		this.dbFileName = dbFileName;
		initDatabase();
	}
	
	/**
	 * Open database connection
	 * After use SQL operations with db need to call commit() method
	 * @return Connection
	 * @author drunia
	 */
	public void beginTransaction() {
		try {
			if ((c != null) && (!c.isClosed())) {
				ui.error(new SQLException("Old connection to db not closed!\n" +
					"Try db.commit() or db.rollback(), do it!"));
			} else {
				c = DriverManager.getConnection("jdbc:sqlite:" + dbFileName);	
				c.setAutoCommit(false);
			}
		} catch (SQLException e) {
			ui.error(e);
		}
	}
	
	/**
	 * Initialize database (create tables, if not exists)
	 * @author drunia
	 */
	private void initDatabase() {
		/*
		 * Try connect with db
		 */
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			ui.error(e);
		}
		/*
		 * Initialize tables
		 */
		try {			
			beginTransaction();
		    st = c.createStatement();
			String sql = null;		
			/*
			 * dbconf table
			 */ 
			sql = "CREATE TABLE IF NOT EXISTS dbconf " +
				"(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
				"db_ver INTEGER NOT NULL);";
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
		}
		initialized = true;
		commit();
	}
	
	/**
	 * Commit actions and close connection to db for multi user access in write mode
	 * @author drunia
	 */
	 public void commit() {
		if (initialized) 
			try {
				c.commit();
				c.close();
			} catch (SQLException e) {
				ui.error(e);
			}
	 }
	 
	 /**
	  * Rollback actions and close connection to db for multi user access in write mode
	  * @author drunia
	  */
	 public void rollback() {
		if (initialized)
			try {
				//c.rollback();
				c.close();
			} catch (SQLException e) {
				ui.error(e);
			}
	 }
	
	/**
	 * Getting the database version
	 * @author drunia
	 * @return int -1 table is empty or version of db
	 */
	public int getVersion() {
		int res = -1;
		String sql = "SELECT db_ver FROM dbconf WHERE " + 
			"id = (SELECT MAX(id) FROM dbconf);";
		try {
			int rowsCount = 
				executeQuery("SELECT COUNT(id) FROM dbconf;").getInt(1); 
			if (rowsCount > 0) res = executeQuery(sql).getInt(1);
		} catch (SQLException e) {
			ui.error(e);
		}
		return res;
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
		} catch (SQLException e) {
			ui.error(e);
		}
		return res; 
	}
	
	/**
	 * Proxy method update/insert action to db
	 * @param preparedSql - prepared sql query like "select * from a where a.b = ? and a.c = ?"
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
			st = c.createStatement();
			rs = st.executeQuery(sql);
		} catch (SQLException e) {
			ui.error(e);
		} 
		return rs;
	}
	
	/**
	 * Proxy method select action to db
	 * @param preparedSql - prepared sql query like "select * from a where a.b = ? and a.c = ?"
	 * @param parameters - array of value of "?" in queue
	 * @author drunia
	 */
	public ResultSet executeQuery(String preparedSql, Object[] parameters) {
		ResultSet rs = null;
		try {;
			PreparedStatement pst = c.prepareStatement(preparedSql);
			for (int i = 0; i < parameters.length; i++) 
				pst.setObject(i + 1, parameters[i]);
			rs = pst.executeQuery();
		} catch (SQLException e) {
			ui.error(e);
		} 
		return rs;
	}
}