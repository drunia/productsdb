/**
 * Category controller
 * @author drunia
 * @since 28.09.2013
 */
 
package ua.drunia.prodsdb.logic;
 
import ua.drunia.prodsdb.gui.IUserUI;
import ua.drunia.prodsdb.util.LogUtil;

import java.util.logging.Logger;
import java.sql.ResultSet;

 
public class CategoryController extends Controller {
	private static Logger log = Logger.getLogger(CategoryController.class.getName());
	
	/**
	 * Contructor controller with db and ui
	 * @author drunia
	 */
	public CategoryController(Database db, IUserUI ui) {
		super(db, ui);
		log.addHandler(LogUtil.getFileHandler());
	}
 
	/**
	 * Method add new category to db
	 * @param id - category id
	 * @param parentId - Parent category id 
	 * @param name - name of category
	 * @param desc - description of category
	 * @return boolean - true if all is OK
	 * @author drunia
	 */
	public boolean addCategory(int id, int parentId, String name, String desc) {
		if (!db.beginTransaction()) {
			String err = "Can'not begin transaction";
			ui.error(new Exception(err));
			log.warning(err);
		}
		String sql = "INSERT INTO categories (cat_id, cat_parent_id, name, description) " +
			" VALUES ('" + id + "', '" + parentId + "', '" + name + "', '" + desc + "');";
		boolean res = (db.executeUpdate(sql) > 0);
		db.commit();
		return res;
	}
	
	/**
	 * Method remove category to db
	 * @param id - category id in database
	 * @param desc - description of category
	 * @author drunia
	 */
	public boolean removeCategory(int id) {
		if (!db.beginTransaction()) {
			String err = "Can'not begin transaction";
			ui.error(new Exception(err));
			log.warning(err);
		}
		String sql = "DELETE FROM categories WHERE cat_id = '" + id + "';";
		boolean res = (db.executeUpdate(sql) > 0);
		db.commit();
		return res;
	}
	
	/**
	 * Return categories from database
	 * @return ResultSet - all categories from db
	 * @author drunia
	 */
	public ResultSet getCategories() {
		if (!db.beginTransaction()) {
			String err = "Can'not begin transaction";
			ui.error(new Exception(err));
			log.warning(err);
		}
		String sql = "SELECT cat_id, cat_parent_id, name, description FROM categories;";
		ResultSet res = db.executeQuery(sql);
		db.commit();
		return res;
	}
	
}