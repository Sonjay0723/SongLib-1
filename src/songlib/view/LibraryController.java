/*
 * LibraryController.java
 * Elbert Basolis (egb37)
 * Alex Rossi (aer112)
 */

package songlib.view;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.io.*;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.FocusModel;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import java.util.Optional;


public class LibraryController implements Initializable {

	@FXML private ListView<Song> songList;
	@FXML private Button deleteSong;
	@FXML private Button addSong;
	@FXML private Button saveSong;
	
	@FXML private TextField addName;
	@FXML private TextField addArtist;
	@FXML private TextField addAlbum;
	@FXML private TextField addYear;

	@FXML private TextField detailName;
	@FXML private TextField detailArtist;
	@FXML private TextField detailAlbum;
	@FXML private TextField detailYear;

	@FXML private Label addWarning;
	@FXML private Label detailWarning;
	boolean newLib;  //used to check if the instance being run creates a new library or loads a library

	ObservableList<Song> songs = FXCollections.observableArrayList();

	@Override
	public void initialize(URL url, ResourceBundle rb) {

		//Add Listeners
		//Listener for change of selection
		songList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Song>(){
			public void changed(ObservableValue<? extends Song> ov, 
					Song old_val, Song new_val) {

				if(new_val == null){
					detailName.setText("");
					detailArtist.setText("");
					detailAlbum.setText("");
					detailYear.setText("");
				}else{
					detailName.setText(new_val.name.trim());
					detailArtist.setText(new_val.artist.trim());
					detailAlbum.setText(new_val.album.trim());
					detailYear.setText(new_val.year.trim());
				}
			}
		});

