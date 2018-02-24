package application;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

    private Button startButton;
    private Button restartTabataButton;
    private Button restartRoundButton;
    private FXMLLoader loader;
    private Scene scene;
    private Pane root;
    private String css;

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

	startButton = (Button) loader.getNamespace().get("startBtn");
	restartTabataButton = (Button) loader.getNamespace().get("restartBtn");
	restartRoundButton = (Button) loader.getNamespace().get("tabRestartBtn");

	restartTabataButton.setDisable(true);
	restartRoundButton.setDisable(true);
	
	startButton.setOnAction(new StartEvent());
	

    }

    public static void main(String[] args) {
	launch(args);
    }
    
    private void changeBackground(BackgroundIMG state) {
	System.out.println(root.getStyleClass());
	root.getStyleClass().remove(1);
	root.getStyleClass().add(state.getCssClass());
	System.out.println(root.getStyleClass());
	root.getStylesheets().clear();
	root.getStylesheets().add(css);
    }

    class StartEvent implements EventHandler<ActionEvent> {

	private boolean flag = true;
	// private Runnable r = new Timer();
	private Runnable r = () -> {
	    while (true) {
		System.out.println("Timer on");
		try {
		    Thread.sleep(1000);
		} catch (InterruptedException e) {
		    System.out.println("Interrupted");
		    break;
		}
	    }
	};
	private Thread t;

	@Override
	public void handle(ActionEvent arg0) {
	    if (flag) {
		changeBackground(BackgroundIMG.EXERCISE);
		t = new Thread(r);
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
