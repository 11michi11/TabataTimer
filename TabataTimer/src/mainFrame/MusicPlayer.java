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
	
	public MusicPlayer(String songName) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
		this.songName=songName;
		this.loadClip();
	}
	
	public void loadClip() throws LineUnavailableException, IOException, UnsupportedAudioFileException {
		Clip clip;
		InputStream iStream=Main.class.getClassLoader().getResourceAsStream("resources/"+songName);
		AudioInputStream audioStream=AudioSystem.getAudioInputStream(new BufferedInputStream(iStream));
        AudioFormat format=audioStream.getFormat();
        DataLine.Info info=new DataLine.Info(Clip.class, format);
        clip=(Clip)AudioSystem.getLine(info);
        clip.open(audioStream);
        this.gainControl=(FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
        this.lastFrame=0;
        this.musicClip=clip;
    }
	
	//Function responsible for playing and resuming music
		public void playMusic() {
	        if (this.lastFrame<this.musicClip.getFrameLength()) {
	        	this.musicClip.setFramePosition(lastFrame);
	        } else{
	        	this.musicClip.setFramePosition(0);
	        }
		    this.musicClip.start();  
	        System.out.println("Play:"+this.songName); //for debug, prints currently playing clip
		}

		//Function responsible foe pausing music
		public void pauseMusic() {
			//Check if clip exist 
			if(this.musicClip==null) {
				System.out.println("Clip doesn't exist");
				return;
			}
			//Case for currClip
			if (this.musicClip.isRunning()) {
				//Storing current frame position in lastFrame
	            lastFrame=this.musicClip.getFramePosition();
	            this.musicClip.stop();
	        }else {
	        	//When music isn't playing, prints that message 
	        	System.out.println("Music isn't playing");
	        }
			
			System.out.println("Pause:"+this.songName); //for debug, print currently pausing clip
		}
		
		public boolean isRunning() {
			return this.musicClip.isRunning();
		}
	
	

	
	

}
