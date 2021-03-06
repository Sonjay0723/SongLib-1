/*
 * Song.java
 * Elbert Basolis (egb37)
 * Alex Rossi (aer112)
 */
package songlib.view;

import java.io.Serializable;

public class Song implements Comparable, Serializable{

	String name;
	String artist;
	String album;
	String year;
	
	public Song(String name, String artist){
		this.name = name;
		this.artist = artist;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}
	
	public String toString(){
		return this.name;
	}

	@Override
	public int compareTo(Object o) {
		String a = this.name + this.artist;
		Song bs = (Song)o;
		String b = bs.name+bs.artist;
		return a.compareToIgnoreCase(b);
	}
}
