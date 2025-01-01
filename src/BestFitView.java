import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Scanner;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class BestFitView extends BorderPane {

	static LinkedList<PCB> jobQ = new LinkedList<PCB>();
	static PriorityQueue<Hole> holes = new PriorityQueue<Hole>();

	static int jobQOriginalSize = 0;
	final static int RECTANGLE_WIDTH = 110;
	final static int RECTANGLE_HEIGHT = 52;
	static final int OS_SIZE = 512;
	Label currentStatus = new Label("Press next to load the next step, You can use the bottom scrollbar to navigate through the steps!");
	static PriorityQueue<PCB> nextTime = new PriorityQueue<>(); // this is only used to aid in the FX
	static int currentTime = 0;
	static Button refreshBtn = new Button("Refresh");
	ScrollPane mainScrollP = new ScrollPane();
	static HBox mainHB = new HBox();
	Button resetBtn = new Button("Reset");
	static int hole_count = 0;

	static PCB[] mainMemory = new PCB[2048];

	public BestFitView() throws FileNotFoundException {
		Button nextBtn = new Button("Next");
		readReadyQFile();
		readJobQFile();
		mainHB.setSpacing(15);
		setTop(currentStatus);
		currentStatus.setFont(new Font(24));
		setAlignment(currentStatus, Pos.CENTER);
		setMargin(currentStatus, new Insets(15));
		createJobQFX();
//		createMemoryFX();
		memFX();
		ScrollPane mainScrollP = new ScrollPane(mainHB);
		mainScrollP.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
		mainScrollP.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
		setCenter(mainScrollP);
		setMargin(mainScrollP, new Insets(15));

		nextBtn.setOnAction(e -> {
			if (!jobQ.isEmpty() || !memoryIsEmpty()) {


				advanceTime();
				createJobQFX();
				memFX();
				PCB p = insertFromJobQ();
				if(p!=null) {
				displayMessage("Next, Process with ID="+p.getProcessID()+" is inserted");
				createJobQFX();
				memFX();
				}

//				autoScrollToEnd(mainScrollP);

			}
		});

		resetBtn.setOnAction(e -> {
			resetSimulation();
			createJobQFX();
			memFX();
		});

		VBox rightVB = new VBox(nextBtn, resetBtn);
		rightVB.setAlignment(Pos.CENTER);
		rightVB.setSpacing(15);
		setRight(rightVB);
		setMargin(rightVB, new Insets(15));

	}

	public void displayMessage(String s) {
		Label l = new Label(s + " --------->");
		l.setFont(new Font(20));
		l.setAlignment(Pos.CENTER);
		VBox statusVB = new VBox(l);
		statusVB.setAlignment(Pos.CENTER);
		mainHB.getChildren().add(statusVB);
	}

	private boolean memoryIsEmpty() {
		for (int i = 512; i < mainMemory.length; i++) {
			if (mainMemory[i] != null)
				return false;
		}
		return true;
	}

	public void advanceTime() {
		if (!nextTime.isEmpty()) {
			PCB process = nextTime.poll();
			removeFromMemory(process);
			currentTime = process.getFinishTime();
			String processes = "Next, Process with ID=" + process.getProcessID() +" finishes executing\n";
			while (!nextTime.isEmpty() && nextTime.peek().getFinishTime() == currentTime) {
				PCB process2 = nextTime.poll();
				removeFromMemory(process2);
				processes += "Next, Process with ID=" + process2.getProcessID() +" finishes executing\n";
				}

			displayMessage(processes);

			if (hole_count > 3) {
				createJobQFX();
				memFX();
				displayMessage("Time for compaction! ");
				compaction();
			}

		}
	}

	private PCB insertFromJobQ() {
		for(PCB process: jobQ) {
			Hole hole = findBestFit(process);
			if(hole!=null) {
				jobQ.remove(process);
				holes.remove(hole);
				process.setBaseRegister(hole.getHoleStartAddress());
				process.setFinishTime(process.getTimeInMemory()+currentTime);
				nextTime.add(process);
	            for (int j = hole.getHoleStartAddress(); j < hole.getHoleStartAddress() + process.getProcessSize(); j++) {
	                mainMemory[j] = process;
	            }
				
	            if (hole.getHoleSize() > process.getProcessSize()) {
	                holes.add(new Hole(hole.getHoleSize() - process.getProcessSize(), hole.getHoleStartAddress() + process.getProcessSize()));
	            } else {
	                hole_count--;
	            }
				return process;
				}
			}
		return null;
		}
	
	
	private Hole findBestFit(PCB process) { // best fit algorithm
		int min = Integer.MAX_VALUE;
		Hole minHole = null;
		for(Hole hole: holes) {
			if(process.getProcessSize() <= hole.getHoleSize() && hole.getHoleSize() < min ) {
				minHole = hole;
				min = hole.getHoleSize();
			}
		}
		return minHole;
	}



	private void removeFromMemory(PCB process) {
		for (int i = 512; i < mainMemory.length; i++) {
			if (mainMemory[i] != null && mainMemory[i].equals(process)) {
				mainMemory[i] = null;
			}
		}
		holes.add(new Hole(process.getProcessSize(), process.getBaseRegister()));
		hole_count++;

	}

	private void compaction() {
		int k = OS_SIZE;
		PCB[] memory = new PCB[2048];
		int base = OS_SIZE;
		HashSet<PCB> seenProcesses = new HashSet<>();
		for (int i = 512; i < mainMemory.length; i++) {
			PCB process = mainMemory[i];
			if (process != null && !seenProcesses.contains(process)) {
				process.setBaseRegister(base);
				base += process.getProcessSize();
				seenProcesses.add(process);
			}
			if (process != null) {
				memory[k++] = mainMemory[i];
			}
		}
		int size = 0;
		for (Hole h : holes) {
			size += h.getHoleSize();
		}
		holes.clear();
		int j = 512;
		while (memory[j] != null)
			j++;
		holes.add(new Hole(size, j));
		mainMemory = memory;
		hole_count = 1;
	}

	private void createJobQFX() {
		VBox vb = new VBox();
		Label label = new Label("Job Queue");
		vb.getChildren().add(label);
		label.setTextFill(Color.BLACK);
		label.setFont(new Font("", 20));
		for (int i = 0; i < jobQ.size(); i++) {
			if (i == 0) {
				PCB job = jobQ.get(i);
				Rectangle r = new Rectangle();
				r.setFill(Color.LIGHTBLUE);
				r.setStroke(Color.BLACK);
				Label l = new Label("Process: " + job.getProcessID() + "\nSize: " + job.getProcessSize() + " MB"
						+ "\nTime Required: " + job.getTimeInMemory());
				l.setFont(new Font("", 15));
				l.setTextFill(Color.BLACK);
				r.setWidth(RECTANGLE_WIDTH + 15);
				r.setHeight(RECTANGLE_HEIGHT + 0.2 * job.getProcessSize());

				StackPane sp = new StackPane(r, l);

				Label front = new Label("Front ->");
				front.setTextFill(Color.BLACK);
				front.setFont(new Font("", 20));
				front.setAlignment(Pos.CENTER);
				HBox hb = new HBox(front, sp);
				vb.getChildren().add(hb);
				continue;
			}
			PCB job = jobQ.get(i);
			Rectangle r = new Rectangle();
			r.setFill(Color.LIGHTBLUE);
			r.setStroke(Color.BLACK);
			Label l = new Label("Process: " + job.getProcessID() + "\nSize: " + job.getProcessSize() + " MB"
					+ "\nTime Required: " + job.getTimeInMemory());
			l.setFont(new Font("", 15));
			l.setTextFill(Color.BLACK);
			r.setWidth(RECTANGLE_WIDTH + 15);
			r.setHeight(RECTANGLE_HEIGHT + 0.2 * job.getProcessSize());
			StackPane sp = new StackPane(r, l);
			vb.getChildren().add(sp);
		}
		if (jobQ.size() < jobQOriginalSize) {
			for (int i = jobQ.size(); i < jobQOriginalSize; i++) {
				Rectangle r = new Rectangle();
				r.setFill(Color.WHITE);
				r.setStroke(Color.BLACK);
				r.setWidth(RECTANGLE_WIDTH + 15);
				r.setHeight(RECTANGLE_HEIGHT);
				vb.getChildren().add(r);
			}
		}
		vb.setAlignment(Pos.CENTER);

		mainHB.getChildren().add(vb);
	}

	public static void readJobQFile() throws FileNotFoundException {
		jobQ.clear();
		File jobQFile = Main.jobQFile;
		Scanner sc = new Scanner(jobQFile);
		// job q
		while (sc.hasNext()) {
			int processID = Integer.parseInt(sc.next());
			int processSize = Integer.parseInt(sc.next());
			int timeInMemory = Integer.parseInt(sc.next());
			PCB process = new PCB(processID, processSize, timeInMemory);
			jobQ.offer(process);
		}
		jobQOriginalSize = jobQ.size();
	}

	public static void readReadyQFile() throws FileNotFoundException {
		mainMemory = new PCB[2048];
		File readyQFile = Main.readyQFile;
		Scanner sc = new Scanner(readyQFile);
		// ready q
		int base = OS_SIZE;
		while (sc.hasNext()) {
			int processID = Integer.parseInt(sc.next());
			int processSize = Integer.parseInt(sc.next());
			int timeInMemory = Integer.parseInt(sc.next());
			PCB process = new PCB(processID, processSize, timeInMemory);
			process.setBaseRegister(base);
			for (int i = base; i < base + processSize; i++) {
				mainMemory[i] = process;
			}
			process.setLimitRegister(processSize);
			base += processSize;
			process.setFinishTime(process.getTimeInMemory());
			nextTime.add(process);
		}
		if(base < 2047) {
			holes.add(new Hole(2048-base,base));
			hole_count++;
		}
	}

	public static void resetSimulation() {
		try {
			mainHB.getChildren().clear();
			holes.clear();
			currentTime = 0;
			jobQ.clear();
			mainMemory = new PCB[2048];
			readReadyQFile();
			readJobQFile();
			refreshBtn.fire();
			hole_count = 0;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void memFX() {
	    VBox vb = new VBox();
	    vb.setSpacing(0);
	    Label label = new Label("Main Memory");
	    label.setTextFill(Color.BLACK);
	    label.setFont(new Font("", 20));
	    vb.getChildren().add(label);

	    drawPCB(vb, "OS\n512 MB", 512, 0, Color.SANDYBROWN);

	    for (int i = 512; i < mainMemory.length; i++) {
	        if (mainMemory[i] != null) {
	            PCB process = mainMemory[i];
	            drawPCB(vb, "Process: " + process.getProcessID() + "\nSize: " + process.getProcessSize() + " MB" +
	                    "\nTime In Memory: " + process.getTimeInMemory(), process.getProcessSize(), process.getBaseRegister(), Color.LIGHTGREEN);
	            i += process.getProcessSize() - 1;
	        } else {
	            Hole hole = findHoleAtAddress(i);
	            if (hole != null) {
	                drawPCB(vb, "Hole\nSize: " + hole.getHoleSize() + " MB", hole.getHoleSize(), hole.getHoleStartAddress(), Color.DARKGRAY);
	                i += hole.getHoleSize() - 1;
	            }
	        }
	    }

	    Label timeL = new Label("At Time=" + currentTime);
	    timeL.setFont(Font.font("Arial", FontWeight.BOLD, 20));
	    timeL.setAlignment(Pos.CENTER);
	    vb.getChildren().add(timeL);
	    mainHB.getChildren().add(vb);
	}

	private void drawPCB(VBox vb, String labelText, int size, int baseAddress, Color color) {
	    Rectangle r = new Rectangle();
	    r.setFill(color);
	    r.setStroke(Color.BLACK);
	    Label l = new Label(labelText);
	    l.setFont(new Font("", 11.8));
	    l.setTextFill(Color.BLACK);
	    r.setWidth(RECTANGLE_WIDTH);
	    r.setHeight(RECTANGLE_HEIGHT + 0.2 * size);
	    StackPane sp = new StackPane(r, l);

	    HBox hbox = new HBox();
	    hbox.setSpacing(0);

	    Label baseL = new Label(baseAddress + "-");
	    Label limitL = new Label();
	    baseL.setStyle("-fx-font-weight: bold; -fx-font-size: 10.2px;");
	    limitL.setStyle("-fx-font-weight: bold; -fx-font-size: 10.2px;");
	    baseL.setAlignment(Pos.TOP_RIGHT);
	    limitL.setAlignment(Pos.BOTTOM_RIGHT);

	    hbox.setAlignment(Pos.CENTER_RIGHT);
	    VBox labelsBox = new VBox(baseL, limitL);
	    labelsBox.setAlignment(Pos.TOP_RIGHT);
	    labelsBox.setSpacing(r.getHeight() - 30);
	    hbox.getChildren().addAll(labelsBox, sp);
	    vb.getChildren().add(hbox);
	    vb.setMinWidth(RECTANGLE_WIDTH + 50);
	}
	private Hole findHoleAtAddress(int address) {
	    for(Hole hole: holes) {
	        if (hole.getHoleStartAddress() == address) {
	            return hole;
	        }
	    }
	    return null;
	}

	private void autoScrollToEnd(ScrollPane scrollPane) {
		Platform.runLater(() -> {
			scrollPane.layout();
			scrollPane.setHvalue(scrollPane.getHmax());
		});
	}

}