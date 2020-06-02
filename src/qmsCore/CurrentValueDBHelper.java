package qmsCore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import application.DialogViewer;

public class CurrentValueDBHelper {
	private Connection c = null;
	private Statement stmt = null;
	private ResultSet rs = null;
	private PreparedStatement prepStmt;

	public void connectToDB() {
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:lastTokenValue.sqlite");
			c.setAutoCommit(true);
		} catch (ClassNotFoundException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		} catch (SQLException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		}

	}

	public void closeDBConnection() {
		try {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (prepStmt != null)
				prepStmt.close();
			if (c != null)
				c.close();
		} catch (SQLException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		}
	}
	
	public void insertIntoCurrentValue(int tokenNo){
		connectToDB();
		try {
			prepStmt = c.prepareStatement("update currentValue set tokenNo=? where id=1");
			prepStmt.setInt(1, tokenNo);
			prepStmt.executeUpdate();
		} catch (SQLException e) {
			DialogViewer.showException(e);
			e.printStackTrace();
			LogFileStore.WritetoFile(e);
		}
	}
	
	public int getLastGrabbedValue(){
		connectToDB();
		int tokenNumber = 0;
		try {
			stmt = c.createStatement();
			rs = stmt.executeQuery("select * from currentValue");
			while(rs.next()){
				tokenNumber = rs.getInt("tokenNo");
			}
		} catch (SQLException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		}
		closeDBConnection();
		return tokenNumber;
	}
}
