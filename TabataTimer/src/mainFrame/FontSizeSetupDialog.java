package mainFrame;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class FontSizeSetupDialog extends JDialog{
	
	public FontSizeSetupDialog(JFrame owner){
		super(owner, "Font Size Setup", true);
		//Creating panel for components and labels
		JPanel fontPanel=new JPanel();
		JLabel countdownFontSizeLabel=new JLabel("Countdown font size: ");
		JLabel roundsFontSizeLabel=new JLabel("Rounds font size: ");
		Preferences node=((Main) owner).getPreferencesNode();
		
		//Setting font for labels
		countdownFontSizeLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
		roundsFontSizeLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
		
		//Creating texts fields for adjusting fonts size
		Countdown countComp=Main.getCountdownComponent();
		Rounds roundsComp=Main.getRoundsComponent();
		JTextField countdownTextField=new JTextField(Integer.toString(countComp.getFontSize()), 2);
		JTextField roundsTextField=new JTextField(Integer.toString(roundsComp.getFontSize()), 2);
		
		//Button for saving changes
		JButton save=new JButton("Save");
		save.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String countdownSizeString=countdownTextField.getText().trim();
				String roundsSizeString=roundsTextField.getText().trim();
				
				countComp.setFontSize(Integer.valueOf(countdownSizeString));
				roundsComp.setFontSize(Integer.valueOf(roundsSizeString));
				node.putInt("countdownFontSize", Integer.valueOf(countdownSizeString));
				node.putInt("roundsFontSize", Integer.valueOf(roundsSizeString));
				setVisible(false);	
				owner.paintComponents(owner.getGraphics());
			}
		});
		
		//Button for restoring default settings
		JButton defaultSet=new JButton("Default");
		defaultSet.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				countdownTextField.setText("250");
				roundsTextField.setText("80");
				
				countComp.setFontSize(250);
				roundsComp.setFontSize(80);
				node.putInt("countdownFontSize", 250);
				node.putInt("roundsFontSize", 80);
			}
		});
		
		//Setting grid layout
		fontPanel.setLayout(new GridLayout(3, 2));
		
		fontPanel.add(countdownFontSizeLabel);
		fontPanel.add(countdownTextField);
		fontPanel.add(roundsFontSizeLabel);
		fontPanel.add(roundsTextField);
		fontPanel.add(defaultSet);
		fontPanel.add(save);
		
		add(fontPanel);
		setLocationRelativeTo(owner);
		pack();
	}
}
