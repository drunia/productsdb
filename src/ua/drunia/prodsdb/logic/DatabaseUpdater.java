/**
 * Database updater class
 * Update tables in database if shema changes
 * ++ version of dabatbase
 * @author drunia
 * @since 26.09.2013
 */
 
package ua.drunia.prodsdb.logic;

import java.sql.SQLException;

import ua.drunia.prodsdb.logic.Database;
 
 
public class DatabaseUpdater {
	private Database db;
	/**
	 * Constructor 
	 * @param db - Database object
	 * @author drunia
	 */
	public DatabaseUpdater(Database db) {
		this.db = db;
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
			if (i == 0) {;
				if (!(createNewDB() && db.executeUpdate(updateSql[++localVer]) > 0)) {
					throw new SQLException("Error in create new database!");
				}
				continue;
			} 
			update = db.executeUpdate(updateSql[++localVer]) > 0;
			if (!update) {
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
		if (!(db.executeUpdate(sql) > 0)) return false; 
		/*
		 * products table
		 */ 
		sql = "CREATE TABLE products (" + 
			"id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + 
			"product_name TEXT NOT NULL);";
		if (!(db.executeUpdate(sql) > 0)) return false;
		/*
		 * clients table
		 */ 
		sql = "CREATE TABLE clients (" + 
			"id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + 
			"client_name TEXT NOT NULL);";
		if (!(db.executeUpdate(sql) > 0)) return false;
	   /*
		* shoppinng table
		*/ 
		sql = "CREATE TABLE shopping (" + 
			"id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + 
			"clients_id INTEGER NOT NULL, products_id INTEGER NOT NULL);";
		if (!(db.executeUpdate(sql) > 0)) return false;
		return true;
	}
}
