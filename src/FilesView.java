import java.io.File;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FilesView extends BorderPane {
	
	public FilesView(Stage stage , Scene scene) {
		Button chooseReadyQBtn = new Button("Choose File");
		Button chooseJobQBtn = new Button("Choose File");
		Label chooseReadyQL = new Label("Choose the file containing the ready queue:");
		Label chooseJobQL = new Label("Choose the file containing the job queue:");

		chooseReadyQL.setFont(new Font(19));
		chooseJobQL.setFont(new Font(19));

		GridPane gp = new GridPane();

		chooseReadyQBtn.setOnAction(e->{
			FileChooser fc = new FileChooser();
			File f = fc.showOpenDialog(stage);
			if(f!=null) {
				Main.readyQFile = f;
				gp.add(new Label(f.getName()), 2, 0);
			}
		});
		
		chooseJobQBtn.setOnAction(e->{
			FileChooser fc = new FileChooser();
			File f = fc.showOpenDialog(stage);
			if(f!=null) {
				Main.jobQFile = f;
				gp.add(new Label(f.getName()), 2, 1);

			}
		});
		
		
		gp.add(chooseReadyQL, 0, 0);
		gp.add(chooseReadyQBtn, 1, 0);
		gp.add(chooseJobQL, 0, 1);
		gp.add(chooseJobQBtn, 1, 1);
		setCenter(gp);
		gp.setAlignment(Pos.CENTER);
		gp.setHgap(15);
		gp.setVgap(15);
		setAlignment(gp, Pos.CENTER);

	}

}
