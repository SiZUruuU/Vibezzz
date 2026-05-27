package ControlPanel;

import java.util.ArrayList;
//Music Database
public class MusicHandler {

    private ArrayList<Song> playlist = new ArrayList<>();
    
    public MusicHandler() {
        //ichange lang ang path
        Song track1 = new Song("Edamame", "BBNO$ & Rich Brian", "C:/Music/edamame.mp3", "C:/Images/edamame.png");
        Song track2 = new Song("Violet", "Connor Price (feat. Killa)", "C:/Music/violet.mp3", "C:/Images/violet.png");
        Song track3 = new Song("Snowman", "Sia", "C:/Music/snowman.mp3", "C:/Images/snowman.png");
        Song track4 = new Song("party at the club bug!", "spellcasting", "C:/Music/partyattheclubbug.mp3", "C:/Images/partyattheclubbug.png");
        Song track5 = new Song("Innocence", "Daniel Caesar", "C:/Music/innocence.mp3", "C:/Images/innocence.png");
        Song track6 = new Song("Landed In Brooklyn", "Khantrast", "C:/Music/landedinbrooklyn.mp3", "C:/Images/landedinbrooklyn.png");
        Song track7 = new Song("Paint The Town Red", "Doja Cat", "C:/Music/paintthetownred.mp3", "C:/Images/paintthetownred.png");
        Song track8 = new Song("Flamenco House", "Iapix", "C:/Music/flamencohouse.mp3", "C:/Images/flamencohouse.png");
        //Song track10 = new Song("Ice Man", "Drake", "C:/Music/iceman.wav", "C:/Images/iceman.png");

        playlist.add(track1);
        playlist.add(track2);
        playlist.add(track3);
        playlist.add(track4);
        playlist.add(track5);
        playlist.add(track6);
        playlist.add(track7);
        playlist.add(track8);
        //playlist.add(track9);
        
    }

    public ArrayList<Song> getPlaylist() {
        return playlist;
    }
}
