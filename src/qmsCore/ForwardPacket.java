package qmsCore;

import java.io.Serializable;

public class ForwardPacket implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Token token;
	private String counter;
	public Token getToken() {
		return token;
	}
	public void setToken(Token token) {
		this.token = token;
	}
	public String getCounter() {
		return counter;
	}
	public void setCounter(String counter) {
		this.counter = counter;
	}
}
