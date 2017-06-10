package mainFrame;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import javax.swing.JComponent;

public class Rounds extends JComponent{
	private int round=0;
	private int totalRounds;
	private int tabats=0;
	private int tabatsTotal;
	private int fontSize=80;
	
	private int DEFAULT_WIDTH=950;
	private int DEFAULT_HEIGHT=200;
	
	public Rounds(int totalRounds, int tabatsTotal) {
		this.totalRounds=totalRounds;
		this.tabatsTotal=tabatsTotal;
		Toolkit kit=Toolkit.getDefaultToolkit();
		Dimension screenSize=kit.getScreenSize();
		int screenWidth=screenSize.width;
		if(screenWidth!=1920) {
			fontSize=60;
			DEFAULT_WIDTH=800;
			DEFAULT_HEIGHT=150;
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2=(Graphics2D) g;
		//Font size should be dependent of screen resolution, 
				//because on res lower than Full HD this font is too big and doesn't fits in the window
		Font sansbold80=new Font("SansSerif", Font.BOLD, fontSize);
		FontRenderContext context=g2.getFontRenderContext();
		Rectangle2D bounds=sansbold80.getStringBounds("Runds:8/8 Tabaty:0/10", context);
		double stringWidth=bounds.getWidth();
		double stringHeight=bounds.getHeight();
		double ascent=-bounds.getY();
		bounds.setRect((int)(getWidth()-stringWidth)/2, (int)(getHeight()-stringHeight)/2, stringWidth, stringHeight);
		g2.setFont(sansbold80);
		//g2.draw(bounds);
		g2.drawString("Rund:"+round+"/"+totalRounds+" Tabaty:"+tabats+"/"+tabatsTotal, (int)(getWidth()-stringWidth)/2, (int) ((getHeight()-stringHeight)/2+ascent));
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
	
	public void setRound(int x) {
		this.round=x;
	}
	
	public void addRound(int x) {
		this.round+=x;
	}
	
	public int getRound() {
		return this.round;
	}
	
	
	public void setTotalRounds(int x) {
		this.totalRounds=x;
	}
	
	public int getTotalRounds() {
		return totalRounds;
	}
	
	public void addTabTotal(int x) {
		this.tabatsTotal+=x;
	}
	
	
	public void setTab(int x) {
		this.tabats=x;
	}
	
	public void addTab(int x) {
		this.tabats+=x; 
	}
	
	public int getTab() {
		return this.tabats;
	}
	
	
	public void setTabTotal(int x) {
		this.tabatsTotal=x;
	}
	
	public int getTabTotal() {
		return this.tabatsTotal;
	}
	
	public void addTotalRounds(int x) {
		this.totalRounds+=x;
	}
}
