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
			c.setAutoCommit(false);
		    st = c.createStatement();
			String sql = null;		
			/*
			 * dbconf table
			 */ 
			sql = "CREATE TABLE IF NOT EXISTS dbconf " +
				"(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
				"db_ver INTEGER NOT NULL); " +
				"INSERT INTO dbconf (db_ver) VALUES (1);";
			st.executeUpdate(sql);
			c.commit();
			/*
			 * products table
			 */ 
			sql = "CREATE TABLE IF NOT EXISTS products (" + 
				"id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + 
				"product_name TEXT NOT NULL);";
			st.executeUpdate(sql);
			c.commit();
			/*
			 * clients table
			 */ 
			sql = "CREATE TABLE IF NOT EXISTS clients (" + 
				"id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + 
				"client_name TEXT NOT NULL);";
			st.executeUpdate(sql);
			c.commit();
			/*
			 * shoppinng table
			 */ 
			sql = "CREATE TABLE IF NOT EXISTS shopping (" + 
				"id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + 
				"clients_id INTEGER NOT NULL, products_id INTEGER NOT NULL);";
			st.executeUpdate(sql);
			c.commit();
		    st.close();	
			//c.close();
		} catch (SQLException e) {
			ui.error(e);
			return false;
		}
		return true;
	}
	
	/**
	 * Commit actions
	 * @author drunia
	 */
	 public void commit() {
		if (initialized) 
			try {
				c.commit();
			} catch (SQLException e) {
				ui.error(e);
			}
	 }
	 
	 /**
	  * Rollback actions
	  * @author drunia
	  */
	 public void rollback() {
		if (initialized)
		try {
				c.rollback();
			} catch (SQLException e) {
				ui.error(e);
			}
	 }
	
	/**
	 * Getting the database version
	 * @author drunia
	 */
	public int getVersion() {
		int res = -1;
		String sql = "SELECT db_ver FROM dbconf WHERE " + 
			"id = (SELECT MAX(id) FROM dbconf);";
		ResultSet rs = executeQuery(sql);
		try {
			res = rs.getInt(1);
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
			//c = DriverManager.getConnection("jdbc:sqlite:" + dbFileName);
			st = c.createStatement();
			rs = st.executeQuery(sql);
			ui.message(String.valueOf(rs.next()));
			//st.close();
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