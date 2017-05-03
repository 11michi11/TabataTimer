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


/**
 * TabataTimer is exercise assistant program for tabata training.
 * It displays countdown for exercise and rest part, number of rounds and number of done tabata.
 * It also plays music for each round.
 * Training can be adjusted in settings menu. 
 * @author Micha³ Pompa
 * @version 1.9 
 */

public class Main extends JFrame {
	//Main panel for components used for Tabata functionality, like countdown and rounds
	private TabataPanel tabataPanel;
	//Variables used for playing music. They are used for storing last played frame of songs or timer sound.
	private int lastFrame;
	private int lastFrameT;
	//Clips for playing music
    private Clip currentClip;	//currently playing song
    private Clip timerClip; //Tabata timer sounds 
    private File audioFile;	//Temporary file used for loading songs as a file from resources. It is used by loadClip() for creating Clips 
    private FloatControl gainControl; // Variable responsible for volume control of currClip
    private JMenuBar menuBar;	//Menu bar for main frame
    private TabSetupDialog tabSetupDialog;	//Dialog responsible for showing setup dialog box
    private ArrayList<String> soundsNames=new ArrayList<String>(); //ArrayList witch contains names of all the songs used during the training
    private int musicIndx=0; //index of currently playing song in soudsNames
	final static Main frame=new Main(); //Main JFrame of application
	
