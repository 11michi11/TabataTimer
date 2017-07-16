package mainFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import javax.swing.*;

import java.util.prefs.Preferences;


/**
 * TabataTimer is exercise assistant program for tabata training.
 * It displays countdown for exercise and rest part, number of rounds and number of done tabata.
 * It also plays music for each round.
 * Training can be adjusted in settings menu. 
 * @author Micha³ Pompa
 * @version Alpha 2.1 
 */

public class Main extends JFrame {
	public static TabataPanel tabataPanel;
	public static TabSetupDialog tabSetupDialog;	//Dialog responsible for showing tabata setup dialog box
	public final static Main frame=new Main(); //Main JFrame of application
    private JMenuBar menuBar;	//Menu bar for main frame
    private JButton startButton;
    private FontSizeSetupDialog fontSizeSetupDialog; //Dialog responsible for showing font size setup dialog box
	public final Preferences root=Preferences.userRoot();
	public final Preferences node=root.node("/TabataTimer");
	
	public Main() {
		setFrameSize();
		setLocationRelativeTo(null);
		
		//Creating TabataPanel tabataPanel. This is main panel of frame. 
		//It is responsible for displaying background.
		//It is also main panel for components witch are responsible for displaying countdown and rounds	
		tabataPanel=new TabataPanel();

		Action startAction=new StartAction("Start");
		setTabataPanelComponents(startAction);
		add(tabataPanel);
		
		setInputMap(startAction);
		
		menuBar=new JMenuBar();
		setJMenuBar(menuBar);
		addMenuBarItems(menuBar);
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("TabataTimer - Alfa 2.0");
				frame.setVisible(true);
				
				JOptionPane.showMessageDialog(frame,
						"Welcom to Tabata Timer, your best training partner.\n"
						+ "To start or pause Tabata press SPACE.\n"
						+ "To setup your training, go to SETTINGS.\n"
						+ "If you get lost, go to HELP\n"
						+ "It is recommended use in fullscreen mode\n",
						"Start Messege", JOptionPane.PLAIN_MESSAGE);
				
			}
		});
	}
	
	private void setTabataPanelComponents(Action startAction) {
		//Can be started by button or pressing space key.
		startButton=new JButton(startAction);
		tabataPanel.add(startButton);
		 
		Countdown count=new Countdown(20);
		count.setOpaque(false);
		tabataPanel.add(count);
		
		Rounds rounds=new Rounds(node.getInt("rounds", 8),node.getInt("tabats", 3));
		rounds.setOpaque(false);
		tabataPanel.add(rounds);
	}

	private void setInputMap(Action startAction) {
		InputMap imap=tabataPanel.getInputMap(JComponent.WHEN_FOCUSED);
		imap.put(KeyStroke.getKeyStroke("space"),"panel.start");
		ActionMap amap=tabataPanel.getActionMap();
		amap.put("panel.start", startAction);
	}
	
	private void setFrameSize() {
		Toolkit kit=Toolkit.getDefaultToolkit();
		Dimension screenSize=kit.getScreenSize();
		int screenHeight=screenSize.height;
		int screenWidth=screenSize.width;
		setSize(screenWidth/2, screenWidth/2);
	}
	
	private void addMenuBarItems(JMenuBar menuBar) {
		addSettingsMenu(menuBar);
		addStatsMenu(menuBar);
		addHelpMenu(menuBar);
	}
	
	private void addSettingsMenu(JMenuBar menuBar) {
		JMenu settingsMenu=new JMenu("Settings");
		menuBar.add(settingsMenu);
		JMenuItem tabSetup=settingsMenu.add("Setup Tabata");
		tabSetup.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(tabSetupDialog==null	)
					tabSetupDialog=new TabSetupDialog(Main.this);
				tabSetupDialog.setVisible(true);
			}
		});
		
		JMenuItem fontSizeSetup=settingsMenu.add("Change font size");
		fontSizeSetup.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(fontSizeSetupDialog==null)
					fontSizeSetupDialog=new FontSizeSetupDialog(Main.this);
				fontSizeSetupDialog.setVisible(true);
				
			}
		});
	}

	private void addStatsMenu(JMenuBar menuBar) {
		JMenu statsMenu=new JMenu("Stats");
		menuBar.add(statsMenu);
		JMenuItem comingSoon=statsMenu.add("Coming Soon");
	}

	private void addHelpMenu(JMenuBar menuBar) {
		JMenu helpMenu=new JMenu("Help");
		menuBar.add(helpMenu);
		JMenuItem aboutItem=helpMenu.add("About");
		aboutItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				JOptionPane.showMessageDialog(Main.this, 
						"This is bacis timer for Tabata.\n"
						+ "Tabata is made of 8 rounds,\n"
						+ "20 seconds of excercise and 10 seconds for rest.\n"
						+ "After each Tabata there is Rest Round\n"
						+ "(you can realax for 30 seconds).\n"
						+ "Number of rounds in each Tabata and number of Tabats\n"
						+ "in trening can by adjusted in SETTINGS -> Setup Tabata.\n"
						+ "For example excercises go to HELP -> EXAMPLES.\n\n"
						+ "Author: Micha³ Pompa\n"
						+ "Contact: 11michi11@gmail.com",
						"About", JOptionPane.PLAIN_MESSAGE);
			}
		});
		//Element 'Examples' shows dialog box with example of training 
		JMenuItem examplesItem=helpMenu.add("Examples");
		examplesItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(Main.this,
						"This is example set of excercises for 8 round Tabata:\n"
						+ "1. Burpees\n"
						+ "2. Squats with front kick\n"
						+ "3. Jumping Jacks\n"
						+ "4. Mountain Climbing\n"
						+ "5. Burpees\n"
						+ "6. Situps\n"
						+ "7. Jumping Jacks\n"
						+ "8. Shadowboxing" ,
						"Examples", JOptionPane.PLAIN_MESSAGE);
				
			}
		});
	}

	public static Countdown getCountdownComponent() {
		Component[] components=tabataPanel.getComponents();
		Countdown countComp=new Countdown(20);
		for(Component comp:components) 
			if(comp instanceof Countdown)
				countComp=(Countdown)comp;
		
		return countComp;
	}
		
	public static Rounds getRoundsComponent() {
		Component[] components=tabataPanel.getComponents();
		Rounds roundsComp=new Rounds(8,8);
		for(Component comp:components) 
			if(comp instanceof Rounds) 
				roundsComp=(Rounds)comp;
			
		return roundsComp;
	}
	
	public Preferences getPreferencesNode() {
		return node;
	}
		
	//Class responsible for Action. It's starting and interrupting Thread Timer when specific action is performed.
	//Those action are clicking the "Start" button and pressing 'SPACE' key
	public class StartAction extends AbstractAction{
		//When flag is true that means Timer must be started and when flag is false, it must be interrupted 
		private boolean flag=true;
		private Runnable r=new Timer();
		private Thread t;
		
		public StartAction(String name) {
			putValue(Action.NAME, name);
			putValue(Action.SHORT_DESCRIPTION, "Start countdown");
		}
		
		public void actionPerformed(ActionEvent event) {
			if(flag) {
				t=new Thread(r);
				t.start();
				//Changing start button description
				startButton.setText("Pause");
				flag=false;
			}else {
				//Changing start button description
				startButton.setText("Start");
				//Changing background color of tabataPanel
				tabataPanel.setColors(Color.WHITE, Color.BLUE);
				tabataPanel.repaint();
				//Interrupting Timer thread
				t.interrupt();
				flag=true;
			}
		}
	}
			
}