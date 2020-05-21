package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import qmsCore.CurrentTokenProcessorDB;
import qmsCore.LogFileStore;
import qmsCore.TokenStockOperatorDB;

public class PresetController {
	@FXML
	private TextField presetValue;
	
	@FXML
	public void onPresetButtonClicked(ActionEvent event){
		boolean isInt = false;
		boolean isValid = false;
		try {
			int number = Integer.parseInt(presetValue.getText().trim());
			isInt = true;
			if(new TokenStockOperatorDB().getFinalCountToken().getTokenNumber() >= number){
				isValid = true;
			}else{
				DialogViewer.showInformationDialog("Invalid", "Very high number "+ number , "The Token Hasn't reached to" + number);
			}
			if(isInt && isValid){
				new CurrentTokenProcessorDB().resetCurrentTokenProcessing(number-1);
				DialogViewer.showInformationDialog("Success", "Preset Successful", "Token preset has been completed to " + number);
			}
		} catch (NumberFormatException e) {
			DialogViewer.showInformationDialog("Error", "Incorrect Format", "Preset Value should be a valid number");
			LogFileStore.WritetoFile(e);
			return;
		}
		((Node) event.getSource()).getScene().getWindow().hide();
	}
	@FXML
	public void OnCancelButtonClicked(ActionEvent event){
		((Node) event.getSource()).getScene().getWindow().hide();
	}
}
