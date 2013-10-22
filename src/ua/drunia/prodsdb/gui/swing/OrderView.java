/**
 * OrderView
 * @since 21.10.2013
 * @author drunia
 */
 
package ua.drunia.prodsdb.gui.swing;

import javax.swing.*;

import ua.drunia.prodsdb.gui.*; 
import ua.drunia.prodsdb.logic.*;
import ua.drunia.prodsdb.util.*;

public class OrderView extends JPanel implements IUserUI, Controller.ISqlResultListener {
	private static Logger log = Logger.getLogger(OrderView.class.getName());
	private RootFrame prodsdb;
	private JButton editOrderBtn = new JButton();
	private JButton delOrderBtn  = new JButton();
	private JButton updOrdersBtn = new JButton();

	/**
	 * Default constructor 
	 * @param prodsdb reference to root JFrame
	 * @author drunia
	 */
	public OrderView(RootFrame prodsdb) {
		this.prodsdb = prodsdb;
		setLayout(new BorderLayout());
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
		prodsdb.confirm(msg);
	}
	
	/**
	 * Localize UI. This method must 
     * reinitialize language dependens components	 
	 * @param langRes initialized java.util.Properties
	 * @author drunia
	 */
	public void localize(Properties langRes) {
	
	}
	
	///////////////////////////////////Helper classes//////////////////////////////////
	
	/** 
	 * Table model for orders
	 * @author drunia
	 */
	private class OrderTableModel implements TableModel {
	
	}
	
	/** 
	 * Table model for orders details
	 * @author drunia
	 */
	private class OrderDetailsTableModel implements TableModel {
	
	}
	

}