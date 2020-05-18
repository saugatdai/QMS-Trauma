package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import qmsCore.Client;
import qmsCore.DBHelper;
import qmsCore.Operator;
import qmsCore.Server;

public class IdleServerController implements Initializable {
	private Server server;
	@FXML
	private Label serverStatus;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Client client = new Client();
		Operator operator = new Operator();
		operator.setFirstName("IdleServer");
		operator.setLastName("On System");
		client.setServerIp(Server.getNonLoopBackInetAddress().toString());
		client.setServerPort(8888);
		operator.setClientInfo(client);
		if (new DBHelper().ifNotConfiguredServer()) {
			serverStatus.setText("Please configure server first");
		} else {
			String ip = client.getServerIp();
			server = new Server(operator, null, null);
			server.setOnFailed((Void) -> serverStatus.setText("Server failed"));
			server.setOnRunning((Void) -> serverStatus.setText("Server Running at IP : "
					+ ip.substring(1, ip.length()) + " and Port : " + client.getServerPort()));
			server.setOnCancelled((Void) -> serverStatus.setText("Server cancelled"));
			server.start();
		}
	}
	@FXML
	public void onTicketDispenserButtonClicked(ActionEvent event){
		// TODO ticket dispenser printer interfacing code...
		System.out.println("Ticket dispenser button clicked.....");
	}
}
