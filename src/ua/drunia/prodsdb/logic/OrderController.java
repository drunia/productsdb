/**
 * OrderController class
 * @since 17.10.2013
 * @author drunia
 */
 
package ua.drunia.prodsdb.logic;

import java.util.logging.Logger;
import ua.drunia.prodsdb.gui.IUserUI;
import ua.drunia.prodsdb.util.LogUtil;

public class OrderController extends Controller {
	private Logger log = Logger.getLogger(OrderController.class.getName());
	
	//Default controller
	public OrderController(Database db, IUserUI ui) {
		super(db, ui);
		log.addHandler(LogUtil.getFileHandler());
	}
	
	/**
	 * Get all orders from database
	 * @author drunia
	 */
	public void getOrders() {
		if (!db.beginTransaction()) return;
		String sql = "SELECT DISTINCT * FROM orders;";
		ResultSet res = db.executeQuery(sql);
		if (sqlListener != null) sqlListener.sqlQueryReady(res, callerId);
		db.commit();
	}
	
	public boolean addOrder(String articul, int client_id, int[] product_id) {
		if (!db.beginTransaction()) return;
		String sql = null;
		int datetime = Calendar.getInstance().getTimeInMillis(); 
		for (int i : product_id) {
			sql = "INSERT INTO orders (order_articul, client_id, product_id, order_date) " +
			"values ('" + articul + "', '" + client_id + "', '" + product_id[i] + "', '" + datetime + "');";
			if (db.executeUpdate(sql) == 0) {
				log.warning("Error in add order to base !");
				db.rollback();
				return false;
			}
		}
		return true;
	}
}
