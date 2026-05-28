package ControlPanel;

public class Song {
    private String title;
    private String artist;
    private String audioPath;
    private String imagePath;

    public Song(String title, String artist, String audioPath, String imagePath) {
        this.title = title;
        this.artist = artist;
        this.audioPath = audioPath;
        this.imagePath = imagePath;
    }

    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getAudioPath() { return audioPath; }
    public String getImagePath() { return imagePath; }
}
