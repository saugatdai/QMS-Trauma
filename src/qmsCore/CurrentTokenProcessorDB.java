package qmsCore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import application.DialogViewer;

public class CurrentTokenProcessorDB {
	private Connection c = null;
	private Statement stmt = null;
	private ResultSet rs = null;
	private PreparedStatement prepStmt;

	public void connectToDB() {
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:currentToken.sqlite");
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

	public Token getCurrentProcessingToken() {
		Token t = new Token();
		connectToDB();
		String sql = "select * from currentTokenProcessing where id=1";
		try {
			stmt = c.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				t.setTokenNumber(rs.getInt("currentTokenProcessing"));
			}
		} catch (SQLException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		}
		closeDBConnection();
		return t;
	}

	public void updateCurrentTokenProcessing(Token currentToken) {
		connectToDB();
		String sql = "update currentTokenProcessing set currentTokenProcessing = ? where id = ?";
		try {
			prepStmt = c.prepareStatement(sql);
			prepStmt.setInt(1, currentToken.getTokenNumber());
			prepStmt.setInt(2, 1);
			prepStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			LogFileStore.WritetoFile(e);
		}
		closeDBConnection();
	}

	public void resetCurrentTokenProcessing() {
		connectToDB();
		String sql = "update currentTokenProcessing set currentTokenProcessing = 0 where id = 1";
		try {
			stmt = c.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		}
		closeDBConnection();
	}
	
	public void resetCurrentTokenProcessing(int tokenNumber){
		connectToDB();
		String sql = "update currentTokenProcessing set currentTokenProcessing = " + tokenNumber + " where id = 1";
		try {
			stmt = c.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		}
		closeDBConnection();
	}
}
