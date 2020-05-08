package application;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import qmsCore.LogFileStore;
import qmsCore.Registrator;
import qmsCore.Validator;

public class AdminController {
	
	@FXML
	public void onAddUserClicked(ActionEvent event){
		try {
			Registrator reg = new Registrator();
			Stage primaryStage = new Stage();
			FXMLLoader loader = new FXMLLoader();
			Pane root = loader.load(getClass().getResource("/application/resources/Register.fxml").openStream());
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			// hide the current login field
			((Node) event.getSource()).getScene().getWindow().hide();
			reg.provideRegistrationFields(primaryStage);
		} catch (IOException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		}
	}
	@FXML
	public void onViewUsersClicked(ActionEvent event){
		try {
			Stage primaryStage = new Stage();
			FXMLLoader loader = new FXMLLoader();
			Pane root = loader.load(getClass().getResource("/application/resources/ViewUsers.fxml").openStream());
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.initModality(Modality.APPLICATION_MODAL);
			primaryStage.setTitle("Operators List");
			primaryStage.show();
		} catch (IOException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		}
	}
	@FXML
	public void onChangePasswordClicked(ActionEvent event){
		try {
			Stage primaryStage = new Stage();
			FXMLLoader loader = new FXMLLoader();
			Pane root = loader.load(getClass().getResource("/application/resources/ChangeAdminPassword.fxml").openStream());
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.initModality(Modality.APPLICATION_MODAL);
			primaryStage.setTitle("Change Admin Password");
			//get the controller first
			AdminPasswordChangeController controller = loader.getController();
			controller.setSourceWindwo(((Node)event.getSource()).getScene().getWindow());
			primaryStage.show();
		} catch (IOException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		}
	}
	@FXML
	public void onLogOutClicked(ActionEvent event){
		((Node) event.getSource()).getScene().getWindow().hide();
		Stage loginStage = new Stage();
		try {
			new Validator().generateLoginForm(loginStage);
		} catch (IOException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		}
	}
	
}
