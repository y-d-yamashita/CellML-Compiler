package jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Logger
 * @author y-yamashita
 *
 */
public class CCLogger {
private static final Logger logger;
private static final String loggerName = "CellMLCompilerLogger";

/**** Enable ****/
private static final Level logLevel = Level.INFO;
/**** Disenable ***/
//private static final Level logLevel = Level.WARNING;
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
private CCLogger(){}
/**
 * Simple log method
 * @param msg: log message
 */
public static void log(Class<?> cl,String msg){

	logger.info("\n" +
			"[ class ]: "+cl.getName()+"\n"+
			"[ method]: "+Thread.currentThread().getStackTrace()[2].getMethodName()+"\n"+
			"[message]: "+msg);
}
public static void log(Class<?> cl,int value){
	logger.info("\n" +
			"[ class ]: "+cl.getName()+"\n"+
			"[ method]: "+Thread.currentThread().getStackTrace()[2].getMethodName()+"\n"+
			"[message]: "+value);
}

public static void log(Class<?> cl){

	logger.info("\n" +
			"[ class ]: "+cl.getName()+"\n"+
			"[ method]: "+Thread.currentThread().getStackTrace()[2].getMethodName()+"\n");
}
public static void log(String msg){
	logger.info("\n" +
			"[ class ]: "+Thread.currentThread().getStackTrace()[2].getClassName()+"\n"+
			"[ method]: "+Thread.currentThread().getStackTrace()[2].getMethodName()+"\n"+
			"[message]: "+msg);

}
public static void log(int value){
	logger.info("[message]: "+value);
}
}
