/**
 * Swing UI
 * Checked text input of TextComponents  
 * @since 14.10.2013
 */
 
package ua.drunia.prodsdb.util;

import java.util.HashMap;
import java.util.ArrayList;

import javax.swing.text.JTextComponent;
import java.awt.Color;


/** 
 * Class checked all inputs and set resulting boolean value
 * @author drunia
 */
public class CheckInput {
	private final Color errColor = new Color(255, 209, 209);
	private final Color okColor = new Color(236, 255, 209);
	private ArrayList<JTextComponent> inputs;
	private ArrayList<String> checkCriteries;
	private ArrayList<String> checkErrMsgs;
	private String checkErrMsg;
	
	/**
	 * Default constructor
	 *
	 * Use:
	 * CheckInput check = new CheckInput();
	 * //check for empty input
	 * check.add(myTextField, ".+", "Field not be empty !");
	 * if (check.doCheck()) 
	 *	 System.out.println("All ok");
	 * else 
	 *   System.out.println("Format error: " + check.getErrCheckMessage());
	 *
	 * @author drunia
	 */
	public CheckInput() {
		inputs = new ArrayList<JTextComponent>();
		checkCriteries = new ArrayList<String>();
		checkErrMsgs = new ArrayList<String>();
	}
	
	/** 
	 * Add input component for check
	 * @param c checked component
	 * @param regexCriteria regex check value (input format)
	 * @param checkErrMsg error message if check is fail
	 * @author drunia
	 */
	public void addInput(JTextComponent c, String regexCriteria, String checkErrMsg) {
		boolean addOk = inputs.add(c); 
		if (addOk) addOk = checkCriteries.add(regexCriteria);
		if (addOk) checkErrMsgs.add(checkErrMsg);
	}
	
	/**
	 * Remove input from check result
	 * @param c component for remove
	 * @author drunia
	 */
	public void removeInput(JTextComponent c) {
		int removeIndex = inputs.indexOf(c);
		boolean delOk = inputs.remove(c);
		if (delOk) delOk = (checkCriteries.remove(removeIndex) != null);
		if (delOk) checkErrMsgs.remove(removeIndex);
	}
	
	/**
	 * Check all inputs and mark who is OK or ERROR
	 * @return global check result
	 * @author drunia
	 */
	public boolean doCheck() {
		for (int i = 0; i < inputs.size(); i++) {
			boolean checkOk = inputs.get(i).getText().matches(checkCriteries.get(i));
			if (!checkOk) {
				inputs.get(i).setBackground(errColor);
				checkErrMsg = checkErrMsgs.get(i);
				return false;
			} else
				inputs.get(i).setBackground(okColor);
		}
		return true;
	}
	
	/**
	 * Returns check error message
	 * @return String error message
	 * @author drunia
	 */
	 public String getErrCheckMessage() {
		return checkErrMsg;
	 }
}