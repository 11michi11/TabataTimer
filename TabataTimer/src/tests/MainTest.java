package tests;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.Test;
import mainFrame.Main;

public class MainTest {
	Scanner namesFileScanner;
	Clip musicClip;
	
	@Test
	public void testPlayMusic() throws Exception {
		musicClip=Main.loadClip("resources/single_round_no_music.wav", false);
		Main.playMusic(musicClip);
		assertFalse(musicClip.isRunning());
		Main.pauseMusic(musicClip);
	}

	@Test
	public void testGetMusicFileFromString() throws Exception {
		musicClip=Main.loadClip("resources/single_round_no_music.wav", false);
		assertNotNull(musicClip);
	}
	
	@Test
	public void testLoadNamesFileFromResources() {
		InputStream namesStream=this.getClass().getClassLoader().getResourceAsStream("resources/names.txt");
		namesFileScanner=new Scanner(namesStream);
		assertNotNull(namesFileScanner);
	}
}
