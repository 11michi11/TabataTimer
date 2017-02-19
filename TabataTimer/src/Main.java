import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;
import javax.swing.JFrame;
import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.util.Timer;
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
		
		Action startAction=new StartAction("Start");
		buttonPanel=new JPanel();
		buttonPanel.add(new JButton(startAction));
		Countdown count=new Countdown(20);
		buttonPanel.add(count);
		buttonPanel.add(new Runds(8, 3));
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
		
		public StartAction(String name) {
			putValue(Action.NAME, name);
			putValue(Action.SHORT_DESCRIPTION, "Start countdown");
		}
		
		public void actionPerformed(ActionEvent event) {
			for(int i=0;i<2;i++) {
				Timer timer1=new Timer();
				Timer timer2=new Timer();
				Timer timer3=new Timer();
				((Runds) buttonPanel.getComponent(2)).addCurr(1);
				((Countdown) buttonPanel.getComponent(1)).setSec(20);
				timer1.schedule(new TimerTask() {
					private int counter=0;
					@Override
					public void run() {
						frame.paintComponents(frame.getGraphics());
						System.out.println("1:"+counter);
						if(++counter>20)
							timer1.cancel();
					}
				},0, 1000);
				timer2.schedule(new TimerTask() {
					@Override
					public void run() {
						((Countdown) buttonPanel.getComponent(1)).setSec(11);
					}
					
				}, 20*1000);
				timer3.schedule(new TimerTask() {
					private int counter=0;
					public void run() {
						frame.paintComponents(frame.getGraphics());
						System.out.println("2:"+counter);
						if(++counter>10)
							timer3.cancel();
					}
				},20*1000, 1000);
				try {
					TimeUnit.SECONDS.sleep(30);
				}catch (InterruptedException e) {}
			}
			((Runds) buttonPanel.getComponent(2)).addTab(1); 
			((Runds) buttonPanel.getComponent(2)).setCurr(0);
		}
	}
}