/**
 * Category controller
 * @author drunia
 * @since 28.09.2013
 */
 
package ua.drunia.prodsdb.logic;
 
import ua.drunia.prodsdb.gui.IUserUI;
import ua.drunia.prodsdb.util.LogUtil;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

 
public class CategoryController extends Controller {
	private static Logger log = Logger.getAnonymousLogger();
	private String askDelete, deleteWarn, deleteSubCatWarn;
	
	/**
	 * Controller constructor
	 * @param db initialized database
	 * @param ui reference to owner UI 
	 * @author drunia
	 */
	public CategoryController(Database db, IUserUI ui) {
		super(db, ui);
		log.addHandler(LogUtil.getFileHandler());
	}
 
 	/**
	 * Localize strings for UI
	 * @param langRes language properties from Settings
	 * @author drunia
	 */
	public void localize(Properties langRes) {
		askDelete = langRes.getProperty("CAT_ASK_DELETE");
		deleteWarn = langRes.getProperty("CAT_DELETE_WARN");
		deleteSubCatWarn = langRes.getProperty("CAT_DELETE_SUB_WARN");
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
	public boolean addCategory(int parentId, String name, String desc) {
		if (!db.beginTransaction()) return false;
		String sql = "INSERT INTO categories (cat_parent_id, name, description) " +
			" VALUES ('" + parentId + "', '" + name + "', '" + desc + "');";
		boolean res = (db.executeUpdate(sql) > 0);
		db.commit();	
		
		//request updateUI event
		if (res) ui.updateUI(this);
		return res;
	}
	
	/**
	 * Method remove category from db
	 * Before delete category method check link data from table
	 * products to table categories. If not linked - delete category
	 * @param cat_id id category id in database
	 * @author drunia
	 */
	public boolean removeCategory(int cat_id) {
		if (!ui.confirm(askDelete)) return false;
		if (cat_id == 0 || !db.beginTransaction()) return false;
		
		//check products on link to this category
		String sql = "SELECT COUNT(*) FROM products WHERE product_cat_id = '" + cat_id + "';";
		try {	
			if (db.executeQuery(sql).getInt(1) > 0) {
				db.rollback();
				ui.message(deleteWarn);
				return false;
			}
		} catch (SQLException e) {
			log.warning("Error in check products on link to this category");
		}
		
		//check children categories
		try {	
			sql = "SELECT COUNT(*) FROM categories WHERE cat_parent_id = '" + cat_id + "';";
			if (db.executeQuery(sql).getInt(1) > 0) {
				db.rollback();
				ui.message(deleteSubCatWarn);
				return false;
			}
		} catch (SQLException e) { 
			db.rollback();
			ui.error(e);
			return false;
		}
		
		//delete selected category
		sql = "DELETE FROM categories WHERE cat_id = '" + cat_id + "';";
		boolean res = (db.executeUpdate(sql) > 0);
		db.commit();
		
		//request updateUI event
		if (res) ui.updateUI(this);	
		return res;
	}
	
	/**
	 * Return categories from database
	 * @param callerId - Identify who request this operation
	 * @author drunia
	 */
	public void getCategories(int callerId) {
		if (!db.beginTransaction()) return;
		String sql = "SELECT cat_id, cat_parent_id, name, description FROM categories;";
		ResultSet res = db.executeQuery(sql);
		if (sqlListener != null) sqlListener.sqlQueryReady(res, callerId);
		db.commit();
	}
	
	/**
	 * Method edit category in db
	 * @param cat_id id edit category
	 * @param newParentId - new parent category id 
	 * @param newName - new name of category
	 * @param newDesc - new description of category
	 */
	public boolean editCategory(int cat_id, int newParentId, String newName, String newDesc) {
		if (cat_id == 0 || !db.beginTransaction()) return false;
		String sql = "UPDATE categories SET cat_parent_id = '" + newParentId + "', " +
			"name = '" + newName + "', description = '" + newDesc + "' " +
			"WHERE cat_id = '" + cat_id + "';";
		boolean res = (db.executeUpdate(sql) > 0);
		db.commit();
		if (res) ui.updateUI(this);
		return res;
	}
	
}
