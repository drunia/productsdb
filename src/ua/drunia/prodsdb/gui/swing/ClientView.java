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
import javax.swing.*;
import javax.swing.table.*;

import ua.drunia.prodsdb.logic.*;
import ua.drunia.prodsdb.gui.*;
import ua.drunia.prodsdb.util.*;

public class ClientView extends JPanel implements
	Controller.ISqlResultListener, IUserUI {
	
	private static Logger log = Logger.getAnonymousLogger();
	private JPanel controlPanel, infoPanel;
	private JButton addCliBtn, editCliBtn, delCliBtn, updCliBtn;
	private JTable cliTable;
	private JLabel infoLabel;
	private RootFrame prodsdb;
	private ClientController cc;
	
	/**
	 * Constructor of ClientView
	 * @param prodsdb reference to root JFrame
	 * @author drunia
	 */
	public ClientView(RootFrame prodsdb) {
		this.prodsdb = prodsdb;
		setLayout(new BorderLayout());
		
		cc = new ClientController(prodsdb.getDatabase(), this);
		cc.setSqlResultListener(this);
		
		//creating controlPanel
		controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		addCliBtn = new JButton(); 
		addCliBtn.addActionListener(new AddCliBtnListener());
		editCliBtn = new JButton("2");
		
		delCliBtn = new JButton("3");
		
		updCliBtn = new JButton("4");
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
		add(new JScrollPane(cliTable), BorderLayout.CENTER);
		
	}
	

	
	//addCliBtn events handler class
	private class AddCliBtnListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			cc.addClient("Andrunin Ditry", "0953252604", "Bogodukhov city", "good client");
		}
	}
	
	//addCliBtn events handler class
	private class EditCliBtnListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
		}
	}
	
	//addCliBtn events handler class
	private class DelCliBtnListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
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
	 * Query ready event handler
	 * @param rs prepared ResultSet
	 * @param callerId who requested data from database
	 * @author drunia
	 */
	public boolean sqlQueryReady(ResultSet rs, int callerId) {
		if (callerId == 1) {
			cliTable.setModel(new CliTableModel(rs));
			//cliTable.updateUI();
			System.out.println("sqlQueryReady fired");
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
	}
	
	/////////////////////////////////////////////////////////////////////////////
	/**
	 * Table model class for clients
	 * @author drunia
	 */
	private class CliTableModel extends AbstractTableModel {
		private String[] columns = {"Имя", "Тел", "Адрес"};
		private ArrayList<String[]> rows = new ArrayList<String[]>();
		
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
					row[0] = rs.getString(1);
					row[1] = rs.getString(2);
					row[2] = rs.getString(3);
					row[3] = rs.getString(4);
					row[4] = rs.getString(5);
					rows.add(row);
				}
			} catch (SQLException e) {
				log.warning(e.toString());
			}
		
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
		
	}
	////////////////////////////////////////////////////////////////////////////

} 