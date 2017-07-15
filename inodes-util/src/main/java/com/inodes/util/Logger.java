package com.inodes.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author jdigruttola
 */
public class Logger {
	
	/**
	 * Message helpers
	 */
	public static final String TABULATOR = "     ";
	public static final String FINAL_LINE = "--------------------------------------------------------------------------------------------------------------------";
	
	/**
	 * Levels
	 */
	public static final int LEVEL_DEBUG   = 0;
	public static final int LEVEL_TRACE   = 1;
	public static final int LEVEL_WARNING = 2;
	public static final int LEVEL_INFO    = 3;
	public static final int LEVEL_ERROR   = 4;
	
	/**
	 * Constants
	 */
	public static final SimpleDateFormat DEFAULT_SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * Real logger
	 */
	protected org.apache.log4j.Logger log4jLogger;
	
	/**
	 * Instanciates a new logger
	 * @param clazz
	 */
	protected Logger(Class<?> clazz) {
		log4jLogger = org.apache.log4j.Logger.getLogger(clazz);
	}
	
	/**
	 * Instanciates a new logger
	 * @param logName
	 */
	protected Logger(String logName) {
		log4jLogger = org.apache.log4j.Logger.getLogger(logName);
	}
	
	/**
	 * @see org.apache.log4j.Logger#getLogger(Class)
	 * @param clazz
	 * @return
	 */
	public static Logger getLogger(Class<?> clazz) {
		return new Logger(clazz);
	}
	
	/**
	 * @see org.apache.log4j.Logger#getLogger(String)
	 * @param clazz
	 * @return
	 */
	public static Logger getLogger(String logName) {
		return new Logger(logName);
	}
	
	/**
	 * @see org.apache.log4j.Logger#debug(Object)
	 * @param message
	 */
	public void debug(Object message) {
		log4jLogger.debug(message);
	}

	/**
	 * @see org.apache.log4j.Logger#debug(Object, Throwable)
	 * @param message
	 * @param t
	 */
	public void debug(Object message, Throwable t) {
		log4jLogger.debug(message, t);
	}

	/**
	 * @see @see org.apache.log4j.Logger#info(Object)
	 * @param message
	 */
	public void info(Object message) {
		log4jLogger.info(message);
	}
	
	/**
	 * @see org.apache.log4j.Logger#info(Object, Throwable)
	 * @param message
	 * @param t
	 */
	public void info(Object message, Throwable t) {
		log4jLogger.info(message, t);
	}

	/**
	 * @see org.apache.log4j.Logger#error(Object)
	 * @param message
	 */
	public void error(Object message) {
		log4jLogger.error(message);
	}
	
	/**
	 * @see org.apache.log4j.Logger#error(Object, Throwable)
	 * @param message
	 * @param t
	 */
	public void error(Object message, Throwable t) {
		log4jLogger.error(message, t);
	}

	/**
	 * @see org.apache.log4j.Logger#trace(Object)
	 * @param message
	 */
	public void trace(Object message) {
		log4jLogger.trace(message);
	}
	
	/**
	 * @see org.apache.log4j.Logger#trace(Object, Throwable)
	 * @param message
	 * @param t
	 */
	public void trace(Object message, Throwable t) {
		log4jLogger.trace(message, t);
	}

	/**
	 * @see org.apache.log4j.Logger#warn(Object)
	 * @param message
	 */
	public void warn(Object message) {
		log4jLogger.warn(message);
	}
	
	/**
	 * @see org.apache.log4j.Logger#warn(Object, Throwable)
	 * @param message
	 * @param t
	 */
	public void warn(Object message, Throwable t) {
		log4jLogger.warn(message, t);
	}
	
	/**
	 * Formats the log entry
	 * @param level
	 * @param message
	 * @return
	 */
	protected String toLogFormat(int level, String message) {

		String sLevel;
		switch (level) {
		case Logger.LEVEL_TRACE:
			sLevel = "TRACE";
			break;
		case Logger.LEVEL_DEBUG:
			sLevel = "DEBUG";
			break;
		case Logger.LEVEL_WARNING:
			sLevel = "WARN";
			break;
		case Logger.LEVEL_INFO:
			sLevel = "INFO";
			break;
		case Logger.LEVEL_ERROR:
			sLevel = "ERROR";
			break;
		default:
			sLevel = "NONE";
			break;
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("[").append(DEFAULT_SDF.format(new Date())).append("] ")
		.append("[").append(Thread.currentThread().getName()).append("] ")
		.append("(").append(sLevel).append(") ")
		.append(log4jLogger.getName()).append(" - ")
		.append(message).append("\n");
		
		return sb.toString();
	}
	
	/**
	 * This method standardizes the logging mode
	 * @param priority
	 * @param className
	 * @param methodName
	 * @param message
	 */
	public void log(int level, String className, String methodName, String message) {
		String msg = className + " class - " + methodName + " method - Message: " + message;
		switch(level) {
		case LEVEL_DEBUG:
			debug(msg);
			break;
		case LEVEL_INFO:
			info(msg);
			break;
		case LEVEL_TRACE:
			trace(msg);
			break;
		case LEVEL_ERROR:
			error(msg);
			break;
		case LEVEL_WARNING:
			warn(msg);
			break;
		default:
			error(msg);
		}
	}
}