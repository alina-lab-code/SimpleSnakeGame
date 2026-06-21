import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class SoundManager {
    private Clip clip;

    public SoundManager() {
        try {
            // Load the music file from the resources folder
            URL url = getClass().getResource("/music.wav");
            if (url != null) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                clip = AudioSystem.getClip();
                clip.open(audioIn);
            } else {
                System.out.println("Music file not found!");
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
//loop the music
    public void playLoop() {
        if (clip != null) {
            clip.loop(Clip.LOOP_CONTINUOUSLY); // Play forever
            clip.start();
        }
    }

    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public void restart() {
        stop();
        if (clip != null) {
            clip.setFramePosition(0); // Rewind to start
            clip.start(); // Start playing
        }
    }
}