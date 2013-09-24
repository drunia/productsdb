package ua.drunia.prodsdb.gui;

import java.io.File;

import java.sql.ResultSet;

public interface IUserUI {
	final static int DIALOG_OPEN_MODE = 0;
	final static int DIALOG_SAVE_MODE = 1;
	/**
	 * Update UI, when data in database ready/change
	 * @param rs - Prepared ResultSet
	 * @author drunia
	 */
    void updateUI(ResultSet rs);
	/**
	 * Show error dialog, if error occured
	 * @author drunia
	 */
	void error(Exception e);
	/**
	 * Confirmation dialog yes/no
	 * @param msg - Query message
	 * @author drunia
	 */
	boolean confirm(String msg);
}