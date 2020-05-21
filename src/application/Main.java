package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;
import qmsCore.LogFileStore;
import qmsCore.Validator;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		Validator validator = new Validator();
		try {
			validator.generateLoginForm(primaryStage);
		} catch (IOException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
