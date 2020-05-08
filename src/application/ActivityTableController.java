package application;

import java.net.URL;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import qmsCore.CurrentTokenProcessorDB;
import qmsCore.ProcessToken;
import qmsCore.TokenStockOperatorDB;

public class ActivityTableController implements Initializable {

	@FXML
	private TableView<ActivityTable> table;
	@FXML
	private TableColumn<ActivityTable, Integer> id;
	@FXML
	private TableColumn<ActivityTable, Integer> tokenNo;
	@FXML
	private TableColumn<ActivityTable, String> processedBy;
	@FXML
	private TableColumn<ActivityTable, String> time;
	@FXML
	private TableColumn<ActivityTable, String> processorName;
	@FXML
	private TableColumn<ActivityTable, String> officeID;
	@FXML
	private TableColumn<ActivityTable, String> counter;

	public void initialize(URL location, java.util.ResourceBundle resources) {

		id.setCellValueFactory(new PropertyValueFactory<ActivityTable, Integer>("id"));
		tokenNo.setCellValueFactory(new PropertyValueFactory<ActivityTable, Integer>("tokenNo"));
		processedBy.setCellValueFactory(new PropertyValueFactory<ActivityTable, String>("processedBy"));
		time.setCellValueFactory(new PropertyValueFactory<ActivityTable, String>("time"));
		processorName.setCellValueFactory(new PropertyValueFactory<ActivityTable, String>("processorName"));
		officeID.setCellValueFactory(new PropertyValueFactory<ActivityTable, String>("officeID"));
		counter.setCellValueFactory(new PropertyValueFactory<ActivityTable, String>("counter"));
		table.setItems(new ProcessToken().getActivityTableInformation());

	};

	@FXML
	public void onResetAllClicked(ActionEvent event) {
		if (DialogViewer.showConfirmationDialog("Are you sure ?", "Guarented Data Deletion",
				"Everything rows of the table will be deleted including the current token counts and processing")){
			new ProcessToken().deleteAllRows();
			new TokenStockOperatorDB().resetFinalCountToken();
			new CurrentTokenProcessorDB().resetCurrentTokenProcessing();
			for ( int i = 0; i<table.getItems().size(); i++) {
			    table.getItems().clear();
			}	
		}
	}
}
