import java.awt.EventQueue;

import javax.swing.JFrame;

public class Main {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				SimpleFrame frame=new SimpleFrame(900,900);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});
	}
}


class SimpleFrame extends JFrame{
	
	public SimpleFrame(int width, int height) {
		setSize(width, height);
	}
}