/**
 * Database updater class
 * Update tables in database if shema changes
 * ++ version of dabatbase
 * @author drunia
 * @since 26.09.2013
 */
 
package ua.drunia.prodsdb.logic;

import java.sql.SQLException;
import java.util.logging.Logger;

import ua.drunia.prodsdb.logic.Database;
import ua.drunia.prodsdb.util.LogUtil;
 
 
public class DatabaseUpdater {
	private static Logger log = Logger.getLogger(DatabaseUpdater.class.getName());
	private Database db;
	/**
	 * Constructor 
	 * @param db - Database object
	 * @author drunia
	 */
	public DatabaseUpdater(Database db) {
		this.db = db;
		log.addHandler(LogUtil.getFileHandler());
	}
	
	/**
	 * Update version of database
	 * @author drunia
	 */
	 public boolean update() throws SQLException {
		if (!db.beginTransaction()) return false;
		
		int localVer = db.getVersion();
		if (Database.DB_VER <= localVer) {
			db.rollback();
			return false;
		}
		
		/*
		 * updateSql - array of sql query to update db
		 * updateSql[index] -  index - version of database
		 */
		String[] updateSql = new String[Database.DB_VER + 1];
		updateSql[1] = "INSERT OR REPLACE INTO dbconf (db_ver) VALUES (" + Database.DB_VER + ");";
		
		boolean update = false;
		for (int i = localVer; i < Database.DB_VER; i++) {
			if (i == 0) {
				boolean a = createNewDB();
				boolean b = db.executeUpdate(updateSql[++localVer]) > 0;
				if (!(a && b)) {
					db.rollback();
					throw new SQLException("Error in create new database!");
				}
				continue;
			} 
			update = db.executeUpdate(updateSql[++localVer]) > 0;
			if (!update) {
				db.rollback();
				throw new SQLException("Error in update database to ver: " + localVer);
			}
		}
		db.commit();
		return true;
	 }
	 
	 /**
	  * Creating new empty database
	  * @return true if all OK
	  * @author drunia
	  */
	 private boolean createNewDB() {
		String sql = null;		
		
		/*
		 * dbconf table
		 */ 
		sql = "CREATE TABLE dbconf (db_ver INTEGER NOT NULL);";
		if ((db.executeUpdate(sql) == -1)) return false; 
		
		/*
		 * products table
		 */ 
		sql = "CREATE TABLE products (" + 
			"product_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + 
			"name TEXT NOT NULL, product_cat_id INTEGER NOT NULL);";
		if ((db.executeUpdate(sql) == -1)) return false; 
		
		/*
		 * clients table
		 */ 
		sql = "CREATE TABLE clients (" + 
			"client_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + 
			"client_name TEXT NOT NULL, client_tel TEXT NOT NULL, client_address TEXT, " +
			"client_notes TEXT);";
		if ((db.executeUpdate(sql) == -1)) return false; 
		
		/*
		 * categories table
		 */ 
		sql = "CREATE TABLE categories (" + 
			"cat_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + 
			"cat_parent_id INTEGER NOT NULL," + 
			"name TEXT NOT NULL, description TEXT);";
		if ((db.executeUpdate(sql) == -1)) return false; 
		
	   /*
		* orders table
		*/ 
		sql = "CREATE TABLE orders (" + 
			"order_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + 
			"order_articul TEXT NOT NULL, client_id INTEGER NOT NULL, " + 
			"product_id INTEGER NOT NULL, order_date TEXT NOT NULL);";
		if ((db.executeUpdate(sql) == -1)) return false; 
		
		return true;
	}
}
