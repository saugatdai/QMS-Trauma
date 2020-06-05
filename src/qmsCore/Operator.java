package qmsCore;

import java.io.Serializable;

public class Operator implements Serializable {

	private static final long serialVersionUID = 1L;
	private Client clientInfo;
	private String authority;
	private String firstName;
	private String middleName;
	private String lastName;
	private String officeId;
	private String userName;
	private String password;
	private int id;
	private String counter;
	
	public Client getClientInfo(){
		return this.clientInfo;
	}
	public void setClientInfo(Client clientInfo){
		this.clientInfo = clientInfo;
	}

	public String getCounter() {
		return this.counter;
	}

	public void setCounter(String counter) {
		this.counter = counter;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		String content = "Name : " + this.firstName + " " + this.middleName + " " + this.lastName + "\n"
				+ "Authority : " + this.authority + "\n" + "Office_id : " + this.officeId + "\n" + "Userame : "
				+ this.userName + "\n" + "Password : " + this.password + "\n" + "id : " + this.id + "\n";
		return content;

	}
	public String getFullName(){
		return this.firstName + " " + this.middleName + " " + this.lastName;
	}
	public void provideInformation() {

	}

	public void supplyServerInfo() {

	}

	public void viewClientServerInterface() {

	}

	public void updateInfo() {

	}

}
