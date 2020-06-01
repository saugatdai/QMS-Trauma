package qmsCore;

import java.io.Serializable;

public class ClientPacket implements Serializable {

	private static final long serialVersionUID = 1L;

	private String status;
	private Token token;
	private String operatorName;
	private String operatorUserName;
	private String operatorId;
	private String operatorCounter;
	// incase of forward request
	private String forwardToCounter;

	public ClientPacket(String status, Token t, Operator op) {
		this.status = status;
		this.token = t;
		this.operatorName = op.getFullName();
		this.operatorUserName = op.getUserName();
		this.operatorId = op.getOfficeId();
		this.operatorCounter = op.getCounter();
	}

	public void setForwardToCounter(String forwardToCounter) {
		this.forwardToCounter = forwardToCounter;
	}

	public String getForwardToCounter() {
		return this.forwardToCounter;
	}

	public String getCounter() {
		return this.operatorCounter;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public String getOperatorUserName() {
		return operatorUserName;
	}

	public void setOperatorUserName(String operatorUserName) {
		this.operatorUserName = operatorUserName;
	}

	public String getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}

	@Override
	public String toString() {
		return "Token : " + token.getTokenNumber() + "\nStatus : " + status.toString() + "\nOperator Name : "
				+ operatorName + "\nOperator username : " + operatorUserName + "\nOperatorId" + operatorId;
	}

}
