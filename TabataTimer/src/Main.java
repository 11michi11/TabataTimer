import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
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

public class Main extends JFrame {
	private JPanel buttonPanel;
	private JPanel test;
	private TabataPanel tabataPanel;
	private int lastFrame;
    private Clip clip;
    private String audioFile;
    private FloatControl gainControl;
    private JMenuBar menuBar;
    private TabSetupDialog tabSetupDialog;
	final static Main frame=new Main();
	
	public Main() {
		//audioFile="C:/Users/Michi/Desktop/Programowanie/workspace/test/TNT_High_Quality.wav";
		audioFile="D:/Muzyka/Blowing in the Wind - Bob Dylan.mp3";
		Toolkit kit=Toolkit.getDefaultToolkit();
		Dimension screenSize=kit.getScreenSize();
		int screenHeight=screenSize.height;
		int screenWidth=screenSize.width;
		setSize(screenWidth/2, screenWidth/2);
		
		tabataPanel=new TabataPanel();
		tabataPanel.setColors(Color.WHITE, Color.YELLOW);
		add(tabataPanel);
		
		buttonPanel=new JPanel();
		Action startAction=new StartAction("Start");
		buttonPanel.add(new JButton(startAction));
		
		Countdown count=new Countdown(20);
		count.setOpaque(false);
		buttonPanel.add(count);
		Runds runds=new Runds(2,2);
		runds.setOpaque(false);
		buttonPanel.add(runds);
		//buttonPanel.setBackground(Color.blue);
		add(buttonPanel);
		
		/*test=new JPanel();
		test.setOpaque(false);
		test.add(new JButton("Test"));
		test.add(new JButton("-"));
		test.add(new JButton("+"));
		add(test, BorderLayout.SOUTH);
		*/
		
		InputMap imap=buttonPanel.getInputMap(JComponent.WHEN_FOCUSED);
		imap.put(KeyStroke.getKeyStroke("space"),"panel.start");
		ActionMap amap=buttonPanel.getActionMap();
		amap.put("panel.start", startAction);
		setLocationRelativeTo(null);
		
		
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
	
	public void playMusic() {
		if (clip==null) {
            try {
                loadClip(new File(audioFile));
                clip.start();
            } catch (LineUnavailableException | IOException | UnsupportedAudioFileException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(Main.this, "Failed to load audio clip", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            if (clip.isRunning()) {
                lastFrame=clip.getFramePosition();
                clip.stop();
            } else {
                if (lastFrame<clip.getFrameLength()) {
                    clip.setFramePosition(lastFrame);
                } else{
                    clip.setFramePosition(0);
                }
                clip.start();
            }
        }
	}
	
	protected void loadClip(File audioFile) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
		//AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
		AudioInputStream audioStream=AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, AudioSystem.getAudioInputStream(audioFile));
		//^ this make MP3 work
        AudioFormat format=audioStream.getFormat();
        DataLine.Info info=new DataLine.Info(Clip.class, format);
        this.clip=(Clip)AudioSystem.getLine(info);
        this.clip.open(audioStream);
        gainControl=(FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
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
			//tabPanel.add(tabSetup);
			//add(tabPanel);
			
			JPanel tabSetButtonPanel=new JPanel();
			JButton subT=new JButton("-");
			subT.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					((Runds)buttonPanel.getComponent(2)).addTabTotal(-1);
					repaint();
				}
			});
			//tabSetButtonPanel.add(subT);
			
