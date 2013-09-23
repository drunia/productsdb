package ua.drunia.prodsdb.gui;

import java.io.File;

public interface IUserUI {
	static int DIALOG_OPEN_MODE = 0;
	static int DIALOG_SAVE_MODE = 1;
	/**
	 * Update UI, when data changes
	 */
    void updateUI();
	/**
	 * Show error dialog, if error occured
	 */
	void error(Exception e);
	/**
	 * Confirmation dialog
	 */
	boolean confirm(String msg);
}