/**
 * OrderView
 * @since 21.10.2013
 * @author drunia
 */
 
package ua.drunia.prodsdb.gui.swing;

import javax.swing.*;
import javax.swing.table.*;
import java.util.*;
import java.sql.*;
import java.awt.*;
import java.util.logging.*;

import ua.drunia.prodsdb.gui.*; 
import ua.drunia.prodsdb.logic.*;
import ua.drunia.prodsdb.util.*;

public class OrderView extends JPanel implements IUserUI, Controller.ISqlResultListener {
	private static Logger log = Logger.getLogger(OrderView.class.getName());
	private RootFrame prodsdb;
	private JPanel mainBtnPanel  = new JPanel();
	private JButton editOrderBtn = new JButton();
	private JButton delOrderBtn  = new JButton();
	private JButton updOrderBtn  = new JButton();

	/**
	 * Default constructor 
	 * @param prodsdb reference to root JFrame
	 * @author drunia
	 */
	public OrderView(RootFrame prodsdb) {
		this.prodsdb = prodsdb;
		setLayout(new BorderLayout());
		
		//Top buttons panel
		((FlowLayout) mainBtnPanel.getLayout()).setAlignment(FlowLayout.LEFT);
		mainBtnPanel.add(editOrderBtn);
		mainBtnPanel.add(delOrderBtn);
		mainBtnPanel.add(updOrderBtn);
		add(mainBtnPanel, BorderLayout.PAGE_START);
		
	}
	
	/**
	 * Called, when data in database ready/change
	 * @param rs - prepared ResultSet from db
	 * @param callerId - Identificate caller
	 * @author drunia
	 */
	public boolean sqlQueryReady(ResultSet rs, int callerId) {
	
		//returned if event not handled
		return false;
	}
	
	/**
	 * Update UI, when model changes
	 * @param source update UI initiator
	 * @author drunia
	 */
	@Override
	public void updateUI(Object source) {
	
	}
	
	/**
	 * Show error to user
	 * @param e throwed exception
	 * @author drunia
	 */
	@Override
	public void error(Exception e) {
		prodsdb.error(e);
	}
	
	/**
	 * Show message to user
	 * @param msg message to user
	 * @author drunia
	 */
	@Override
	public void message(String msg) {
		prodsdb.message(msg);
	}
	
	/**
	 * Confirmation dialog yes/no
	 * @param msg query message
	 * @author drunia
	 */	
	@Override
	public boolean confirm(String msg) {
		return prodsdb.confirm(msg);
	}
	
	/**
	 * Localize UI. This method must 
     * reinitialize language dependens components	 
	 * @param langRes initialized java.util.Properties
	 * @author drunia
	 */
	public void localize(Properties langRes) {
		//buttons
		editOrderBtn.setText(langRes.getProperty("ORD_EDIT_BTN"));
		delOrderBtn.setText(langRes.getProperty("ORD_DEL_BTN"));
		updOrderBtn.setText(langRes.getProperty("ORD_UPD_BTN"));
	}
	
	///////////////////////////////////Helper classes//////////////////////////////////
	
	/** 
	 * Table model for orders
	 * @author drunia
	 */
	private class OrderTableModel extends DefaultTableColumnModel {
	
	}
	
	/** 
	 * Table model for orders details
	 * @author drunia
	 */
	private class OrderDetailsTableModel extends DefaultTableColumnModel {
	
	}
	

}