		addFromFile();
		songList.setItems(songs);
		songList.getSelectionModel().selectFirst();

	}

	//Checks for library file
	//Adds any songs found to 'songs' and allows initialize() to add them to the FX object
	private void addFromFile() {

		newLib = true;

		try{
			File f = new File("lib.ser");
			if(!f.exists()){
				f.createNewFile();
				System.out.println("file created");
			}else{
				newLib = false;
			}
		}catch(IOException e){
			System.out.println("Could not create file!");
			e.printStackTrace();
		}

		//reads existing file and puts it into this song library if the file exists
		if(newLib == false){
			try{
				FileInputStream fileIn = new FileInputStream("lib.ser");
				ObjectInputStream in = new ObjectInputStream(fileIn);
				System.out.println("found file");
				try{
					while(true){
						Song temp = (Song)in.readObject();
						songs.add(temp);
					}
				}catch(EOFException e){
					System.out.println("Songlist successfully loaded");
					in.close();
					fileIn.close();
				}
			}catch(IOException | ClassNotFoundException c){
				System.out.println("Corrupted file, overwriting this one.");
				corruptedLibrary();
			}
		}		
	}

	//If addFromFile finds a corrupted/empty library, it does this.
	private void corruptedLibrary() {
		try {
			Files.delete(Paths.get("lib.ser"));
			File f = new File("lib.ser");
			if(!f.exists()){
				f.createNewFile();
				System.out.println("New library created.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void addSong(ActionEvent action){
		
		//Validate input, hide or show warning
		if("".equals(addName.getText()) || "".equals(addArtist.getText())){
			addWarning.setText("Song must have a name and artist! Try again.");
			addWarning.setOpacity(1);
			return;
		}else{
			addWarning.setOpacity(0);
		}
		
		//Check for redundant song/artist combo
		for(Song s : songs){
			if(s.getName().equals(addName.getText().trim()) && s.getArtist().equals(addArtist.getText().trim())){
				addWarning.setText("Song from that artist exists! Try another.");
				addWarning.setOpacity(1);
				addName.setText("");
				addArtist.setText("");
				addAlbum.setText("");
				addYear.setText("");
				return;
			}
			addWarning.setOpacity(0);
		}
	
		//Add that bad boy
		Song newSong = new Song(addName.getText().trim(), addArtist.getText().trim());
		newSong.setAlbum(addAlbum.getText().trim());
		newSong.setYear(addYear.getText().trim());

		Alert confirm = new Alert(AlertType.CONFIRMATION);
		confirm.setTitle("Add Song Confirmation");
		confirm.setHeaderText("Are you sure that you want to add "+newSong.name+" by "+newSong.artist+"?");
		confirm.setContentText("Are you really sure that you want to do this?");
		
		Optional<ButtonType> result = confirm.showAndWait();
		if(result.get() == ButtonType.OK){
			
			songs.add(newSong);
			songList.getSelectionModel().select(newSong);
	
			//Clear add fields
			addName.setText("");
			addArtist.setText("");
			addAlbum.setText("");
			addYear.setText("");
	
			FXCollections.sort(songs);
			serialize();
		}else{
			addName.setText("");
			addArtist.setText("");
			addAlbum.setText("");
			addYear.setText("");		
		}
	}

	@FXML
	private void deleteSong(ActionEvent action){
		
		Song s = songList.getSelectionModel().getSelectedItem();

		Alert confirm = new Alert(AlertType.CONFIRMATION);
		confirm.setTitle("Delete Confirmation");
		confirm.setHeaderText("Are you sure that you want to delete "+s.name+" by "+s.artist+"?");
		confirm.setContentText("Are you really sure that you want to do this?");
		
		Optional<ButtonType> result = confirm.showAndWait();
		if(result.get() == ButtonType.OK){	
			if(s != null){
	
				int nextIndex = songList.getSelectionModel().getSelectedIndex() + 1;
				
				//Selected song is not the last one
				if(nextIndex != songs.size()){
					songList.getSelectionModel().select(nextIndex);
				}
				
				songs.remove(s);	
				FXCollections.sort(songs);
				serialize();
			}
		}
	}

	@FXML
	private void saveSong(ActionEvent action){
		if("".equals(detailName.getText()) || "".equals(detailArtist.getText())){
			detailWarning.setText("Song must have a name and artist! Try again.");
			detailWarning.setOpacity(1);
			return;
		}else{
			detailWarning.setOpacity(0);
		}

		Song selectedSong = songList.getSelectionModel().getSelectedItem();
		
		for(Song s : songs){
			if(s != selectedSong){
				if(/*s.getAlbum().equals(detailAlbum.getText().trim()) && s.getYear().equals(detailYear.getText().trim()) && */
						s.getName().equals(detailName.getText().trim()) && s.getArtist().equals(detailArtist.getText().trim())){

					detailWarning.setText("Duplicate! Try another.");
					detailWarning.setOpacity(1);
					detailName.setText("");
					detailArtist.setText("");
					detailAlbum.setText("");
					detailYear.setText("");
					return;
					
				}
				detailWarning.setOpacity(0);
			}
		}
	
		//Check for redundant song/artist combo
		Alert confirm = new Alert(AlertType.CONFIRMATION);
		confirm.setTitle("Edit confirmatiion");
		confirm.setHeaderText("Are you sure that you want to edit "+selectedSong.name+" by "+selectedSong.artist+"?");
		confirm.setContentText("Are you really sure that you want to do this?");
		
		Optional<ButtonType> result = confirm.showAndWait();
		if(result.get() == ButtonType.OK){
	
			String tempName, tempArtist, tempAlbum, tempYear;
			tempName = detailName.getText().trim();
			tempArtist = detailArtist.getText().trim();
			tempAlbum = detailAlbum.getText().trim();
			tempYear = detailYear.getText().trim();
	
			if(selectedSong != null){
				selectedSong.setAlbum(tempAlbum);
				selectedSong.setArtist(tempArtist);
				selectedSong.setName(tempName);
				selectedSong.setYear(tempYear);
	
				songList.getSelectionModel().select(selectedSong);
				//Clear add fields
				addName.setText("");
				addArtist.setText("");
				addAlbum.setText("");
				addYear.setText("");
				FXCollections.sort(songs);
				serialize();
			}
		}
	}

	private void serialize(){
		try{
			FileOutputStream fileOut = new FileOutputStream("lib.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			for(Song s : songs){
				out.writeObject(s);
			}
			out.close();
			fileOut.close();
			System.out.println("Serialized data is saved in ");
		}catch(IOException i){
			i.printStackTrace();
		}
	}
}
