package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import qmsCore.DBHelper;

public class ClientViewUsersController implements Initializable {

	@FXML
	private TableView<OperatorTable> table;
	@FXML
	private TableColumn<OperatorTable, Integer> id;
	@FXML
	private TableColumn<OperatorTable, String> officeID;
	@FXML
	private TableColumn<OperatorTable, String> fname;
	@FXML
	private TableColumn<OperatorTable, String> mname;
	@FXML
	private TableColumn<OperatorTable, String> lname;
	@FXML
	private Label dbID;
	@FXML
	private Label dbOfficeID;
	@FXML
	private Label dbFname;
	@FXML
	private Label dbMname;
	@FXML
	private Label dbLname;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		id.setCellValueFactory(new PropertyValueFactory<OperatorTable, Integer>("id"));
		officeID.setCellValueFactory(new PropertyValueFactory<OperatorTable, String>("officeID"));
		fname.setCellValueFactory(new PropertyValueFactory<OperatorTable, String>("fname"));
		mname.setCellValueFactory(new PropertyValueFactory<OperatorTable, String>("mname"));
		lname.setCellValueFactory(new PropertyValueFactory<OperatorTable, String>("lname"));
		table.setItems(new DBHelper().getAllOperatorTable());

		// setting click event for table row

		table.setRowFactory(e -> {
			TableRow<OperatorTable> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
					OperatorTable clickedRow = row.getItem();
					dbID.setText(clickedRow.getId().toString());
					dbOfficeID.setText(clickedRow.getOfficeID());
					dbFname.setText(clickedRow.getFname());
					dbMname.setText(clickedRow.getMname());
					dbLname.setText(clickedRow.getLname());
				}
			});

			return row;
		});

	}
}
