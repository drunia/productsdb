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
	Controller.ISqlResultListener, IUserUI {
	
	private Logger log = Logger.getAnonymousLogger();
	private RootFrame prodsdb;
	private CategoryController cc;
	private JTree tree;
	private JScrollPane catScroll;
	private JButton addCatBtn, delCatBtn, updCatBtn;
	private JSplitPane split;
	private JTextArea catInfo;
	private DefaultMutableTreeNode rootNode;
	private ArrayList<Category> cats;
	private Category root;
	
	//////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Add/Edit category dialog
	 * @author drunia
	 */
	private class AddDialog extends JDialog {
		private JPanel editPanel = new JPanel();
		private JButton okBtn = new JButton("Ок");
		private JButton cancelBtn = new JButton("Отмена");
		private JComboBox<Category> catCmbBox = new JComboBox<Category>();
		private JTextField catNameTf = new JTextField();
		private JTextArea descArea = new JTextArea();
		private JLabel parentLb = new JLabel("Родительская категория:");
		private JLabel nameLb = new JLabel("Название категории:");
		private JLabel descLb = new JLabel("Описание категории:");
		
		public AddDialog(boolean isEdit) {
			super(prodsdb, "Добавление категории", true);
			setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			setSize(400, 300);
			setLocationRelativeTo(prodsdb);
			
			//get selected node for editing or adding new
			DefaultMutableTreeNode node = 
				(DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			
			GridLayout gridLay = new GridLayout(3, 2);
			gridLay.setVgap(5); gridLay.setHgap(5);
			editPanel.setLayout(gridLay);
			editPanel.add(parentLb);
			
			//init categories list
			Category root = new Category();
			root.name = "Корень";
			catCmbBox.addItem(root);
			for (int i = 0; i < cats.size(); i++) {
				catCmbBox.addItem(cats.get(i));
				if (isEdit) {
					DefaultMutableTreeNode parent = 
						(DefaultMutableTreeNode) node.getParent();
					if (parent == null) parent = node;
					Category c = (Category) parent.getUserObject();
					if (c == cats.get(i)) catCmbBox.setSelectedItem(c);
				}
			}
			editPanel.add(catCmbBox);
			
			editPanel.add(nameLb);
			editPanel.add(catNameTf);
			editPanel.add(descLb);
			descArea.setBorder(
				BorderFactory.createEtchedBorder(1));
			descArea.setPreferredSize(new Dimension(0, 50));
			add(descArea, BorderLayout.CENTER);
			add(editPanel, BorderLayout.PAGE_START);
			
			//buttons
			okBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
				}
			});
			cancelBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
				}
			});
			FlowLayout flowLay = new FlowLayout();
			JPanel btnPanel = new JPanel(flowLay);
			btnPanel.add(okBtn); btnPanel.add(cancelBtn);
			add(btnPanel, BorderLayout.PAGE_END);
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Realization of TreeSelectionListener inteface
	 * @author drunia
	 */
	private class TreeSelListener implements TreeSelectionListener {
		@Override
		//called when value selection of node changes
		public void valueChanged(TreeSelectionEvent e) {
			DefaultMutableTreeNode node = 
				(DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			if (node == null || node == rootNode) return;
			//insert description in catInfo
			Category c = (Category) node.getUserObject();
			catInfo.setText("cat_id  = " + c.cat_id + "\nparent_id = " +
				c.parent_id + "\n\n" + c.description);
		}
	}

	////////////////////////////////////////////////////////////////////////////
	
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
	
	////////////////////////////////////////////////////////////////////////////
	
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
				new AddDialog(true).setVisible(true);
			}
		});
		
		//delete buton
		delCatBtn = new JButton("Удалить");
		delCatBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode n = 
					(DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				if (n == null) return;
				Category c = (Category) n.getUserObject();
				cc.removeCategory(c.cat_id);
			}
		});
		
		//update button
		updCatBtn = new JButton("Обновить");
		updCatBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//update categories tree
				cc.getCategories(1);
			}
		});
		
		//buttons panel
		JPanel btnPanel = new JPanel();
		btnPanel.add(addCatBtn);
		btnPanel.add(delCatBtn);
		btnPanel.add(updCatBtn);
		add(btnPanel, BorderLayout.PAGE_END);
		
		//categories tree
		Dimension minSize = new Dimension(200, 200);
		
		rootNode = new DefaultMutableTreeNode();
		root = new Category();
		root.cat_id = 0; root.parent_id = 0;
		rootNode.setUserObject(root);
		
		tree = new JTree(rootNode);
		tree.setMinimumSize(minSize);
		tree.getSelectionModel().setSelectionMode(
			TreeSelectionModel.SINGLE_TREE_SELECTION);
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
	
	/**
	 * Database answers handler, called when database complete 
	 * controller request
	 * @param rs completed ResultSet from database
	 * @param callerId identifi who requested data
	 * @author drunia
	 */
	public boolean sqlQueryReady(ResultSet rs, int callerId) {
		switch (callerId) {
			case 1: 
				try {
					return buildTreeFromDatabase(rs);
				} catch (SQLException e) {
					log.warning(e.toString());
				}
			break;
		}
		
		//returned false if events not been handled
		return false;
	}
	
	/**
	 * Building categories tree from database
	 * @param rs complete ResultSet from database 
	 * @author drunia
	 */
	private boolean buildTreeFromDatabase(ResultSet rs) throws SQLException {
		cats = new ArrayList<Category>();
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
		DefaultMutableTreeNode node = null;	
		for (int i = 0; i < cats.size(); i++) {
			Category c = cats.get(i);
			if (c.parent_id == 0) {
				node = new DefaultMutableTreeNode(c);
				node.setUserObject(c);			
				rootNode.add(node);
				addSubNode(node, cats);
			}
		}
		//update tree UI
		tree.updateUI();
		//expand all tree
		for (int i = 0; i < tree.getRowCount(); i++) tree.expandRow(i);
		
		return true;
	}
			
	/* Add recursive subnode 
	 * @param n parent node
	 * @param cats list of all categories
	 * @author drunia
	 */
	private void addSubNode(DefaultMutableTreeNode n, ArrayList<Category> cats) {
		DefaultMutableTreeNode node = null;	
		for (int i = 0; i < cats.size(); i++) {
			Category c = (Category) n.getUserObject();
			Category c1 = cats.get(i);
			if (c1.parent_id == c.cat_id) {
				node = new DefaultMutableTreeNode(c1);
				node.setUserObject(c1);			
				n.add(node);
				addSubNode(node, cats);
			}
		}
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
		return (JOptionPane.showConfirmDialog(prodsdb, msg, "Подтверждение",
			JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION);
	}
	
	/**
	 * Called when model/controller wants show message to user
	 * @author drunia
	 */
	public void message(String msg){
		prodsdb.message(msg);
	}
	
	/**
	 * Localize view
	 * @param langRes initialized ResourceBundle
	 * @author drunia
	 */
	public void localize(Properties langRes) {
		//tree
		root.name = langRes.getProperty("CAT_TREE_ROOT");
		tree.updateUI();
		//buttons
		addCatBtn.setText(langRes.getProperty("CAT_ADD_BTN"));
		delCatBtn.setText(langRes.getProperty("CAT_DEL_BTN"));
		updCatBtn.setText(langRes.getProperty("CAT_UPD_BTN"));
	}
}