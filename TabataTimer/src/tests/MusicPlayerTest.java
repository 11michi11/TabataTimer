package tests;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.Before;
import org.junit.Test;
import mainFrame.Main;
import mainFrame.MusicPlayer;

public class MusicPlayerTest {
	private MusicPlayer musicPlayer;
	
	
	@Before
	public void initObjects() {
		try {
			musicPlayer=new MusicPlayer("single_round_no_music.wav");
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			fail();
		}
	}
	
	@Test
	public void testPlayMusic() {
		musicPlayer.play();
		assertFalse(musicPlayer.isRunning());
		musicPlayer.pause();
	
	}
	
	@Test
	public void testPauseMusic() {
		musicPlayer.play();
		musicPlayer.pause();
		assertTrue(musicPlayer.isRunning());
		assertEquals(musicPlayer.getFramePosition(), musicPlayer.getLastFrame());
	}
	
	@Test
	public void testLoadNamesFileFromResources() {
		InputStream namesStream=this.getClass().getClassLoader().getResourceAsStream("resources/names.txt");
		Scanner namesFileScanner=new Scanner(namesStream);
		assertNotNull(namesFileScanner);
	}
	
	@Test
	public void testGetNextSongName() {
		String songName=musicPlayer.getNextSongName();
		assertTrue(musicPlayer.songsNames.contains(songName));
	}
}
