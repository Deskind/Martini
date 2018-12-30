package com.deskind.martiniboot;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.deskind.martiniboot.connection.SocketPlug;
import com.deskind.martiniboot.entities.LuckyGuy;
import com.deskind.martiniboot.fxcontrollers.MainController;
import com.deskind.martiniboot.runnables.AliveTask;
import com.deskind.martiniboot.trade.flow.RandomFlow;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

@SpringBootApplication
public class MartiniBootApplication extends Application{
	
	private static final String MAIN_FXML_LOCATION = "/fxml/main.fxml";
	private static final String STYLE_LOCATION = "/styles/style.css";
	private static final String COMMON_LOSS_KEY = "commonLossLastValue";
	
	private static MainController mainController;
	private static LuckyGuy luckyGuy;
	private static SocketPlug socketPlug;
	
	private static Timer stayAlive = new Timer();
	private static TimerTask aliveTask;
	
	private static Preferences preferences;
	
	private static RandomFlow randomFlow;

	public static void main(String[] args) {
		
		//start boot application
		SpringApplication.run(MartiniBootApplication.class, args);
		
		//run FX app
		launch(args);
	}
	
	@Override
	public void init() throws Exception {
		super.init();
		Platform.setImplicitExit(true);
	}


	@Override
	public void start(Stage stage) throws Exception {
		
		//prepare main window
		Scene scene = prepareScene(MAIN_FXML_LOCATION, STYLE_LOCATION);
		stage.setScene(scene);
		stage.show();
		
		//initialize web socket connection
		socketPlug = new SocketPlug();
		socketPlug.connect();
		
		//start alive task
		aliveTask = new AliveTask(socketPlug);
		stayAlive.schedule(aliveTask, 33_333, 33_333);
		
	}
	
	private Scene prepareScene(String mainFxmlLocation, String styleLocation) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		
		loader.setLocation(MartiniBootApplication.class.getResource(mainFxmlLocation));
		loader.setClassLoader(MartiniBootApplication.class.getClassLoader());
		
		Parent parent = (Parent)loader.load(MartiniBootApplication.class.getResourceAsStream(mainFxmlLocation));
		
		Scene scene = new Scene(parent);
		scene.getStylesheets().add(styleLocation);
		
		return scene;
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		
		//cancel stay alive timer
		stayAlive.cancel();
		
		//msg
		System.out.println("Application stopped ... ");
	}
	
	public float getCommonLossLastValue() {
		return preferences.getFloat(COMMON_LOSS_KEY, 0);
	}

	//SETTERS
	public static void setLuckyGuy(LuckyGuy guy) {
		luckyGuy = guy;
	}
	
	public static void setSocketPlug(SocketPlug plug) {
		socketPlug = plug;
	}
	
	public static void setMainController(MainController mainController) {
		MartiniBootApplication.mainController = mainController;
	}

	//GETTERS
	public static LuckyGuy getLuckyGuy() {
		return luckyGuy;
	}

	
	public static SocketPlug getSocketPlug() {
		return socketPlug;
	}

	public static MainController getMainController() {
		return mainController;
	}

	public static void startRandomFlow(RandomFlow randomFlow) {
		MartiniBootApplication.randomFlow = randomFlow;
		
		randomFlow.makeLuckyBet();
	}

	public static RandomFlow getRandomFlow() {
		return randomFlow;
	}
}

