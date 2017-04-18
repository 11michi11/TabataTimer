import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import javax.swing.JComponent;

public class Runds extends JComponent{
	private int curr=0;
	private int tabats=0;
	private int tabatsTotal;
	private int total;
	private static final int DEFAULT_WIDTH=950;
	private static final int DEFAULT_HEIGHT=200;
	
	public Runds(int total, int tabatsTotal) {
		this.total=total;
		this.tabatsTotal=tabatsTotal;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2=(Graphics2D) g;
		Font sansbold80=new Font("SansSerif", Font.BOLD, 80);
		FontRenderContext context=g2.getFontRenderContext();
		Rectangle2D bounds=sansbold80.getStringBounds("Runds:8/8 Tabaty:0/10", context);
		double stringWidth=bounds.getWidth();
		double stringHeight=bounds.getHeight();
		double ascent=-bounds.getY();
		bounds.setRect((int)(getWidth()-stringWidth)/2, (int)(getHeight()-stringHeight)/2, stringWidth, stringHeight);
		g2.setFont(sansbold80);
		//g2.draw(bounds);
		g2.drawString("Rund:"+curr+"/"+total+" Tabaty:"+tabats+"/"+tabatsTotal, (int)(getWidth()-stringWidth)/2, (int) ((getHeight()-stringHeight)/2+ascent));
	}
	
	public void setCurr(int x) {
		this.curr=x;
	}
	
	public int getCurr() {
		return this.curr;
	}
	
	public void addCurr(int x) {
		this.curr+=x;
	}
	
	public void setTab(int x) {
		this.tabats=x;
	}
	
	public void addTab(int x) {
		this.tabats+=x; 
	}
	
	public int getTabs() {
		return this.tabats;
	}
	
	public int getTabsTotal() {
		return this.tabatsTotal;
	}
	
	public int getTotal() {
		return total;
	}
	
	public void addTabTotal(int x) {
		this.tabatsTotal+=x;
	}
	
	public void addTotal(int x) {
		this.total+=x;
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(DEFAULT_WIDTH,DEFAULT_HEIGHT);
	 }
}
