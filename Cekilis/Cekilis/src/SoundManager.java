import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundManager {
    public void playApplause(String filePath) {
        new Thread(() -> {
            try {
                File audioFile = new File(filePath);
                if (!audioFile.exists()) {
                    System.out.println("Ses dosyası bulunamadı: " + filePath);
                    return;
                }
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }).start();
    }
}