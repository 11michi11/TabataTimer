package mainFrame;

import java.awt.Color;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

//Timer thread is responsible for changing state of tabataPanel components. It is also responsible for the whole process of Tabata.
//That means it plays and stops music when needed, changes background colour, changes countdown, rounds and tabats values.
public class Timer implements Runnable{
    private static MusicPlayer currentSong;	
    private static MusicPlayer timerSong; 
    private static MusicPlayer endingSong;
	
	private int seconds;
	private boolean changeMusic;
	private boolean runed=false;
	private boolean playTimerClip;
	private boolean playCurrentClip;
	private boolean playEndingClip;
	private boolean paused=false;
	private boolean reset=false;
	private static boolean roundReset=false;
	private Countdown countComp;
	private Rounds roundsComp;
	
	private static ActionEnum actionToDo=ActionEnum.BEFORE;
	//Enum for specific actions in tabata. Used to control program flow in switch
	private enum ActionEnum {RESET, BEFORE, EXERCISE, REST, RESTROUND, ENDROUND};
	
	public void run() {
		countComp=Main.getCountdownComponent();
		roundsComp=Main.getRoundsComponent();
		prepareMusic();
		try{
			while(!Thread.currentThread().isInterrupted()) {					
				if(paused) 
					prepareForActionAfterPause();
				else
					prepareForAction();

				if(actionToDo==ActionEnum.ENDROUND)
					handleEndroundActions();
								
				preformTimerActionForEachSecond();
				
				setNextActionAtTheEndOfRound();
									
			}			
		}catch(InterruptedException e) {
			paused=true;
			currentSong.pause();
			timerSong.pause();
			endingSong.pause();
			System.out.println("Timer interrupted!");
			Thread.currentThread().interrupt();
		}
	}
	
