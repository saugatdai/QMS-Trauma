package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import qmsCore.DBHelper;
import qmsCore.Operator;

public class ChangeInfoController {

	@FXML
	private TextField officeID;
	@FXML
	private TextField firstName;
	@FXML
	private TextField middleName;
	@FXML
	private TextField lastName;

	Operator operator;

	@FXML
	public void onUpdateButtonClicked(ActionEvent event) {
		operator.setOfficeId(officeID.getText());
		operator.setFirstName(firstName.getText());
		operator.setMiddleName(middleName.getText());
		operator.setLastName(lastName.getText());
		if (new DBHelper().saveOperatorUpdateInfo(operator)) {
			DialogViewer.showInformationDialog("Success", "Your Information Was Saved",
					"The Information You Tried To Update Was Successfully Saved");
		} else {
			DialogViewer.showErrorDialog("Failed", "Your Information Was Not Saved",
					"The Information You Tried To Update Was Not Successfully Saved");
		}
		((Node) event.getSource()).getScene().getWindow().hide();
	}

	@FXML
	public void onBackButtonClicked(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide();
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public void setFields() {
		officeID.setText(operator.getOfficeId());
		firstName.setText(operator.getFirstName());
		middleName.setText(operator.getMiddleName());
		lastName.setText(operator.getLastName());
	}

}
