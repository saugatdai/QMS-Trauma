package qmsCore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import application.DialogViewer;

public class BypassedListDBHelper {
	private Connection c = null;
	private Statement stmt = null;
	private ResultSet rs = null;
	private PreparedStatement prepStmt;

	public void connectToDB() {
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:bypassedList.sqlite");
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

	public void StoreToBypassedList(int tokenNo) {
		connectToDB();
		String query = "insert into byPassedTokens(tokenNo) values(?)";
		try {
			prepStmt = c.prepareStatement(query);
			prepStmt.setInt(1, tokenNo);
			prepStmt.executeUpdate();
		} catch (SQLException e) {
			DialogViewer.showException(e);
			System.err.println(e.getMessage());
			LogFileStore.WritetoFile(e);
		}
		closeDBConnection();
	}

	public ArrayList<String> getBypassedList() {
		ArrayList<String> byPassedList = new ArrayList<>();
		connectToDB();
		try {
			stmt = c.createStatement();
			rs = stmt.executeQuery("select * from byPassedTokens");
			while (rs.next()) {
				byPassedList.add(new Integer(rs.getInt("tokenNo")).toString());
			}
		} catch (SQLException e) {
			DialogViewer.showException(e);
			System.err.println(e.getMessage());
			LogFileStore.WritetoFile(e);
		}
		closeDBConnection();
		return byPassedList;
	}

	public void deleteSelectedEntry(int tokenNo) {
		connectToDB();
		try {
			prepStmt = c.prepareStatement("delete from byPassedTokens where tokenNo=?");
			prepStmt.setInt(1, tokenNo);
			prepStmt.executeUpdate();
		} catch (SQLException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		}
		closeDBConnection();
	}
	public void clearAllByPassedList(){
		connectToDB();
		try {
			stmt = c.createStatement();
			stmt.executeUpdate("delete from byPassedTokens where 1");
		} catch (SQLException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		}
	}
}
