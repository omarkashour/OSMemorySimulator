
public class PCB implements Comparable<PCB> {

	private int processID;
	private int processSize;
	private int timeInMemory;
	private int baseRegister = -1;
	private int limitRegister = -1;
	private int finishTime = -1;
	
	
	public PCB(int processID, int processSize, int timeInMemory) {
		super();
		this.processID = processID;
		this.processSize = processSize;
		this.timeInMemory = timeInMemory;
	}


	public PCB(int processID, int processSize, int timeInMemory, int baseRegister, int limitRegister) {
		this.processID = processID;
		this.processSize = processSize;
		this.timeInMemory = timeInMemory;
		this.baseRegister = baseRegister;
		this.limitRegister = limitRegister;
	}


	public int getProcessID() {
		return processID;
	}


	public void setProcessID(int processID) {
		this.processID = processID;
	}


	public int getProcessSize() {
		return processSize;
	}


	public void setProcessSize(int processSize) {
		this.processSize = processSize;
	}


	public int getTimeInMemory() {
		return timeInMemory;
	}


	public void setTimeInMemory(int timeInMemory) {
		this.timeInMemory = timeInMemory;
	}


	public int getFinishTime() {
		return finishTime;
	}


	public void setFinishTime(int finishTime) {
		this.finishTime = finishTime;
	}


	public int getBaseRegister() {
		return baseRegister;
	}


	public void setBaseRegister(int baseRegister) {
		this.baseRegister = baseRegister;
	}


	public int getLimitRegister() {
		return limitRegister;
	}


	public void setLimitRegister(int limitRegister) {
		this.limitRegister = limitRegister;
	}


	public String toString() {
		return "\nProcess ID: " + this.processID + "\n" +
	"Process Size: " + this.processSize + "\n"
	+ "Time in Memory: " + this.timeInMemory + "\n"
	+ "Base Register: " + this.baseRegister + "\n"
	+ "Limit Register: " + this.limitRegister + "\n ===================";
	}


	@Override
	public int compareTo(PCB o) {
		return Integer.compare(this.finishTime, o.finishTime);
	}

}
