package ua.drunia.prodsdb.gui.swing;

import ua.drunia.prodsdb.logic.*;
import ua.drunia.prodsdb.util.*;
import ua.drunia.prodsdb.gui.IUserUI;

import java.util.logging.Logger;
import java.sql.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class CategoryView extends JPanel implements 
	CategoryController.ISqlResultListener, IUserUI {
	
	private Logger log = Logger.getAnonymousLogger();
	private RootFrame prodsdb;
	private CategoryController cc;
	private JTree tree;
	private JScrollPane catScroll;
	private JButton addCatBtn, delCatBtn;
	private JSplitPane split;
	private JTextArea catInfo;
	private DefaultMutableTreeNode rootNode;
	
	/**
	 * Realization of TreeSelectionListener inteface
	 * @author drunia
	 */
	private class TreeSelListener implements TreeSelectionListener {
		@Override
		//called when value selection of node changes
		public void valueChanged(TreeSelectionEvent e) {

		}
	}
	
	/**
	 * Class wrapper for data from database
	 * @author drunia
	 */
	private class Category {
		public int cat_id;
		public int parent_id;
		public String name;
		public String description;
		
		/**
		 * Return name of category
		 * @author drunia
		 */
		@Override
		public String toString() {
			return name;
		}
	}
	
	/**
	 * Constructor of view Category
	 * @param prodsdb reference to root JFrame
	 * @author drunia
	 */
	public CategoryView(RootFrame prodsdb) {
		this.prodsdb = prodsdb;
		log.addHandler(LogUtil.getFileHandler());
		setLayout(new BorderLayout());
		
		//create controller
		cc = new CategoryController(prodsdb.getDatabase(), this);
		cc.setSqlResultListener(this);
		
		//create buttons and place him on panel
		//add button
		addCatBtn = new JButton("Добавить категорию");
		addCatBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog("Введите название категории");
				String desc = JOptionPane.showInputDialog("Введите описание категории");
				if ((name == null) || (desc == null)) {
					error(new Exception("Нельзя внести пустые значения"));
					return;
				}
				cc.addCategory(0, name, desc);
			}
		});
		
		//delete button
		delCatBtn = new JButton("Обновить");
		delCatBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				log.info("update()");
				cc.getCategories(1);
			}
		});
		
		//buttons panel
		JPanel btnPanel = new JPanel();
		btnPanel.add(addCatBtn);
		btnPanel.add(delCatBtn);
		add(btnPanel, BorderLayout.PAGE_END);
		
		//categories tree
		Dimension minSize = new Dimension(200, 200);
		
		rootNode = new DefaultMutableTreeNode("Все");
		tree = new JTree(rootNode);
		tree.setMinimumSize(minSize);
		tree.addTreeSelectionListener(new TreeSelListener());
		
		//info JTextArea for category
		catInfo = new JTextArea();
		catInfo.setEditable(false);
		catInfo.setMinimumSize(minSize);
		
		//splitter
		split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.setLeftComponent(tree);
		split.setRightComponent(catInfo);
		split.setDividerLocation(200);
		
		add(split, BorderLayout.CENTER);
		
		//select all categories from database
		cc.getCategories(1);
	}
	
	//Обработчик всех запросов к БД
	public boolean sqlQueryReady(ResultSet rs, int callerId) {

		//Можно заменить на if (callerId == 1) { .... }
		switch (callerId) {
			//Обрабатываю запрос ны выборку категорий
			//1 - это идентификатор запроса на выбор данных из БД
			case 1: 
				try {
					buildTreeFromDatabase(rs);
				} catch (SQLException e) {
					log.warning(e.toString());
				}
			break;
		}
		
		//returned if events not been handled
		return false;
	}
	
	/**
	 * Building categories tree from database
	 * @author drunia
	 */
	private boolean buildTreeFromDatabase(ResultSet rs) throws SQLException {
		ArrayList<Category> cats = new ArrayList<Category>();
		while (rs.next()) {
			Category cat = new Category();
			cat.cat_id = rs.getInt(1);
			cat.parent_id = rs.getInt(2);
			cat.name = rs.getString(3);
			cat.description = rs.getString(4);
			cats.add(cat);
		}
		
		//add categories to tree
		rootNode.removeAllChildren();
		DefaultMutableTreeNode node = null,  node1 = null;	
		int i = 0;
		while (cats.size() > 0) {
			//root categories
			Category c = cats.get(i);
			if (c.parent_id == 0) {
				//System.out.println("find root " + c.cat_id);	
				node = new DefaultMutableTreeNode(c.name);
				node.setUserObject(c);
				rootNode.add(node);
				cats.remove(c);
				//System.out.println("M deleted id = " + c.cat_id);
				//sub categories
				int j = 0;
				while ((cats.size() > 0) && (j < cats.size()-1)) {
					Category c1 = cats.get(j);
					if (c1.parent_id == c.cat_id) {
						node1 = new DefaultMutableTreeNode(c1.name);
						node1.setUserObject(c1);
						node.add(node1); 
						node = node1; c = c1; 
						cats.remove(c); j--;
						//System.out.println("S deleted id = " + c.cat_id);
					} 
					j++;
				}
			} else i++;
		}
		tree.updateUI();
		return true;
	}
	
	
	/**
	 * Called when controller/model wants update GUI
	 * @author drunia
	 */
	public void updateUI(Object source) {
		log.info("updateUI");
		//re-selecting categories from database
		cc.getCategories(1);
	}
	
	/**
	 * Called when model/controller want show error
	 * @author drunia
	 */
	public void error(Exception e) {
		prodsdb.error(e);
	}
	
	/**
	 * Called when model/controller something wants confirm
	 * @return boolean true - yes or false - no
	 * @author drunia
	 */
	public boolean confirm(String msg) {
		return false;
	}
	
	/**
	 * Called when model/controller wants show message to user
	 * @author drunia
	 */
	public void message(String msg){
		prodsdb.message(msg);
	}
	
}