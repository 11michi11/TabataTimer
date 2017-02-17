import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;
import javax.swing.JFrame;
import java.awt.*;
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
		
		JButton start=new JButton("Start");
		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				for(int i=0;i<5;i++) {
					frame.paintComponents(frame.getGraphics());
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch(InterruptedException e) {}
				}
			}
		});
		
		buttonPanel=new JPanel();
		buttonPanel.add(start);
		buttonPanel.add(new Countdown());
		add(buttonPanel);
		
		//pack();
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
	
	
}