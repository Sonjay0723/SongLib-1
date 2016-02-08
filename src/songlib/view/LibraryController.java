/*
 * LibraryController.java
 * Elbert Basolis (egb37)
 * Alex Rossi (aer112)
 */

package songlib.view;

import java.net.URL;
import java.util.ResourceBundle;

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
	
	final ObservableList<Song> songs = FXCollections.observableArrayList();
	
	/*
	 * TODO:
	 * Make ObservableList reflect changes from save (Changing song will not show up on left)
	 * Sort on edit/add
	 * Figure out persistance and loading from file?
	 * Do we have to change errors to a popup dialauge?
	 * Select next song if current is deleted, previous if no next
	 */
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		songList.setItems(songs);
		
		//Pull details from persistant storage
		//If it does that, select top
		
		//Listner for change of selection
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
	}
	
	@FXML
	private void deleteSong(ActionEvent action){
		//Add logic
		Song s = songList.getSelectionModel().getSelectedItem();
		
		
		songs.remove(s);
		
		//Shouldn't have to sort???
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
		
		//Don't forget to sort this
		
	
		
		//DONT FORGET TO SORT
		

	}
	
}
