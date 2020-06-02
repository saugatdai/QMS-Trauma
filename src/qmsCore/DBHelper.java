package qmsCore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import application.DialogViewer;
import application.OperatorTable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DBHelper {
	private Connection c = null;
	private Statement stmt = null;
	private ResultSet rs = null;
	private PreparedStatement prepStmt;

	public void connectToDB() {
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:QMS.sqlite");
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

	public boolean ifNotConfiguredServer() {
		boolean confirmation = true;
		connectToDB();
		try {
			stmt = c.createStatement();
			rs = stmt.executeQuery("select * from serverInfo");
			if (!rs.next())
				confirmation = true;
			else
				confirmation = false;
			closeDBConnection();
		} catch (SQLException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		}
		return confirmation;
	}

	public boolean saveServerInfo(String ip, int port, String counter) {
		try {
			if (new DBHelper().ifNotConfiguredServer()) {
				connectToDB();
				stmt = c.createStatement();
				stmt.executeUpdate("insert into serverInfo (serverIp,serverPort,counter) values (" + "'" + ip + "','"
						+ port + "','" + counter + "')");
				closeDBConnection();
				return true;
			} else {
				connectToDB();
				prepStmt = c.prepareStatement(
						"update serverInfo set serverIp = ?, serverPort = ?, counter = ? where id = ?");
				prepStmt.setString(1, ip);
				prepStmt.setInt(2, port);
				prepStmt.setString(3, counter);
				prepStmt.setInt(4, 1);
				prepStmt.executeUpdate();
				closeDBConnection();
				return true;
			}
		} catch (SQLException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		}
		return false;

	}

	public Client getServerInfo() {
		Client client = new Client();
		connectToDB();
		try {
			prepStmt = c.prepareStatement("select * from serverInfo where id = ?");
			prepStmt.setInt(1, 1);
			rs = prepStmt.executeQuery();
			while (rs.next()) {
				client.setServerPort(rs.getInt("serverPort"));
				client.setServerIp(rs.getString("serverIp"));
			}
		} catch (SQLException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		}
		closeDBConnection();

		return client;
	}

	public void storeActivityLog() {

	}

	public void updateUpperToken() {

	}

	public int getTotalOperators() {
		connectToDB();
		int rowCount = 0;
		try {
			stmt = c.createStatement();
			// count the total number of rows
			rs = stmt.executeQuery("select COUNT(*) from operators");
			rs.next();
			rowCount = rs.getInt(1);
			closeDBConnection();
		} catch (SQLException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		}

		return rowCount;
	}

	public boolean requestLoginCredentials(String username, String password) {
		connectToDB();
		boolean success = false;
		try {
			prepStmt = c.prepareStatement("select * from operators where username=? and password=?");
			prepStmt.setString(1, username);
			prepStmt.setString(2, password);
			rs = prepStmt.executeQuery();
			if (rs.next())
				success = true;
			closeDBConnection();
		} catch (SQLException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		}
		return success;

	}

	public boolean isUniqueUser(String username) {
		boolean yes = true;
		connectToDB();
		try {
			prepStmt = c.prepareStatement("select * from operators where username = ?");
			prepStmt.setString(1, username);
			rs = prepStmt.executeQuery();
			if (rs.next())
				yes = false;
			closeDBConnection();
		} catch (SQLException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		}
		return yes;
	}

	public boolean storeNewUser(Operator operator) {
		connectToDB();
		try {
			prepStmt = c.prepareStatement(
					"insert into operators (office_id,username,password,fname,mname,lname) values (?,?,?,?,?,?)");
			prepStmt.setString(1, operator.getOfficeId().toUpperCase());
			prepStmt.setString(2, operator.getUserName());
			prepStmt.setString(3, operator.getPassword());
			prepStmt.setString(4, operator.getFirstName().toUpperCase());
			prepStmt.setString(5, operator.getMiddleName().toUpperCase());
			prepStmt.setString(6, operator.getLastName().toUpperCase());
			prepStmt.executeUpdate();
			closeDBConnection();
			return true;
		} catch (SQLException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		}
		return false;

	}

	public Operator requestOperatorInfoByLoginCredentials(String username, String password) {
		Operator operator = new Operator();
		connectToDB();
		try {
			prepStmt = c.prepareStatement("select * from operators where username=? and password=?");
			prepStmt.setString(1, username);
			prepStmt.setString(2, password);
			rs = prepStmt.executeQuery();
			while (rs.next()) {
				operator.setFirstName(rs.getString("fname"));
				operator.setMiddleName(rs.getString("mname"));
				operator.setLastName(rs.getString("lname"));
				operator.setUserName(rs.getString("username"));
				operator.setPassword(rs.getString("password"));
				operator.setId(rs.getInt("id"));
				operator.setOfficeId(rs.getString("office_id"));
			}
			closeDBConnection();
		} catch (SQLException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		}
		return operator;
	}

	public boolean saveOperatorUpdateInfo(Operator operator) {

		connectToDB();
		try {
			prepStmt = c.prepareStatement(
					"update operators set office_id = ?, fname = ?, mname = ?, lname = ? where id = ?");
			prepStmt.setString(1, operator.getOfficeId().toUpperCase());
			prepStmt.setString(2, operator.getFirstName().toUpperCase());
			prepStmt.setString(3, operator.getMiddleName().toUpperCase());
			prepStmt.setString(4, operator.getLastName().toUpperCase());
			prepStmt.setInt(5, operator.getId());
			prepStmt.executeUpdate();
			closeDBConnection();
			return true;
		} catch (SQLException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		}
		return false;
	}

	public boolean changeOperatorLoginCredentials(Operator operator) {
		connectToDB();
		try {
			prepStmt = c.prepareStatement("update operators set username = ?, password = ? where id = ?");
			prepStmt.setString(1, operator.getUserName());
			prepStmt.setString(2, operator.getPassword());
			prepStmt.setInt(3, operator.getId());
			prepStmt.executeUpdate();
			closeDBConnection();
			return true;
		} catch (SQLException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		}
		return false;
	}

	public String getAdminPassword() {
		String password = null;
		connectToDB();
		try {
			stmt = c.createStatement();
			rs = stmt.executeQuery("select * from admin where id = 1");
			password = rs.getString("password");
			closeDBConnection();
		} catch (SQLException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		}
		return password;
	}

	public ObservableList<OperatorTable> getAllOperatorTable() {
		ArrayList<OperatorTable> list = new ArrayList<>();
		connectToDB();
		try {
			stmt = c.createStatement();
			rs = stmt.executeQuery("select * from operators");
			while (rs.next()) {
				OperatorTable op = new OperatorTable(rs.getInt("id"), rs.getString("office_id"), rs.getString("username"),
						rs.getString("password"), rs.getString("fname"), rs.getString("mname"), rs.getString("lname"));
				list.add(op);
			}
		} catch (SQLException e) {
			DialogViewer.showException(e);
			closeDBConnection();
			LogFileStore.WritetoFile(e);
		}
		return FXCollections.observableArrayList(list); 
	}
	public boolean deleteOperatorByID(int id){
		connectToDB();
		try {
			prepStmt = c.prepareStatement("Delete from operators where id = ?");
			prepStmt.setInt(1, id);
			prepStmt.executeUpdate();
			closeDBConnection();
			return true;
		} catch (SQLException e) {
			DialogViewer.showException(e);
			closeDBConnection();
			LogFileStore.WritetoFile(e);
		}
		return false;
	}
	public boolean changeAdminPassword(String password){
		try {
			connectToDB();
			prepStmt = c.prepareStatement("update admin set password = ? where id = 1");
			prepStmt.setString(1, password);
			prepStmt.executeUpdate();
			closeDBConnection();
			return true;
		} catch (SQLException e) {
			DialogViewer.showException(e);
			closeDBConnection();
			LogFileStore.WritetoFile(e);
		}
		return false;
	}
	
	public String getCurrentCounter(){
		String counter = null;
		try {
			connectToDB();
			stmt = c.createStatement();
			rs = stmt.executeQuery("select * from serverInfo where id = 1");
			counter = rs.getString("counter");
			closeDBConnection();
		} catch (SQLException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		}
		return counter;
	}
}
