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
	private static Logger log = Logger.getAnonymousLogger();
	
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
		
		//request updateUI event
		if (res) ui.updateUI(this);
		return res;
	}
	
	/**
	 * Service method for removeCategory()
	 * Recursive delete categories in tree
	 * @param cat_id root category
	 * @return boolean [true - delete OK | false delete FAIL]
	 * @author drunia
     */	 
	private boolean removeSubCategories(int cat_id) {
		//ATENTION beginTransaction() must be already opened.
		String sql = "SELECT cat_id FROM categories WHERE cat_parent_id = " + cat_id;
		ResultSet rs = db.executeQuery(sql);
		boolean res = false;
		try {
			while (rs.next()) {
				int rm_id = rs.getInt(1);
				res = removeSubCategories(rm_id);
				sql = "DELETE FROM categories WHERE cat_id = '" + rm_id + "';";
				res = (db.executeUpdate(sql) > 0);
			}
		} catch (SQLException e) { log.warning(e.toString()); }
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
		if (cat_id == 0 || !db.beginTransaction()) return false;
		
		//check products on link to this category
		String sql = "SELECT COUNT(*) FROM products WHERE product_cat_id = '" + cat_id + "';";
		try {	
			if (db.executeQuery(sql).getInt(1) > 0) {
				db.rollback();
				ui.message("You can not delete this category, she linked with products");
				return false;
			}
		} catch (SQLException e) {
			log.log(Level.WARNING, "Error in check products on link to this category", e);
		}
		
		//check children categories
		boolean deleteChild = true;
		try {	
			deleteChild = true;
			sql = "SELECT COUNT(*) FROM categories WHERE cat_parent_id = '" + cat_id + "';";
			if (db.executeQuery(sql).getInt(1) > 0) {
				boolean uiconfirm = ui.confirm("Удалить с подкатегориями ?");
				if (uiconfirm) {
					deleteChild = removeSubCategories(cat_id);
				} else {
					db.rollback();
					return false;
				}
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
		if (res && deleteChild) ui.updateUI(this);	
		return (res && deleteChild);
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
		return res;
	}
	
}
