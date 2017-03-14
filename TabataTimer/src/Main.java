import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;
import javax.swing.JFrame;
import java.awt.*;
import java.io.*;
import java.net.URL;

import sun.audio.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main extends JFrame {
	private JPanel buttonPanel;
	final static Main frame=new Main();
	public Main() {
		
		Toolkit kit=Toolkit.getDefaultToolkit();
		Dimension screenSize=kit.getScreenSize();
		int screenHeight=screenSize.height;
		int screenWidth=screenSize.width;
		setSize(screenWidth/2, screenWidth/2);
		buttonPanel=new JPanel();
		
		Action startAction=new StartAction("Start");
		buttonPanel.add(new JButton(startAction));
		
		Countdown count=new Countdown(20);
		buttonPanel.add(count);
		buttonPanel.add(new Runds(2, 2));
		//buttonPanel.setBackground(Color.blue);
		add(buttonPanel);
		
		InputMap imap=buttonPanel.getInputMap(JComponent.WHEN_FOCUSED);
		imap.put(KeyStroke.getKeyStroke("space"),"panel.start");
		ActionMap amap=buttonPanel.getActionMap();
		amap.put("panel.start", startAction);
		setLocationRelativeTo(null);
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
	
	
	public class StartAction extends AbstractAction{
		private boolean flag=true;
		private Runnable r=new Timer();
		private Thread t;
		private Runnable ra=new Audio();
		private Thread ta;
		public StartAction(String name) {
			putValue(Action.NAME, name);
			putValue(Action.SHORT_DESCRIPTION, "Start countdown");
		}
		
		public void actionPerformed(ActionEvent event) {
			if(flag) {
				t=new Thread(r);
				t.start();
				ta=new Thread(ra);
				ta.start();
				((JButton)buttonPanel.getComponent(0)).setText("Pause");
				
				flag=false;
			}else {
				((JButton)buttonPanel.getComponent(0)).setText("Start");
				buttonPanel.setBackground(Color.BLUE);
				t.interrupt();
				ta.interrupt();
				flag=true;
			}
		}
	}
	
	public class Audio implements Runnable{
		private boolean started=false;
		public void run() {
			while(!Thread.currentThread().isInterrupted()) {
				if(!started) {
					String audioFilePath="C:/Users/Michi/Desktop/Programowanie/workspace/test/TNT_High_Quality.wav";
					AudioPlayer audio=new AudioPlayer();
					audio.play(audioFilePath);
					started=true;
				}
			}
		}
	}
	
	
	public class Timer implements Runnable{
		private int current;
		private boolean started=false;
		private boolean rest=false;
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
						buttonPanel.setBackground(Color.GREEN);
						while(current>0) {
							current--;
							TimeUnit.SECONDS.sleep(1);
							frame.paintComponents(frame.getGraphics());
							((Countdown) buttonPanel.getComponent(1)).addSec(-1);
						}	
						current=10;
					}
					((Countdown) buttonPanel.getComponent(1)).setSec(current);
					buttonPanel.setBackground(Color.RED);
					while(current>0) {
						current--;
						TimeUnit.SECONDS.sleep(1);
						frame.paintComponents(frame.getGraphics());
						((Countdown) buttonPanel.getComponent(1)).addSec(-1);
					}
					if(((Runds)buttonPanel.getComponent(2)).getCurr()==((Runds)buttonPanel.getComponent(2)).getTotal()) {
						((Runds) buttonPanel.getComponent(2)).addTab(1); 
						((Runds) buttonPanel.getComponent(2)).setCurr(0);
						frame.paintComponents(frame.getGraphics());
					}
					started=false;
					rest=false;
					if(((Runds)buttonPanel.getComponent(2)).getTabs()==((Runds)buttonPanel.getComponent(2)).getTabsTotal()) 
						break;
				}
			}catch(InterruptedException e) {
				System.out.println("Timer interrupted!");
			}
			
		}
	}	
}