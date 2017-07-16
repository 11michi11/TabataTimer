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
	
	
	@Test
	public void initObjects() {
		try {
			MusicPlayer musicPlayer=new MusicPlayer("single_round_no_music.wav");
			assertNotNull(musicPlayer);
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			fail();
		}
		
	}
	
	@Test
	public void testPlayMusic() {
		try {
			MusicPlayer musicPlayer=new MusicPlayer("single_round_no_music.wav");
			musicPlayer.play();
			assertFalse(musicPlayer.isRunning());
			musicPlayer.pause();
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			fail();
		}
	}
	
	@Test
	public void testPauseMusic() {
		try {
			MusicPlayer musicPlayer=new MusicPlayer("single_round_no_music.wav");
			musicPlayer.play();
			try {
				TimeUnit.MILLISECONDS.sleep(10);
			} catch (InterruptedException e) {
				fail();
			}
			musicPlayer.pause();
			assertFalse(musicPlayer.isRunning());
			assertEquals(musicPlayer.getFramePosition(), musicPlayer.getLastFrame());
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
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
	public void testGetNextSongName() {
		try {
			MusicPlayer musicPlayer=new MusicPlayer("single_round_no_music.wav");
			assertNotNull(musicPlayer);
			String songName=musicPlayer.getNextSongName();
			assertTrue(musicPlayer.songsNames.contains(songName));
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			fail();
		}
		
	}
	
	@Test
	public void testGetFrameLenght() {
		try {
			MusicPlayer musicPlayer=new MusicPlayer("single_round_no_music.wav");
			assertNotNull(musicPlayer);
			int frameLenght=musicPlayer.getFrameLenght();
			assertEquals(frameLenght, 1439887);
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			fail();
		}
	}
	
	@Test
	public void testSkipSongToEnd() {
		try {
			MusicPlayer musicPlayer=new MusicPlayer("single_round_no_music.wav");
			assertNotNull(musicPlayer);
			musicPlayer.skipSongToEnd();
			int frameLenght=musicPlayer.getFrameLenght();
			assertEquals(frameLenght, 1439887);
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			fail();
		}
	}
}
