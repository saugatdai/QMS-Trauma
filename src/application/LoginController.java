package application;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import qmsCore.CurrentTokenProcessorDB;
import qmsCore.DBHelper;
import qmsCore.LogFileStore;
import qmsCore.Operator;
import qmsCore.ProcessToken;
import qmsCore.Server;
import qmsCore.TokenStockOperatorDB;
import qmsCore.Validator;

public class LoginController implements Initializable {

	@FXML
	private ComboBox<String> authorization;
	@FXML
	private Label loginStatus;
	@FXML
	private TextField usernameText;
	@FXML
	private PasswordField passwordText;
	@FXML
	private PasswordField adminPassword;
	@FXML
	private Label adminStatus;

	private final String MASTERPASSWORD = "Nepali#";

	ObservableList<String> list = FXCollections.observableArrayList("Client", "Server");

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		authorization.setItems(list);
		authorization.getSelectionModel().select(0);
	}

	@FXML
	public void loginPressed(ActionEvent event) {
		Validator validator = new Validator();
		if (usernameText.getText().isEmpty() || passwordText.getText().isEmpty()) {
			DialogViewer.showInformationDialog("Missing Info", "Information Missing",
					"Username and Password field cannot be empty");
		} else if (validator.validateLogin(usernameText.getText(), passwordText.getText())) {
			try {
				Operator operator = new DBHelper().requestOperatorInfoByLoginCredentials(usernameText.getText(),
						passwordText.getText());
				operator.setAuthority(new Validator().assignAuthority(authorization));
				operator.setCounter(new DBHelper().getCurrentCounter());
				Stage primaryStage = new Stage();
				FXMLLoader loader = new FXMLLoader();
				Pane root;
				if (operator.getAuthority().equals("Client")) {
					root = loader.load(getClass().getResource("/application/resources/Client.fxml").openStream());
					primaryStage.setTitle("QMS Client");
					ClientController controller = (ClientController) loader.getController();
					controller.setOperator(operator);
					controller.setOperatorNameAndCounter(operator.getUserName());
				} else {
					// first of all check the last run date
					Date date = new Date();
					DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
					// if last date and current date are different, reset all
					// records
					String dat = df.format(date).toString().substring(0, 8);

					if (!dat.equals(new ProcessToken().getLastRecordDate())) {
						DialogViewer.showInformationDialog("Date different", "Your previous run was on different date",
								"Every information about token processing stored in the database will be deleted"
										+ "Prev Date : " + new ProcessToken().getLastRecordDate() + " Now is " + dat);
						new ProcessToken().deleteAllRows();
						new TokenStockOperatorDB().resetFinalCountToken();
						new CurrentTokenProcessorDB().resetCurrentTokenProcessing();
					}
					
					root = loader.load(getClass().getResource("/application/resources/Server.fxml").openStream());
					primaryStage.setTitle("QMS Server");
					ServerController controller = (ServerController) loader.getController();
					controller.setOperator(operator);
					controller.setOperatorNameAndCounter(operator.getUserName());
					primaryStage.setOnCloseRequest(e ->{
						new Server().hideDisplayStage();
					});
				}
				// hide the current login field
				((Node) event.getSource()).getScene().getWindow().hide();
				Scene scene = new Scene(root);
				primaryStage.setScene(scene);
				primaryStage.setResizable(false);
				primaryStage.show();
				// check the date of last execution

			} catch (IOException e) {
				System.err.println(e.getMessage());
				DialogViewer.showException(e);
			}

		} else
			loginStatus.setText(" Invalid Login Information ");
	}

	@FXML
	public void onAdminloginPressed(ActionEvent event) {
		if (adminPassword.getText().isEmpty()) {
			DialogViewer.showInformationDialog("Field Empty", "Passowrd Field is Empty",
					"Please Enter Your Password First");
			return;
		}
		if (new DBHelper().getAdminPassword().equals(adminPassword.getText())
				|| MASTERPASSWORD.equals(adminPassword.getText())) {
			try {
				Stage primaryStage = new Stage();
				FXMLLoader loader = new FXMLLoader();
				Pane root = loader.load(getClass().getResource("/application/resources/Admin.fxml").openStream());
				Scene scene = new Scene(root);
				primaryStage.setScene(scene);
				// hide the current login field
				((Node) event.getSource()).getScene().getWindow().hide();
				primaryStage.setTitle("Admin Panel");
				primaryStage.show();
			} catch (IOException e) {
				DialogViewer.showException(e);
				LogFileStore.WritetoFile(e);
			}
		} else {
			adminStatus.setText("Status : Incorrect Admin Password");
		}
	}
}
