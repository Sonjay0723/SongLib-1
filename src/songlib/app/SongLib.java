/*
 * SongLib.java
 * Elbert Basolis (egb37)
 * Alex Rossi (aer112)
 */

package songlib.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.*;
import java.io.File;

public class SongLib extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		AnchorPane root = (FXMLLoader.load(getClass().getResource("/songlib/view/Library.fxml")));
		
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Song Library");
		primaryStage.setResizable(false);  
		primaryStage.show();

	}

	public static void main(String[] args) {
		launch(args);

	}

}
