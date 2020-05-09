package application;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import qmsCore.DBHelper;
import qmsCore.LogFileStore;
import qmsCore.Operator;
import qmsCore.Validator;

public class ChangeLoginController {
	
	Operator operator;
	@FXML
	private TextField username;
	@FXML
	private TextField password;
	@FXML
	private TextField confirmPassword;
	Stage sourceStage;
	
	public void setOperatorAndStage(Operator operator, Stage sourceStage){
		this.operator = operator;
		this.sourceStage = sourceStage;
	}
	
	public void setFields(){
		username.setText(operator.getUserName());
	}
	
	@FXML
	public void onBackButtonClicked(ActionEvent event){
		((Node) event.getSource()).getScene().getWindow().hide();
	}
	@FXML
	public void onChangeClicked(ActionEvent event){
		if(username.getText().isEmpty() || password.getText().isEmpty()){
			DialogViewer.showInformationDialog("Field Empty", "Some Fields are Empty", "All fields are compulsory to be filled");
			return;
		}
		else if(!password.getText().equals(confirmPassword.getText())){
			DialogViewer.showErrorDialog("Error", "Password Error", "The two Passwords Doesn't Match");
			return;
		}
		else if(!username.getText().equals(operator.getUserName()) && !new DBHelper().isUniqueUser(username.getText())){
			DialogViewer.showErrorDialog("Error", "Username Already Used", "Please try a differnet username");
			return;
		}
		operator.setUserName(username.getText());
		operator.setPassword(password.getText());
		if(new DBHelper().changeOperatorLoginCredentials(operator)){
			DialogViewer.showInformationDialog("Success", "Login Information Changed", "Your Please Login Again to Continue");
			((Node) event.getSource()).getScene().getWindow().hide();
			sourceStage.close();
			Stage loginStage = new Stage();
			try {
				new Validator().generateLoginForm(loginStage);
			} catch (IOException e) {
				DialogViewer.showException(e);
				LogFileStore.WritetoFile(e);
			}
		}
		else{
			DialogViewer.showInformationDialog("Failed", "Not Changed", "The information you tried to update was not updated");
			((Node) event.getSource()).getScene().getWindow().hide();
		}
	}
}
