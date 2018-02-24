package mainFrame;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import application.BackgroundIMG;
import application.Countdown;
import application.Main;
import application.Rounds;

//Timer thread is responsible for changing state of tabataPanel components. It is also responsible for the whole process of Tabata.
//That means it plays and stops music when needed, changes background colour, changes countdown, rounds and tabats values.
public class Timer implements Runnable {
    private static MusicPlayer currentSong;
    private static MusicPlayer timerSong;
    private static MusicPlayer endingSong;

    private int seconds;
    private boolean changeMusic;
    private boolean runed = false;
    private boolean playTimerClip;
    private boolean playCurrentClip;
    private boolean playEndingClip;
    private boolean paused = false;
    private boolean reset = false;
    private static boolean roundReset = false;
    private Countdown count;
    private Rounds rounds;

    // Enum for specific actions in tabata. Used to control program flow in switch
    private enum ActionEnum {
	RESET, BEFORE, EXERCISE, REST, RESTROUND, ENDROUND
    };

    private static ActionEnum actionToDo = ActionEnum.BEFORE;

    public Timer(Countdown count, Rounds rounds) {
	this.count = count;
	this.rounds = rounds;
    }

    public void run() {
	prepareMusic();
	try {
	    while (!Thread.currentThread().isInterrupted()) {
		if (paused)
		    prepareForActionAfterPause();
		else
		    prepareForAction();

		if (actionToDo == ActionEnum.ENDROUND)
		    handleEndroundActions();

		preformTimerActionForEachSecond();

		setNextActionAtTheEndOfRound();

	    }
	} catch (InterruptedException e) {
	    paused = true;
	    currentSong.pause();
	    timerSong.pause();
	    endingSong.pause();
	    System.out.println("Timer interrupted!");
	    Thread.currentThread().interrupt();
	}
    }

    private void preformTimerActionForEachSecond() throws InterruptedException {
	System.out.println(actionToDo + "in while");
	while (seconds > 0) {
	    TimeUnit.SECONDS.sleep(1);
	    seconds--;
	    count.addSec(-1);

	    if ((changeMusic && seconds == 5) || (actionToDo == ActionEnum.RESTROUND && seconds == 25)
		    || (actionToDo == ActionEnum.RESTROUND && seconds == 5)) {
		if (currentSong != null && currentSong.isRunning())
		    currentSong.pause();

		try {
		    currentSong = new MusicPlayer(currentSong.getNextSongName());
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
		    e.printStackTrace();
		}

		currentSong.setGainValue(-15.0f);
		currentSong.play();
		changeMusic = false;
	    }

	    if (actionToDo == ActionEnum.RESTROUND && seconds == 10)
		timerSong.play();

	}
    }

    private void handleEndroundActions() throws InterruptedException {
	if (endingSong != null && !endingSong.isRunning() && playEndingClip)
	    endingSong.play();
	TimeUnit.MILLISECONDS.sleep(500);
	System.out.println(reset);
	while (reset == false) {
	    if (!endingSong.isRunning()) {
		System.out.println("reset");
		reset = true;
		break;
	    } else if (Thread.currentThread().isInterrupted()) {
		System.out.println("reset");
		reset = true;
		endingSong.pause();
		break;
	    }
	    if (endingSong.getFramePosition() == endingSong.getFrameLenght())
		System.out.println("End");
	}
	System.out.println("Exit");
    }

