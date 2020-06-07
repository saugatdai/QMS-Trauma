package qmsCore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import application.DialogViewer;

public class PrimaryQueueDBHelper {
	private Connection c = null;
	private Statement stmt = null;
	private ResultSet rs = null;
	private PreparedStatement prepStmt;

	public void connectToDB() {
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:primaryQueue.sqlite");
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

	public void insertIntoPrimaryQueue(ForwardPacket packet) {
		connectToDB();
		try {
			prepStmt = c.prepareStatement("Insert into primaryQueueTable values(?,?)");
			prepStmt.setInt(1, packet.getToken().getTokenNumber());
			prepStmt.setString(2, packet.getCounter());
			prepStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			LogFileStore.WritetoFile(e);
		}
		closeDBConnection();
	}

	public ForwardPacket extractTokenAndDelete(ForwardPacket packet) {
		connectToDB();
		ForwardPacket pack = new ForwardPacket();
		try {
			// the extraction process
			stmt = c.createStatement();
			// count the total number of rows
			System.out.println("Extracting from primary queue....");
			rs = stmt.executeQuery("select * from primaryQueueTable where counter = " + packet.getCounter()
					+ " order by tokenNo limit 1");
			while (rs.next()) {
				pack.setCounter(rs.getString("counter"));
				Token t = new Token();
				t.setTokenNumber(rs.getInt("tokenNo"));
				pack.setToken(t);
				System.out.println("Extracted the primary Queue token... " + pack.getToken().getTokenNumber());
			}
			// the deletion process
			System.out.println("Deleting the primary Queue token...");
			prepStmt = c.prepareStatement("delete from primaryQueueTable where tokenNo = ? and counter = ?");
			prepStmt.setInt(1, pack.getToken().getTokenNumber());
			prepStmt.setString(2, pack.getCounter());
			prepStmt.executeUpdate();
		} catch (SQLException e) {
			e.getMessage();
			LogFileStore.WritetoFile(e);
		}

		closeDBConnection();
		return pack;
	}

	public int getPrimaryQueueForCounter(String counter) {
		connectToDB();
		int rowCount = 0;
		try {
			stmt = c.createStatement();
			// count the total number of rows
			rs = stmt.executeQuery("select COUNT(*) from primaryQueueTable where counter = " + counter);
			rs.next();
			rowCount = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
			LogFileStore.WritetoFile(e);
		}
		closeDBConnection();

		return rowCount;
	}

	public void clearPrimaryQueue() {
		connectToDB();
		try {
			stmt = c.createStatement();
			stmt.executeUpdate("delete from primaryQueueTable where 1");
		} catch (SQLException e) {
			e.printStackTrace();
			LogFileStore.WritetoFile(e);
		}
	}
}
