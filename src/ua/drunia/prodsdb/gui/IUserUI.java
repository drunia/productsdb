package ua.drunia.prodsdb.gui;

import java.io.File;

import java.sql.ResultSet;

public interface IUserUI {
	/**
	 * Update, when model want update data in UI
	 * @author drunia
	 */
    void updateUI(Object source);
	/**
	 * Show error dialog, if error occured
	 * @param e - raised Exception
	 * @author drunia
	 */
	void error(Exception e);
	/**
	 * Show message dialog
	 * @param msg - message to user
	 * @author drunia
	 */
	void message(String msg);
	/**
	 * Confirmation dialog yes/no
	 * @param msg - query message
	 * @author drunia
	 */	
	boolean confirm(String msg);
}
