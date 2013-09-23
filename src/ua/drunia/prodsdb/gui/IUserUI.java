package ua.drunia.prodsdb.gui;

import java.io.File;

import java.sql.ResultSet;

public interface IUserUI {
	static int DIALOG_OPEN_MODE = 0;
	static int DIALOG_SAVE_MODE = 1;
	/**
	 * Update UI, when data changes
	 * rs - java.sql.ResultSet - answer to ui query
	 */
    void updateUI(ResultSet rs);
	/**
	 * Show error dialog, if error occured
	 */
	void error(Exception e);
	/**
	 * Confirmation dialog
	 */
	boolean confirm(String msg);
}