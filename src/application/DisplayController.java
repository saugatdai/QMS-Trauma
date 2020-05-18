package application;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import qmsCore.ClientPacket;

public class DisplayController {
	@FXML
	private Label counter1;
	@FXML
	private Label counter2;
	@FXML
	private Label counter3;
	@FXML
	private Label counter4;
	@FXML
	private Label token1;
	@FXML
	private Label token2;
	@FXML
	private Label token3;
	@FXML
	private Label token4;
	@FXML
	private Label message;

	private static String[] keys = new String[4];
	private static String[] values = new String[4];

	public void roll(ClientPacket packet) {

		keys[3] = keys[2];
		values[3] = values[2];
		keys[2] = keys[1];
		values[2] = values[1];
		keys[1] = keys[0];
		values[1] = values[0];
		keys[0] = packet.getCounter();
		values[0] = new Integer(packet.getToken().getTokenNumber()).toString();
	}

	public void setDatas(ClientPacket packet) {
		if(values[0] == null){
			roll(packet);
		}
		else if (!values[0].equals(new Integer(packet.getToken().getTokenNumber()).toString())) {
			roll(packet);
		}
	}

	public void updateDisplay() {
		System.out.println("\n\nupdating display as : ");
		for (int i = 0; i < keys.length; i++) {
			System.out.println(keys[i] + "=>" + values[i]);
		}
		message.setText("Token Number " + values[0] + " please proceed to OPD Number " + keys[0]);
		counter1.setText(keys[0]);
		token1.setText(values[0]);
		counter2.setText(keys[1]);
		token2.setText(values[1]);
		counter3.setText(keys[2]);
		token3.setText(values[2]);
		counter4.setText(keys[3]);
		token4.setText(values[3]);

		counter1.setTextFill(Color.web("#000099"));
		token1.setTextFill(Color.web("#000099"));
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.4), evt -> counter1.setVisible(false)),
				new KeyFrame(Duration.seconds(0.6), evt -> counter1.setVisible(true)));
		Timeline timeline2 = new Timeline(new KeyFrame(Duration.seconds(0.4), evt -> token1.setVisible(false)),
				new KeyFrame(Duration.seconds(0.6), evt -> token1.setVisible(true)));
		Timeline timeline3 = new Timeline(new KeyFrame(Duration.seconds(0.4), evt -> message.setVisible(false)),
				new KeyFrame(Duration.seconds(0.6), evt -> message.setVisible(true)));
		timeline.setCycleCount(13);
		timeline2.setCycleCount(13);
		timeline3.setCycleCount(13);
		timeline.play();
		timeline2.play();
		timeline3.play();

	}

}
