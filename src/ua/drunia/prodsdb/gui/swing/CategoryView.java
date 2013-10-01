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
	private class TreeSelectionListener {
		//called when value selection of node changes
		public void valueChanged(TreeSelectionEvent e) {
			log.info("value changed");
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
		delCatBtn = new JButton("Удалить категорию");
		delCatBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cc.removeCategory(0);
			}
		});
		
		//buttons panel
		JPanel btnPanel = new JPanel();
		btnPanel.add(addCatBtn);
		btnPanel.add(delCatBtn);
		add(btnPanel, BorderLayout.PAGE_END);
		
		//categories tree
		rootNode = new DefaultMutableTreeNode("Все");
		tree = new JTree(rootNode);
		
		//info JTextArea for category
		catInfo = new JTextArea();
		catInfo.setEditable(false);
		
		//splitter
		split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.setLeftComponent(tree);
		split.setRightComponent(catInfo);
		
		add(split, BorderLayout.CENTER);
		
		//select all categories from database
		cc.getCategories(1);
	}
	
	//Обработчик всех запросов к БД
	public boolean sqlQueryReady(ResultSet rs, int callerId) {
		Thread t = new Thread() { 
			public void run() {
				log.info("Current thread name is " + Thread.currentThread().getName());
			}; };
		t.setName("druniaListenerThread");
		t.start();
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
		while (rs.next()) {
			Category cat = new Category();
			cat.cat_id = rs.getInt(1);
			cat.parent_id = rs.getInt(1);
			cat.name = rs.getString(3);
			cat.description = rs.getString(4);
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(cat.name);
			node.setUserObject(cat);
			rootNode.add(node);
		}
		return true;
	}
	
	
	/**
	 * Called when controller/model wants update GUI
	 * @author drunia
	 */
	public void updateUI(Object source) {
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