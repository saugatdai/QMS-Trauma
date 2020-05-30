package application;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import qmsCore.BypassedListDBHelper;
import qmsCore.ClientPacket;
import qmsCore.CurrentValueDBHelper;
import qmsCore.DBHelper;
import qmsCore.ForwardPacket;
import qmsCore.LogFileStore;
import qmsCore.Operator;
import qmsCore.Server;
import qmsCore.ServerThreadProcessor;
import qmsCore.ShortcutProperty;
import qmsCore.Status;
import qmsCore.Token;

public class ServerController implements Initializable {

	private Server server = null;
	private ClientPacket packetNew;
	private Operator operator;
	private boolean serverRunning = false;
	private HashMap<String, String> shortcuts;

	@FXML
	private Label operatorName;
	@FXML
	private Label serverStatus;
	@FXML
	private MenuBar menuBar;
	@FXML
	private Button serverStartButton;
	@FXML
	private Label tokenNumber;
	@FXML
	private ListView<String> byPassedList;
	@FXML
	private Label counter;
	@FXML
	private Button nextButton;
	@FXML
	private Button completeButton;
	@FXML
	private Button againButton;
	@FXML
	private Button minimizeButton;
	@FXML
	private ComboBox<String> counters;
	ObservableList<String> counterss = FXCollections.observableArrayList("1", "2", "3", "4", "5", "6", "7", "8", "9",
			"10", "11", "12");

	private ArrayList<String> byPassedArrayList = new ArrayList<>();

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public Operator getOperator() {
		return this.operator;
	}

