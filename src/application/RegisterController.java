package application;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import qmsCore.DBHelper;
import qmsCore.LogFileStore;
import qmsCore.Operator;
import qmsCore.Registrator;

public class RegisterController {

	@FXML
	private TextField officeID;
	@FXML
	private TextField firstName;
	@FXML
	private TextField middleName;
	@FXML
	private TextField lastName;
	@FXML
	private TextField userName;
	@FXML
	private TextField password;
	@FXML
	private TextField rePassword;
	@FXML
	private Label registerStatus;

	@FXML
	public void registerClicked(ActionEvent event) {
		Operator operator = new Operator();
		Registrator registrator = new Registrator();

		if (!password.getText().equals(rePassword.getText())) {
			registerStatus.setTextFill(Color.FIREBRICK);
			registerStatus.setText("Password Doesn't Match");
			return;
		}
		operator.setOfficeId(officeID.getText());
		operator.setFirstName(firstName.getText());
		operator.setMiddleName(middleName.getText());
		operator.setLastName(lastName.getText());
		operator.setUserName(userName.getText());
		operator.setPassword(password.getText());
		if (registrator.requiredFieldsLeft(operator)) {
			officeID.setStyle("-fx-border-color: #05d1ff");
			firstName.setStyle("-fx-border-color: #05d1ff");
			lastName.setStyle("-fx-border-color: #05d1ff");
			userName.setStyle("-fx-border-color: #05d1ff");
			password.setStyle("-fx-border-color: #05d1ff");
			rePassword.setStyle("-fx-border-color: #05d1ff;");
			registerStatus.setTextFill(Color.FIREBRICK);
			registerStatus.setText("Highlited fields are extremely important");
		} else if (!new DBHelper().isUniqueUser(userName.getText())) {
			registerStatus.setTextFill(Color.FIREBRICK);
			registerStatus.setText("username : " + userName.getText() + " already used");
		} else {
			if (new DBHelper().storeNewUser(operator))
				registerStatus.setText("Registration Successful");
			else
				registerStatus.setText("Registration Failed");
		}

	}
	
	@FXML
	public void backButtonClicked(ActionEvent event){
		try {
			Stage primaryStage = new Stage();
			FXMLLoader loader = new FXMLLoader();
			TabPane root = loader.load(getClass().getResource("/application/resources/Login.fxml").openStream());
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			//hide the current login field
			((Node)event.getSource()).getScene().getWindow().hide();
			primaryStage.setTitle("Login ");
			primaryStage.show();
		} catch (IOException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		}
	}

}
