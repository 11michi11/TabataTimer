package application;

import javafx.application.Platform;
import javafx.scene.control.Label;

public class Rounds {
    private int round = 0;
    private int totalRounds;
    private int tabats = 0;
    private int tabatsTotal;
    private Label roundsLabel;
    private Label tabataLabel;

    public Rounds(int totalRounds, int tabatsTotal, Label roundsLabel, Label tabataLabel) {
	this.totalRounds = totalRounds;
	this.tabatsTotal = tabatsTotal;
	this.roundsLabel = roundsLabel;
	this.tabataLabel = tabataLabel;
	this.roundsLabel.setText("Round:" + round + "/" + totalRounds);
	this.tabataLabel.setText("Tabaty:" + tabats + "/" + tabatsTotal);
    }

    public int getRound() {
	return this.round;
    }

    public void setRound(int x) {
	this.round = x;
	setText();
    }

    public void addRound(int x) {
	this.round += x;
	setText();
    }

    public void setTotalRounds(int x) {
	this.totalRounds = x;
	setText();
    }

    public void addTotalRounds(int x) {
	this.totalRounds += x;
	setText();
    }

    public int getTotalRounds() {
	return totalRounds;
    }

    public void addTabTotal(int x) {
	this.tabatsTotal += x;
	setText();
    }

    public void setTab(int x) {
	this.tabats = x;
	setText();
    }

    public void addTab(int x) {
	this.tabats += x;
	setText();
    }

    public int getTab() {
	return this.tabats;
    }

    public void setTabTotal(int x) {
	this.tabatsTotal = x;
	setText();
    }

    public int getTabTotal() {
	return this.tabatsTotal;
    }

    private void setText() {
	Platform.runLater(() -> {
	    this.roundsLabel.setText("Round:" + round + "/" + totalRounds);
	    this.tabataLabel.setText("Tabats:" + tabats + "/" + tabatsTotal);
	});
    }

}
