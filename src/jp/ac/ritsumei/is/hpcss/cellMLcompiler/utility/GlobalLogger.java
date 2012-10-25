package jp.ac.ritsumei.is.hpcss.cellMLcompiler.utility;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Logger
 * @author y-yamashita
 *
 */
public class GlobalLogger {
private static final Logger logger;
private static final String loggerName = "GlobalLogger";
private static final Level logLevel = Level.WARNING;
static{
	logger = Logger.getLogger(loggerName);
	ConsoleHandler handler = new ConsoleHandler();
	handler.setFormatter(new SimpleFormatter());
	handler.setLevel(logLevel);
	logger.setLevel(logLevel);
	logger.addHandler(handler);
	logger.setUseParentHandlers(false);
}
/** Constructor */
private GlobalLogger(){}
/**
 * Simple log method
 * @param msg: log message
 */
public static void log(String msg){
	logger.info(msg);
}
}
