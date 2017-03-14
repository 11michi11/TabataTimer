import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioPlayer implements LineListener{
	boolean playCompleted;
	
	void play(String audioFilePath) {
		File audioFile=new File(audioFilePath);
		
		try {
			AudioInputStream audioStream=AudioSystem.getAudioInputStream(audioFile);
			AudioFormat format=audioStream.getFormat();
			DataLine.Info info=new DataLine.Info(Clip.class, format);
			Clip audioClip=(Clip)AudioSystem.getLine(info);
			audioClip.addLineListener(this);
			audioClip.open(audioStream);
			audioClip.start();
			
			while(!playCompleted) {
				try {
					Thread.sleep(1000);
				}catch (InterruptedException e) {
					e.printStackTrace();
					audioClip.stop();
				}
			}
			
			audioClip.close();
		}catch(UnsupportedAudioFileException e) {
			System.out.println("The specified audio file is not supported.");
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            System.out.println("Audio line for playing back is unavailable.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error playing the audio file.");
            e.printStackTrace();
        }
	}
	
	@Override
	public void update(LineEvent event) {
		LineEvent.Type type=event.getType();
		if(type==LineEvent.Type.START) {
			System.out.println("Playback started");
		}else if(type==LineEvent.Type.STOP) {
			playCompleted=true;
			System.out.println("Playback completed");
		}
	}
}
