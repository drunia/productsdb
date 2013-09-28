/**
 * Abstract controller class
 * @auhor drunia
 * @since 25.09.2013
 */
package ua.drunia.prodsdb.logic;

import ua.drunia.prodsdb.logic.Database;
import ua.drunia.prodsdb.gui.IUserUI;

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