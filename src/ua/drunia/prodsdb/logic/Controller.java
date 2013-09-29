/**
 * Abstract controller class
 * @auhor drunia
 * @since 25.09.2013
 */
package ua.drunia.prodsdb.logic;

import ua.drunia.prodsdb.logic.Database;
import ua.drunia.prodsdb.gui.IUserUI;

import java.sql.ResultSet;

public abstract class Controller {
	protected Database db;
	protected IUserUI ui;
	protected ISqlResultListener sqlListener;

	/**
	 * Subinterface for listen sql query results 
	 * @author drunia
	 */
	public interface ISqlResultListener {
		/**
		* Called, when data in database ready/change
		* @param rs - prepared ResultSet from db
		* @param callerId - Identificate caller
		* @author drunia
		*/
		boolean sqlQueryReady(ResultSet rs, int callerId);
	}
	
	/**
	 * Default constructor
	 * @param db - initialized database
	 * @param ui - controlled view
	 * @author drunia
	 */
	public Controller(Database db, IUserUI ui) {
		this.db = db;
		this.ui = ui;
	}
	
	/**
	 * Universal sql query to database
	 * @param sql - sql query 
	 * @param callerId - id who initialize sql query
	 * <code>
	 * If query is update db (INSERT,UPDATE,DELETE)
	 * returned int value (ResultSet.getInt(1)) may be:
	 * > 0 - count of updated rows or query no take effect or error.
	 * </code>
	 * @author drunia
	 */
	public boolean sql(String sql, int callerId) {
		if (!db.beginTransaction()) 
			return false;
		else {
			boolean isUpdateSql = 
				sql.toUpperCase().startsWith("UPDATE") ||
				sql.toUpperCase().startsWith("INSERT") ||
				sql.toUpperCase().startsWith("DELETE");
			if (isUpdateSql) 	
				return (db.executeUpdate(sql) != -1);
			else {
				ResultSet rs = null;
				rs = db.executeQuery(sql);
				if (!(sqlListener == null))
					sqlListener.sqlQueryReady(rs, callerId);
				db.commit();
				return true;
			}
		}
	}
	
	/**
	 * Set initialized database
	 * @param db - initialized database
	 * @author drunia
	 */
	public void setDatabase(Database db) {
		this.db = db;
	}
	
	/**
	 * Set controlled view
	 * @param ui - controled view
	 * @author drunia
	 */
	public void setView(IUserUI ui) {
		this.ui = ui;
	}
	
	/**
	 * Set the ISqlResultListener object to listen sql result events
	 * @param sqlListener - object implemented Controller.ISqlResultListener interface
	 */
	public void setSqlResultListener(ISqlResultListener sqlListener) {
		this.sqlListener = sqlListener;
	}
}