			JButton addT=new JButton("+");
			addT.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					((Runds)buttonPanel.getComponent(2)).addTabTotal(1);
					repaint();
				}
			});
			//tabSetButtonPanel.add(addT);
			
			JButton subR=new JButton("-");
			subR.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					((Runds)buttonPanel.getComponent(2)).addTotal(-1);
					repaint();
				}
			});
			//tabSetButtonPanel.add(subR);
			
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
			JPanel wrappingPanel=new JPanel();
			wrappingPanel.add(ok);
			//add(wrappingPanel);
			
			//tabSetButtonPanel.add(addR);
			//add(tabSetButtonPanel);
			GroupLayout layout=new GroupLayout(tabSetButtonPanel);
			tabSetButtonPanel.setLayout(layout);
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(true);
			
			layout.setHorizontalGroup(layout.createSequentialGroup()
					.addComponent(tabSetup)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(subT)
							.addComponent(subR))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
							.addComponent(addT)
							.addComponent(addR)
							.addComponent(ok)));
				
			layout.setVerticalGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE))
							.addComponent(tabSetup)
							.addComponent(subT)
							.addComponent(addT)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(subR)
							.addComponent(addR))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
							.addComponent(ok)));
			add(tabSetButtonPanel);
			
			
			setLocationRelativeTo(owner);
			//setSize(500,300);
			pack();
			
			
		}
		
		public class TabSetupComponent extends JComponent{
			private static final int DEFAULT_WIDTH=175;
			private static final int DEFAULT_HEIGHT=100;
			
			@Override
			public void paintComponent(Graphics g) {
				Graphics2D g2=(Graphics2D)g;
				Font sansbold25=new Font("SansSerif", Font.BOLD, 25);
				FontRenderContext context=g2.getFontRenderContext();
				Rectangle2D bounds=sansbold25.getStringBounds("Tabats:00", context);
				double stringWidth=bounds.getWidth();
				double stringHeight=bounds.getHeight();
				
			//	DEFAULT_WIDTH=(int)stringWidth;
			//	DEFAULT_HEIGHT=(int)stringHeight;
				g.setFont(sansbold25);
				g.drawString("Tabats:"+((Runds)buttonPanel.getComponent(2)).getTabsTotal(), 0, 25);
				g.drawString("Runds:"+((Runds)buttonPanel.getComponent(2)).getTotal(), 0, 50);
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
				tabataPanel.setColors(Color.WHITE, Color.BLUE);
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
		public void run() {
			try{
				while(!Thread.currentThread().isInterrupted()) {
					if(!started) {
						((Runds) buttonPanel.getComponent(2)).addCurr(1);
						((Countdown) buttonPanel.getComponent(1)).setSec(20);
						current=20;
						started=true;
					}
					
					if(!rest) {
						tabataPanel.setColors(Color.WHITE, Color.GREEN);
						//System.out.println(tabataPanel.getColorsS());
						tabataPanel.repaint();
						playMusic();
						while(current>0) {
							TimeUnit.SECONDS.sleep(1);
							current--;
							((Countdown) buttonPanel.getComponent(1)).addSec(-1);
							buttonPanel.getComponent(1).repaint();
							buttonPanel.getComponent(2).repaint();
							//frame.paintComponents(frame.getGraphics());
						}	
						current=10;
						rest=true;
					}
					
					((Countdown) buttonPanel.getComponent(1)).setSec(current);
					tabataPanel.setColors(Color.WHITE, Color.RED);
					tabataPanel.repaint();
					if(isMusic) {
						playMusic();
						gainControl.setValue(-15.0f);
						isMusic=false;
					}
					
					while(current>0) {
						TimeUnit.SECONDS.sleep(1);
						current--;
						((Countdown) buttonPanel.getComponent(1)).addSec(-1);
						buttonPanel.getComponent(1).repaint();
						buttonPanel.getComponent(2).repaint();
						//frame.paintComponents(frame.getGraphics());
					}
					if(((Runds)buttonPanel.getComponent(2)).getCurr()==((Runds)buttonPanel.getComponent(2)).getTotal()) {
						((Runds) buttonPanel.getComponent(2)).addTab(1); 
						((Runds) buttonPanel.getComponent(2)).setCurr(0);
						buttonPanel.getComponent(1).repaint();
						buttonPanel.getComponent(2).repaint();
						//frame.paintComponents(frame.getGraphics());
					}
					isMusic=true;
					started=false;
					rest=false;
					if(((Runds)buttonPanel.getComponent(2)).getTabs()==((Runds)buttonPanel.getComponent(2)).getTabsTotal()) 
						break;
				}
			}catch(InterruptedException e) {
				if(!rest) 
					playMusic();
				System.out.println("Timer interrupted!");
			}
			
		}
	}	
}