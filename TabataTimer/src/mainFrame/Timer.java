package mainFrame;

import java.awt.Color;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import mainFrame.Main;



//Timer thread is responsible for changing state of tabataPanel components. It is also responsible for the whole process of Tabata.
//That means it plays and stops music when needed, changes background colour, changes countdown, rounds and tabats values.
public class Timer implements Runnable{
	private int seconds;
	private boolean changeMusic;
	private boolean runed=false;
	private boolean playTimerClip;
	private boolean playCurrentClip;
	private boolean playEndingClip;
	private boolean paused=false;
	private boolean reset=false;
	private Countdown countComp;
	private Rounds roundsComp;
	private ActionEnum actionToDo=ActionEnum.BEFORE;
	//Enum for specific actions in tabata. Used to control program flow in switch
	private enum ActionEnum {RESET, BEFORE, EXERCISE, REST, RESTROUND, ENDROUND};
	
	public void run() {
		countComp=Main.getCountdownComponent();
		roundsComp=Main.getRoundsComponent();
		try{
			while(!Thread.currentThread().isInterrupted()) {
				if(!paused) {
					switch(actionToDo) {
						case RESET:
							roundsComp.setRound(0);
							roundsComp.setTab(0);
							Main.timerSong.skipSongToEnd();;
							seconds=0;
							break;
						case BEFORE:
							roundsComp.setRound(0);
							roundsComp.setTab(0);
							Main.timerSong.skipSongToEnd();
							if(Main.currentSong==null) {
								try{
									Main.currentSong=new MusicPlayer();
								}catch (Exception e) {
									e.printStackTrace();
								}
							}
							System.out.println(actionToDo.toString());
							if(!runed) {
								seconds=10;
								countComp.setSec(seconds);
								runed=true;
							}
							
							Main.tabataPanel.setColors(Color.WHITE, Color.BLUE);
							Main.tabataPanel.repaint();
							
							Main.timerSong.play();
							playTimerClip=true;
							changeMusic=true;
							runed=false;
				
							break;
						case EXERCISE:
							Main.currentSong.setGainValue(5.0f);
							roundsComp.addRound(1);
							Main.tabataPanel.setColors(Color.WHITE, Color.GREEN);
							seconds=20;
							countComp.setSec(seconds);
							Main.tabataPanel.repaint();
							playCurrentClip=true;
							
							break;
						case REST:
							if(!Main.currentSong.isRunning()) 
								Main.currentSong.play();
							Main.tabataPanel.setColors(Color.WHITE, Color.RED);
							seconds=10;
							countComp.setSec(seconds);
							Main.tabataPanel.repaint();
							
							changeMusic=true;
							Main.currentSong.setGainValue(-15.0f);
							Main.timerSong.pause();
							Main.timerSong.skipSongToEnd();
							Main.timerSong.play();
							
							break;
						case RESTROUND:
							Main.timerSong.pause();
							
							seconds=30;
							countComp.setSec(seconds);
							roundsComp.addTab(1); 
							roundsComp.setRound(0);
							Main.tabataPanel.setColors(Color.WHITE, Color.BLUE);
							Main.tabataPanel.repaint();
							playTimerClip=false;
							
							changeMusic=true;
							Main.currentSong.setGainValue(-15.0f);
									
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
										
				//When resumed after pause those actions will be performed
				if(paused) {
					//Restore seconds state
					countComp.setSec(seconds);
					//Switch for actions
					switch(actionToDo) {
						case EXERCISE:
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
							roundsComp.addTab(1);
							Main.tabataPanel.setColors(Color.WHITE, Color.YELLOW);
							playEndingClip=true;
							break;
						case RESET:
							roundsComp.setRound(0);
							roundsComp.setTab(0);
							Main.timerSong.skipSongToEnd();
							seconds=0;
							Main.tabataPanel.setColors(Color.WHITE, Color.YELLOW);
							break;
						default:
							System.out.println("Something wrong after paused!!!");
							break;
					}
					Main.tabataPanel.repaint();
					
					if(Main.timerSong!=null&&!Main.timerSong.isRunning()&&playTimerClip)
						Main.timerSong.play();
					if(Main.currentSong!=null&&!Main.currentSong.isRunning()&&playCurrentClip)
						Main.currentSong.play();
					if(Main.endingSong!=null&&!Main.endingSong.isRunning()&&playEndingClip)
						Main.endingSong.play();
												
					paused=false;
				}
				if(Main.endingSong!=null&&!Main.endingSong.isRunning()&&playEndingClip)
					Main.endingSong.play();
				
				while(actionToDo==ActionEnum.ENDROUND) {
					if(!Main.endingSong.isRunning()) {
						actionToDo=ActionEnum.RESET;
						Thread.currentThread().interrupt();
					}
				}
										
				System.out.println(actionToDo+"in while");
				while(seconds>0) {
					TimeUnit.SECONDS.sleep(1);
					seconds--;
					countComp.addSec(-1);
					countComp.repaint();
					roundsComp.repaint();
					
					if((changeMusic&&seconds==5)||(actionToDo==ActionEnum.RESTROUND&&seconds==25)||(actionToDo==ActionEnum.RESTROUND&&seconds==5)) {
						if(Main.currentSong!=null&&Main.currentSong.isRunning())
							Main.currentSong.pause();
						
						Main.currentSong=new MusicPlayer(Main.currentSong.getNextSongName());
						Main.currentSong.setGainValue(-15.0f);
						Main.currentSong.play();
						changeMusic=false;
					}

					if(actionToDo==ActionEnum.RESTROUND&&seconds==10)
						Main.timerSong.play();

				}
				
				//choose next action
				switch(actionToDo) {
					case EXERCISE:
						//check if current tabata is done and if yes, go to rest round
						if(roundsComp.getRound()==roundsComp.getTotalRounds()) {
							//check if training is done and if yes, go to reset
							if(roundsComp.getTab()+1==roundsComp.getTabTotal()) {
								Main.currentSong.pause();
								Main.timerSong.pause();
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
						if(reset)
							actionToDo=ActionEnum.RESET;
						break;
					case RESET:
						actionToDo=ActionEnum.BEFORE;
						break;
					default:
						System.out.println("Action error!!!");
						break;
				}						
			}			
		}catch(InterruptedException e) {
			paused=true;
			Main.currentSong.pause();
			Main.timerSong.pause();
			System.out.println("Timer interrupted!");
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
	}
}
