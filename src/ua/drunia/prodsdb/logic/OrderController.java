/**
 * Order Controller class
 * @since 17.10.2013
 * @author drunia
 */
 
package ua.drunia.prodsdb.logic;

import java.util.logging.Logger;
import ua.drunia.prodsdb.gui.IUserUI;
import ua.drunia.prodsdb.util.LogUtil;

public class OrderController extends Controller {
	private Logger log = Logger.getLogger(OrderController.class.getName());
	
	//Default constructor
	public OrderController(Database db, IUserUI ui) {
		super(db, ui);
		log.addHandler(LogUtil.getFileHandler());
	}
	
	/**
	 * Get all orders from database
	 * @param callerId identificate who request this data
	 * @author drunia
	 */
	public void getOrders(int callerId) {
		if (!db.beginTransaction()) return;
		String sql = "SELECT DISTINCT order_articul, order_date, client_name " +
			"FROM orders, clients WHERE orders.client_id = clients.client_id;";
		ResultSet res = db.executeQuery(sql);
		if (sqlListener != null) sqlListener.sqlQueryReady(res, callerId);
		db.commit();
	}
	
	/**
	 * Get details for order
	 * @param articul articul order
	 * @param callerId identificate who request this data
	 * @author drunia
	 */
	public void getOrderDetails(String articul, int callerId) {
		if (!db.beginTransaction()) return;
		String sql = "SELECT * FROM orders, products WHERE " + 
			"order_articul = '" + articul + "' AND orders.product_id = products.product_id;";
		ResultSet res = db.executeQuery(sql);
		if (sqlListener != null) sqlListener.sqlQueryReady(res, callerId);
		db.commit();
	}
	
	
	/**
	 * Add new order into database	 
	 * @param articul order articul/number
	 * @param client_id client id linked with this order
	 * @param product_id array of products selected for this order
	 * @author drunia
	 */
	public boolean addOrder(String articul, int client_id, int[] product_id) {
		if (!db.beginTransaction()) return false;
		String sql = null;
		int datetime = Calendar.getInstance().getTimeInMillis(); 
		for (int i : product_id) {
			sql = "INSERT INTO orders (order_articul, client_id, product_id, order_date) " +
				"values ('" + articul + "', '" + client_id + "', '" + product_id[i] + "', '" + datetime + "');";
			if (db.executeUpdate(sql) == 0) {
				db.rollback();
				log.warning("Error add order to base !");
				return false;
			}
		}
		db.commit();
		ui.updateUI(this);
		return true;
	}
	
	/**
	 * Edit order in base
	 * @param articul articul of edit order
	 * @param product_id ids of products
	 * @author drunia
	 */
	public boolean editOrder(String articul, int[] product_id) {
		if (!db.beginTransaction()) return false;
		String sql = null;
		int datatime  = 0;
		int client_id = 0;
		//get old order date & order client
		try {
			sql = "SELECT DISTINCT order_date, client_id FROM orders WHERE order_articul = '" + articul + "';";
			int datatime = db.executeQuery(sql).getInt(1);
			int client_id = db.executeQuery(sql).getInt(2);
		} catch (SQLException e) {
			db.rollback();
			log.warning("Error in edit (GET OLD DATA) - " + e.toString());
			return false;
		}
		//delete old order data
		sql = "DELETE FROM orders WHERE articul = '" + editArticul + "';";
		if (db.executeUpdate(sql) == 0) {
			db.rollback();
			log.warning("Error in edit order articul # = " + editArticul + " (Error delete old data)");
			return false;
		}
		//insert new order data 
		for (i : products_id) {
			sql = "INSERT INTO orders (order_articul, client_id, product_id, order_date) " +
				"values ('" + articul + "', '" + client_id + "', '" + product_id[i] + "', '" + datetime + "');";
			if (db.executeUpdate(sql) == 0) {
				db.rollback();
				log.warning("Error edit order (inserting new data to base)");
				return false;
			}
		}
		db.commit();
		ui.updateUI(this);
		return true;
	}
	
	/**
	 * Remove articul from database
	 * @param articul articul to delete
	 * @author drunia
	 */
	public boolean removeOrder(String articul) {
		if (!db.beginTransaction()) return;
		String sql = "DELETE FROM orders WHERE order_articul = '" + articul + "';";
		if (db.executeUpdate == 0) {
			db.rollback();
			log.warning("Error remove articul " + articul + " from database !");
			return false;
		}
		db.commit();
		ui.updateUI(this);
		return true;
	}
}
