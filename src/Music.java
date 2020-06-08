import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import java.io.File;
import java.util.ArrayList;

public class Music {
    private ArrayList<String> songs;
    private Clip clip;
    private long clipPosition = 0;

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
                clip.setMicrosecondPosition(clipPosition);
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
            clipPosition = clip.getMicrosecondPosition();
            clip.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setVolume(double gain) {
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

        float dB = (float) (Math.log(gain) / Math.log(10.0) * 20.0);
        gainControl.setValue(dB);
    }
}
