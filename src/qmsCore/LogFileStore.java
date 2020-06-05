package qmsCore;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogFileStore {
	public static void WritetoFile(Exception e) {
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("dd_MM_yy HH:mm:ss");
		// if last date and current date are different, reset all
		// records
		String dat = df.format(date).toString().substring(0, 8);
		try {
			FileWriter fw = new FileWriter(System.getProperty("user.dir").toString() + "/Logs/" + dat + ".txt", true);
			fw.append("Date : Time => " + df.format(date).toString() + "\n");
			fw.append("exception Message : " + e.getMessage() + "\n");
			fw.append("Exception Stacktrace : \n " + getStackTrace(e) + "\n\n");
			fw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public static String getStackTrace(Exception e){
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
}