    private void prepareForAction() {
	switch (actionToDo) {
	case RESET:
	    reset = false;
	    rounds.setRound(0);
	    rounds.setTab(0);
	    count.setSec(0);
	    timerSong.skipSongToEnd();
	    currentSong.skipSongToEnd();
	    seconds = count.getSec();
	    playCurrentClip = false;
	    break;
	case BEFORE:
	    rounds.setRound(0);
	    rounds.setTab(0);
	    timerSong.skipSongToEnd();
	    if (currentSong == null) {
		try {
		    currentSong = new MusicPlayer();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	    System.out.println(actionToDo.toString());
	    if (!runed) {
		count.setSec(10);
		seconds = count.getSec();
		runed = true;
	    }

	    Main.changeBackground(BackgroundIMG.MAIN);

	    timerSong.play();
	    playTimerClip = true;
	    changeMusic = true;
	    runed = false;

	    break;
	case EXERCISE:
	    currentSong.setGainValue(5.0f);
	    if (!roundReset)
		rounds.addRound(1);
	    else {
		currentSong.setSongOnFiveSeconds();
		timerSong.setSongOnTenSeconds();
		roundReset = false;
	    }

	    Main.changeBackground(BackgroundIMG.EXERCISE);
	    count.setSec(20);
	    seconds = count.getSec();
	    playCurrentClip = true;

	    break;
	case REST:
	    if (!currentSong.isRunning())
		currentSong.play();
	    Main.changeBackground(BackgroundIMG.REST);
	    count.setSec(10);
	    seconds = count.getSec();

	    changeMusic = true;
	    currentSong.setGainValue(-15.0f);
	    timerSong.pause();
	    timerSong.skipSongToEnd();
	    timerSong.play();

	    break;
	case RESTROUND:
	    timerSong.pause();

	    count.setSec(30);
	    seconds = count.getSec();
	    rounds.addTab(1);
	    rounds.setRound(0);
	    Main.changeBackground(BackgroundIMG.PAUSE);
	    playTimerClip = false;

	    changeMusic = true;
	    currentSong.setGainValue(-15.0f);

	    break;
	case ENDROUND:
	    rounds.addTab(1);
	    Main.changeBackground(BackgroundIMG.MAIN);
	    playEndingClip = true;
	    break;
	default:
	    System.out.println(actionToDo.toString());
	    System.out.println("Something wrong happend!!!");
	    break;
	}
    }

    private void prepareForActionAfterPause() {
	// Restore seconds state
	count.setSec(seconds);
	// Switch for actions
	switch (actionToDo) {
	case EXERCISE:
	    if (roundReset) {
		changeMusic = false;
		currentSong.setGainValue(5.0f);
		currentSong.setSongOnFiveSeconds();
		timerSong.setSongOnTenSeconds();
		count.setSec(20);
		seconds = count.getSec();
		roundReset = false;
	    }
	    Main.changeBackground(BackgroundIMG.EXERCISE);
	    break;
	case REST:
	    Main.changeBackground(BackgroundIMG.REST);
	    break;
	case BEFORE:
	    if (seconds <= 5)
		playCurrentClip = true;
	    Main.changeBackground(BackgroundIMG.MAIN);
	    break;
	case RESTROUND:
	    if (seconds <= 10)
		playTimerClip = true;
	    changeMusic = true;
	    Main.changeBackground(BackgroundIMG.PAUSE);
	    break;
	case ENDROUND:
	    Main.changeBackground(BackgroundIMG.MAIN);
	    playEndingClip = false;
	    reset = false;
	    actionToDo = ActionEnum.RESET;
	    break;
	case RESET:
	    rounds.setRound(0);
	    rounds.setTab(0);
	    timerSong.skipSongToEnd();
	    currentSong.skipSongToEnd();
	    count.setSec(0);
	    seconds = count.getSec();
	    playCurrentClip = false;
	    Main.changeBackground(BackgroundIMG.MAIN);
	    break;
	default:
	    System.out.println("Something wrong after paused!!!");
	    break;
	}

	checkIfMusicIsPlayingAndResumeIfNeeded();

	paused = false;
    }

    private void setNextActionAtTheEndOfRound() {
	switch (actionToDo) {
	case EXERCISE:
	    // check if current tabata is done and if yes, go to rest round
	    if (rounds.getRound() == rounds.getTotalRounds()) {
		// check if training is done and if yes, go to reset
		if (rounds.getTab() + 1 == rounds.getTabTotal()) {
		    currentSong.pause();
		    timerSong.pause();
		    Main.changeBackground(BackgroundIMG.MAIN);
		    actionToDo = ActionEnum.ENDROUND;
		    break;
		}

		Main.changeBackground(BackgroundIMG.EXERCISE);
		actionToDo = ActionEnum.RESTROUND;
		break;
	    }

	    actionToDo = ActionEnum.REST;
	    break;
	case REST:
	    actionToDo = ActionEnum.EXERCISE;
	    break;
	case BEFORE:
	    actionToDo = ActionEnum.EXERCISE;
	    break;
	case RESTROUND:
	    actionToDo = ActionEnum.EXERCISE;
	    break;
	case ENDROUND:
	    if (reset) {
		actionToDo = ActionEnum.RESET;
		playEndingClip = false;
		Thread.currentThread().interrupt();
		return; // from Thread
	    }
	    break;
	case RESET:
	    actionToDo = ActionEnum.BEFORE;
	    break;
	default:
	    System.out.println("Action error!!!");
	    break;
	}
    }

    private void checkIfMusicIsPlayingAndResumeIfNeeded() {
	if (timerSong != null && !timerSong.isRunning() && playTimerClip)
	    timerSong.play();
	if (currentSong != null && !currentSong.isRunning() && playCurrentClip)
	    currentSong.play();
	if (endingSong != null && !endingSong.isRunning() && playEndingClip)
	    endingSong.play();
    }

    private void prepareMusic() {
	try {
	    if (endingSong == null)
		endingSong = new MusicPlayer("Bill_Conti_-_Gonna_Fly_Now.wav");
	    if (timerSong == null)
		timerSong = new MusicPlayer("single_round_no_music.wav");
	} catch (LineUnavailableException e2) {
	    e2.printStackTrace();
	} catch (IOException e2) {
	    e2.printStackTrace();
	} catch (UnsupportedAudioFileException e2) {
	    e2.printStackTrace();
	}
    }

    public static void resetTabata() {
	actionToDo = ActionEnum.RESET;
    }

    public static void resetRound() {
	actionToDo = ActionEnum.EXERCISE;
	roundReset = true;
    }
}
