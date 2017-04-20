import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
import java.util.stream.Stream;

public class Main extends JFrame {
	private TabataPanel buttonPanel;
	private JPanel test;
	private int lastFrame;
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
		//audioFile="C:/Users/Michi/Desktop/Programowanie/workspace/test/TNT_High_Quality.wav";
		//audioFile="D:/Muzyka/Blowing in the Wind - Bob Dylan.mp3";
		
		try(Stream<Path> paths = Files.walk(Paths.get("C:/Users/Michi/Desktop/Programowanie/Git/TabataTimer/Edited"))) {
		    paths.forEach(filePath -> {
		        if (Files.isRegularFile(filePath)) {
		            //System.out.println(filePath);
		            soundsPaths.add(filePath.toString());
		        }
		    });
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		for(String e:soundsPaths)
			System.out.println(e);
		
		Toolkit kit=Toolkit.getDefaultToolkit();
		Dimension screenSize=kit.getScreenSize();
		int screenHeight=screenSize.height;
		int screenWidth=screenSize.width;
		setSize(screenWidth/2, screenWidth/2);
		setLocationRelativeTo(null);
		
		buttonPanel=new TabataPanel();
		Action startAction=new StartAction("Start");
		buttonPanel.add(new JButton(startAction));
		
		Countdown count=new Countdown(20);
		count.setOpaque(false);
		buttonPanel.add(count);
		Runds runds=new Runds(2,2);
		runds.setOpaque(false);
		buttonPanel.add(runds);
		add(buttonPanel);
		
		InputMap imap=buttonPanel.getInputMap(JComponent.WHEN_FOCUSED);
		imap.put(KeyStroke.getKeyStroke("space"),"panel.start");
		ActionMap amap=buttonPanel.getActionMap();
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
		
		JMenu statsMenu=new JMenu("Stats");
		menuBar.add(statsMenu);
		JMenu helpMenu=new JMenu("Help");
		menuBar.add(helpMenu);
		JMenuItem aboutItem=helpMenu.add("About");
		aboutItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				JOptionPane.showMessageDialog(Main.this, "Test messege", "About", JOptionPane.PLAIN_MESSAGE);
			}
		});
		
	}
	
	public void loadClip(File audioFile) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
		//AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
		AudioInputStream audioStream=AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, AudioSystem.getAudioInputStream(audioFile));
		//^ this make MP3 work
        AudioFormat format=audioStream.getFormat();
        DataLine.Info info=new DataLine.Info(Clip.class, format);
        currClip=(Clip)AudioSystem.getLine(info);
        currClip.open(audioStream);
        gainControl=(FloatControl)currClip.getControl(FloatControl.Type.MASTER_GAIN);
        lastFrame=0;
    }
	
	public void playMusic() {
        if (lastFrame<currClip.getFrameLength()) {
            currClip.setFramePosition(lastFrame);
        } else{
            currClip.setFramePosition(0);
        }
        currClip.start();  
        System.out.println("Play");
	}
	
	public void pauseMusic() {
		if (currClip.isRunning()) {
            lastFrame=currClip.getFramePosition();
            currClip.stop();
        }else {
        	System.out.println("Music isn't playing");
        }
		System.out.println("Pause");
	}
	
	
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("TabataTimer - Alfa 1.0");
				frame.setVisible(true);
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
					((Runds)buttonPanel.getComponent(2)).addTabTotal(-1);
					repaint();
				}
			});
			
			JButton addT=new JButton("+");
			addT.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					((Runds)buttonPanel.getComponent(2)).addTabTotal(1);
					repaint();
				}
			});
			
			JButton subR=new JButton("-");
			subR.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					((Runds)buttonPanel.getComponent(2)).addTotal(-1);
					repaint();
				}
			});
			
			JButton addR=new JButton("+");
			addR.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					((Runds)buttonPanel.getComponent(2)).addTotal(1);
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
				g.drawString("Tabats:"+((Runds)buttonPanel.getComponent(2)).getTabsTotal(), 0, 25);
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
				g.drawString("Runds:"+((Runds)buttonPanel.getComponent(2)).getTotal(), 0, 25);
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
				((JButton)buttonPanel.getComponent(0)).setText("Pause");
				flag=false;
			}else {
				((JButton)buttonPanel.getComponent(0)).setText("Start");
				buttonPanel.setColors(Color.WHITE, Color.BLUE);
				buttonPanel.repaint();
				t.interrupt();
				flag=true;
			}
		}
	}
	
	
	public class Timer implements Runnable{
		private int current;
		private boolean started=false;
		private boolean rest=false;
		private boolean isMusic=true;
		private boolean before=false;
		private boolean played=true;
		private boolean reset=false;
		
		public void run() {
			try{
				while(!Thread.currentThread().isInterrupted()) {
					if(reset) {
						((Runds)buttonPanel.getComponent(2)).setTab(0);
						reset=false;
					}
					if(!before) {
						current=5;
						((Countdown) buttonPanel.getComponent(1)).setSec(5);
						buttonPanel.setColors(Color.WHITE, Color.BLUE);
						buttonPanel.repaint();
						if(played) {
							audioFile=new File(soundsPaths.get(musicIndx).toString());
							musicIndx++;
							loadClip(audioFile);
							played=false;
						}
						playMusic();
						gainControl.setValue(-15.0f);
						while(current>0) {
							TimeUnit.SECONDS.sleep(1);
							current--;
							((Countdown) buttonPanel.getComponent(1)).addSec(-1);
							buttonPanel.getComponent(1).repaint();
							buttonPanel.getComponent(2).repaint();
						}	
						before=true;
					}
					
					if(!started) {
						gainControl.setValue(6.0f);
						((Runds) buttonPanel.getComponent(2)).addCurr(1);
						((Countdown) buttonPanel.getComponent(1)).setSec(20);
						current=20;
						started=true;
					}
					
					if(!rest) {
						if(!currClip.isRunning())
							playMusic();
						buttonPanel.setColors(Color.WHITE, Color.GREEN);
						buttonPanel.repaint();
						
						//System.out.println("FP:"+currClip.getFramePosition());
						while(current>0) {
							TimeUnit.SECONDS.sleep(1);
							current--;
							((Countdown) buttonPanel.getComponent(1)).addSec(-1);
							buttonPanel.getComponent(1).repaint();
							buttonPanel.getComponent(2).repaint();
						}	
						current=10;
						rest=true;
					}
					
					if(!currClip.isRunning())
						playMusic();
					((Countdown) buttonPanel.getComponent(1)).setSec(current);
					buttonPanel.setColors(Color.WHITE, Color.RED);
					buttonPanel.repaint();
					gainControl.setValue(-15.0f);
					
					while(current>0) {
						if(current==5) {
							pauseMusic();
							played=true;
							audioFile=new File(soundsPaths.get(musicIndx).toString());
							musicIndx++;
							System.out.println(musicIndx);
							loadClip(audioFile);
							gainControl.setValue(-15.0f);
							playMusic();
							played=false;
						}
							
						TimeUnit.SECONDS.sleep(1);
						current--;
						((Countdown) buttonPanel.getComponent(1)).addSec(-1);
						buttonPanel.getComponent(1).repaint();
						buttonPanel.getComponent(2).repaint();
					}
					if(((Runds)buttonPanel.getComponent(2)).getCurr()==((Runds)buttonPanel.getComponent(2)).getTotal()) {
						((Runds) buttonPanel.getComponent(2)).addTab(1); 
						((Runds) buttonPanel.getComponent(2)).setCurr(0);
						//buttonPanel.getComponent(1).repaint();
						//buttonPanel.getComponent(2).repaint();
						buttonPanel.setColors(Color.WHITE, Color.BLUE);
						buttonPanel.repaint();
						
						current=20;
						((Countdown) buttonPanel.getComponent(1)).setSec(current);
						while(current>0) {
							if(current==5) {
								pauseMusic();
								played=true;
								audioFile=new File(soundsPaths.get(musicIndx).toString());
								musicIndx++;
								System.out.println(musicIndx);
								loadClip(audioFile);
								gainControl.setValue(-15.0f);
								playMusic();
								played=false;
							}
							TimeUnit.SECONDS.sleep(1);
							current--;
							((Countdown) buttonPanel.getComponent(1)).addSec(-1);
							buttonPanel.getComponent(1).repaint();
							buttonPanel.getComponent(2).repaint();
						}	
						played=true;
					}
					started=false;
					rest=false;
					
					if(((Runds)buttonPanel.getComponent(2)).getTabs()==((Runds)buttonPanel.getComponent(2)).getTabsTotal()) {
						pauseMusic();
						buttonPanel.setColors(Color.WHITE, Color.BLUE);
						buttonPanel.repaint();
						reset=true;
						break;
					}
				}
			}catch(InterruptedException e) {
				pauseMusic();
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