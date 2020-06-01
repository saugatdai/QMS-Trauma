package qmsCore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;

import application.DialogViewer;

public class Client implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String serverIp;
	private int serverPort;
	private Socket clientSocket;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	
	public void sendStatus(ClientPacket packet) throws IOException{
		if(outputStream == null){
			outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
			System.out.println("output stream object created");
		}
		System.out.println("writing to the stream");
		outputStream.writeObject(packet);
	}
	public void connectToServer() throws UnknownHostException, IOException{
		clientSocket = new Socket(serverIp, serverPort);
	}
	public ClientPacket getServerResponse() throws IOException, ClassNotFoundException{
		ClientPacket packet = null;
		try {
			if(inputStream == null){
				inputStream = new ObjectInputStream(clientSocket.getInputStream());
			}
			packet = (ClientPacket) inputStream.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			LogFileStore.WritetoFile(e);
		}
		return packet;
	}
	public void closeAll(){
		try {
			if(clientSocket != null)
				clientSocket.close();
			if(inputStream != null)
				inputStream.close();
			if(outputStream != null)
				outputStream.close();
		} catch (IOException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		}
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
	@Override
	public String toString() {
		String info = "Server IP : " + serverIp + "\nServer Port : " + serverPort;
		return info;
	}
	
}
