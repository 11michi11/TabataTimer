import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import javax.swing.JComponent;

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
		g.setFont(sansbold30);
		g2.draw(bounds);
		g.drawString("Seconds:"+sec, (int)(getWidth()-stringWidth)/2, (int) ((getHeight()-stringHeight)/2+ascent));
		if(sec>0) 
			sec--;
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(DEFAULT_WIDTH,DEFAULT_HEIGHT);
	}
	
}