package mainFrame;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class TabSetupDialog extends JDialog{
	Rounds roundsComp;
	
	public TabSetupDialog(JFrame owner) {
		super(owner, "Tabata Setup", true);
		roundsComp=Main.getRoundsComponent();
		Preferences node=((Main) owner).getPreferencesNode();
		
		//Main panel for components
		JPanel tabPanel=new JPanel();
		//Component responsible for adjusting number of tabats in training 
		TabSetupComponent tabSetup=new TabSetupComponent();
		//Component responsible for adjusting number of rounds in tabata
		RoundsSetupComponent rundsSetup=new RoundsSetupComponent();
		
		//Button for decreasing tabats number
		JButton subT=new JButton("-");
		subT.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(roundsComp.getTabTotal()>1) {
					roundsComp.addTabTotal(-1);
				}else {
					JOptionPane.showMessageDialog(Main.tabSetupDialog, "Number of tabats must be grater than 0!");
				}
				repaint();
			}
		});
		
		//Button for increasing tabats number
		JButton addT=new JButton("+");
		addT.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				roundsComp.addTabTotal(1);
				repaint();
			}
		});
		
		//Button for decreasing number of rounds in tabata
		JButton subR=new JButton("-");
		subR.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(roundsComp.getTotalRounds()>1) {
					roundsComp.addTotalRounds(-1);
				}else {
					JOptionPane.showMessageDialog(Main.tabSetupDialog, "Number of rounds must be grater than 0!");
				}
				repaint();
			}
		});
		
		//Button for increasing number of rounds in tabata
		JButton addR=new JButton("+");
		addR.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				roundsComp.addTotalRounds(1);
				repaint();
			}
		});
		
		//Button for applying changes and repainting frame
		JButton ok=new JButton("OK");
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				owner.paintComponents(owner.getGraphics());
				setVisible(false);					
			}
		});
		
		
		//Button for saving settings in preferences
		JButton save=new JButton("Save");
		save.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				node.putInt("rounds", roundsComp.getTotalRounds());
				node.putInt("tabats", roundsComp.getTabTotal());					
			}
		});
		
		//Setting up GroupLayout for components in tabPanel
		GroupLayout layout=new GroupLayout(tabPanel);
		tabPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(tabSetup)
						.addComponent(subT)
						.addComponent(addT))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(rundsSetup)
						.addComponent(subR)
						.addComponent(addR))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(ok)
						.addComponent(save)));
			
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(tabSetup)
						.addComponent(rundsSetup))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(subT)
						.addComponent(subR)
						.addComponent(ok))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(addT)
						.addComponent(addR)
						.addComponent(save)));
		
		add(tabPanel);
		setLocationRelativeTo(owner);
		pack();
	}
	
	//Class responsible for component to adjust number of tabats in training 
	public class TabSetupComponent extends JComponent{
		//Setting default width and height for getPrefferedSize()
		private static final int DEFAULT_WIDTH=115;
		private static final int DEFAULT_HEIGHT=25;
		
		@Override
		public void paintComponent(Graphics g) {
			//Creating new font
			//It should be dependent of the screen resolution
			Font sansbold25=new Font("SansSerif", Font.BOLD, 25);
			g.setFont(sansbold25);
			g.drawString("Tabats:"+roundsComp.getTabTotal(), 0, 25);
		}
		
		@Override
		public Dimension getPreferredSize() {
			return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		}
	}
	
	//Class responsible for component to adjust number of rounds in tabata
	public class RoundsSetupComponent extends JComponent{
		//Setting default width and height for getPrefferedSize()
		private static final int DEFAULT_WIDTH=115;
		private static final int DEFAULT_HEIGHT=25;
		
		//Creating new font
		//It should be dependent of the screen resolution
		@Override
		public void paintComponent(Graphics g) {
			Font sansbold25=new Font("SansSerif", Font.BOLD, 25);
			g.setFont(sansbold25);
			g.drawString("Runds:"+roundsComp.getTotalRounds(), 0, 25);
		}
		
		@Override
		public Dimension getPreferredSize() {
			return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		}
	}
}
