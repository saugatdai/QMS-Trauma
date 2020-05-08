package application;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import javafx.stage.Window;
import qmsCore.DBHelper;
import qmsCore.LogFileStore;
import qmsCore.Validator;

public class AdminPasswordChangeController {
	@FXML
	private PasswordField password;
	@FXML
	private PasswordField confirm;
	
	private Window sourceWindow;
	
	public void setSourceWindwo(Window sourceWindow){
		this.sourceWindow = sourceWindow;
	}
	
	@FXML
	public void onChangeButtonClicked(ActionEvent event) {
		if (password.getText().isEmpty() || confirm.getText().isEmpty()) {
			DialogViewer.showInformationDialog("Information Missing", "Some Fields are not fielled out",
					"Please fill up Password and Confirm Password field");
			return;
		}
		else if(!password.getText().equals(confirm.getText())){
			DialogViewer.showErrorDialog("Mismatch", "Password Mismatch", "The two password fields should have the same values");
		}
		else{
			if(new DBHelper().changeAdminPassword(this.password.getText())){
				DialogViewer.showInformationDialog("Success", "Query Updated", "Admin Password Changed Successfully");
				((Node) event.getSource()).getScene().getWindow().hide();
				sourceWindow.hide();
				Stage loginStage = new Stage();
				try {
					new Validator().generateLoginForm(loginStage);
				} catch (IOException e) {
					DialogViewer.showException(e);
					LogFileStore.WritetoFile(e);
				}
			}
			else{
				DialogViewer.showErrorDialog("Error", "Not Completed", "Admin password not changed");
				((Node) event.getSource()).getScene().getWindow().hide();
			}
		}
	}
}
