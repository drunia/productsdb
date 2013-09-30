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
	public boolean addCategory(int parentId, String name, String desc) {
		if (!db.beginTransaction()) return false;

		String sql = "INSERT INTO categories (cat_parent_id, name, description) " +
			" VALUES ('" + parentId + "', '" + name + "', '" + desc + "');";
		boolean res = (db.executeUpdate(sql) > 0);
		db.commit();
		
		//Request updateUI event
		ui.updateUI(this);
		
		return res;
	}
	
	/**
	 * Method remove category from db
	 * @param id - category id in database
	 * @author drunia
	 */
	public boolean removeCategory(int id) {
		if (!db.beginTransaction()) return false;
		
		//check products on link to this category
		String sql = "SELECT COUNT(*) FROM products WHERE product_cat_id = '" + id + "';";
		try {	
			if (db.executeQuery(sql).getInt(1) > 0) {
				db.rollback();
				ui.message("You can not delete this category, she linked with products");
				return false;
			}
		} catch (SQLException e) {
			log.log(Level.WARNING, "Error in check products on link to this category", e);
		}
		
		sql = "DELETE FROM categories WHERE cat_id = '" + id + "';";
		boolean res = (db.executeUpdate(sql) > 0);
		db.commit();
		
		//Request updateUI event
		ui.updateUI(this);
		
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
		if (!(sqlListener == null))
			sqlListener.sqlQueryReady(res, callerId);
		db.commit();
	}
	
	/**
	 * Method edit category in db
	 * @param idEditCategory - edit category
	 * @param newParentId - new parent category id 
	 * @param newName - new name of category
	 * @param newDesc - new description of category
	 */
	public boolean editCategory(int idEditCategory, int newParentId, String newName, String newDesc) {
		if (!db.beginTransaction()) return false;
		
		String sql = "UPDATE categories SET cat_parent_id = '" + newParentId + "', " +
			"name = '" + newName + "', description = '" + newDesc + "' " +
			"WHERE cat_id = '" + idEditCategory + "';";
		boolean res = (db.executeUpdate(sql) > 0);
		db.commit();
		
		//Request updateUI event
		ui.updateUI(this);
		
		return res;
	}
	
}