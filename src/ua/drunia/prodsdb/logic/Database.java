package ua.drunia.prodsdb.logic;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.logging.Logger;

import org.sqlite.JDBC;
import org.sqlite.SQLiteDataSource;

public class Database {
	private static Logger log = Logger.getLogger(Database.class.getName());
	public static final int DB_VER = 1; 
	public boolean initialized;
	private Connection c;
	private Statement st;
	private String dbFileName;
	
	/**
	 * Initialize sqllite database
	 * @param dbFileName - Path to database file
	 */
	public Database(String dbFileName) {
		this.dbFileName = dbFileName;
		if (initialized = initDatabase()) {
			DatabaseUpdater dbup = new DatabaseUpdater(this);
			try {
				if (dbup.update()) log.info("Database update OK to version: " + Database.DB_VER);
			} catch (SQLException e) {
				log.warning(e.toString());
			}
		}
	}
	
	/**
	 * Initialize database (try load driver)
	 * @author drunia
	 */
	private boolean initDatabase() {
		try {
			Class.forName("org.sqlite.JDBC");
			return true;
		} catch (ClassNotFoundException e) {
			log.warning(e.toString());
			return false;
		}
		
	}
	
	/**
	 * Open database connection
	 * After use SQL operations with db need to call commit() method
	 * @return Connection
	 * @author drunia
	 */
	public boolean beginTransaction() {
		try {
			if ((c != null) && (!c.isClosed())) {
				log.warning(new SQLException("Old connection to db not closed!\n" +
					"Need db.commit() or db.rollback(), do it!").toString());
				return false;	
			} else {
				c = DriverManager.getConnection("jdbc:sqlite:" + dbFileName);	
				c.setAutoCommit(false);
			}
		} catch (SQLException e) {
			log.warning(e.toString());
			return false;
		}
		return true;
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
				log.warning(e.toString());
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
				log.warning(e.toString());
			}
	 }
	
	/**
	 * Getting the database version
	 * @author drunia
	 * @return int 0 table is not exist or version of db
	 */
	public int getVersion() {
		int res = 0;
		String sql = "SELECT db_ver FROM dbconf;";
		try {
			int rowsCount = 
				executeQuery("SELECT COUNT(name) FROM sqlite_master WHERE name = 'dbconf';").getInt(1); 
			if (rowsCount > 0) res = executeQuery(sql).getInt(1);
		} catch (SQLException e) {
			log.warning(e.toString());
		}
		return res;
	}
	
	/**
	 * Proxy method update/insert action to db
	 * @param sql - sql query
	 * @return 0 if all OK -1 if error occured
	 * @author drunia
	 */
	public int executeUpdate(String sql) {
		int res = 0;
		try {
			Statement st = c.createStatement();
			res = st.executeUpdate(sql);
		} catch (SQLException e) {
			log.warning(e.toString());
			res = -1;
		}
		return res; 
	}
	
	/**
	 * Proxy method update/insert action to db
	 * @param preparedSql - prepared sql query like "select * from a where a.b = ? and a.c = ?"
	 * @param parameters - array of value of "?" in queue
	 * @return 0 if all OK -1 if error occured
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
			log.warning(e.toString());
			res = -1;
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
			log.warning(e.toString());
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
		try {
			PreparedStatement pst = c.prepareStatement(preparedSql);
			for (int i = 0; i < parameters.length; i++) 
				pst.setObject(i + 1, parameters[i]);
			rs = pst.executeQuery();
		} catch (SQLException e) {
			log.warning(e.toString());
		} 
		return rs;
	}
}