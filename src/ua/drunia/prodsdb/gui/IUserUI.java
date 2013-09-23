package ua.drunia.prodsdb.gui;

public interface IUserUI {
	static int DIALOG_OPEN_MODE = 0;
	static int DIALOG_SAVE_MODE = 1;
	/**
	 * Returned data from SQL query to db
	 */
    void showData(String[] fields, String[] rows);
	/**
	 * Show error dialog, if error occured
	 */
	void showError(Exception e);
	/**
	 * Confirmation dialog
	 */
	boolean confirm(String msg);
	/**
	 * Open/Save dialog
	 */
	File fileChoose(int type);
}