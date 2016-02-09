/*
 * LibraryController.java
 * Elbert Basolis (egb37)
 * Alex Rossi (aer112)
 */

package songlib.view;

import java.net.URL;
import java.util.ResourceBundle;
import java.io.*;
import java.io.File;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.FocusModel;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;


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
	
	/*
	 * TODO:
	 * TEST
	 * DONE Make ObservableList reflect changes from save (Changing song will not show up on left)
	 * Sort on edit/add
	 * DONE Figure out persistence and loading from file?
	 * Do we have to change errors to a popup dialog?
	 * Select next song if current is deleted, previous if no next
	 * Change the path of the file to be read/written to
	 * DONE Restrict Save/Delete to only work if a song is selected
	 */
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		newLib = true;

		try{
			//**********************NEEDS TO BE CHANGED
			File f = new File("C:/Users/Alexr0/Documents/lib.ser");
			if(!f.exists()){
				f.createNewFile();
				//System.out.println("file created");
			}else{
				
				//checks if the file is empty
				try{
					//*****************************NEEDS TO BE CHANGED
					BufferedReader br = new BufferedReader(new FileReader("C:/Users/Alexr0/Documents/lib.ser"));
					if( br.readLine() == null){
						newLib = true;
					}else{
						newLib = false;
					}
				}catch(FileNotFoundException nf){
					System.out.println("File not found");
				}
			}
		}catch(Exception m){
			//System.out.println("the error is here");
		}
		System.out.println("test");
		
		//reads existing file and puts it into this song library if the file exists
		if(newLib == false){
			System.out.println("test2");
			try{
				//*****************************NEEDS TO BE CHANGED
				FileInputStream fileIn = new FileInputStream("C:/Users/Alexr0/Documents/lib.ser");
				ObjectInputStream in = new ObjectInputStream(fileIn);
				System.out.println("test2");
				try{
					while(true){
						//System.out.println("huh?");
						Song temp = (Song)in.readObject();
						songs.add(temp);
					}
				}catch(EOFException e){
					System.out.println("Songlist successfully loaded");
					in.close();
					fileIn.close();
				}
				
			}catch(IOException i){
				i.printStackTrace();
				return;
			}catch(ClassNotFoundException c){
				System.out.println("Not found");
				c.printStackTrace();
				return;
			}
		}
	
		songList.setItems(songs);
		
		//Pull details from persistent storage
		//If it does that, select top
		//huh
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
                    detailName.setText(new_val.name);
                    detailArtist.setText(new_val.artist);
                    detailAlbum.setText(new_val.album);
                    detailYear.setText(new_val.year);
				}
			}
		});
		
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
			if(s.getName().equals(addName.getText()) && s.getArtist().equals(addArtist.getText())){
				addWarning.setText("Song from that artist exists! Try another.");
				addWarning.setOpacity(1);
				return;
			}
			addWarning.setOpacity(0);
		}
		
		//Add that bad boy
		Song newSong = new Song(addName.getText(), addArtist.getText());
		newSong.setAlbum(addAlbum.getText());
		newSong.setYear(addYear.getText());
		
		songs.add(newSong);
		
		songList.getSelectionModel().select(newSong);
		
		//Clear add fields
		addName.setText("");
		addArtist.setText("");
		addAlbum.setText("");
		addYear.setText("");
		
		//Don't forget to sort this
	
			serialize();
		
	}
	
	@FXML
	private void deleteSong(ActionEvent action){
		//Add logic
		Song s = songList.getSelectionModel().getSelectedItem();
		
		if(s != null){
		songs.remove(s);
		
		//Shouldn't have to sort???
		
		serialize();
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
	
		//Check for redundant song/artist combo
		for(Song s : songs){
			if(s.getAlbum().equals(detailAlbum.getText()) && s.getYear().equals(detailYear.getText()) && s.getName().equals(detailName.getText()) && s.getArtist().equals(detailArtist.getText())){
				detailWarning.setText("Duplicate! Try another.");
				detailWarning.setOpacity(1);
				return;
			}
			detailWarning.setOpacity(0);
		}

		//Added from here **********************************************

		String tempName, tempArtist, tempAlbum, tempYear;
		tempName = detailName.getText();
		tempArtist = detailArtist.getText();
		tempAlbum = detailAlbum.getText();
		tempYear = detailYear.getText();
		
		
		Song s = songList.getSelectionModel().getSelectedItem();
		if(s != null){
		songs.remove(s);
		
		
		
		//Add that bad boy
		Song newSong = new Song(tempName, tempArtist);
		newSong.setAlbum(tempAlbum);
		newSong.setYear(tempYear);
		
		songs.add(newSong);
		
		songList.getSelectionModel().select(newSong);
		//to here*********************************************************
		//Clear add fields
		addName.setText("");
		addArtist.setText("");
		addAlbum.setText("");
		addYear.setText("");
		
		//DONT FORGET TO SORT
		serialize();
		}
	}
	
	private void serialize(){
		try{
			//***************************NEEDS TO BE CHANGED
			FileOutputStream fileOut = new FileOutputStream("C:/Users/Alexr0/Documents/lib.ser");
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
