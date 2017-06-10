package mainFrame;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class MusicPlayer {
	private int lastFrame;
	private String songName;
	private FloatControl gainControl;
	private Clip musicClip;
	
	public MusicPlayer(String songName) {
		this.songName=songName;
	}
	
	public void loadClip(String audioFile) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
		Clip clip;
		InputStream iStream=Main.class.getClassLoader().getResourceAsStream(audioFile);
		AudioInputStream audioStream=AudioSystem.getAudioInputStream(new BufferedInputStream(iStream));
        AudioFormat format=audioStream.getFormat();
        DataLine.Info info=new DataLine.Info(Clip.class, format);
        clip=(Clip)AudioSystem.getLine(info);
        clip.open(audioStream);
        this.gainControl=(FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
        this.lastFrame=0;
        this.musicClip=clip;
    }
	
	

}
