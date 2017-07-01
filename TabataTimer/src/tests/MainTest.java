package tests;

import static org.junit.Assert.*;
import java.util.prefs.Preferences;
import javax.swing.JButton;
import org.junit.Test;
import mainFrame.Countdown;
import mainFrame.Main;
import mainFrame.Rounds;

public class MainTest {

	@Test
	public void testGetCountdownComponent() {
		Countdown countComp=Main.getCountdownComponent();
		assertNotNull(countComp);
		assertTrue(Main.tabataPanel.isAncestorOf(countComp));
	}
	
	@Test
	public void testGetRoundsComponent() {
		Rounds roundsComp=Main.getRoundsComponent();
		assertNotNull(roundsComp);
		assertTrue(Main.tabataPanel.isAncestorOf(roundsComp));
	}
	
	@Test
	public void testGetPreferenceNode() {
		Main frame=new Main();
		Preferences node=frame.getPreferencesNode();
		assertNotNull(node);
	}
	
	@Test
	public void testGetStartButton() {
		Main frame=new Main();
		JButton startBtn=frame.getStartButton();
		assertNotNull(startBtn);
		assertTrue(Main.tabataPanel.isAncestorOf(startBtn));
	}

}
