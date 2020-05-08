package application;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ActivityTable {
	private final SimpleIntegerProperty id;
	private final SimpleIntegerProperty tokenNo;
	private final SimpleStringProperty processedBy;
	private final SimpleStringProperty time;
	private final SimpleStringProperty processorName;
	private final SimpleStringProperty officeID;
	private final SimpleStringProperty counter;
	
	public ActivityTable(Integer id, int tokenNo, String processedBy, String time, String processorName, String officeID, String counter){
		super();
		this.id = new SimpleIntegerProperty(id);
		this.tokenNo = new SimpleIntegerProperty(tokenNo);
		this.processedBy = new SimpleStringProperty(processedBy);
		this.time = new SimpleStringProperty(time);
		this.processorName = new SimpleStringProperty(processorName);
		this.officeID = new SimpleStringProperty(officeID);
		this.counter = new SimpleStringProperty(counter);
	}
	
	public int getId(){
		return this.id.get();
	}
	public int getTokenNo(){
		return this.tokenNo.get();
	}
	public String getProcessedBy(){
		return this.processedBy.get();
	}
	public String getTime(){
		return this.time.get();
	}
	public String getProcessorName(){
		return this.processorName.get();
	}
	public String getOfficeID(){
		return this.officeID.get();
	}
	public String getCounter(){
		return this.counter.get();
	}
}