	public Main() {
		
		//Loading names.txt form resources.
		//names.txt contains names of songs, which are used as background music during training. Names are separated by space
		InputStream namesStream=this.getClass().getClassLoader().getResourceAsStream("resources/names.txt");
		System.out.println(namesStream+"#"); //for debug, sometimes namesStream was null
		Scanner in=new Scanner(namesStream);
		
		
		//Loading songs names to ArrayList<String> soundsPaths
		while(in.hasNext()) {
			soundsNames.add(in.next());
		}
		
		//for debug, prints the content of ArrayList<String> soundsPaths to check if names loaded properly
		for(String e:soundsNames)
			System.out.println(e);
		
		
		try {
			//Loading song to a File audioFile from resources
			audioFile=new File(this.getClass().getClassLoader().getResource("resources/single_round_no_music.mp3").getFile());
			//Creating Clip timerClip and preparing it to be playable in loadClip() function
			timerClip=loadClip(audioFile,true);
			System.out.println(timerClip);	//for debug, to check if Clip timerClip was created properly
		} catch (LineUnavailableException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (UnsupportedAudioFileException e2) {
			e2.printStackTrace();
		}
		
		//Creating Toolkit kit to get screen size
		Toolkit kit=Toolkit.getDefaultToolkit();
		Dimension screenSize=kit.getScreenSize();
		int screenHeight=screenSize.height;
		int screenWidth=screenSize.width;
		//Setting size of the frame to square with a side of a half screen width
		setSize(screenWidth/2, screenWidth/2);
		//Setting frame relative location to null
		setLocationRelativeTo(null);
		
		//Creating TabataPanel tabataPanel. This is main panel of frame. 
		//It is responsible for displaying background.
		//It is also main panel for components witch are responsible for displaying countdown and rounds	
		tabataPanel=new TabataPanel();
		//Creating new Action startAction.
		//It is responsible for starting training by running countdown and music in new Thread Timer.
		//Can be started by button or pressing space key.
		Action startAction=new StartAction("Start");
		tabataPanel.add(new JButton(startAction));
		
		//Creating new component Countdown count. It is responsible for displaying counting down. 
		Countdown count=new Countdown(20);
		count.setOpaque(false);
		tabataPanel.add(count);
		
		//Creating new component Runds. It is responsible for displaying number of rounds and tabats.
		Runds runds=new Runds(8,2);
		runds.setOpaque(false);
		tabataPanel.add(runds);
		
		//Adding tabataPanel to frame
		add(tabataPanel);
		
		//Creating InputMap and ActionMap to enable staring by pressing space key
		InputMap imap=tabataPanel.getInputMap(JComponent.WHEN_FOCUSED);
		imap.put(KeyStroke.getKeyStroke("space"),"panel.start");
		ActionMap amap=tabataPanel.getActionMap();
		amap.put("panel.start", startAction);
		
		//Creating JMenuBar menuBar for frame, and adding menu elements
		menuBar=new JMenuBar();
		setJMenuBar(menuBar);
		//Settings element, for setting up training
		JMenu settingsMenu=new JMenu("Settings");
		menuBar.add(settingsMenu);
		//Adding setup for Tabata, it creates and shows dialog box to adjust amount of rounds or tabatas
		JMenuItem tabSetup=settingsMenu.add("Setup Tabata");
		tabSetup.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(tabSetupDialog==null	)
					tabSetupDialog=new TabSetupDialog(Main.this);
				tabSetupDialog.setVisible(true);
			}
		});
		
		//Adding stats menu, but it will be developed in the future
		
		JMenu statsMenu=new JMenu("Stats");
		menuBar.add(statsMenu);
		JMenuItem comingSoon=statsMenu.add("Coming Soon");
		
		//Adding help menu and its elements, 'About' and 'Examples'
		JMenu helpMenu=new JMenu("Help");
		menuBar.add(helpMenu);
		//Element 'About' shows dialog box that informs user about functionality of the program and explains basic usage
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
				frame.setTitle("TabataTimer - Alfa 1.9");
				frame.setVisible(true);
				
				JOptionPane.showMessageDialog(frame,
						"Welcom to Tabata Timer, your best training partner.\n"
						+ "To start or pause Tabata press SPACE.\n"
						+ "To setup your training, go to SETTINGS.\n"
						+ "If you get lost, go to HELP\n",
						"Start Messege", JOptionPane.PLAIN_MESSAGE);
				
			}
		});
	}
	
	//Function responsible for loading songs from File audioClip and creating and preparing Clip object
	public Clip loadClip(File audioFile, boolean timer) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
		Clip clip;
		System.out.println(audioFile.getName()+"!"); //for debug, prints name of currently used file
		//InputStream iStream=Main.class.getClassLoader().getResourceAsStream(audioFile.toString());
		//System.out.println(iStream+" istream");
		//AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
		//^ Próby zmuszenia odczytu z pliku jar do dzia³ani.
		//Próbowa³em zamiast przekazywaæ do fcji AudioSystem.getAudioInput() objektu File, przekazywaæ wczeœniej stworzony obiekt InputStream
		//Ale to te¿ nie dzia³a³o.
		
		//Creating and preparing Clip clip for being playable
		AudioInputStream audioStream=AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED,
				AudioSystem.getAudioInputStream(audioFile));
		//^ this make MP3 work, by specifying Encoding
        AudioFormat format=audioStream.getFormat();
        DataLine.Info info=new DataLine.Info(Clip.class, format);
        clip=(Clip)AudioSystem.getLine(info);
        clip.open(audioStream);
        if(!timer)
        	gainControl=(FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
        lastFrame=0;
        return clip;
    }
		
	//Function responsible for playing and resuming music
	public void playMusic(Clip clip) {
		//Case for currClip
		if(clip==currentClip) {
			//When lastFrame is greater than clip length in frames that means that clip was played through to the end and needs to by reseted
	        if (lastFrame<clip.getFrameLength()) {
	            clip.setFramePosition(lastFrame);
	        } else{
	            clip.setFramePosition(0);
	        }
		}else if(clip==timerClip){  //case for timerClip
			if (lastFrameT<clip.getFrameLength()) {
	            clip.setFramePosition(lastFrameT);
	        } else{
	            clip.setFramePosition(0);
	        }
		}
		
		//Staring playing clip
        clip.start();  
        System.out.println("Play:"+(clip==currentClip?"curr":"timer")); //for debug, prints currently playing clip
	}

	//Function responsible foe pausing music
	public void pauseMusic(Clip clip) {
		//Case for currClip
		if(clip==currentClip) {
			if (clip.isRunning()) {
				//Storing current frame position in lastFrame
	            lastFrame=clip.getFramePosition();
	            clip.stop();
	        }else {
	        	//When music isn't playing, prints that message 
	        	System.out.println("Music isn't playing");
	        }
		}else if(clip==timerClip){ //case for timerClip
			if (clip.isRunning()) {
				//Storing current frame position in lastFrame
	            lastFrameT=clip.getFramePosition();
	            clip.stop();
	        }else {
	        	//When music isn't playing, prints that message 
	        	System.out.println("Music isn't playing");
	        }
		}
		System.out.println("Pause:"+(clip==currentClip?"curr":"timer")); //for debug, print currently pausing clip
	}
		
	//Class responsible for dialog box used to set up Tabata
	public class TabSetupDialog extends JDialog{
		
		public TabSetupDialog(JFrame owner) {
			super(owner, "Tabata Setup", true);
			//Main panel for components
			JPanel tabPanel=new JPanel();
			//Component responsible for adjusting number of tabats in training 
			TabSetupComponent tabSetup=new TabSetupComponent();
			//Component responsible for adjusting number of rounds in tabata
			RundsSetupComponent rundsSetup=new RundsSetupComponent();
			
			//Button for decreasing tabats number
			JButton subT=new JButton("-");
			subT.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					((Runds)tabataPanel.getComponent(2)).addTabTotal(-1);
					repaint();
				}
			});
			
			//Button for increasing tabats number
			JButton addT=new JButton("+");
			addT.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					((Runds)tabataPanel.getComponent(2)).addTabTotal(1);
					repaint();
				}
			});
			
			//Button for decreasing number of rounds in tabata
			JButton subR=new JButton("-");
			subR.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					((Runds)tabataPanel.getComponent(2)).addTotalRounds(-1);
					repaint();
				}
			});
			
			//Button for increasing number of rounds in tabata
			JButton addR=new JButton("+");
			addR.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					((Runds)tabataPanel.getComponent(2)).addTotalRounds(1);
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
							.addComponent(ok)));
				
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
							.addComponent(addR)));
			
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
				g.drawString("Tabats:"+((Runds)tabataPanel.getComponent(2)).getTabTotal(), 0, 25);
			}
			
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
			}
		}
		
		//Class responsible for component to adjust number of rounds in tabata
		public class RundsSetupComponent extends JComponent{
			//Setting default width and height for getPrefferedSize()
			private static final int DEFAULT_WIDTH=115;
			private static final int DEFAULT_HEIGHT=25;
			
			//Creating new font
			//It should be dependent of the screen resolution
			@Override
			public void paintComponent(Graphics g) {
				Font sansbold25=new Font("SansSerif", Font.BOLD, 25);
				g.setFont(sansbold25);
				g.drawString("Runds:"+((Runds)tabataPanel.getComponent(2)).getTotalRounds(), 0, 25);
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
	
	
	//And there is where the HELL begins. This is rubbish and must by rewrited ASAP.
	//But it's working somehow, with bugs of course.
	//I really don't recommend looking into it, because it will cause headache. 
	//Timer thread is responsible for changing state of tabataPanel components. It is also responsible for the whole process of Tabata.
	//That means it plays and stops music when needed, changes background colour, changes countdown, rounds and tabats values.
	//I won't comment it, because when I was writing it, only I and God knows what is going on. Now, only God knows.
	//But it will be rewrtied so don't worry
	public class Timer implements Runnable{
		private int seconds;
		private boolean started=false;
		private boolean rest=false;
		private boolean before=false;
		private boolean played=true;
		private boolean reset=false;
		private boolean restRound=false;
		private boolean runed=false;
		private Random rn=new Random();
		
		public void run() {
			try{
				int rand;
				/*	WORK IN PROGRESS
				String actionToDo="";
				
					while(!Thread.currentThread().isInterrupted()) {
						switch(actionToDo) {
							case "reset":{
								((Runds)tabataPanel.getComponent(2)).setTab(0);
								lastFrameT=timerClip.getFrameLength();
								actionToDo="before";
								break;
							}
							case "before":{
								if(!runed) {
									seconds=10;
									runed=true;
								}
								
								tabataPanel.setColors(Color.WHITE, Color.BLUE);
								tabataPanel.repaint();
								
								playMusic(timerClip);
								
								if(played) {
									do {
										rand=rn.nextInt(soundsNames.size());
									}while(musicIndx==rand);
									musicIndx=rand;
									System.out.println(soundsNames.get(musicIndx));
									audioFile=new File(this.getClass().getClassLoader().getResource("resources/"+soundsNames.get(musicIndx)).getFile());
									currentClip=loadClip(audioFile, false);
									played=false;
								}
								
								gainControl.setValue(-15.0f);
								
								before=true;
								runed=false;
							}
					 }
				 }
				  
				  
				  
				 */
				while(!Thread.currentThread().isInterrupted()) {
					if(reset) {
						((Runds)tabataPanel.getComponent(2)).setTab(0);
						lastFrameT=timerClip.getFrameLength();
						reset=false;
					}
					if(!before) {
						System.out.println("1");
						playMusic(timerClip);
						if(!runed)
							seconds=10;
						runed=true;
						((Countdown) tabataPanel.getComponent(1)).setSec(seconds);
						tabataPanel.setColors(Color.WHITE, Color.BLUE);
						tabataPanel.repaint();
						if(played) {
							do {
								rand=rn.nextInt(soundsNames.size());
							}while(musicIndx==rand);
							musicIndx=rand;
							System.out.println(soundsNames.get(musicIndx));
							audioFile=new File(this.getClass().getClassLoader().getResource("resources/"+soundsNames.get(musicIndx)).getFile());
							currentClip=loadClip(audioFile, false);
							played=false;
						}
						
						gainControl.setValue(-15.0f);
						while(seconds>0) {
							if(seconds==5) 
								playMusic(currentClip);
							TimeUnit.SECONDS.sleep(1);
							seconds--;
							((Countdown) tabataPanel.getComponent(1)).addSec(-1);
							tabataPanel.getComponent(1).repaint();
							tabataPanel.getComponent(2).repaint();
						}	
						before=true;
						runed=false;
					}
					
					if(!started) {
						gainControl.setValue(5.0f);
						((Runds) tabataPanel.getComponent(2)).addRound(1);
						((Countdown) tabataPanel.getComponent(1)).setSec(20);
						seconds=20;
						started=true;
					}
					
					if(!timerClip.isRunning()&&!restRound) {
						playMusic(timerClip);
						System.out.println("3");
					}
					if(!rest) {
						if(!currentClip.isRunning()) 
							playMusic(currentClip);
						tabataPanel.setColors(Color.WHITE, Color.GREEN);
						tabataPanel.repaint();
						
						while(seconds>0) {
							TimeUnit.SECONDS.sleep(1);
							seconds--;
							((Countdown) tabataPanel.getComponent(1)).addSec(-1);
							tabataPanel.getComponent(1).repaint();
							tabataPanel.getComponent(2).repaint();
						}	
						pauseMusic(timerClip);
						lastFrameT=timerClip.getFrameLength();
						System.out.println("4");
						playMusic(timerClip);
						seconds=10;
						rest=true;
					}
					
					if(!currentClip.isRunning())
						playMusic(currentClip);
					if(!timerClip.isRunning()&&!restRound) {
						playMusic(timerClip);
						System.out.println("5");
					}
					if(((Runds)tabataPanel.getComponent(2)).getRound()==((Runds)tabataPanel.getComponent(2)).getTotalRounds()) 
						pauseMusic(timerClip);
					
					
					
					((Countdown) tabataPanel.getComponent(1)).setSec(seconds);
					tabataPanel.setColors(Color.WHITE, Color.RED);
					tabataPanel.repaint();
					gainControl.setValue(-15.0f);
					
					while(seconds>0) {	
						if(seconds==10&&restRound) {
							playMusic(timerClip);
							System.out.println("6");
						}
						if(!timerClip.isRunning()&&seconds<10&&restRound) {
							playMusic(timerClip);
							System.out.println("8");
						}
						if(seconds==5) {
							pauseMusic(currentClip);
							played=true;
							do {
								rand=rn.nextInt(soundsNames.size());
							}while(musicIndx==rand);
							musicIndx=rand;
							System.out.println(soundsNames.get(musicIndx));
							audioFile=new File(this.getClass().getClassLoader().getResource("resources/"+soundsNames.get(musicIndx)).getFile());
							currentClip=loadClip(audioFile, false);
							gainControl.setValue(-15.0f);
							playMusic(currentClip);
							played=false;
						}
							
						TimeUnit.SECONDS.sleep(1);
						seconds--;
						((Countdown) tabataPanel.getComponent(1)).addSec(-1);
						tabataPanel.getComponent(1).repaint();
						tabataPanel.getComponent(2).repaint();
					}
					if(((Runds)tabataPanel.getComponent(2)).getRound()==((Runds)tabataPanel.getComponent(2)).getTotalRounds()) 
						pauseMusic(timerClip);
					
					if(((Runds)tabataPanel.getComponent(2)).getRound()==((Runds)tabataPanel.getComponent(2)).getTotalRounds()) {
						restRound=true;
						((Runds) tabataPanel.getComponent(2)).addTab(1); 
						((Runds) tabataPanel.getComponent(2)).setRound(0);
						tabataPanel.setColors(Color.WHITE, Color.BLUE);
						tabataPanel.repaint();
		
						pauseMusic(timerClip);
						seconds=20;
						((Countdown) tabataPanel.getComponent(1)).setSec(seconds);
						while(seconds>0) {
							if(seconds==10&&restRound) {
								playMusic(timerClip);
								System.out.println("7");
							}
							if(seconds==5) {
								pauseMusic(currentClip);
								played=true;
								do {
									rand=rn.nextInt(soundsNames.size());
								}while(musicIndx==rand);
								musicIndx=rand;
								System.out.println(soundsNames.get(musicIndx));
								audioFile=new File(this.getClass().getClassLoader().getResource("resources/"+soundsNames.get(musicIndx)).getFile());
								currentClip=loadClip(audioFile, false);
								gainControl.setValue(-15.0f);
								playMusic(currentClip);
								played=false;
							}
							TimeUnit.SECONDS.sleep(1);
							seconds--;
							((Countdown) tabataPanel.getComponent(1)).addSec(-1);
							tabataPanel.getComponent(1).repaint();
							tabataPanel.getComponent(2).repaint();
						}	
						played=true;
						restRound=false;
					}else {
						restRound=false;
					}
					started=false;
					rest=false;
					
					if(((Runds)tabataPanel.getComponent(2)).getTab()==((Runds)tabataPanel.getComponent(2)).getTabTotal()) {
						pauseMusic(currentClip);
						pauseMusic(timerClip);
						tabataPanel.setColors(Color.WHITE, Color.BLUE);
						tabataPanel.repaint();
						reset=true;
						before=false;
						break;
					}
				}
			}catch(InterruptedException e) {
				pauseMusic(currentClip);
				pauseMusic(timerClip);
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