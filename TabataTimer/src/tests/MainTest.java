package tests;

import static org.junit.Assert.*;
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
	
	

}
