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
	
	/**
	 * Constructor of ClientView
	 * @param prodsdb reference to root JFrame
	 * @author drunia
	 */
	public ClientView(RootFrame prodsdb) {
		this.prodsdb = prodsdb;
		setLayout(new BorderLayout());
		
		ClientController cc = new ClientController(prodsdb.getDatabase(), this);
		
		//creating controlPanel
		controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		addCliBtn = new JButton("1"); 
		editCliBtn = new JButton("2");
		delCliBtn = new JButton("3");
		updCliBtn = new JButton("4");
		
		//add buttons to controlPanel
		controlPanel.add(addCliBtn);
		controlPanel.add(editCliBtn);
		controlPanel.add(delCliBtn);
		controlPanel.add(updCliBtn);
		
		//add controlPanel to view
		add(controlPanel, BorderLayout.PAGE_START);
		
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
	}
	
	//addCliBtn events handler class
	private class DelCliBtnListener implements ActionListener {
	}
	
	//addCliBtn events handler class
	private class UpdCliBtnListener implements ActionListener {
	}
	
	/**
	 * Query ready event handler
	 * @param rs prepared ResultSet
	 * @param callerId who requested data from database
	 * @author drunia
	 */
	public boolean sqlQueryReady(ResultSet rs, int callerId) {
		if (callerId == 1) {
		
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
		//cc.getClients(1);
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
		
	}

} 