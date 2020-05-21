package application;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import qmsCore.Operator;

public class OperatorTable {
	private final SimpleIntegerProperty id;
	private final SimpleStringProperty officeID;
	private final SimpleStringProperty username;
	private final SimpleStringProperty password;
	private final SimpleStringProperty fname;
	private final SimpleStringProperty mname;
	private final SimpleStringProperty lname;
	
	public OperatorTable(Integer id, String officeID, String username, String password, String fname, String mname, String lname){
		super();
		this.id = new SimpleIntegerProperty(id);
		this.officeID = new SimpleStringProperty(officeID);
		this.username = new SimpleStringProperty(username);
		this.password = new SimpleStringProperty(password);
		this.fname = new SimpleStringProperty(fname);
		this.mname = new SimpleStringProperty(mname);
		this.lname = new SimpleStringProperty(lname);
	}
	
	
	public OperatorTable(Operator operator){
		this.id = new SimpleIntegerProperty(operator.getId());
		this.officeID = new SimpleStringProperty(operator.getOfficeId());
		this.username = new SimpleStringProperty(operator.getUserName());
		this.password = new SimpleStringProperty(operator.getPassword());
		this.fname = new SimpleStringProperty(operator.getFirstName());
		this.mname = new SimpleStringProperty(operator.getMiddleName());
		this.lname = new SimpleStringProperty(operator.getLastName());
	}
	public Integer getId() {
		return id.get();
	}

	public String getOfficeID() {
		return officeID.get();
	}

	public String getUsername() {
		return username.get();
	}

	public String getPassword() {
		return password.get();
	}

	public String getFname() {
		return fname.get();
	}

	public String getMname() {
		return mname.get();
	}

	public String getLname() {
		return lname.get();
	}
	
	
}
