import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import javax.swing.JComponent;

public class Countdown extends JComponent{
	private int sec;
	private static final int DEFAULT_WIDTH=800;
	private static final int DEFAULT_HEIGHT=300;
	
	public Countdown(int sec) {
		this.sec=sec;
	}
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2=(Graphics2D)g;
		Font sansbold250=new Font("SansSerif", Font.BOLD, 250);
		FontRenderContext context=g2.getFontRenderContext();
		Rectangle2D bounds=sansbold250.getStringBounds("00", context);
		double stringWidth=bounds.getWidth();
		double stringHeight=bounds.getHeight();
		double ascent=-bounds.getY();
		bounds.setRect((int)(getWidth()-stringWidth)/2, (int)(getHeight()-stringHeight)/2, stringWidth, stringHeight);
		g2.setFont(sansbold250);
		//g2.draw(bounds);
		g2.drawString(Integer.toString(sec), (int)(getWidth()-stringWidth)/2, (int) ((getHeight()-stringHeight)/2+ascent));
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(DEFAULT_WIDTH,DEFAULT_HEIGHT);
	}
	
	public void setSec(int x) {
		this.sec=x;
	}
	
	public void addSec(int x) {
		this.sec+=x;
	}
	
}