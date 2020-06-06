package qmsCore;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.TooManyListenersException;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

public class PowerModule implements SerialPortEventListener {

	// class variables used for serial communications.
	private Enumeration<?> ports = null;

	// to map the port names with com port identifier
	private HashMap<String, CommPortIdentifier> portMap = new HashMap<>();

	// this is the object that contains the opened port
	private CommPortIdentifier selectedPortIdentifier = null;
	private SerialPort serialPort = null;

	// input and output streams for sending and receiving datas
	private InputStream input = null;
	private OutputStream output = null;

	// time out value for serial connection
	private final int TIMEOUT = 2000;

	// some ascii values for certain things:
	private final int NEWLINE = 10;

	// what happens when serial event occurs
	@Override
	public void serialEvent(SerialPortEvent arg0) {
		try {
			byte singleData = (byte) input.read();
			if (singleData != NEWLINE) {
				System.out.print(new String(new byte[] { singleData }));
			} else {
				System.out.print("\n");
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
			LogFileStore.WritetoFile(e);
		}
	}

	// search for available serial ports
	public void searchForPorts() {
		ports = CommPortIdentifier.getPortIdentifiers();
		while (ports.hasMoreElements()) {
			CommPortIdentifier curPort = (CommPortIdentifier) ports.nextElement();
			// get only serial ports
			if (curPort.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				portMap.put(curPort.getName(), curPort);
			}
		}
	}

	// connect to the serial port
	public boolean connectToSerialPort(String portName) {
		boolean success = false;
		selectedPortIdentifier = (CommPortIdentifier) portMap.get(portName);
		CommPort commPort = null;
		try {
			commPort = selectedPortIdentifier.open(getClass().getName(), TIMEOUT);
			serialPort = (SerialPort) commPort;
			success = true;
		} catch (PortInUseException e) {
			System.err.println(e.getMessage());
			LogFileStore.WritetoFile(e);
		}
		return success;
	}

	// initialization of input output streams
	public boolean InitializeIOStream() {
		boolean successful = false;

		try {
			input = serialPort.getInputStream();
			output = serialPort.getOutputStream();
			return successful;
		} catch (IOException e) {
			System.err.println(e.getMessage());
			LogFileStore.WritetoFile(e);
			return successful;
		}
	}

	// setting up event listeners to read data
	public boolean initListener() {
		boolean success = false;
		try {
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
			success = true;
		} catch (TooManyListenersException e) {
			System.err.println(e.getMessage());
			LogFileStore.WritetoFile(e);
		}
		return success;
	}

	// disconnecting from the serial port
	public void disConnect() {

		try {
			serialPort.removeEventListener();
			input.close();
			output.close();
			serialPort.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			LogFileStore.WritetoFile(e);
		}
	}

	// write data to the serial port hex form
	public boolean write(byte b) {
		boolean success = false;
		try {
			output.write(b);
			success = true;
		} catch (IOException e) {
			System.err.println(e.getMessage());
			LogFileStore.WritetoFile(e);
		}
		return success;
	}

	// write data to the serial port hex form
	public boolean write(int b) {
		boolean success = false;
		try {
			output.write(b);
			success = true;
		} catch (IOException e) {
			System.err.println(e.getMessage());
			LogFileStore.WritetoFile(e);
		}
		return success;
	}

	// set serial port parameters
	public void setSerialPortParams() {
		try {
			serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		} catch (UnsupportedCommOperationException e) {
			System.out.println(e.getMessage());
			LogFileStore.WritetoFile(e);
		}
	}

	public HashMap<String, CommPortIdentifier> getPortMap() {
		return portMap;
	}

	public void setPortMap(HashMap<String, CommPortIdentifier> portMap) {
		this.portMap = portMap;
	}

}
