/**
 * Client controller
 * @since 03.10.2013
 * @author drunia
 */
 
package ua.drunia.prodsdb.logic;
 
import ua.drunia.prodsdb.gui.IUserUI;
import ua.drunia.prodsdb.util.LogUtil;
 
import java.util.logging.*;
import java.sql.*;
import java.util.Properties;
 
public class ClientController extends Controller {
	private Logger log = Logger.getAnonymousLogger();
	private String askDelete, deleteWarn;
	
	/**
	 * Controller constructor
	 * @param db initialized database
	 * @param ui reference to owner UI 
	 * @author drunia
	 */
	public ClientController(Database db, IUserUI ui) {
		super(db, ui);
		log.addHandler(LogUtil.getFileHandler());
	}
	
	/**
	 * Localize strings for UI
	 * @param langRes language properties from Settings
	 * @author drunia
	 */
	public void localize(Properties langRes) {
		askDelete = langRes.getProperty("CLI_ASK_DELETE");
		deleteWarn = langRes.getProperty("CLI_DELTE_WARN");
	}
	
	/**
	 * Get all clients from database
	 * @param callerId identificate who request this data
	 * @author drunia
	 */
	public void getClients(int callerId) {
		if (!db.beginTransaction()) return;
		String sql = "SELECT * FROM clients";
		ResultSet res = db.executeQuery(sql);
		if (sqlListener != null) sqlListener.sqlQueryReady(res, callerId);
		db.commit();
	}
	
	/**
	 * Add new client in database
	 * @param name client nane (FIO)
	 * @param tel client telephone like +30991234567
	 * @param address client address or address mail company
	 * @param notes notes for current client like "good stable client"
	 * @return true if adding is OK
	 * @author drunia
	 */
	public boolean addClient(String name, String tel, String address, String notes) {
		if (!db.beginTransaction()) return false;
		String sql = "INSERT INTO clients (client_name, client_tel, client_address, client_notes) " +
			"VALUES ('" + name + "', '" + tel + "', '" + address + "', '" + notes +"');";	
		boolean res = (db.executeUpdate(sql) > 0);
		db.commit();
		if (res) ui.updateUI(this);
		return res;
	}
	
	/**
	 * Delete client from database
	 * Before delete method check link table 'orders' to this client
	 * @param id client id in database
	 * @return true if deleting is OK
	 * @author drunia
	 */
	public boolean removeClient(int id) {
		if (!ui.confirm(askDelete)) return false;
		if (!db.beginTransaction()) return false;
		//check link from table 'orders'
		String sql = "SELECT COUNT(*) FROM orders WHERE client_id = '" + id + "';";
		boolean res = false;
		try {
			res = (db.executeQuery(sql).getInt(1) > 0);
		} catch (SQLException e) {
			db.rollback();
			ui.error(e);
			return false;
		}
		if (res) {
			db.rollback();
			ui.error(new Exception(deleteWarn));
			return false;
		}
		//if links not found
		sql = "DELETE FROM clients WHERE client_id = '" + id + "';";
		res = (db.executeUpdate(sql) > 0);
		db.commit();
		if (res) ui.updateUI(this);
		return res;
	}
	
	/**
	 * Edit client in database
	 * @param editId edited client id in database
	 * @param newName new client name
	 * @param newTel new client telephone
	 * @param newAddress new client address
	 * @param newNotes new notes by this client
	 * @return true if editing is OK
	 * @author drunia
	 */
	public boolean editClient(int editId, String newName, String newTel, String newAddress,
		String newNotes) {
		if (!db.beginTransaction()) return false;
		String sql = "UPDATE clients SET client_name = '" + newName +
			"', client_tel = '" + newTel + "', client_address = '" + newAddress +
			"', client_notes = '" + newNotes + "' WHERE client_id = '" + editId + "';";
		boolean res = (db.executeUpdate(sql) > 0);
		db.commit();
		if (res) ui.updateUI(this);
		return res;
	}
}