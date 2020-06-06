import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.io.File;
import java.util.ArrayList;

public class Music {
    private ArrayList<String> songs;
    private Clip clip;

    //TODO - get list of song names using asset loader
    //TODO - keep track of position in song after mute and carry on from where song left off (need to store current song and timestamp)
    //TODO - instead of passing the song in, loop through list of all songs
    //TODO - allow user to adjust volume of audio using slider

    Music(ArrayList<String> songs) {
        this.songs = songs;
    }

    void playSong(String musicLocation) {
        try {
            File musicPath = new File(musicLocation);

            if (musicPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                clip = AudioSystem.getClip();
                clip.open(audioInput );
                clip.start();
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                System.out.println("File path not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void stopSong() {
        try {
            clip.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
