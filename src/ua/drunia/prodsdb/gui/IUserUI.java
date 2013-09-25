package ua.drunia.prodsdb.gui;

import java.io.File;

import java.sql.ResultSet;

public interface IUserUI {
	final static int DIALOG_OPEN_MODE = 0;
	final static int DIALOG_SAVE_MODE = 1;
	/**
	 * Update UI, when data in database ready/change
	 * @param rs - prepared ResultSet from db
	 * @author drunia
	 */
    void updateUI(ResultSet rs);
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
	/**
	 * Choose file dialog
	 * @param promptMsg - Prompt message to user
	 * @author drunia
	 */
	 File chooseFile(String promptMsg, int dialogMode);
}