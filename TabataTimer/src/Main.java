import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;
import java.awt.*;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import sun.audio.*;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class Main extends JFrame {
	private TabataPanel tabataPanel;
	private JPanel test;
	private int lastFrame;
	private int lastFrameT;
    private Clip currClip;
    private Clip timerClip;
    private File audioFile;
    private FloatControl gainControl;
    private JMenuBar menuBar;
    private TabSetupDialog tabSetupDialog;
    private ArrayList<String> soundsPaths=new ArrayList<String>();
    private int musicIndx=0;
	final static Main frame=new Main();
	
	public Main() {
		
		//Loading names.txt form resources.
		//names.txt contains names of songs, which are used as background music during training. Names are separated by space
		InputStream namesStream=this.getClass().getClassLoader().getResourceAsStream("resources/names.txt");
		System.out.println(namesStream+"#"); //for debug, sometimes namesStream was null
		Scanner in=new Scanner(namesStream);
		
		
		//Loading songs names to ArrayList<String> soundsPaths
		while(in.hasNext()) {
			soundsPaths.add(in.next());
		}
		
		//for debug, prints the content of ArrayList<String> soundsPaths to check if names loaded properly
		for(String e:soundsPaths)
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
		Runds runds=new Runds(2,2);
		runds.setOpaque(false);
		tabataPanel.add(runds);
		
		//Adding tabataPanel to frame
		add(tabataPanel);
		
		//Creating InputMap and ActionMap to enable staring by pressing space key
		InputMap imap=tabataPanel.getInputMap(JComponent.WHEN_FOCUSED);
		imap.put(KeyStroke.getKeyStroke("space"),"panel.start");
		ActionMap amap=tabataPanel.getActionMap();
		amap.put("panel.start", startAction);
		
		//Creating JMenuBar menuBar for frame, and adding 
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
	
	public Clip loadClip(File audioFile, boolean timer) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
		Clip clip;
		System.out.println(audioFile.getName()+"!");
		//InputStream iStream=Main.class.getClassLoader().getResourceAsStream(audioFile.toString());
		//System.out.println(iStream+" istream");
		//AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
		AudioInputStream audioStream=AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED,
				AudioSystem.getAudioInputStream(audioFile));
		//^ this make MP3 work
        AudioFormat format=audioStream.getFormat();
        DataLine.Info info=new DataLine.Info(Clip.class, format);
        clip=(Clip)AudioSystem.getLine(info);
        clip.open(audioStream);
        if(!timer)
        	gainControl=(FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
        lastFrame=0;
        return clip;
    }
	
	public void playMusic(Clip clip) {
		if(clip==currClip) {
	        if (lastFrame<clip.getFrameLength()) {
	            clip.setFramePosition(lastFrame);
	        } else{
	            clip.setFramePosition(0);
	        }
		}else if(clip==timerClip){
			if (lastFrameT<clip.getFrameLength()) {
	            clip.setFramePosition(lastFrameT);
	        } else{
	            clip.setFramePosition(0);
	        }
		}
        clip.start();  
        System.out.println("Play:"+(clip==currClip?"curr":"timer"));
	}
	
	public void pauseMusic(Clip clip) {
		if(clip==currClip) {
			if (clip.isRunning()) {
	            lastFrame=clip.getFramePosition();
	            clip.stop();
	        }else {
	        	System.out.println("Music isn't playing");
	        }
		}else if(clip==timerClip){
			if (clip.isRunning()) {
	            lastFrameT=clip.getFramePosition();
	            clip.stop();
	        }else {
	        	System.out.println("Music isn't playing");
	        }
		}
		System.out.println("Pause:"+(clip==currClip?"curr":"timer"));
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
	
	public class TabSetupDialog extends JDialog{
		
		public TabSetupDialog(JFrame owner) {
			super(owner, "Tabata Setup", true);
			JPanel tabPanel=new JPanel();
			TabSetupComponent tabSetup=new TabSetupComponent();
			RundsSetupComponent rundsSetup=new RundsSetupComponent();
			
			JButton subT=new JButton("-");
			subT.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					((Runds)tabataPanel.getComponent(2)).addTabTotal(-1);
					repaint();
				}
			});
			
			JButton addT=new JButton("+");
			addT.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					((Runds)tabataPanel.getComponent(2)).addTabTotal(1);
					repaint();
				}
			});
			
			JButton subR=new JButton("-");
			subR.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					((Runds)tabataPanel.getComponent(2)).addTotalRounds(-1);
					repaint();
				}
			});
			
			JButton addR=new JButton("+");
			addR.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					((Runds)tabataPanel.getComponent(2)).addTotalRounds(1);
					repaint();
				}
			});
			
			JButton ok=new JButton("OK");
			ok.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					frame.paintComponents(frame.getGraphics());
					setVisible(false);					
				}
			});
			
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
		
		public class TabSetupComponent extends JComponent{
			private static final int DEFAULT_WIDTH=100;
			private static final int DEFAULT_HEIGHT=25;
			
			@Override
			public void paintComponent(Graphics g) {
				Font sansbold25=new Font("SansSerif", Font.BOLD, 25);
				g.setFont(sansbold25);
				g.drawString("Tabats:"+((Runds)tabataPanel.getComponent(2)).getTabTotal(), 0, 25);
			}
			
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
			}
		}
		
		public class RundsSetupComponent extends JComponent{
			private static final int DEFAULT_WIDTH=100;
			private static final int DEFAULT_HEIGHT=25;
			
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
	
	public class StartAction extends AbstractAction{
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
				((JButton)tabataPanel.getComponent(0)).setText("Pause");
				flag=false;
			}else {
				((JButton)tabataPanel.getComponent(0)).setText("Start");
				tabataPanel.setColors(Color.WHITE, Color.BLUE);
				tabataPanel.repaint();
				t.interrupt();
				flag=true;
			}
		}
	}
	
	public class Timer implements Runnable{
		private int current;
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
							current=10;
						runed=true;
						((Countdown) tabataPanel.getComponent(1)).setSec(current);
						tabataPanel.setColors(Color.WHITE, Color.BLUE);
						tabataPanel.repaint();
						if(played) {
							do {
								rand=rn.nextInt(soundsPaths.size());
							}while(musicIndx==rand);
							musicIndx=rand;
							System.out.println(soundsPaths.get(musicIndx));
							audioFile=new File(this.getClass().getClassLoader().getResource("resources/"+soundsPaths.get(musicIndx)).getFile());
							currClip=loadClip(audioFile, false);
							played=false;
						}
						
						gainControl.setValue(-15.0f);
						while(current>0) {
							if(current==5) 
								playMusic(currClip);
							TimeUnit.SECONDS.sleep(1);
							current--;
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
						current=20;
						started=true;
					}
					
					if(!timerClip.isRunning()&&!restRound) {
						playMusic(timerClip);
						System.out.println("3");
					}
					if(!rest) {
						if(!currClip.isRunning()) 
							playMusic(currClip);
						tabataPanel.setColors(Color.WHITE, Color.GREEN);
						tabataPanel.repaint();
						
						while(current>0) {
							TimeUnit.SECONDS.sleep(1);
							current--;
							((Countdown) tabataPanel.getComponent(1)).addSec(-1);
							tabataPanel.getComponent(1).repaint();
							tabataPanel.getComponent(2).repaint();
						}	
						pauseMusic(timerClip);
						lastFrameT=timerClip.getFrameLength();
						System.out.println("4");
						playMusic(timerClip);
						current=10;
						rest=true;
					}
					
					if(!currClip.isRunning())
						playMusic(currClip);
					if(!timerClip.isRunning()&&!restRound) {
						playMusic(timerClip);
						System.out.println("5");
					}
					if(((Runds)tabataPanel.getComponent(2)).getRound()==((Runds)tabataPanel.getComponent(2)).getTotalRounds()) 
						pauseMusic(timerClip);
					
					
					
					((Countdown) tabataPanel.getComponent(1)).setSec(current);
					tabataPanel.setColors(Color.WHITE, Color.RED);
					tabataPanel.repaint();
					gainControl.setValue(-15.0f);
					
					while(current>0) {	
						if(current==10&&restRound) {
							playMusic(timerClip);
							System.out.println("6");
						}
						if(!timerClip.isRunning()&&current<10&&restRound) {
							playMusic(timerClip);
							System.out.println("8");
						}
						if(current==5) {
							pauseMusic(currClip);
							played=true;
							do {
								rand=rn.nextInt(soundsPaths.size());
							}while(musicIndx==rand);
							musicIndx=rand;
							audioFile=new File(this.getClass().getClassLoader().getResource("resources/"+soundsPaths.get(musicIndx)).getFile());
							currClip=loadClip(audioFile, false);
							gainControl.setValue(-15.0f);
							playMusic(currClip);
							played=false;
						}
							
						TimeUnit.SECONDS.sleep(1);
						current--;
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
						current=20;
						((Countdown) tabataPanel.getComponent(1)).setSec(current);
						while(current>0) {
							if(current==10&&restRound) {
								playMusic(timerClip);
								System.out.println("7");
							}
							if(current==5) {
								pauseMusic(currClip);
								played=true;
								do {
									rand=rn.nextInt(soundsPaths.size());
								}while(musicIndx==rand);
								musicIndx=rand;
								audioFile=new File(this.getClass().getClassLoader().getResource("resources/"+soundsPaths.get(musicIndx)).getFile());
								currClip=loadClip(audioFile, false);
								gainControl.setValue(-15.0f);
								playMusic(currClip);
								played=false;
							}
							TimeUnit.SECONDS.sleep(1);
							current--;
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
						pauseMusic(currClip);
						pauseMusic(timerClip);
						tabataPanel.setColors(Color.WHITE, Color.BLUE);
						tabataPanel.repaint();
						reset=true;
						before=false;
						break;
					}
				}
			}catch(InterruptedException e) {
				pauseMusic(currClip);
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