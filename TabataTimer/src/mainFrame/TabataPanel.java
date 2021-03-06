package mainFrame;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public class TabataPanel extends JPanel{
	//Colors for gradient background
	private Color color1=Color.WHITE;
	private Color color2=Color.YELLOW;
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
        Graphics2D g2=(Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        int w=getWidth();
        int h=getHeight();
        //Creating gradient colour
	    GradientPaint gp=new GradientPaint(0, 0, color1, 0, h, color2);
	    g2.setPaint(gp);
	    //Drawing rect witch is in fact a background for frame
	    g2.fillRect(0, 0, w, h);
	}
	
	
	public void setColors(Color color1, Color color2) {
		this.color1=color1;
		this.color2=color2;
	}
	
	public String getColorsS() {
		return "Color1:"+color1.toString()+", Color2:"+color2.toString();
	}
	
}
