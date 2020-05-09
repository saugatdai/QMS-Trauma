package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import qmsCore.Client;
import qmsCore.DBHelper;
import qmsCore.Operator;
import qmsCore.Server;

public class ClientConfigurationController implements Initializable {
	@FXML
	private TextField serverIP;
	@FXML
	private TextField serverPort;
	@FXML
	private ComboBox<String> counters;

	private Operator operator;
	ObservableList<String> counter = FXCollections.observableArrayList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
			"11", "12");

	@FXML
	public void onServerSettingSaveClicked(ActionEvent event) {
		if (serverIP.getText().isEmpty() || serverPort.getText().isEmpty()) {
			DialogViewer.showInformationDialog("Missing Info", "Information Missing",
					"ServerIP and Port should not be empty");
		} else {
			if (new DBHelper().saveServerInfo(serverIP.getText(), Integer.parseInt(serverPort.getText()),
					counters.getValue())) {
				if (operator.getClientInfo() != null) {
					// reloading the server configuration for the operator
					Client c = new DBHelper().getServerInfo();
					operator.getClientInfo().setServerIp(c.getServerIp());
					operator.getClientInfo().setServerPort(c.getServerPort());
					// reload complete
				}
				operator.setCounter(new DBHelper().getCurrentCounter());
				DialogViewer.showInformationDialog("Successful", "Information Stored!!", "Your Information is Saved");
				((Node) event.getSource()).getScene().getWindow().hide();
			} else {
				DialogViewer.showErrorDialog("Error", "Something Went Wrong",
						"The information was not successfully saved");
			}
		}
	}

	@FXML
	public void onBackButtonPressed(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		counters.setItems(counter);
		if (!new DBHelper().ifNotConfiguredServer()) {
			Client c = new DBHelper().getServerInfo();
			Integer port = c.getServerPort();
			serverIP.setText(c.getServerIp());
			serverPort.setText(port.toString());
			counters.getSelectionModel().select(new DBHelper().getCurrentCounter());
		}
	}

	public void serverIPInitializer() {
		serverIP.setText(Server.getNonLoopBackInetAddress().getHostAddress());
		serverIP.setDisable(true);
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}
}
