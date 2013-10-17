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
}
