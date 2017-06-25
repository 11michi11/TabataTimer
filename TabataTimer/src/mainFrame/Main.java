package mainFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.io.*;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;


/**
 * TabataTimer is exercise assistant program for tabata training.
 * It displays countdown for exercise and rest part, number of rounds and number of done tabata.
 * It also plays music for each round.
 * Training can be adjusted in settings menu. 
 * @author Micha³ Pompa
 * @version Alpha 2.1 
 */

public class Main extends JFrame {
	private TabataPanel tabataPanel;
	
	//Clips for playing music
    private static MusicPlayer currentSong;	
    private static MusicPlayer timerSong; 
    private static MusicPlayer endingSong;  
    private JMenuBar menuBar;	//Menu bar for main frame
    private TabSetupDialog tabSetupDialog;	//Dialog responsible for showing tabata setup dialog box
    private FontSizeSetupDialog fontSizeSetupDialog; //Dialog responsible for showing font size setup dialog box
	final static Main frame=new Main(); //Main JFrame of application
	private final Preferences root=Preferences.userRoot();
	private final Preferences node=root.node("/TabataTimer");
	
	public Main() {
		
		try {
			endingSong=new MusicPlayer("Bill_Conti_-_Gonna_Fly_Now.wav");
			timerSong=new MusicPlayer("single_round_no_music.wav");
		} catch (LineUnavailableException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (UnsupportedAudioFileException e2) {
			e2.printStackTrace();
		}

		Toolkit kit=Toolkit.getDefaultToolkit();
		Dimension screenSize=kit.getScreenSize();
		int screenHeight=screenSize.height;
		int screenWidth=screenSize.width;
		setSize(screenWidth/2, screenWidth/2);
		setLocationRelativeTo(null);
		
		//Creating TabataPanel tabataPanel. This is main panel of frame. 
		//It is responsible for displaying background.
		//It is also main panel for components witch are responsible for displaying countdown and rounds	
		tabataPanel=new TabataPanel();

		//Can be started by button or pressing space key.
		Action startAction=new StartAction("Start");
		tabataPanel.add(new JButton(startAction));
		 
		Countdown count=new Countdown(20);
		count.setOpaque(false);
		tabataPanel.add(count);
		
		Rounds runds=new Rounds(node.getInt("rounds", 8),node.getInt("tabats", 3));
		runds.setOpaque(false);
		tabataPanel.add(runds);
		
		add(tabataPanel);
		
		InputMap imap=tabataPanel.getInputMap(JComponent.WHEN_FOCUSED);
		imap.put(KeyStroke.getKeyStroke("space"),"panel.start");
		ActionMap amap=tabataPanel.getActionMap();
		amap.put("panel.start", startAction);
		
		menuBar=new JMenuBar();
		setJMenuBar(menuBar);
		JMenu settingsMenu=new JMenu("Settings");
		menuBar.add(settingsMenu);
		JMenuItem tabSetup=settingsMenu.add("Setup Tabata");
		tabSetup.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(tabSetupDialog==null	)
					tabSetupDialog=new TabSetupDialog(Main.this);
				tabSetupDialog.setVisible(true);
			}
		});
		
		JMenuItem fontSizeSetup=settingsMenu.add("Change font size");
		fontSizeSetup.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(fontSizeSetupDialog==null)
					fontSizeSetupDialog=new FontSizeSetupDialog(Main.this);
				fontSizeSetupDialog.setVisible(true);
				
			}
		});
		
		
		JMenu statsMenu=new JMenu("Stats");
		menuBar.add(statsMenu);
		JMenuItem comingSoon=statsMenu.add("Coming Soon");
		
		JMenu helpMenu=new JMenu("Help");
		menuBar.add(helpMenu);
		JMenuItem aboutItem=helpMenu.add("About");
		aboutItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				JOptionPane.showMessageDialog(Main.this, 
						"This is bacis timer for Tabata.\n"
						+ "Tabata is made of 8 rounds,\n"
						+ "20 seconds of excercise and 10 seconds for rest.\n"
						+ "After each Tabata there is Rest Round\n"
						+ "(you can realax for 30 seconds).\n"
						+ "Number of rounds in each Tabata and number of Tabats\n"
						+ "in trening can by adjusted in SETTINGS -> Setup Tabata.\n"
						+ "For example excercises go to HELP -> EXAMPLES.\n\n"
						+ "Author: Micha³ Pompa\n"
						+ "Contact: 11michi11@gmail.com",
						"About", JOptionPane.PLAIN_MESSAGE);
			}
		});
		//Element 'Examples' shows dialog box with example of training 
		JMenuItem examplesItem=helpMenu.add("Examples");
		examplesItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(Main.this,
						"This is example set of excercises for 8 round Tabata:\n"
						+ "1. Burpees\n"
						+ "2. Squats with front kick\n"
						+ "3. Jumping Jacks\n"
						+ "4. Mountain Climbing\n"
						+ "5. Burpees\n"
						+ "6. Situps\n"
						+ "7. Jumping Jacks\n"
						+ "8. Shadowboxing" ,
						"Examples", JOptionPane.PLAIN_MESSAGE);
				
			}
		});
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("TabataTimer - Alfa 2.0");
				frame.setVisible(true);
				
				JOptionPane.showMessageDialog(frame,
						"Welcom to Tabata Timer, your best training partner.\n"
						+ "To start or pause Tabata press SPACE.\n"
						+ "To setup your training, go to SETTINGS.\n"
						+ "If you get lost, go to HELP\n"
						+ "It is recommended use in fullscreen mode\n",
						"Start Messege", JOptionPane.PLAIN_MESSAGE);
				
			}
		});
	}
	
	public class FontSizeSetupDialog extends JDialog{
		
		public FontSizeSetupDialog(JFrame owner){
			super(owner, "Font Size Setup", true);
			//Creating panel for components and labels
			JPanel fontPanel=new JPanel();
			JLabel countdownFontSizeLabel=new JLabel("Countdown font size: ");
			JLabel roundsFontSizeLabel=new JLabel("Rounds font size: ");
			
			//Setting font for labels
			countdownFontSizeLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
			roundsFontSizeLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
			
			//Creating texts fields for adjusting fonts size
			JTextField countdownTextField=new JTextField(Integer.toString(((Countdown) tabataPanel.getComponent(1)).getFontSize()), 2);
			JTextField roundsTextField=new JTextField(Integer.toString(((Rounds) tabataPanel.getComponent(2)).getFontSize()), 2);
			
			//Button for saving changes
			JButton save=new JButton("Save");
			save.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					String countdownSizeString=countdownTextField.getText().trim();
					String roundsSizeString=roundsTextField.getText().trim();
					
					((Countdown) tabataPanel.getComponent(1)).setFontSize(Integer.valueOf(countdownSizeString));
					((Rounds) tabataPanel.getComponent(2)).setFontSize(Integer.valueOf(roundsSizeString));
					node.putInt("countdownFontSize", Integer.valueOf(countdownSizeString));
					node.putInt("roundsFontSize", Integer.valueOf(roundsSizeString));
					setVisible(false);	
					frame.paintComponents(frame.getGraphics());
				}
			});
			
			//Button for restoring default settings
			JButton defaultSet=new JButton("Default");
			defaultSet.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					countdownTextField.setText("250");
					roundsTextField.setText("80");
					
					((Countdown) tabataPanel.getComponent(1)).setFontSize(250);
					((Rounds) tabataPanel.getComponent(2)).setFontSize(80);
					node.putInt("countdownFontSize", 250);
					node.putInt("roundsFontSize", 80);
				}
			});
			
			//Setting grid layout
			fontPanel.setLayout(new GridLayout(3, 2));
			
			fontPanel.add(countdownFontSizeLabel);
			fontPanel.add(countdownTextField);
			fontPanel.add(roundsFontSizeLabel);
			fontPanel.add(roundsTextField);
			fontPanel.add(defaultSet);
			fontPanel.add(save);
			
			add(fontPanel);
			setLocationRelativeTo(owner);
			pack();
		}
	}
		
	public class TabSetupDialog extends JDialog{
		
		public TabSetupDialog(JFrame owner) {
			super(owner, "Tabata Setup", true);
			//Main panel for components
			JPanel tabPanel=new JPanel();
			//Component responsible for adjusting number of tabats in training 
			TabSetupComponent tabSetup=new TabSetupComponent();
			//Component responsible for adjusting number of rounds in tabata
			RoundsSetupComponent rundsSetup=new RoundsSetupComponent();
			
			//Button for decreasing tabats number
			JButton subT=new JButton("-");
			subT.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if(((Rounds)tabataPanel.getComponent(2)).getTabTotal()>1) {
						((Rounds)tabataPanel.getComponent(2)).addTabTotal(-1);
					}else {
						JOptionPane.showMessageDialog(tabSetupDialog, "Number of tabats must be grater than 0!");
					}
					repaint();
				}
			});
			
			//Button for increasing tabats number
			JButton addT=new JButton("+");
			addT.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					((Rounds)tabataPanel.getComponent(2)).addTabTotal(1);
					repaint();
				}
			});
			
			//Button for decreasing number of rounds in tabata
			JButton subR=new JButton("-");
			subR.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if(((Rounds)tabataPanel.getComponent(2)).getTotalRounds()>1) {
						((Rounds)tabataPanel.getComponent(2)).addTotalRounds(-1);
					}else {
						JOptionPane.showMessageDialog(tabSetupDialog, "Number of rounds must be grater than 0!");
					}
					repaint();
				}
			});
			
			//Button for increasing number of rounds in tabata
			JButton addR=new JButton("+");
			addR.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					((Rounds)tabataPanel.getComponent(2)).addTotalRounds(1);
					repaint();
				}
			});
			
			//Button for applying changes and repainting frame
			JButton ok=new JButton("OK");
			ok.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					frame.paintComponents(frame.getGraphics());
					setVisible(false);					
				}
			});
			
			
			//Button for saving settings in preferences
			JButton save=new JButton("Save");
			save.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					node.putInt("rounds", ((Rounds)tabataPanel.getComponent(2)).getTotalRounds());
					node.putInt("tabats", ((Rounds)tabataPanel.getComponent(2)).getTabTotal());					
				}
			});
			
			//Setting up GroupLayout for components in tabPanel
			GroupLayout layout=new GroupLayout(tabPanel);
			tabPanel.setLayout(layout);
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(true);
			
			layout.setVerticalGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(tabSetup)
							.addComponent(subT)
							.addComponent(addT))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(rundsSetup)
							.addComponent(subR)
							.addComponent(addR))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
							.addComponent(ok)
							.addComponent(save)));
				
			layout.setHorizontalGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(tabSetup)
							.addComponent(rundsSetup))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(subT)
							.addComponent(subR)
							.addComponent(ok))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(addT)
							.addComponent(addR)
							.addComponent(save)));
			
			add(tabPanel);
			setLocationRelativeTo(owner);
			pack();
		}
		
		//Class responsible for component to adjust number of tabats in training 
		public class TabSetupComponent extends JComponent{
			//Setting default width and height for getPrefferedSize()
			private static final int DEFAULT_WIDTH=115;
			private static final int DEFAULT_HEIGHT=25;
			
			@Override
			public void paintComponent(Graphics g) {
				//Creating new font
				//It should be dependent of the screen resolution
				Font sansbold25=new Font("SansSerif", Font.BOLD, 25);
				g.setFont(sansbold25);
				g.drawString("Tabats:"+((Rounds)tabataPanel.getComponent(2)).getTabTotal(), 0, 25);
			}
			
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
			}
		}
		
		//Class responsible for component to adjust number of rounds in tabata
		public class RoundsSetupComponent extends JComponent{
			//Setting default width and height for getPrefferedSize()
			private static final int DEFAULT_WIDTH=115;
			private static final int DEFAULT_HEIGHT=25;
			
			//Creating new font
			//It should be dependent of the screen resolution
			@Override
			public void paintComponent(Graphics g) {
				Font sansbold25=new Font("SansSerif", Font.BOLD, 25);
				g.setFont(sansbold25);
				g.drawString("Runds:"+((Rounds)tabataPanel.getComponent(2)).getTotalRounds(), 0, 25);
			}
			
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
			}
		}
	}
	
	
	//Class responsible for Action. It's starting and interrupting Thread Timer when specific action is performed.
	//Those action are clicking the "Start" button and pressing 'SPACE' key
	public class StartAction extends AbstractAction{
		//When flag is true that means Timer must be started and when flag is false, it must be interpuped 
		private boolean flag=true;
		private Runnable r=new Timer();
		private Thread t;
		
		public StartAction(String name) {
			putValue(Action.NAME, name);
			putValue(Action.SHORT_DESCRIPTION, "Start countdown");
		}
		
		public void actionPerformed(ActionEvent event) {
			if(flag) {
				t=new Thread(r);
				t.start();
				//Changing start button description
				((JButton)tabataPanel.getComponent(0)).setText("Pause");
				flag=false;
			}else {
				//Changing start button description
				((JButton)tabataPanel.getComponent(0)).setText("Start");
				//Changing background color of tabataPanel
				tabataPanel.setColors(Color.WHITE, Color.BLUE);
				tabataPanel.repaint();
				//Interrupting Timer thread
				t.interrupt();
				flag=true;
			}
		}
	}
		
	//Enum for specific actions in tabata. Used to control program flow in switch
	public enum ActionEnum {RESET, BEFORE, EXERCISE, REST, RESTROUND, ENDROUND};

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
		private ActionEnum actionToDo=ActionEnum.BEFORE;
		
		public void run() {
			try{
				while(!Thread.currentThread().isInterrupted()) {
					if(!paused) {
						switch(actionToDo) {
							case RESET:
								((Rounds) tabataPanel.getComponent(2)).setRound(0);
								((Rounds)tabataPanel.getComponent(2)).setTab(0);
								timerSong.skipSongToEnd();;
								seconds=0;
								break;
							case BEFORE:
								((Rounds) tabataPanel.getComponent(2)).setRound(0);
								((Rounds)tabataPanel.getComponent(2)).setTab(0);
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
									seconds=10;
									((Countdown) tabataPanel.getComponent(1)).setSec(seconds);
									runed=true;
								}
								
								tabataPanel.setColors(Color.WHITE, Color.BLUE);
								tabataPanel.repaint();
								
								timerSong.play();
								playTimerClip=true;
								changeMusic=true;
								runed=false;
					
								break;
							case EXERCISE:
								currentSong.setGainValue(5.0f);
								((Rounds) tabataPanel.getComponent(2)).addRound(1);
								tabataPanel.setColors(Color.WHITE, Color.GREEN);
								seconds=20;
								((Countdown) tabataPanel.getComponent(1)).setSec(seconds);
								tabataPanel.repaint();
								playCurrentClip=true;
								
								break;
							case REST:
								if(!currentSong.isRunning()) 
									currentSong.play();
								tabataPanel.setColors(Color.WHITE, Color.RED);
								seconds=10;
								((Countdown) tabataPanel.getComponent(1)).setSec(seconds);
								tabataPanel.repaint();
								
								changeMusic=true;
								currentSong.setGainValue(-15.0f);
								timerSong.pause();
								timerSong.skipSongToEnd();
								timerSong.play();
								
								break;
							case RESTROUND:
								timerSong.pause();
								
								seconds=30;
								((Countdown) tabataPanel.getComponent(1)).setSec(seconds);
								((Rounds) tabataPanel.getComponent(2)).addTab(1); 
								((Rounds) tabataPanel.getComponent(2)).setRound(0);
								tabataPanel.setColors(Color.WHITE, Color.BLUE);
								tabataPanel.repaint();
								playTimerClip=false;
								
								changeMusic=true;
								currentSong.setGainValue(-15.0f);
										
								break;
							case ENDROUND:
								((Rounds) tabataPanel.getComponent(2)).addTab(1);
								tabataPanel.setColors(Color.WHITE, Color.YELLOW);
								tabataPanel.repaint();
								//actionToDo=ActionEnum.RESET;
								//Thread.currentThread().interrupt();
								//WORK IN PROGRESS - will be added later
								//play end music - Bill Conti - Gonna Fly Now, 
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
						((Countdown) tabataPanel.getComponent(1)).setSec(seconds);
						//Switch for actions
						switch(actionToDo) {
							case EXERCISE:
								tabataPanel.setColors(Color.WHITE, Color.GREEN);
								break;
							case REST:
								tabataPanel.setColors(Color.WHITE, Color.RED);
								break;
							case BEFORE:
								if(seconds<=5)
									playCurrentClip=true;
								tabataPanel.setColors(Color.WHITE, Color.BLUE);
								break;
							case RESTROUND:
								if(seconds<=10)
									playTimerClip=true;
								changeMusic=true;
								tabataPanel.setColors(Color.WHITE, Color.BLUE);
								break;
							case ENDROUND:
								((Rounds) tabataPanel.getComponent(2)).addTab(1);
								tabataPanel.setColors(Color.WHITE, Color.YELLOW);
								playEndingClip=true;
								break;
							case RESET:
								((Rounds) tabataPanel.getComponent(2)).setRound(0);
								((Rounds)tabataPanel.getComponent(2)).setTab(0);
								timerSong.skipSongToEnd();
								seconds=0;
								tabataPanel.setColors(Color.WHITE, Color.YELLOW);
								break;
							default:
								System.out.println("Something wrong after paused!!!");
								break;
						}
						tabataPanel.repaint();
						
						if(timerSong!=null&&!timerSong.isRunning()&&playTimerClip)
							timerSong.play();
						if(currentSong!=null&&!currentSong.isRunning()&&playCurrentClip)
							currentSong.play();
						if(endingSong!=null&&!endingSong.isRunning()&&playEndingClip)
							endingSong.play();
													
						paused=false;
					}
					
					while(actionToDo==ActionEnum.ENDROUND) {
						if(!endingSong.isRunning())
							Thread.currentThread().interrupt();
					}
											
					System.out.println(actionToDo+"in while");
					while(seconds>0) {
						TimeUnit.SECONDS.sleep(1);
						seconds--;
						((Countdown) tabataPanel.getComponent(1)).addSec(-1);
						tabataPanel.getComponent(1).repaint();
						tabataPanel.getComponent(2).repaint();
						
						if((changeMusic&&seconds==5)||(actionToDo==ActionEnum.RESTROUND&&seconds==25)||(actionToDo==ActionEnum.RESTROUND&&seconds==5)) {
							if(currentSong!=null&&currentSong.isRunning())
								currentSong.pause();
							
							currentSong=new MusicPlayer(currentSong.getNextSongName());
							currentSong.setGainValue(-15.0f);
							currentSong.play();
							changeMusic=false;
						}

						if(actionToDo==ActionEnum.RESTROUND&&seconds==10)
							timerSong.play();

					}
					
					//choose next action
					switch(actionToDo) {
						case EXERCISE:
							//check if current tabata is done and if yes, go to rest round
							if(((Rounds)tabataPanel.getComponent(2)).getRound()==((Rounds)tabataPanel.getComponent(2)).getTotalRounds()) {
								//check if training is done and if yes, go to reset
								if(((Rounds)tabataPanel.getComponent(2)).getTab()+1==((Rounds)tabataPanel.getComponent(2)).getTabTotal()) {
									currentSong.pause();
									timerSong.pause();
									tabataPanel.setColors(Color.WHITE, Color.YELLOW);
									tabataPanel.repaint();
									actionToDo=ActionEnum.ENDROUND;
									break;
								}
								
								tabataPanel.setColors(Color.WHITE, Color.BLUE);
								tabataPanel.repaint();
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
				currentSong.pause();
				timerSong.pause();
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
}