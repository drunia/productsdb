/**
 * Order View
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
	private JButton delOrderBtn = new JButton();

	/**
	 * Default constructor 
	 * @param prodsdb reference to root JFrame
	 * @author drunia
	 */
	public OrderView(RootFrame prodsdb) {
		this.prodsdb = prodsdb;
		setLayout(new BorderLayout());
	}

}