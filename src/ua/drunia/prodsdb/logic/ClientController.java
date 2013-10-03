/**
 * Client controller
 * @since 03.10.2013
 * @author drunia
 */
 
package ua.drunia.prodsdb.logic;
 
import ua.drunia.prodsdb.gui.IUserUI;
import ua.drunia.prodsdb.util.LogUtil;
 
import java.util.logging.*;
 
public class ClientController extends Controller implements
	IUserUI, Controller.SqlQueryReadyListener {

	private Logger = Logger.getAnonymousLogger();
	
	/**
	 * Default constructor
	 * @author drunia
	 */
	public ClientController(Database db, IUserUI ui) {
		super(db, ui);
		log.addHandler(LogUtil.getFileHandler());
	}
	
	
}