	@FXML
	public void onClientConfigurationClicked(ActionEvent event) {
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
			primaryStage.initOwner(menuBar.getScene().getWindow());
			// now the code continues
			primaryStage.setTitle("Client Configuration");
			primaryStage.setOnHidden(e -> {
				counter.setText(operator.getCounter());
			});
			ClientConfigurationController controller = loader.getController();
			controller.serverIPInitializer();
			primaryStage.show();
		} catch (IOException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		}
	}

	@FXML
	public void onPresetClicked(ActionEvent event) {
		try {
			Stage primaryStage = new Stage();
			FXMLLoader loader = new FXMLLoader();
			Pane root = loader.load(getClass().getResource("/application/resources/Preset.fxml").openStream());
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			// display the new window as POP UP and disable the events on the
			// main window
			primaryStage.initModality(Modality.APPLICATION_MODAL);
			primaryStage.initOwner(menuBar.getScene().getWindow());
			// now the code continues
			primaryStage.setTitle("Preset Token");
			primaryStage.show();
		} catch (IOException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		}
	}

	@FXML
	public void onServerStartClicked(ActionEvent event) {

		if (new DBHelper().ifNotConfiguredServer()) {
			serverStatus.setText("Please configure server first");
		} else {
			operator.setClientInfo(new DBHelper().getServerInfo());
			server = new Server(operator, serverStartButton, serverStatus);
			server.setOnFailed((Void) -> serverStatus.setText("Server failed"));
			server.setOnRunning((Void) -> serverStatus.setText("Server Running"));
			server.setOnCancelled((Void) -> serverStatus.setText("Server cancelled"));
			server.start();
			serverStartButton.setDisable(true);
			serverRunning = true;
		}
	}

	@FXML
	public void onShortcutsClicked(ActionEvent event) {
		try {
			Stage primaryStage = new Stage();
			FXMLLoader loader = new FXMLLoader();
			Pane root = loader.load(getClass().getResource("/application/resources/Shortcuts.fxml").openStream());
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			// display the new window as POP UP and disable the events on the
			// main window
			primaryStage.initModality(Modality.APPLICATION_MODAL);
			primaryStage.initOwner(menuBar.getScene().getWindow());
			// now the code continues
			primaryStage.setTitle("Client Configuration");
			primaryStage.setOnHidden(e -> {
				counter.setText(operator.getCounter());
			});
			primaryStage.initStyle(StageStyle.UTILITY);
			primaryStage.setOnHidden(e -> {
				shortcuts = new ShortcutProperty().receiveProperties();
			});
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
			LogFileStore.WritetoFile(e);
		}
	}

	// code for the next button
	@FXML
	public void onNextButtonClicked(ActionEvent event) {
		//in order to avoid exception during Integer.parseInt()
		if (!tokenNumber.getText().isEmpty()) {
			Token t = new Token();
			// if no token message set token number to zero
			// otherwise load token number from the label
			if (!tokenNumber.getText().equals("No Token")) {
				try {
					t.setTokenNumber(Integer.parseInt(tokenNumber.getText()));
				} catch (NumberFormatException e1) {
					e1.printStackTrace();
					LogFileStore.WritetoFile(e1);
				}
			} else {
				t.setTokenNumber(0);
			}

			// step 1: store the token to the bypassed list
			if (!(tokenNumber.getText().equals("0000") || tokenNumber.getText().equals("No Token"))) {
				// insert into database first
				new BypassedListDBHelper().StoreToBypassedList(Integer.parseInt(tokenNumber.getText()));
				// and then display it visibly
				byPassedArrayList.add(tokenNumber.getText());
				ObservableList<String> list = FXCollections.observableArrayList(byPassedArrayList);
				byPassedList.setItems(list);
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
	}

	@FXML
	public void onCompletedClicked(ActionEvent event) {
		if(!tokenNumber.getText().isEmpty()) {
			Token t = new Token();
			// if no token message set token number to zero
			// otherwise load token number from the label
			if (!tokenNumber.getText().equals("No Token")) {
				try {
					t.setTokenNumber(Integer.parseInt(tokenNumber.getText()));
				} catch (NumberFormatException e1) {
					e1.printStackTrace();
					LogFileStore.WritetoFile(e1);
				}
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

	@FXML
	public void onCallAgainClicked(ActionEvent event) {

		if(!tokenNumber.getText().isEmpty()) {
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

	@FXML
	public void onLogoutPressed(ActionEvent event) {
		try {
			if (server != null) {
				server.stopServer();
				server.cancel();
			}
			Stage primaryStage = new Stage();
			FXMLLoader loader = new FXMLLoader();
			TabPane root = loader.load(getClass().getResource("/application/resources/Login.fxml").openStream());
			// hide the current window
			((Node) event.getSource()).getScene().getWindow().hide();
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			new Server().hideDisplayStage();
			primaryStage.show();
		} catch (IOException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		}
	}

	@FXML
	public void onEditInformationClicked(ActionEvent event) {
		try {
			Stage primaryStage = new Stage();
			FXMLLoader loader = new FXMLLoader();
			Pane root = loader.load(getClass().getResource("/application/resources/ChangeInfo.fxml").openStream());
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			// display the new window as POP UP and disable the events on the
			// main window
			primaryStage.initModality(Modality.APPLICATION_MODAL);
			primaryStage.initOwner(menuBar.getScene().getWindow());
			// now the code continues
			primaryStage.setTitle("Update Information");
			ChangeInfoController controller = (ChangeInfoController) loader.getController();
			// get the latest operator
			operator = new DBHelper().requestOperatorInfoByLoginCredentials(operator.getUserName(),
					operator.getPassword());
			controller.setOperator(operator);
			controller.setFields();
			primaryStage.show();
		} catch (IOException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		}

	}

	@FXML
	public void onViewUsersClicked(ActionEvent event) {
		try {
			Stage primaryStage = new Stage();
			FXMLLoader loader = new FXMLLoader();
			Pane root = loader.load(getClass().getResource("/application/resources/ClientViewUsers.fxml").openStream());
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			// display the new window as POP UP and disable the events on the
			// main window
			primaryStage.initModality(Modality.APPLICATION_MODAL);
			primaryStage.initOwner(menuBar.getScene().getWindow());
			primaryStage.setTitle("View Users");
			primaryStage.show();
			// now the code continues
		} catch (IOException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		}
	}

	@FXML
	public void onChangeLoginInfoClicked(ActionEvent event) {
		try {
			Stage primaryStage = new Stage();
			FXMLLoader loader = new FXMLLoader();
			Pane root = loader.load(getClass().getResource("/application/resources/ChangeLogin.fxml").openStream());
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			// display the new window as POP UP and disable the events on the
			// main window
			primaryStage.initModality(Modality.APPLICATION_MODAL);
			primaryStage.initOwner(menuBar.getScene().getWindow());
			// now the code continues
			primaryStage.setTitle("Change Login Information");
			// get the controller for setting up the data
			ChangeLoginController controller = (ChangeLoginController) loader.getController();
			// get the latest operator
			operator = new DBHelper().requestOperatorInfoByLoginCredentials(operator.getUserName(),
					operator.getPassword());
			Stage stage = (Stage) menuBar.getScene().getWindow();
			controller.setOperatorAndStage(operator, stage);
			controller.setFields();
			primaryStage.showAndWait();

		} catch (IOException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		}
	}

	@FXML
	public void onViewActivitiesClicked(ActionEvent event) {
		try {
			Stage primaryStage = new Stage();
			FXMLLoader loader = new FXMLLoader();
			Pane root = loader.load(getClass().getResource("/application/resources/ActivityTable.fxml").openStream());
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			// display the new window as POP UP and disable the events on the
			// main window
			primaryStage.initModality(Modality.APPLICATION_MODAL);
			primaryStage.initOwner(menuBar.getScene().getWindow());
			// now the code continues
			primaryStage.setTitle("Activity Log");
			primaryStage.show();
		} catch (IOException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		}
	}

	public void setOperatorNameAndCounter(String name) {
		this.operatorName.setText(name);
		this.counter.setText(operator.getCounter());
	}

	@FXML
	public void onClearBypassedListClicked(ActionEvent event) {
		new BypassedListDBHelper().clearAllByPassedList();
		byPassedArrayList.clear();
		byPassedList.setItems(FXCollections.observableArrayList(byPassedArrayList));
	}

	@FXML
	public void onLoadLastDataClicked(ActionEvent event) {
		tokenNumber.textProperty().unbind();
		tokenNumber.setText(new Integer(new CurrentValueDBHelper().getLastGrabbedValue()).toString());
	}

	@FXML
	public void onResetTokenValueClicked(ActionEvent event) {
		tokenNumber.textProperty().unbind();
		tokenNumber.setText("0000");
	}

	@FXML
	public void handleKeyTypedEvent(KeyEvent ke) {
		System.out.println("Key pressed ......" + ke.getCode());
		if (ke.getCode().toString().equals(shortcuts.get("callAgain"))) {
			againButton.fire();
		} else if (ke.getCode().toString().equals(shortcuts.get("completed"))) {
			completeButton.fire();
		} else if (ke.getCode().toString().equals(shortcuts.get("byPass"))) {
			nextButton.fire();
		} else if (ke.getCode().toString().equals(shortcuts.get("maxMin"))) {
			minimizeButton.fire();
		}
	}

	@FXML
	public void onMinimizePressed(ActionEvent event) {
		if (serverRunning) {
			Stage primaryStage = new Stage();
			FXMLLoader loader = new FXMLLoader();
			try {
				Pane root = loader.load(getClass().getResource("/application/resources/Minimized.fxml").openStream());
				Scene scene = new Scene(root);
				primaryStage.setScene(scene);
				primaryStage.initStyle(StageStyle.UNDECORATED);
				MinimizedServerController controller = loader.getController();
				primaryStage.setResizable(false);
				primaryStage.setAlwaysOnTop(true);
				controller.setOperator(operator);
				controller.setTokenNumber(tokenNumber.getText());
				((Node) event.getSource()).getScene().getWindow().hide();
				primaryStage.show();
			} catch (IOException e) {
				e.printStackTrace();
				DialogViewer.showException(e);
				LogFileStore.WritetoFile(e);
			}
		}

	}

	@FXML
	public void onForwardButtonPressed(ActionEvent event) {
		ForwardPacket packetF;
		try {
			packetF = new ForwardPacket();
			Token t = new Token();
			t.setTokenNumber(Integer.parseInt(tokenNumber.getText()));
			packetF.setToken(t);
			packetF.setCounter(counters.getSelectionModel().getSelectedItem());

			// insert into primary queue
			ServerThreadProcessor.storeToPrimaryQueue(packetF);

			// new the background processing tasks
			Task<String> task = new Task<String>() {
				@Override
				protected String call() throws Exception {
					// send the currently completed token to the server
					System.out.println("Sending token : " + t.getTokenNumber());
					// prepare the client packet for sending it to the
					// server
					ClientPacket packet = new ClientPacket(Status.FORWARDED_TOKEN.toString(), t, operator);

					// If not a call again function, then do some of other
					// operations
					if (!packet.getStatus().equals(Status.CALL_AGAIN.toString())) {
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

		} catch (NumberFormatException e) {
			LogFileStore.WritetoFile(e);
			return;
		}
	}

	public void disableServerStartButton() {
		serverStartButton.setDisable(true);
	}

	public void setTokenNumber(String number) {
		tokenNumber.setText(number);
	}

	public void setServerRunning() {
		serverRunning = true;
	}

	public void setServerStatusText() {
		serverStatus.setText("Server Running");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// load bypassed list at first
		byPassedArrayList = new BypassedListDBHelper().getBypassedList();
		byPassedList.setItems(FXCollections.observableArrayList(byPassedArrayList));
		ContextMenu context = new ContextMenu();

		MenuItem item1 = new MenuItem("Delete");
		context.getItems().add(item1);

		item1.setOnAction(e -> {
			// remove from the database first
			new BypassedListDBHelper()
					.deleteSelectedEntry(Integer.parseInt(byPassedList.getSelectionModel().getSelectedItem()));
			// and then remove it from the view
			byPassedArrayList.remove(byPassedList.getSelectionModel().getSelectedItem());
			byPassedList.setItems(FXCollections.observableArrayList(byPassedArrayList));
		});
		byPassedList.setOnMouseClicked(e -> {
			if (e.getClickCount() == 2) {
				// what to do on double click on the selected item..
				Token t = new Token();
				t.setTokenNumber(Integer.parseInt(byPassedList.getSelectionModel().getSelectedItem()));
				Task<String> task = new Task<String>() {
					@Override
					protected String call() throws Exception {
						System.out.println("Sending token : " + t.getTokenNumber());
						ClientPacket packet = new ClientPacket(Status.CALL_AGAIN.toString(), t, operator);

						// send data to the serial port

						// display data to the monitor
						ServerThreadProcessor.displayData(packet);
						// sound the speaker
						ServerThreadProcessor.soundTheSpeaker(packet);
						return null;
					}
				};
				new Thread(task, operator.getOfficeId()).start();
				// send data to the serial port
				System.out.println("Send data to the serial port...");
				System.out.println("Sound the speaker.....");
				System.out.println();
				System.out.println();

			}
		});
		byPassedList.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			public void handle(MouseEvent event) {
				if (event.getButton() == MouseButton.SECONDARY
						&& byPassedList.getSelectionModel().getSelectedItem() != null) {
					context.show(byPassedList, event.getScreenX(), event.getScreenY());
				} else if (event.getButton() == MouseButton.PRIMARY) {
					context.hide();
				}
			};
		});

		counters.setItems(counterss);
		shortcuts = new ShortcutProperty().receiveProperties();

	}

}
