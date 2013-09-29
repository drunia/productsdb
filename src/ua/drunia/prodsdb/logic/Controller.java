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
	 * @return rs - ResultSet
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
				ui.updateUI(rs, callerId);
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
	protected void setDatabase(Database db) {
		this.db = db;
	}
	
	/**
	 * Set controlled view
	 * @param ui - controled view
	 * @author drunia
	 */
	protected void setView(IUserUI ui) {
		this.ui = ui;
	}
}