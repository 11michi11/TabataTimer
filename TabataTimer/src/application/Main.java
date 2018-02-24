package application;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import mainFrame.Timer;

public class Main extends Application {

    private Button startButton;
    private Button restartTabataButton;
    private Button restartRoundButton;
    private FXMLLoader loader;
    private Scene scene;
    private static Pane root;
    private static String css;
    private Countdown countdown;
    private Rounds rounds;

    @Override
    public void start(Stage primaryStage) {
	loader = null;
	scene = null;
	try {
	    loader = new FXMLLoader();
	    loader.setLocation(getClass().getClassLoader().getResource("TabataPanel.fxml"));
	    root = (Pane) loader.load();
	    scene = new Scene(root, 650, 900);
	    css = getClass().getResource("/application/application.css").toExternalForm();
	    scene.getStylesheets().add(css);
	    primaryStage.setScene(scene);
	    primaryStage.show();
	} catch (Exception e) {
	    e.printStackTrace();
	    return;
	}
	
	countdown = new Countdown((Label) loader.getNamespace().get("timeLabel"));
	Label rLabel = (Label) loader.getNamespace().get("roundLabel");
	Label tLabel = (Label) loader.getNamespace().get("tabataLabel");
	rounds = new Rounds(8,4,rLabel, tLabel);
	
	startButton = (Button) loader.getNamespace().get("startBtn");
	restartTabataButton = (Button) loader.getNamespace().get("restartBtn");
	restartRoundButton = (Button) loader.getNamespace().get("tabRestartBtn");

	restartTabataButton.setDisable(true);
	restartRoundButton.setDisable(true);
	
	startButton.setOnAction(new StartEvent(countdown, rounds));
    }

    public static void main(String[] args) {
	launch(args);
    }
    
    public static void changeBackground(BackgroundIMG state) {
	System.out.println(root.getStyleClass());
	root.getStyleClass().remove(1);
	root.getStyleClass().add(state.getCssClass());
	System.out.println(root.getStyleClass());
	root.getStylesheets().clear();
	root.getStylesheets().add(css);
    }

    class StartEvent implements EventHandler<ActionEvent> {

	private boolean flag = true;
	private Runnable r;
	private Thread t;
	
	public StartEvent(Countdown count, Rounds rounds) {
	    r = new Timer(count, rounds);
	}

	@Override
	public void handle(ActionEvent arg0) {
	    if (flag) {
		changeBackground(BackgroundIMG.EXERCISE);
		t = new Thread(r);
		t.setDaemon(true);
		t.start();
		// Changing start button description
		startButton.setText("Pause");
		restartTabataButton.setDisable(true);
		restartRoundButton.setDisable(true);
		flag = false;
	    } else {
		// Changing start button description
		startButton.setText("Start");
		restartTabataButton.setDisable(false);
		restartRoundButton.setDisable(false);
		// Changing background color of tabataPanel
		changeBackground(BackgroundIMG.PAUSE);
		// Interrupting Timer thread
		t.interrupt();
		flag = true;
	    }
	}
	
	

    }
}
