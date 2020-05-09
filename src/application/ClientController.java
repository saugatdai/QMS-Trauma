package application;

import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
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
import qmsCore.LogFileStore;
import qmsCore.Operator;
import qmsCore.ShortcutProperty;
import qmsCore.Status;
import qmsCore.Token;

public class ClientController implements Initializable {

	private Operator operator;
	@FXML
	private Label operatorName;
	@FXML
	private Label serverStatus;
	@FXML
	private MenuBar menuBar;
	@FXML
	private Label tokenNumber;
	@FXML
	private ListView<String> byPassedList;
	@FXML
	private Label counter;
	@FXML
	private Button nextButton;
	@FXML
	private Button againButton;
	@FXML
	private Button completeButton;
	@FXML
	private Button minimizeButton;
	@FXML
	private ComboBox<String> counters;
	ObservableList<String> counterss = FXCollections.observableArrayList("1", "2", "3", "4", "5", "6", "7", "8", "9",
			"10", "11", "12");

	private ArrayList<String> byPassedArrayList = new ArrayList<>();
	private ClientPacket packetNew;
	private HashMap<String, String> shortcuts;

	private boolean connectedToTheServer = false;

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
			primaryStage.show();
		} catch (IOException e) {
			DialogViewer.showException(e);
			LogFileStore.WritetoFile(e);
		}
	}

	@FXML
	public void onConnectClicked(ActionEvent event) {
		if (new DBHelper().ifNotConfiguredServer()) {
			serverStatus.setText("Please Configure Server Info");
		} else {
			try {
				serverStatus.setText("Connecting...");
				operator.setClientInfo(new DBHelper().getServerInfo());
				operator.getClientInfo().connectToServer();
				operator.setCounter(new DBHelper().getCurrentCounter());
				serverStatus.setText("Connected to the server");
				connectedToTheServer = true;
				return;
			} catch (UnknownHostException e) {
				DialogViewer.showInformationDialog("Error", "Unconfigured server", "Either server IP or port is Wrong");
				DialogViewer.showException(e);
				LogFileStore.WritetoFile(e);
			} catch (IOException e) {
				DialogViewer.showException(e);
				LogFileStore.WritetoFile(e);
			}
		}
		serverStatus.setText("Failed to connect server");
	}

	@FXML
	public void onNextButtonClicked(ActionEvent event) {
		if (!tokenNumber.getText().isEmpty()) {
			Token t = new Token();
			if (!tokenNumber.getText().equals("No Token")) {
				t.setTokenNumber(Integer.parseInt(tokenNumber.getText()));
			} else {
				t.setTokenNumber(0);
			}
			if (connectedToTheServer) {
				if (!(tokenNumber.getText().equals("0000") || tokenNumber.getText().equals("No Token"))) {
					// insert into database first
					new BypassedListDBHelper().StoreToBypassedList(Integer.parseInt(tokenNumber.getText()));
					// and then display it visibilly
					byPassedArrayList.add(tokenNumber.getText());
					ObservableList<String> list = FXCollections.observableArrayList(byPassedArrayList);
					byPassedList.setItems(list);
				}
				Task<String> task = new Task<String>() {
					@Override
					protected String call() throws Exception {
						try {
							// send the currently completed token to the server
							System.out.println("Sending token : " + t.getTokenNumber());
							// prepare the client packet for sending it to the
							// server
							ClientPacket packet = new ClientPacket(Status.BYPASSED.toString(), t, operator);
							// sending the packet to the server
							operator.getClientInfo().sendStatus(packet);
							// receive the new packet from the server
							packetNew = operator.getClientInfo().getServerResponse();
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
						} catch (IOException e) {
							e.printStackTrace();
							LogFileStore.WritetoFile(e);
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
							LogFileStore.WritetoFile(e);
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
			} else {
				serverStatus.setText("Please Connect to server");
			}
		}
	}

	@FXML
	public void onCallAgainClicked(ActionEvent event) {
		if (!tokenNumber.getText().isEmpty()) {
			Token t = new Token();
			if (!tokenNumber.getText().equals("No Token")) {
				t.setTokenNumber(Integer.parseInt(tokenNumber.getText()));
			} else {
				t.setTokenNumber(0);
			}
			if (connectedToTheServer) {
				Task<String> task = new Task<String>() {
					@Override
					protected String call() throws Exception {
						try {
							System.out.println("Sending token : " + t.getTokenNumber());
							ClientPacket packet = new ClientPacket(Status.CALL_AGAIN.toString(), t, operator);
							operator.getClientInfo().sendStatus(packet);
						} catch (IOException e) {
							e.printStackTrace();
							LogFileStore.WritetoFile(e);
						}
						return null;
					}
				};
				new Thread(task, operator.getOfficeId()).start();
			} else {
				serverStatus.setText("Please Connect to server");
			}
		}

	}

	@FXML
	public void onCompletedClicked(ActionEvent event) {
		if(!tokenNumber.getText().isEmpty()) {
			Token t = new Token();
			if (!tokenNumber.getText().equals("No Token")) {
				t.setTokenNumber(Integer.parseInt(tokenNumber.getText()));
			} else {
				t.setTokenNumber(0);
			}
			if (connectedToTheServer) {
				Task<String> task = new Task<String>() {
					@Override
					protected String call() throws Exception {
						try {
							System.out.println("Sending token : " + t.getTokenNumber());
							ClientPacket packet = new ClientPacket(Status.BYPASSED.toString(), t, operator);
							operator.getClientInfo().sendStatus(packet);
							packetNew = operator.getClientInfo().getServerResponse();
							System.out.println(packetNew);
							System.out.println("Counter : " + packet.getCounter());
							System.out.println();
							System.out.println();
							if (packetNew.getToken().getTokenNumber() == 0) {
								this.cancel();
							}
							updateMessage(String.format("%04d", packetNew.getToken().getTokenNumber()));
						} catch (IOException e) {
							e.printStackTrace();
							LogFileStore.WritetoFile(e);
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
							LogFileStore.WritetoFile(e);
						}
						return null;
					}
				};
				tokenNumber.textProperty().bind(task.messageProperty());
				task.setOnCancelled(e -> {
					tokenNumber.textProperty().unbind();
					tokenNumber.setText("No Token");
				});
				new Thread(task, operator.getOfficeId()).start();
			} else {
				serverStatus.setText("Please Connect to server");
			}
		}
	}

	@FXML
	public void onForwardButtonPressed(ActionEvent event) {
		Token t = new Token();
		if (!tokenNumber.getText().equals("No Token")) {
			t.setTokenNumber(Integer.parseInt(tokenNumber.getText()));
		} else {
			t.setTokenNumber(0);
		}
		if (connectedToTheServer) {
			Task<String> task = new Task<String>() {
				@Override
				protected String call() throws Exception {
					try {
						System.out.println("Sending token in forward mode: " + t.getTokenNumber());
						// modified operator counter before sending counter
						ClientPacket packet = new ClientPacket(Status.FORWARDED_TOKEN.toString(), t, operator);
						packet.setForwardToCounter(counters.getSelectionModel().getSelectedItem());
						operator.getClientInfo().sendStatus(packet);
						packetNew = operator.getClientInfo().getServerResponse();
						System.out.println(packetNew);
						System.out.println("Counter : " + packet.getCounter());
						System.out.println();
						System.out.println();
						if (packetNew.getToken().getTokenNumber() == 0) {
							this.cancel();
						}
						updateMessage(String.format("%04d", packetNew.getToken().getTokenNumber()));
					} catch (IOException e) {
						e.printStackTrace();
						LogFileStore.WritetoFile(e);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
						LogFileStore.WritetoFile(e);
					}
					return null;
				}
			};
			tokenNumber.textProperty().bind(task.messageProperty());
			task.setOnCancelled(e -> {
				tokenNumber.textProperty().unbind();
				tokenNumber.setText("No Token");
			});
			new Thread(task, operator.getOfficeId()).start();
		} else {
			serverStatus.setText("Please Connect to server");
		}
	}

	@FXML
	public void onDisconnectClicked(ActionEvent event) {
		operator.getClientInfo().closeAll();
		connectedToTheServer = false;
		serverStatus.setText("Disconnected To the server");
	}

	@FXML
	public void onLogoutPressed(ActionEvent event) {
		try {
			if (connectedToTheServer) {
				operator.getClientInfo().closeAll();
			}
			Stage primaryStage = new Stage();
			FXMLLoader loader = new FXMLLoader();
			TabPane root = loader.load(getClass().getResource("/application/resources/Login.fxml").openStream());
			// hide the current window
			((Node) event.getSource()).getScene().getWindow().hide();
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
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

	public void setOperatorNameAndCounter(String name) {
		this.operatorName.setText(name);
		this.counter.setText(operator.getCounter());
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
		if (connectedToTheServer) {
			Stage primaryStage = new Stage();
			FXMLLoader loader = new FXMLLoader();
			try {
				Pane root = loader
						.load(getClass().getResource("/application/resources/MinimizedClient.fxml").openStream());
				Scene scene = new Scene(root);
				primaryStage.setScene(scene);
				primaryStage.initStyle(StageStyle.UNDECORATED);
				MinimizedClientController controller = loader.getController();
				primaryStage.setResizable(false);
				primaryStage.setAlwaysOnTop(true);
				controller.setOperator(operator);
				controller.setTokenNumber(tokenNumber.getText());
				controller.setConnectedToServer(connectedToTheServer);
				((Node) event.getSource()).getScene().getWindow().hide();
				primaryStage.show();
			} catch (IOException e) {
				e.printStackTrace();
				DialogViewer.showException(e);
				LogFileStore.WritetoFile(e);
			}
		}

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
				Token t = new Token();
				t.setTokenNumber(Integer.parseInt(byPassedList.getSelectionModel().getSelectedItem()));
				if (connectedToTheServer) {
					Task<String> task = new Task<String>() {
						@Override
						protected String call() throws Exception {
							try {
								System.out.println("Sending token : " + t.getTokenNumber());
								ClientPacket packet = new ClientPacket(Status.CALL_AGAIN.toString(), t, operator);
								operator.getClientInfo().sendStatus(packet);
							} catch (IOException e) {
								e.printStackTrace();
								LogFileStore.WritetoFile(e);
							}
							return null;
						}
					};
					new Thread(task, operator.getOfficeId()).start();
				} else {
					serverStatus.setText("Please Connect to server");
				}
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

	public static boolean isNumeric(String str) {
		try {
			Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
			LogFileStore.WritetoFile(nfe);
			return false;
		}
		return true;
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

	public void setConnectedToTheServer(boolean status) {
		connectedToTheServer = status;
	}

	public void setServerStatusText() {
		serverStatus.setText("Connected to the server");
	}

	public void setTokenNumber(String number) {
		tokenNumber.setText(number);
	}

}
