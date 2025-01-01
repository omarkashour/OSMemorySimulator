import java.io.File;
import java.io.FileNotFoundException;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Main extends Application {
	
	static File readyQFile;
	static File jobQFile;
	
	static boolean readyFile = false;
	static boolean jobFile = false;
	public static void main(String[] args) throws FileNotFoundException {
		launch(args);
			
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		TabPane tb = new TabPane();	
		Tab filesTab = new Tab("Files"); 
		Tab firstFitTab = new Tab("First Fit");
		Tab bestFitTab = new Tab("Best Fit");
		Tab worstFitTab = new Tab("Worst Fit");
		filesTab.setClosable(false);
		firstFitTab.setClosable(false);
		bestFitTab.setClosable(false);
		worstFitTab.setClosable(false);
		

		
		
		tb.getTabs().addAll(filesTab,firstFitTab,bestFitTab,worstFitTab);
		Scene scene = new Scene(tb,990,700);
		
		FilesView filesView = new FilesView(primaryStage,scene);
		filesTab.setContent(filesView);
		Button chooseReadyQBtn = new Button("Choose File");
		Button chooseJobQBtn = new Button("Choose File");
		Label chooseReadyQL = new Label("Choose the file containing the ready queue:");
		Label chooseJobQL = new Label("Choose the file containing the job queue:");

		chooseReadyQL.setFont(new Font(19));
		chooseJobQL.setFont(new Font(19));

		GridPane gp = new GridPane();


		chooseReadyQBtn.setOnAction(e->{
			FileChooser fc = new FileChooser();
			File f = fc.showOpenDialog( primaryStage);
			if(f!=null) {
				Main.readyQFile = f;
				gp.add(new Label(f.getName()), 2, 0);
				readyFile = true;
			}
		});
		
		chooseJobQBtn.setOnAction(e->{
			FileChooser fc = new FileChooser();
			File f = fc.showOpenDialog(primaryStage);
			if(f!=null) {
				Main.jobQFile = f;
				gp.add(new Label(f.getName()), 2, 1);
				jobFile = true;
			}
		});
		
		Button loadBtn = new Button("Load Files");

		gp.add(chooseReadyQL, 0, 0);
		gp.add(chooseReadyQBtn, 1, 0);
		gp.add(chooseJobQL, 0, 1);
		gp.add(chooseJobQBtn, 1, 1);
		gp.add(loadBtn, 1, 2);

		BorderPane bp = new BorderPane();
		bp.setCenter(gp);
		gp.setAlignment(Pos.CENTER);
		gp.setHgap(15);
		gp.setVgap(15);
		bp.setAlignment(gp, Pos.CENTER);
		
		loadBtn.setOnAction(e->{
			if(readyFile && jobFile) {
				try {
				FirstFitView ffView = new FirstFitView();

				WorstFitView wfView = new WorstFitView();
			    BestFitView bfView = new BestFitView();

				firstFitTab.setContent(ffView);
				worstFitTab.setContent(wfView);
				bestFitTab.setContent(bfView);
				Label l = new Label("Files loaded successfully.");
				l.setAlignment(Pos.CENTER);
				l.setFont(new Font(27));
				bp.setTop(l);
				bp.setAlignment(l, Pos.CENTER);
				}
				catch(Exception e1) {
					Label l = new Label("Please choose your files correctly.");
					l.setAlignment(Pos.CENTER);
					l.setFont(new Font(27));
					bp.setTop(l);
					bp.setAlignment(l, Pos.CENTER);
				}
				
			}	
			readyFile = false;
			jobFile = false;
			
		});
		filesTab.setContent(bp);
		primaryStage.setTitle("OS Project - Omar Kashour - 1210082");
//		Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
		primaryStage.setScene(scene);
		primaryStage.setFullScreen(true);
		primaryStage.show();
		
	}
	
}
