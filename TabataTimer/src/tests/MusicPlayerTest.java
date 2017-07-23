package tests;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.Before;
import org.junit.Test;
import mainFrame.Main;
import mainFrame.MusicPlayer;

public class MusicPlayerTest {
	
	MusicPlayer musicPlayer;
	
	@Before
	public void initObjects() {
		try {
			musicPlayer=new MusicPlayer("single_round_no_music.wav");
			assertNotNull(musicPlayer);
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
		try {
			TimeUnit.MILLISECONDS.sleep(10);
		} catch (InterruptedException e) {
			fail();
		}
		musicPlayer.pause();
		assertFalse(musicPlayer.isRunning());
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
	
	@Test
	public void testGetFrameLenght() {
		int frameLenght=musicPlayer.getFrameLenght();
		assertEquals(frameLenght, 1439887);
	}
	
	@Test
	public void testSkipSongToEnd() {
		musicPlayer.skipSongToEnd();
		int frameLenght=musicPlayer.getFrameLenght();
		assertEquals(frameLenght, 1439887);
		
	}
	
	@Test
	public void testSetSongOnFiveSeconds() {
		musicPlayer.setSongOnFiveSeconds();
		int frameLenght=musicPlayer.getFramePosition();
		assertEquals(frameLenght, 240000);
	}
	
	@Test
	public void testSetSongOnTenSeconds() {
		musicPlayer.setSongOnTenSeconds();
		int frameLenght=musicPlayer.getFramePosition();
		assertEquals(frameLenght, 480000);
	}
}
