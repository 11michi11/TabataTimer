package mainFrame;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import javax.swing.JComponent;

public class Countdown extends JComponent{
	private int sec;
	private int fontSize=250;
	private int DEFAULT_WIDTH=800;
	private int DEFAULT_HEIGHT=300;
	
	public Countdown(int sec) {
		this.sec=sec;
		Toolkit kit=Toolkit.getDefaultToolkit();
		Dimension screenSize=kit.getScreenSize();
		int screenWidth=screenSize.width;
		
		if(screenWidth<1920) {
			fontSize=150;
			DEFAULT_WIDTH=500;
			DEFAULT_HEIGHT=150;
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2=(Graphics2D)g;
		//Font size should be dependent of screen resolution, 
		//because on res lower than Full HD this font is too big and doesn't fits in the window
		Font sansbold250=new Font("SansSerif", Font.BOLD, fontSize);
		FontRenderContext context=g2.getFontRenderContext();
		//Creating bounds rectangle for debugging purpose
		Rectangle2D bounds=sansbold250.getStringBounds("00", context);
		double stringWidth=bounds.getWidth();
		double stringHeight=bounds.getHeight();
		double ascent=-bounds.getY();
		bounds.setRect((int)(getWidth()-stringWidth)/2, (int)(getHeight()-stringHeight)/2, stringWidth, stringHeight);
		g2.setFont(sansbold250);
		//g2.draw(bounds);
		//Drawing countdown
		g2.drawString(Integer.toString(sec), (int)(getWidth()-stringWidth)/2, (int) ((getHeight()-stringHeight)/2+ascent));
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(DEFAULT_WIDTH,DEFAULT_HEIGHT);
	}
	
	public void setFontSize(int x) {
		this.fontSize=x;
	}
	
	public int getFontSize() {
		return this.fontSize;
	}
	
	public void setSec(int x) {
		this.sec=x;
	}
	
	public int getSec() {
		return this.sec;
	}
	
	public void addSec(int x) {
		this.sec+=x;
	}
	
}