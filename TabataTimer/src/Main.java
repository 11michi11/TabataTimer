import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;
import javax.swing.JFrame;
import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				SimpleFrame frame=new SimpleFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("TabataTimer - Alfa 1.0");
				frame.setVisible(true);
				//Scanner in=new Scanner(System.in);
				int duration=5;
				for(int i=0;i<duration;i++) {
					frame.paintComponents(frame.getGraphics());
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch(InterruptedException e) {}
				}
			}
		});
	}
}


class SimpleFrame extends JFrame{
	
	
	public SimpleFrame() {
		/*Toolkit kit=Toolkit.getDefaultToolkit();
		Dimension screenSize=kit.getScreenSize();
		int screenHeight=screenSize.height;
		int screenWidth=screenSize.width;
		setSize(screenWidth/2, screenWidth/2);*/
		add(new Countdown());
		pack();
		setLocationRelativeTo(null);
	}
	
}


class Countdown extends JComponent{
	private int sec=5;
	private static final int DEFAULT_WIDTH=500;
	private static final int DEFAULT_HEIGHT=200;
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2=(Graphics2D)g;
		Font sansbold30=new Font("SansSerif",Font.BOLD,70);
		FontRenderContext context=g2.getFontRenderContext();
		Rectangle2D bounds=sansbold30.getStringBounds("Seconds:0", context);
		double stringWidth=bounds.getWidth();
		double stringHeight=bounds.getHeight();
		double ascent=-bounds.getY();
		bounds.setRect((int)(getWidth()-stringWidth)/2, (int)(getHeight()-stringHeight)/2, stringWidth, stringHeight);
		g2.draw(bounds);
		g.setFont(sansbold30);
		g.drawString("Seconds:"+sec, (int)(getWidth()-stringWidth)/2, (int) ((getHeight()-stringHeight)/2+ascent));
		sec--;
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(DEFAULT_WIDTH,DEFAULT_HEIGHT);
	}
}




