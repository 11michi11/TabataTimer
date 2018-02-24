package mainFrame;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
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
    private int musicIndx;
    private String songName;
    private FloatControl gainControl;
    private Clip musicClip;
    public final ArrayList<String> songsNames = loadSongsNames();

    public MusicPlayer() throws LineUnavailableException, IOException, UnsupportedAudioFileException {
	Random rn = new Random();
	this.songName = songsNames.get(rn.nextInt(getSongsNamesArraySize()));
	this.musicIndx = songsNames.indexOf(songName);
	this.loadClip();
    }

    public MusicPlayer(String songName) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
	this.songName = songName;
	this.musicIndx = songsNames.indexOf(songName);
	this.loadClip();
    }

    private int getSongsNamesArraySize() {
	return songsNames.size();
    }

    private ArrayList<String> loadSongsNames() {
	ArrayList<String> names = new ArrayList<String>();
	InputStream namesStream = this.getClass().getClassLoader().getResourceAsStream("resources/names.txt");
	Scanner in = new Scanner(namesStream);
	while (in.hasNext()) {
	    names.add(in.next());
	}
	return names;
    }

    private void loadClip() throws LineUnavailableException, IOException, UnsupportedAudioFileException {
	Clip clip;
	InputStream iStream = Main.class.getClassLoader().getResourceAsStream("resources/" + songName);
	AudioInputStream audioStream = AudioSystem.getAudioInputStream(new BufferedInputStream(iStream));
	AudioFormat format = audioStream.getFormat();
	DataLine.Info info = new DataLine.Info(Clip.class, format);
	clip = (Clip) AudioSystem.getLine(info);
	clip.open(audioStream);
	this.gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
	this.lastFrame = 0;
	this.musicClip = clip;
    }

    public void play() {
	if (this.lastFrame < this.musicClip.getFrameLength()) {
	    this.musicClip.setFramePosition(lastFrame);
	} else {
	    this.musicClip.setFramePosition(0);
	}
	this.musicClip.start();
	System.out.println("Play:" + this.songName);
    }

    public void pause() {
	if (this.musicClip == null) {
	    System.out.println("Clip doesn't exist");
	    return;
	}
	if (this.musicClip.isRunning()) {
	    this.musicClip.stop();
	    this.lastFrame = this.musicClip.getFramePosition();
	} else {
	    System.out.println("Music isn't playing");
	}

	System.out.println("Pause:" + this.songName);
    }

    public boolean isRunning() {
	return this.musicClip.isRunning();
    }

    public int getFramePosition() {
	return this.musicClip.getFramePosition();
    }

    public int getLastFrame() {
	return this.lastFrame;
    }

    public void skipSongToEnd() {
	this.lastFrame = this.musicClip.getFrameLength();
    }

    public int getFrameLenght() {
	return this.musicClip.getFrameLength();
    }

    public void setSongOnFiveSeconds() {
	musicClip.setFramePosition(240000);
	lastFrame = 240000;
    }

    public void setSongOnTenSeconds() {
	musicClip.setFramePosition(480000);
	lastFrame = 480000;
    }

    public void setGainValue(float value) {
	this.gainControl.setValue(value);
    }

    public String getNextSongName() {
	int rand;
	Random rn = new Random();
	do {
	    rand = rn.nextInt(getSongsNamesArraySize());
	} while (this.musicIndx == rand);
	this.musicIndx = rand;

	return songsNames.get(this.musicIndx);
    }
}
