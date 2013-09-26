/**
 * Database updater class
 * Update tables in database if shema changes
 * ++ version of dabatbase
 * @author drunia
 * @since 26.09.2013
 */
 
package ua.drunia.prodsdb.logic;
 
 
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
	 public boolean update() {
		int ver = 0;
	 }
}
