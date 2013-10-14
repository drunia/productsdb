/**
 * ClientView 
 * @since 08.10.2013
 * @author drunia
 */
 
package ua.drunia.prodsdb.gui.swing;
 
import java.util.logging.*;
import java.util.*;
import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.table.*;

import ua.drunia.prodsdb.logic.*;
import ua.drunia.prodsdb.gui.*;
import ua.drunia.prodsdb.util.*;

public class ClientView extends JPanel implements
	Controller.ISqlResultListener, IUserUI {
	 
	public final static int ID_COLUMN   = 0;
	public final static int NAME_COLUMN = 1;
	public final static int TEL_COLUMN  = 2;
	public final static int ADDR_COLUMN = 3;
	public final static int NOTE_COLUMN = 4;
	
	private static Logger log = Logger.getAnonymousLogger();
	private JPanel controlPanel, infoPanel;
	private JButton addCliBtn, editCliBtn, delCliBtn, updCliBtn;
	private JTable cliTable;
	private JLabel infoLabel;
	private RootFrame prodsdb;
	private ClientController cc;
	private String cliName, cliTel, cliAddress, cliNotes;
	
	/**
	 * Constructor of ClientView
	 * @param prodsdb reference to root JFrame
	 * @author drunia
	 */
	public ClientView(RootFrame prodsdb) {
		this.prodsdb = prodsdb;
		setLayout(new BorderLayout());
		
		//controller
		cc = new ClientController(prodsdb.getDatabase(), this);
		cc.setSqlResultListener(this);
		
		//creating controlPanel
		controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		//add
		addCliBtn = new JButton(); 
		addCliBtn.addActionListener(new AddCliBtnListener());
		//edit
		editCliBtn = new JButton();
		editCliBtn.addActionListener(new EditCliBtnListener());
		//delete
		delCliBtn = new JButton();
		delCliBtn.addActionListener(new DelCliBtnListener());
		//update
		updCliBtn = new JButton();
		updCliBtn.addActionListener(new UpdCliBtnListener());
		
		//add buttons to controlPanel
		controlPanel.add(addCliBtn);
		controlPanel.add(editCliBtn);
		controlPanel.add(delCliBtn);
		controlPanel.add(updCliBtn);
		
		//add controlPanel to view
		add(controlPanel, BorderLayout.PAGE_START);
		
		//table 
		cliTable = new JTable();
		cliTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		cliTable.getSelectionModel().addListSelectionListener(new CliTableSelectionListener());
		add(new JScrollPane(cliTable), BorderLayout.CENTER);
		
		//info label
		infoLabel = new JLabel();
		add(infoLabel, BorderLayout.PAGE_END);
		
		//selecting data
		updateUI(this);
	}
	

	
	//addCliBtn events handler class
	private class AddCliBtnListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			new AddEditClientDialog(prodsdb, false);
		}
	}
	
	//addCliBtn events handler class
	private class EditCliBtnListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (cliTable.getSelectedRow() == -1) return;
			new AddEditClientDialog(prodsdb, true);
		}
	}
	
	//addCliBtn events handler class
	private class DelCliBtnListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			final int ID_COLUMN = 0;
			int selectedRow = cliTable.getSelectedRow();
			if (selectedRow == -1) return;
			CliTableModel tm = (CliTableModel) cliTable.getModel();
			int id = Integer.parseInt(tm.rows.get(selectedRow)[ID_COLUMN]);
			cc.removeClient(id);
		}
	}
	
	//addCliBtn events handler class
	private class UpdCliBtnListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			cc.getClients(1);
		}
	}
	
	/**
	 * Publish info on infoLabel (html)
	 * @param rowIndex index of selected row
	 * @author drunia
	 */
	private void publishInfo(int rowIndex) {
		if (rowIndex == -1) {
			infoLabel.setVisible(false);
			return;
		} else infoLabel.setVisible(true);
		CliTableModel model = (CliTableModel) cliTable.getModel();
		//build html string
		String html = "<html><table><tr><td>" + cliName +
			":</td><td>" + model.rows.get(rowIndex)[1] + "</td></tr>" +
			"<tr><td>" + cliTel + ":</td><td>" + model.rows.get(rowIndex)[2] +
			"</td></tr><tr><td>" + cliAddress + ":</td><td>" +
			model.rows.get(rowIndex)[3] + "</td></tr><tr><td>" + cliNotes + ":</td>" +
			"<td>" + model.rows.get(rowIndex)[4] + "</td></tr></table></html>";
		infoLabel.setText(html);
	}
	
	/**
	 * Query ready event handler
	 * @param rs prepared ResultSet
	 * @param callerId who requested data from database
	 * @author drunia
	 */
	public boolean sqlQueryReady(ResultSet rs, int callerId) {
		if (callerId == 1) {
			cliTable.setColumnModel(new CliTableColumnModel());
			cliTable.setModel(new CliTableModel(rs));
		}
		
		//default return statement if event not handled
		return false;
	}
	

	/**
	 * Called when controller/model wants update GUI
	 * @author drunia
	 */
	public void updateUI(Object source) {
		//re-selecting clients from database
		cc.getClients(1);
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
		return (JOptionPane.showConfirmDialog(prodsdb, msg, "?",
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
	 * Localize UI. This method must 
     * reinitialize language dependens components	 
	 * @param langRes initialized java.util.ResorceBundle
	 * @author drunia
	 */
	public void localize(Properties langRes) {
		//buttons
		addCliBtn.setText(langRes.getProperty("CLI_ADD_BTN"));
		editCliBtn.setText(langRes.getProperty("CLI_EDIT_BTN"));
		delCliBtn.setText(langRes.getProperty("CLI_DEL_BTN"));
		updCliBtn.setText(langRes.getProperty("CLI_UPD_BTN"));
		//infoLabel
		cliName = langRes.getProperty("CLI_INFO_NAME");
		cliTel = langRes.getProperty("CLI_INFO_TEL");
		cliAddress = langRes.getProperty("CLI_INFO_ADDRESS");
		cliNotes = langRes.getProperty("CLI_INFO_NOTES");
		//table columns (use from infoLabel) 
		CliTableColumnModel colModel = (CliTableColumnModel) cliTable.getColumnModel();
		if (colModel.getColumnCount() == colModel.MAX_COLUMNS) colModel.localize();
		//localize controller 
		cc.localize(langRes);

	}
	
	///////////////////////////////Hepler inner classes//////////////////////////////////
	
	/**
	 * Table model class for clients
	 * @author drunia
	 */
	private class CliTableModel extends AbstractTableModel {
		final static int MAX_COLUMNS = 3;
		private String[] columns = new String[MAX_COLUMNS];
		public ArrayList<String[]> rows = new ArrayList<String[]>();
		
		/**
		 * Default constrictor
		 * @param rs table data source
		 * @author drunia
		 */
		public CliTableModel(ResultSet rs) {
			super();
			//adding data from ResultSet to table
			try {
				while (rs.next()) {
					String[] row = new String[5]; 
					row[ID_COLUMN]   = rs.getString(1);
					row[NAME_COLUMN] = rs.getString(2);
					row[TEL_COLUMN]  = rs.getString(3);
					row[ADDR_COLUMN] = rs.getString(4);
					row[NOTE_COLUMN] = rs.getString(5);
					rows.add(row);
				}
			} catch (SQLException e) {
				log.warning(e.toString());
			}
			//localize
			columns[0] = cliName;
			columns[1] = cliTel;
			columns[2] = cliAddress;
		}

		@Override
		public int getColumnCount() {
			return columns.length;
		}
		
		@Override
		public String getColumnName(int col) {
			return columns[col];
		}
		
		@Override
		public int getRowCount() {
			return rows.size();
		}
		
		@Override
		public Object getValueAt(int row, int col) {
			return rows.get(row)[col + 1];
		}
		
		//Our table is not editable
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}
		
		/**
		 * Clear data in table
		 * @author drunia
		 */
		public void clear() {
			rows.clear();
		}
		
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Table column class for clients 
	 * @author drunia 
	 */
	private class CliTableColumnModel extends DefaultTableColumnModel {
		public final int MAX_COLUMNS = 3;
		/**
		 * Set size limit to column [telefon]
		 * @author drunia
		 */
		@Override
		public TableColumn getColumn(int index) {
			final int TEL_COL = 1; 
			TableColumn tc = super.getColumn(index);
			if (index == TEL_COL) {
				tc.setMinWidth(130);
				tc.setMaxWidth(150);
			}
			return tc;
		}
		
		//localize columns
		public void localize() {
			tableColumns.get(0).setHeaderValue(cliName);
			tableColumns.get(1).setHeaderValue(cliTel);
			tableColumns.get(2).setHeaderValue(cliAddress);
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////

	/**
	 * Selection events listener clas for cliTable
	 * @author drunia
	 */
	private class CliTableSelectionListener implements  ListSelectionListener {
		//selection event
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) return;
			int selectedRow = cliTable.getSelectedRow();
			publishInfo(selectedRow);

		}
	}
	
	//////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Dialog for add or edit client
	 * @author drunia
	 */
	private class AddEditClientDialog extends JDialog {
		private JPanel inputPanel, buttonsPanel;
		private JLabel nameLb, telLb, addrLb, noteLb;
		private JTextField nameTf, telTf, addrTf;
		private JTextArea noteTa;
		private JButton okBtn, cancelBtn;
		private CheckInput checkInput;
		private boolean isEdit;
		private String errCheckMsg;
		
		/**
		 * Default constructor
		 * @author drunia
		 */
		public AddEditClientDialog(JFrame owner, boolean isEdit) {
			super();
			setModal(true);
			setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			setSize(500, 250);
			setLocationRelativeTo(owner);
			this.isEdit = isEdit;
			
			//labels
			nameLb = new JLabel("Name:");
			telLb = new JLabel("Telephone:");
			addrLb = new JLabel("Address:");
			noteLb = new JLabel("Note:");
			
			//inputs
			nameTf = new JTextField();
			telTf = new JTextField(); 
			addrTf = new JTextField(); 
			
			//add inputs for check input format
			checkInput = new CheckInput();
			checkInput.addInput(nameTf, ".+", "Field not be empty");
			checkInput.addInput(telTf, "\\+*[0-9]{10,}", "Error tel format");
			checkInput.addInput(addrTf, ".+", "Field not be empty");
			
			noteTa = new JTextArea();
			noteTa.setLineWrap(true);
			JScrollPane noteScroll = new JScrollPane(noteTa);
			noteScroll.setPreferredSize(new Dimension(0, 50));
			
			//buttons
			BtnActionListener bal = new BtnActionListener();
			okBtn = new JButton("OK"); 
			okBtn.addActionListener(bal);
			cancelBtn = new JButton("Cancel");
			cancelBtn.addActionListener(bal);
			
			//panels
			GridBagLayout gridbag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.BOTH;
			c.ipady = 4; c.insets = new Insets(2, 3, 3, 2);
			inputPanel = new JPanel(gridbag);
			
			//1 row
			c.weightx = 0.0; c.gridx = 0; c.gridy = 0;
			inputPanel.add(nameLb, c);
			c.weightx = 0.2; c.gridx = 1; c.gridy = 0;
			inputPanel.add(nameTf, c);

			c.weightx = 0.0; c.gridx = 2; c.ipadx = 10;
			inputPanel.add(telLb, c);
			c.weightx = 0.1; c.gridx = 3; 
			inputPanel.add(telTf, c);
			
			//2 row
			c.weightx = 0.0; c.gridx = 0; c.gridy = 1;
			inputPanel.add(addrLb, c);		
			c.weightx = 0.1; c.gridx = 1; c.gridwidth = 3;
			inputPanel.add(addrTf, c);
			
			//3 row
			c.weightx = 0.0; c.gridx = 0; c.gridy = 2;
			inputPanel.add(noteLb, c); 
			c.weightx = 0.1; c.gridx = 1; c.gridwidth = 3;
			inputPanel.add(noteScroll, c);
			
			//buttons panel
			buttonsPanel = new JPanel();
			buttonsPanel.add(okBtn); buttonsPanel.add(cancelBtn);
			
			//add panels to dialog
			add(inputPanel, BorderLayout.CENTER);
			add(buttonsPanel, BorderLayout.PAGE_END);
			
			//localize
			localize();
				
			//show
			if (isEdit) fillForEdit();
			setVisible(true);
		}
		
		/** 
		 * ActionEvent handler for buttons
		 * @author drunia
		 */
		private class BtnActionListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton b = (JButton) e.getSource();
				boolean res = false;
				//OK button
				if (b == okBtn) {
					if (checkInput.doCheck()) {
						String name = nameTf.getText();
						String tel  = telTf.getText();
						String addr = addrTf.getText();
						String note = noteTa.getText();
						if (isEdit) {
							int selRow = cliTable.getSelectedRow();
							CliTableModel tm = (CliTableModel) cliTable.getModel();
							int editId = Integer.parseInt(tm.rows.get(selRow)[ID_COLUMN]);
							res = cc.editClient(editId, name, tel, addr, note);
						} else res = cc.addClient(name, tel, addr, note);;
					} else error(new Exception(checkInput.getErrCheckMessage()));
				}
				//Cancel button
				if (b == cancelBtn) res = true;
				if (res) dispose();
			}
		}
		
		
		/**
		 * Initialize inputs for edit
		 * @author drunia
		 */
		 private void fillForEdit() {
			int selectedRow = cliTable.getSelectedRow();
			CliTableModel tm = (CliTableModel) cliTable.getModel();
			
			//get data from table row (table model)
			int id = Integer.parseInt(tm.rows.get(selectedRow)[ID_COLUMN]);
			String name = tm.rows.get(selectedRow)[NAME_COLUMN];
			String tel  = tm.rows.get(selectedRow)[TEL_COLUMN];
			String addr = tm.rows.get(selectedRow)[ADDR_COLUMN];
			String note = tm.rows.get(selectedRow)[NOTE_COLUMN];
			
			//set to UI
			nameTf.setText(name);
			telTf.setText(tel);   
			addrTf.setText(addr); 
			noteTa.setText(note); 
			
			okBtn.requestFocusInWindow();
		 }
		
		/**
		 * Localize this dialog
		 * @param langRes initialized language properties
		 * @author drunia
		 */
		public void localize() {
			Properties langRes = Settings.get().getLangResources();
			if (isEdit)
				setTitle(langRes.getProperty("CLI_EDIT_DIALOG_TITTLE"));
			else
				setTitle(langRes.getProperty("CLI_ADD_DIALOG_TITTLE"));
			//labels
			nameLb.setText(langRes.getProperty("CLI_INFO_NAME") + ":");
			telLb.setText(langRes.getProperty("CLI_INFO_TEL") + ":");
			addrLb.setText(langRes.getProperty("CLI_INFO_ADDRESS") + ":");
			noteLb.setText(langRes.getProperty("CLI_INFO_NOTES") + ":");
			//buttons
			okBtn.setText(langRes.getProperty("ROOT_BTN_OK"));
			cancelBtn.setText(langRes.getProperty("ROOT_BTN_CANCEL"));
			//messages
			errCheckMsg = langRes.getProperty("CLI_ADD_ERR_CHKMSG");
		}
	}
} 