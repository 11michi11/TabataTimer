package application;

import javafx.application.Platform;
import javafx.scene.control.Label;

public class Countdown{
    private int sec;
    private Label label;

    public Countdown(Label label) {
	this.sec = 20;
	this.label = label;
	setSec(sec);
    }

    public void setSec(int sec) {
	this.sec = sec;
	Platform.runLater(() -> {
	    label.setText(Integer.toString(sec));
	 });
    }

    public int getSec() {
	return this.sec;
    }

    public void addSec(int x) {
	this.sec += x;
	setSec(sec);
    }

}
