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
	private JButton addCatBtn, editCatBtn, delCatBtn, updCatBtn;
	private JLabel catInfo;
	private DefaultMutableTreeNode rootNode;
	private ArrayList<Category> cats;
	private Category root;
	private CatPopupMenuListener popupListener;
	
	//////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Add/Edit category dialog
	 * @author drunia
	 */
	private class AddDialog extends JDialog {
		private JPanel editPanel = new JPanel();
		private JButton okBtn = new JButton("OK");
		private JButton cancelBtn = new JButton("Cancel");
		private JComboBox<Category> catCmbBox = new JComboBox<Category>();
		private JTextField catNameTf = new JTextField();
		private JTextArea descArea = new JTextArea();
		private JLabel parentLb = new JLabel("Parent category:");
		private JLabel nameLb = new JLabel("Category name:");
		private JLabel descLb = new JLabel("Category description:");
		private boolean isEdit;
		private Category selCategory;
		
		public AddDialog(boolean isEdit) {
			super(prodsdb, "Add/Edit category", true);
			this.isEdit = isEdit;
			setModal(true);
			setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			setSize(500, 250);
			setLocationRelativeTo(prodsdb);
			
			//get selected node for editing or adding new
			DefaultMutableTreeNode node = 
				(DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			if (node == null) node = rootNode;
			selCategory = (Category) node.getUserObject();		
			Category cat = selCategory;
			if (isEdit) {
				DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
				cat = (Category) parent.getUserObject();
			}
	
			GridLayout gridLay = new GridLayout(3, 2);
			gridLay.setVgap(5); gridLay.setHgap(5);
			editPanel.setLayout(gridLay);
			editPanel.add(parentLb);
			
			//init categories list
			catCmbBox.addItem(root);
			for (int i = 0; i < cats.size(); i++) {
				catCmbBox.addItem(cats.get(i));
				if (cat == cats.get(i)) 
					catCmbBox.setSelectedItem(cat);
			}
			editPanel.add(catCmbBox);
			editPanel.add(nameLb);
			editPanel.add(catNameTf);
			editPanel.add(descLb);
			JScrollPane scrollDesc = new JScrollPane(descArea);
			scrollDesc.setPreferredSize(new Dimension(0, 20));
			add(scrollDesc, BorderLayout.CENTER);
			add(editPanel, BorderLayout.PAGE_START);
			
			//buttons
			okBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Category c = (Category) catCmbBox.getSelectedItem();
					if (AddDialog.this.isEdit) 
						cc.editCategory(selCategory.cat_id, c.cat_id,
							catNameTf.getText(), descArea.getText());
					else
						cc.addCategory(c.cat_id, catNameTf.getText(), descArea.getText());
					dispose();
				}
			});
			cancelBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			FlowLayout flowLay = new FlowLayout();
			JPanel btnPanel = new JPanel(flowLay);
			btnPanel.add(okBtn); btnPanel.add(cancelBtn);
			add(btnPanel, BorderLayout.PAGE_END);
			
			//localize UI
			localize();
			
			//initialize components for edit
			if (isEdit) fillForEdit(selCategory);
		}
		
		/**
		 * Initialize component for edit category
		 * @author drunia
		 */
		public void fillForEdit(Category editCat) {
			catNameTf.setText(editCat.name);
			descArea.setText(editCat.description);
		}
		
		/**
		 * Localize dialog UI
		 * @author drunia
		 */
		public void localize() {
			Properties langRes = Settings.get().getLangResources();
			if (isEdit)
				setTitle(langRes.getProperty("CAT_EDIT_DIALOG_TITTLE"));
			else
				setTitle(langRes.getProperty("CAT_ADD_DIALOG_TITTLE"));
			parentLb.setText(langRes.getProperty("CAT_PARENT_LB") + ":");
			nameLb.setText(langRes.getProperty("CAT_NAME_LB") + ":");
			descLb.setText(langRes.getProperty("CAT_DESC_LB") + ":");
			okBtn.setText(langRes.getProperty("ROOT_BTN_OK"));
			cancelBtn.setText(langRes.getProperty("ROOT_BTN_CANCEL"));
		}
	}
	
	//////////////////////////////////////////////////////////////////////////
	
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
			if (node == null || node == rootNode) {
				//disable edit buttons
				editCatBtn.setEnabled(false);
				delCatBtn.setEnabled(false);
				catInfo.setText("");
				return;
			}
			//insert description in catInfo
			Category c = (Category) node.getUserObject();
			catInfo.setText("<html><table><tr><td>" + c.description + "</td></tr></table></html>");
			//enable edit buttons
			editCatBtn.setEnabled(true);
			delCatBtn.setEnabled(true);
		}
	}

	//////////////////////////////////////////////////////////////////////////
	
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
	
	/////////////////////////////////////////////////////////////////////////
	
	private class CatPopupMenuListener extends MouseAdapter {
		private JPopupMenu catPopupMenu = new JPopupMenu();
		private JMenuItem addItem  = new JMenuItem();
		private JMenuItem editItem = new JMenuItem();
		private JMenuItem delItem  = new JMenuItem();
		private JMenuItem updItem  = new JMenuItem();
		private ActionListener popupActionListener;
		
		//default constructor
		public CatPopupMenuListener() {
			catPopupMenu.add(addItem);
			catPopupMenu.add(editItem);
			catPopupMenu.add(delItem);
			catPopupMenu.add(updItem);
			
			//add action handlers
			popupActionListener = new PopupActionListener();
			addItem.addActionListener(popupActionListener);
			editItem.addActionListener(popupActionListener);
			delItem.addActionListener(popupActionListener);
			updItem.addActionListener(popupActionListener);
		}
		
		//class action listener from PopupMenu items
		private class PopupActionListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object item = e.getSource();
				if (item == addItem) addCatBtn.doClick();
				if (item == editItem) editCatBtn.doClick();
				if (item == delItem) delCatBtn.doClick();
				if (item == updItem) updCatBtn.doClick();
			}
		}
		
		//Event handler, called when mouse button released
		@Override
		public void mouseReleased(MouseEvent e) {
			DefaultMutableTreeNode n = 
				(DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			if (n == null) return;
			if (n == rootNode) {
				editItem.setEnabled(false);
				delItem.setEnabled(false);
			} else {
				editItem.setEnabled(true);
				delItem.setEnabled(true);
			}
			if (e.isPopupTrigger()) {
				int rowUnderCursor = tree.getRowForLocation(e.getX(), e.getY());
				if (rowUnderCursor != -1)
					catPopupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
		
		//localize Popup menu
		public void localize(Properties langRes) {
			addItem.setText(langRes.getProperty("CAT_ADD_BTN"));
			editItem.setText(langRes.getProperty("CAT_EDIT_BTN"));
			delItem.setText(langRes.getProperty("CAT_DEL_BTN"));
			updItem.setText(langRes.getProperty("CAT_UPD_BTN"));
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
		
		//add button
		addCatBtn = new JButton("Add category");
		addCatBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AddDialog(false).setVisible(true);
			}
		});
		
		//edit button
		editCatBtn = new JButton("Edit category");
		editCatBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode n = 
					(DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				if (n == rootNode || n == null) return;
				new AddDialog(true).setVisible(true);
			}
		});
		
		//delete buton
		delCatBtn = new JButton("Delete category");
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
		updCatBtn = new JButton("Update categories");
		updCatBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//update categories tree
				updateUI(this);
			}
		});
		
		//place buttons on panel
		JPanel btnPanel = new JPanel();
		btnPanel.add(addCatBtn);
		btnPanel.add(editCatBtn);
		btnPanel.add(delCatBtn);
		btnPanel.add(updCatBtn);
		((FlowLayout) btnPanel.getLayout()).setAlignment(FlowLayout.LEFT);
		add(btnPanel, BorderLayout.PAGE_START);
		
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
		popupListener = new CatPopupMenuListener();
		tree.addMouseListener(popupListener);
		add(new JScrollPane(tree), BorderLayout.CENTER);
		
		//info JLabel for category
		catInfo = new JLabel();
		add(catInfo, BorderLayout.PAGE_END);
		
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
					//disable edit buttons
					editCatBtn.setEnabled(false);
					delCatBtn.setEnabled(false);
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
		//clear desciption panel
		catInfo.setText("");
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
		editCatBtn.setText(langRes.getProperty("CAT_EDIT_BTN"));
		delCatBtn.setText(langRes.getProperty("CAT_DEL_BTN"));
		updCatBtn.setText(langRes.getProperty("CAT_UPD_BTN"));
		//localize controller 
		cc.localize(langRes);
		//localize popup menu
		popupListener.localize(langRes);
	}
}