	private void preformTimerActionForEachSecond() throws InterruptedException {
		System.out.println(actionToDo+"in while");
		while(seconds>0) {
			TimeUnit.SECONDS.sleep(1);
			seconds--;
			countComp.addSec(-1);
			countComp.repaint();
			roundsComp.repaint();
			
			if((changeMusic&&seconds==5)||(actionToDo==ActionEnum.RESTROUND&&seconds==25)||(actionToDo==ActionEnum.RESTROUND&&seconds==5)) {
				if(currentSong!=null&&currentSong.isRunning())
					currentSong.pause();
				
				try {
					currentSong=new MusicPlayer(currentSong.getNextSongName());
				} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
					e.printStackTrace();
				}
				
				currentSong.setGainValue(-15.0f);
				currentSong.play();
				changeMusic=false;
			}

			if(actionToDo==ActionEnum.RESTROUND&&seconds==10)
				timerSong.play();

		}
	}
	
	private void handleEndroundActions() throws InterruptedException {
		if(endingSong!=null&&!endingSong.isRunning()&&playEndingClip)
			endingSong.play();
		TimeUnit.MILLISECONDS.sleep(500);
		System.out.println(reset);
		while(reset==false) {
			if(!endingSong.isRunning()) {
				System.out.println("reset");
				reset=true;
				break;
			}else if(Thread.currentThread().isInterrupted()) {
				System.out.println("reset");
				reset=true;
				endingSong.pause();
				break;
			}
			if(endingSong.getFramePosition()==endingSong.getFrameLenght())
				System.out.println("End");
		}
		System.out.println("Exit");
	}
	
	private void prepareForAction(){
		switch(actionToDo) {
			case RESET:
				reset=false;
				roundsComp.setRound(0);
				roundsComp.setTab(0);
				countComp.setSec(0);
				timerSong.skipSongToEnd();
				currentSong.skipSongToEnd();
				seconds=countComp.getSec();
				playCurrentClip=false;
				break;
			case BEFORE:
				roundsComp.setRound(0);
				roundsComp.setTab(0);
				timerSong.skipSongToEnd();
				if(currentSong==null) {
					try{
						currentSong=new MusicPlayer();
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
				System.out.println(actionToDo.toString());
				if(!runed) {
					countComp.setSec(10);
					seconds=countComp.getSec();
					runed=true;
				}
				
				Main.tabataPanel.setColors(Color.WHITE, Color.BLUE);
				Main.tabataPanel.repaint();
				
				timerSong.play();
				playTimerClip=true;
				changeMusic=true;
				runed=false;
	
				break;
			case EXERCISE:
				currentSong.setGainValue(5.0f);
				if(!roundReset)
					roundsComp.addRound(1);
				else {
					currentSong.setSongOnFiveSeconds();
					timerSong.setSongOnTenSeconds();
					roundReset=false;
				}
				
				Main.tabataPanel.setColors(Color.WHITE, Color.GREEN);
				countComp.setSec(20);
				seconds=countComp.getSec();
				Main.tabataPanel.repaint();
				playCurrentClip=true;
				
				break;
			case REST:
				if(!currentSong.isRunning()) 
					currentSong.play();
				Main.tabataPanel.setColors(Color.WHITE, Color.RED);
				countComp.setSec(10);
				seconds=countComp.getSec();
				Main.tabataPanel.repaint();
				
				changeMusic=true;
				currentSong.setGainValue(-15.0f);
				timerSong.pause();
				timerSong.skipSongToEnd();
				timerSong.play();
				
				break;
			case RESTROUND:
				timerSong.pause();

				countComp.setSec(30);
				seconds=countComp.getSec();
				roundsComp.addTab(1); 
				roundsComp.setRound(0);
				Main.tabataPanel.setColors(Color.WHITE, Color.BLUE);
				Main.tabataPanel.repaint();
				playTimerClip=false;
				
				changeMusic=true;
				currentSong.setGainValue(-15.0f);
						
				break;
			case ENDROUND:
				roundsComp.addTab(1);
				Main.tabataPanel.setColors(Color.WHITE, Color.YELLOW);
				Main.tabataPanel.repaint(); 
				playEndingClip=true;
				break;
			default:
				System.out.println(actionToDo.toString());
				System.out.println("Something wrong happend!!!");
				break;	
		}
	}

	private void prepareForActionAfterPause() {
		//Restore seconds state
		countComp.setSec(seconds);
		//Switch for actions
		switch(actionToDo) {
			case EXERCISE:
				if(roundReset) {
					changeMusic=false;
					currentSong.setGainValue(5.0f);
					currentSong.setSongOnFiveSeconds();
					timerSong.setSongOnTenSeconds();
					countComp.setSec(20);
					seconds=countComp.getSec();
					roundReset=false;
				}
				Main.tabataPanel.setColors(Color.WHITE, Color.GREEN);
				break;
			case REST:
				Main.tabataPanel.setColors(Color.WHITE, Color.RED);
				break;
			case BEFORE:
				if(seconds<=5)
					playCurrentClip=true;
				Main.tabataPanel.setColors(Color.WHITE, Color.BLUE);
				break;
			case RESTROUND:
				if(seconds<=10)
					playTimerClip=true;
				changeMusic=true;
				Main.tabataPanel.setColors(Color.WHITE, Color.BLUE);
				break;
			case ENDROUND:
				//roundsComp.addTab(1);
				Main.tabataPanel.setColors(Color.WHITE, Color.YELLOW);
				playEndingClip=false;
				reset=false;
				actionToDo=ActionEnum.RESET;
				break;
			case RESET:
				roundsComp.setRound(0);
				roundsComp.setTab(0);
				timerSong.skipSongToEnd();
				currentSong.skipSongToEnd();
				countComp.setSec(0);
				seconds=countComp.getSec();
				playCurrentClip=false;
				Main.tabataPanel.setColors(Color.WHITE, Color.YELLOW);
				break;
			default:
				System.out.println("Something wrong after paused!!!");
				break;
		}
		Main.tabataPanel.repaint();
		
		checkIfMusicIsPlayingAndResumeIfNeeded();
									
		paused=false;
	}
	
	private void setNextActionAtTheEndOfRound() {
		switch(actionToDo) {
			case EXERCISE:
				//check if current tabata is done and if yes, go to rest round
				if(roundsComp.getRound()==roundsComp.getTotalRounds()) {
					//check if training is done and if yes, go to reset
					if(roundsComp.getTab()+1==roundsComp.getTabTotal()) {
						currentSong.pause();
						timerSong.pause();
						Main.tabataPanel.setColors(Color.WHITE, Color.YELLOW);
						Main.tabataPanel.repaint();
						actionToDo=ActionEnum.ENDROUND;
						break;
					}
					
					Main.tabataPanel.setColors(Color.WHITE, Color.BLUE);
					Main.tabataPanel.repaint();
					actionToDo=ActionEnum.RESTROUND;
					break;
				}
				
				actionToDo=ActionEnum.REST;
				break;
			case REST:
				actionToDo=ActionEnum.EXERCISE;
				break;
			case BEFORE:
				actionToDo=ActionEnum.EXERCISE;
				break;
			case RESTROUND:
				actionToDo=ActionEnum.EXERCISE;
				break;
			case ENDROUND:
				if(reset) {
					actionToDo=ActionEnum.RESET;
					playEndingClip=false;
					Thread.currentThread().interrupt();
					return; //from Thread
				}
				break;
			case RESET:
				actionToDo=ActionEnum.BEFORE;
				break;
			default:
				System.out.println("Action error!!!");
				break;
		}	
	}
	
	private void checkIfMusicIsPlayingAndResumeIfNeeded() {
		if(timerSong!=null&&!timerSong.isRunning()&&playTimerClip)
			timerSong.play();
		if(currentSong!=null&&!currentSong.isRunning()&&playCurrentClip)
			currentSong.play();
		if(endingSong!=null&&!endingSong.isRunning()&&playEndingClip)
			endingSong.play();
	}
	
	private void prepareMusic() {
		try {
			if(endingSong==null)
				endingSong=new MusicPlayer("Bill_Conti_-_Gonna_Fly_Now.wav");
			if(timerSong==null)
				timerSong=new MusicPlayer("single_round_no_music.wav");
		} catch (LineUnavailableException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (UnsupportedAudioFileException e2) {
			e2.printStackTrace();
		}
	}

	public static void resetTabata() {
		actionToDo=ActionEnum.RESET;
	}
	
	public static void resetRound() {
		actionToDo=ActionEnum.EXERCISE;
		roundReset=true;
	}
}
