package net.stevenuray.walletexplorer.executables;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;

public class Log4JTest {
	private static Logger log = getLog();
	public static void main(String[] args) {			
		log.info("Hello Log4J");
	}
	private static Logger getLog() {
		BasicConfigurator.configure();		
		Logger log = Logger.getLogger(Log4JTest.class.getName());
		Layout layout = new PatternLayout();	
		Appender appender = new ConsoleAppender(layout);
		appender.setName("Test Appender");			
		log.addAppender(appender);
		return log;
	}
}
