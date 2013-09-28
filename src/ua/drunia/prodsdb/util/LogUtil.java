/**
 * Logging util class
 * @author drunia
 * @since 27.09.2013
 */
package ua.drunia.prodsdb.util;

import java.util.logging.Logger;
import java.util.logging.Level; 
import java.util.logging.FileHandler; 
import java.util.logging.Formatter; 
import java.util.logging.Handler; 
import java.util.logging.LogRecord; 

import java.io.IOException;

public class LogUtil {
	/**
	 * Class for need formatting rules
	 * @author drunia
	 */
	private static class LogFormatter extends Formatter {
		public String format(LogRecord record) {
			return formatMessage(record);
		}
			
		/**
		 * Method formatting log message
		 * @author drunia
		 */
		public String formatMessage(LogRecord record) {
			java.util.Date d = new java.util.Date(record.getMillis()); 
			StringBuilder message = new StringBuilder(record.getLevel() + ": " + d + " ");
			message.append(record.getSourceClassName() + ".");
			message.append(record.getSourceMethodName() + "()\n");
			message.append(record.getMessage() + "\n");
			
			Throwable t = record.getThrown();
			if (t != null && record.getLevel() == Level.WARNING) {
				message.append("Exception: " + t.getMessage() + "\n");
				for (StackTraceElement ste : t.getStackTrace())
					message.append(ste + "\n");
			}
			return message.append("\n").toString();
		}
	}
		
	/**
	 * Return filehandler to log in file
	 * @author drunia
	 */
	public static Handler getFileHandler() {
		FileHandler fh = null;
		try {
			//attach file with max size 1mb
			fh = new FileHandler("productsdb.log", (1024 * 1024 * 1024), 1, true);
			fh.setFormatter(new LogFormatter());
		} catch (IOException e) {
			System.err.println("Error create FileHandler for Logger\n" + e);
		}
		return fh;
	}
}