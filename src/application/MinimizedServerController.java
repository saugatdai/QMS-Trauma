package application;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import qmsCore.BypassedListDBHelper;
import qmsCore.ClientPacket;
import qmsCore.CurrentValueDBHelper;
import qmsCore.LogFileStore;
import qmsCore.Operator;
import qmsCore.Server;
import qmsCore.ServerThreadProcessor;
import qmsCore.ShortcutProperty;
import qmsCore.Status;
import qmsCore.Token;

public class MinimizedServerController implements Initializable {
	private double xOffset = 0;
	private double yOffset = 0;
	private Operator operator;
	private ClientPacket packetNew;
	private HashMap<String, String> shortcuts;

	@FXML
	private Label tokenNumber;
	@FXML
	private VBox vbox;

	public void setTokenNumber(String number) {
		tokenNumber.setText(number);
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	@FXML
	public void onConfigureClicked() {
		try {
			Stage primaryStage = new Stage();
			FXMLLoader loader = new FXMLLoader();
			Pane root = loader
					.load(getClass().getResource("/application/resources/ClientConfiguration.fxml").openStream());
			ClientConfigurationController c = loader.getController();
			c.setOperator(operator);
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			// display the new window as POP UP and disable the events on the
			// main window
			primaryStage.initModality(Modality.APPLICATION_MODAL);
			primaryStage.initOwner(vbox.getScene().getWindow());
			// now the code continues
			primaryStage.setTitle("Client Configuration");
			ClientConfigurationController controller = loader.getController();
			controller.serverIPInitializer();
			primaryStage.show();
		} catch (IOException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		}
	}

	@FXML
	public void handleKeyReleased(KeyEvent key) {
		System.out.println("Key Pressed...." + key.getCode());
		if (key.getCode().toString().equals(shortcuts.get("byPass"))) {
			callNextToken();
		} else if (key.getCode().toString().equals(shortcuts.get("completed"))) {
			callAfterCompleted();
		} else if (key.getCode().toString().equals(shortcuts.get("callAgain"))) {
			callAgain();
		} else if (key.getCode().toString().equals(shortcuts.get("maxMin"))) {
			maximize(key);
		}
	}

	public void callAfterCompleted() {
		if (!tokenNumber.getText().isEmpty()) {
			Token t = new Token();
			// if no token message set token number to zero
			// otherwise load token number from the label
			if (!tokenNumber.getText().equals("No Token")) {
				t.setTokenNumber(Integer.parseInt(tokenNumber.getText()));
			} else {
				t.setTokenNumber(0);
			}

			// step 1: start the background task to operate
			Task<String> task = new Task<String>() {
				@Override
				protected String call() throws Exception {
					// send the currently completed token to the server
					System.out.println("Sending token : " + t.getTokenNumber());
					// prepare the client packet for sending it to the
					// server
					ClientPacket packet = new ClientPacket(Status.COMPLETED.toString(), t, operator);

					// If not a call again function, then do some of other
					// operations
					if (!packet.getStatus().equals(Status.CALL_AGAIN.toString())) {
						if (packet.getToken().getTokenNumber() != 0) {
							// send next token packet
							// store the read information to the database
							System.out.println();
							System.out.println("storing to the database...");
							ServerThreadProcessor.storePacketToDatabase(packet, packet.getCounter());
							System.out.println("stored to the database...");
						}
						// get new token from the database
						System.out.println();
						System.out.println("Requesting new token");
						packet.getToken().setTokenNumber(ServerThreadProcessor.getNewToken(packet).getTokenNumber());
						System.out.println("New token sent...");
						// send the new packet to the client
					}
					// receive the new packet from the server
					packetNew = packet;

					System.out.println(packetNew);
					System.out.println("Counter : " + packet.getCounter());
					System.out.println();
					System.out.println();
					// if zero is returned that means server has no token
					// left
					if (packetNew.getToken().getTokenNumber() == 0) {
						this.cancel();
					}
					// if successfully a valid packet is received display
					// token number..
					updateMessage(String.format("%04d", packetNew.getToken().getTokenNumber()));
					// store current token number to the database that
					// represents the current value
					new CurrentValueDBHelper().insertIntoCurrentValue(packetNew.getToken().getTokenNumber());

					// send data to the serial port
					ServerThreadProcessor.displayData(packet);
					// sound the speaker if the token number is not zero
					if (packetNew.getToken().getTokenNumber() != 0) {
						ServerThreadProcessor.soundTheSpeaker(packet);
					}
					return null;
				}
			};
			tokenNumber.textProperty().bind(task.messageProperty());
			task.setOnCancelled(e -> {
				tokenNumber.textProperty().unbind();
				tokenNumber.setText("No Token");
			});
			task.setOnFailed(e -> {
				tokenNumber.textProperty().unbind();
				tokenNumber.setText("0000");
			});
			new Thread(task, operator.getOfficeId()).start();
		}
	}

	public void callAgain() {
		if (!tokenNumber.getText().isEmpty()) {
			Token t = new Token();
			// if no token message set token number to zero
			// otherwise load token number from the label
			if (!tokenNumber.getText().equals("No Token")) {
				t.setTokenNumber(Integer.parseInt(tokenNumber.getText()));
			} else {
				t.setTokenNumber(0);
			}

			ClientPacket packet = new ClientPacket(Status.CALL_AGAIN.toString(), t, operator);

			Task<String> task = new Task<String>() {
				@Override
				protected String call() throws Exception {
					// send data to the serial port
					ServerThreadProcessor.displayData(packet);
					// sound the speaker if the token number is not zero
					if (packet.getToken().getTokenNumber() != 0) {
						ServerThreadProcessor.soundTheSpeaker(packet);
					}
					return null;
				}
			};

			new Thread(task, operator.getOfficeId()).start();
		}
	}

	public void maximize(KeyEvent event) {
		Stage primaryStage = new Stage();
		FXMLLoader loader = new FXMLLoader();
		try {
			Pane root = loader.load(getClass().getResource("/application/resources/Server.fxml").openStream());
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			ServerController controller = loader.getController();
			controller.setOperator(operator);
			controller.setOperatorNameAndCounter(operator.getUserName());
			controller.setTokenNumber(tokenNumber.getText());
			controller.disableServerStartButton();
			controller.setServerRunning();
			controller.setServerStatusText();
			((Node) event.getSource()).getScene().getWindow().hide();
			primaryStage.show();
			primaryStage.setOnCloseRequest(e -> {
				new Server().hideDisplayStage();
			});

		} catch (IOException e) {
			DialogViewer.showException(e);
			e.printStackTrace();
			LogFileStore.WritetoFile(e);
		}
	}

	public void callNextToken() {
		Token t = new Token();
		// if no token message set token number to zero
		// otherwise load token number from the label
		if (!tokenNumber.getText().equals("No Token")) {
			t.setTokenNumber(Integer.parseInt(tokenNumber.getText()));
		} else {
			t.setTokenNumber(0);
		}

		// step 1: store the token to the bypassed list
		if (!(tokenNumber.getText().equals("0000") || tokenNumber.getText().equals("No Token"))) {
			// insert into database first
			new BypassedListDBHelper().StoreToBypassedList(Integer.parseInt(tokenNumber.getText()));
		}
		// step 2: start the background task to operate
		Task<String> task = new Task<String>() {
			@Override
			protected String call() throws Exception {
				// send the currently completed token to the server
				System.out.println("Sending token : " + t.getTokenNumber());
				// prepare the client packet for sending it to the
				// server
				ClientPacket packet = new ClientPacket(Status.BYPASSED.toString(), t, operator);

				// If not a call again function, then do some of other
				// operations
				if (!packet.getStatus().equals(Status.CALL_AGAIN.toString())) {
					if (packet.getToken().getTokenNumber() != 0) {
						// send next token packet
						// store the read information to the database
						System.out.println();
						System.out.println("storing to the database...");
						ServerThreadProcessor.storePacketToDatabase(packet, packet.getCounter());
						System.out.println("stored to the database...");
					}
					// get new token from the database
					System.out.println();
					System.out.println("Requesting new token");
					packet.getToken().setTokenNumber(ServerThreadProcessor.getNewToken(packet).getTokenNumber());
					System.out.println("New token sent...");
					// send the new packet to the client
				}
				// receive the new packet from the server
				packetNew = packet;

				System.out.println(packetNew);
				System.out.println("Counter : " + packet.getCounter());
				System.out.println();
				System.out.println();
				// if zero is returned that means server has no token
				// left
				if (packetNew.getToken().getTokenNumber() == 0) {
					this.cancel();
				}
				// if successfully a valid packet is received display
				// token number..
				updateMessage(String.format("%04d", packetNew.getToken().getTokenNumber()));
				// store current token number to the database that
				// represents the current value
				new CurrentValueDBHelper().insertIntoCurrentValue(packetNew.getToken().getTokenNumber());

				// send data to the serial port..
				ServerThreadProcessor.displayData(packet);
				// sound the speaker if the token number is not zero
				if (packetNew.getToken().getTokenNumber() != 0) {
					ServerThreadProcessor.soundTheSpeaker(packet);
				}
				return null;
			}
		};
		tokenNumber.textProperty().bind(task.messageProperty());
		task.setOnCancelled(e -> {
			tokenNumber.textProperty().unbind();
			tokenNumber.setText("No Token");
		});
		task.setOnFailed(e -> {
			tokenNumber.textProperty().unbind();
			tokenNumber.setText("0000");
		});
		new Thread(task, operator.getOfficeId()).start();
	}

	public void handleMousePressEvent(MouseEvent event) {
		xOffset = event.getSceneX();
		yOffset = event.getSceneY();
	}

	public void handleMouseDragEvent(MouseEvent event) {
		((Node) event.getSource()).getScene().getWindow().setX(event.getScreenX() - xOffset);
		((Node) event.getSource()).getScene().getWindow().setY(event.getScreenY() - yOffset);
		;

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		shortcuts = new ShortcutProperty().receiveProperties();
	}
}
