package application;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import qmsCore.ShortcutProperty;

public class ShortcutsController implements Initializable {
	@FXML
	private TextField callAgain;
	@FXML
	private TextField completed;
	@FXML
	private TextField  byPass;
	@FXML
	private TextField maxMin;

	private HashMap<String, String> shortcuts = new HashMap<>();
	
	@FXML
	public void callAgainPressKey(KeyEvent event) {
		callAgain.setText("");
	}
	
	@FXML
	public void completedPressKey(KeyEvent event) {
		completed.setText("");
	}
	
	@FXML
	public void bypassPressKey(KeyEvent event) {
		byPass.setText("");
	}
	
	@FXML
	public void maxMinPressKey(KeyEvent event) {
		maxMin.setText("");
	}
	
	@FXML
	public void callAgainReleasedKey(KeyEvent event) {
		callAgain.setText(event.getCode().toString());
	}
	
	@FXML 
	public void onCompletedReleaseKey(KeyEvent event) {
		completed.setText(event.getCode().toString());
	}
	
	@FXML
	public void onBypassKeyReleased(KeyEvent event) {
		byPass.setText(event.getCode().toString());
	}
	
	@FXML
	public void onMaxMinKeyReleased(KeyEvent event) {
		maxMin.setText(event.getCode().toString());
	}
	
	@FXML
	public void onDoneButtonClicked(ActionEvent event) {
		shortcuts.put("callAgain", callAgain.getText());
		shortcuts.put("completed", completed.getText());
		shortcuts.put("byPass", byPass.getText());
		shortcuts.put("maxMin", maxMin.getText());
		new ShortcutProperty().StoreProperties(shortcuts);
		((Node) event.getSource()).getScene().getWindow().hide();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		shortcuts = new ShortcutProperty().receiveProperties();
		callAgain.setText(shortcuts.get("callAgain"));
		completed.setText(shortcuts.get("completed"));
		byPass.setText(shortcuts.get("byPass"));
		maxMin.setText(shortcuts.get("maxMin"));
	}
}
