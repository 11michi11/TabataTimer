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
import mainFrame.MusicPlayer;

public class MainTest {
	
	@Test
	public void testPlayMusic() {
		try{
			MusicPlayer music=new MusicPlayer("single_round_no_music.wav");
			music.playMusic();
			assertFalse(music.isRunning());
			music.pauseMusic();
		}catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testGetMusicFileFromString() {
		try{
			MusicPlayer music=new MusicPlayer("single_round_no_music.wav");
			music.loadClip();
			assertNotNull(music);
		}catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testLoadNamesFileFromResources() {
		InputStream namesStream=this.getClass().getClassLoader().getResourceAsStream("resources/names.txt");
		Scanner namesFileScanner=new Scanner(namesStream);
		assertNotNull(namesFileScanner);
	}
	
	@Test
	public void testPauseMusic() throws LineUnavailableException, IOException, UnsupportedAudioFileException {
		try{
			MusicPlayer music=new MusicPlayer("single_round_no_music.wav");
			music.playMusic();
			music.pauseMusic();
			assertTrue(music.isRunning());
			assertEquals(music.getFramePosition(), music.getLastFrame());
		}catch (Exception e) {
			fail();
		}
		
	}
}
