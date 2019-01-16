package com.deskind.martiniboot;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.deskind.martiniboot.connection.SocketPlug;
import com.deskind.martiniboot.controllers.MainController;
import com.deskind.martiniboot.entities.LuckyGuy;
import com.deskind.martiniboot.runnables.AliveTask;
import com.deskind.martiniboot.trade.flow.Flow;
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
	private static final String SERVER_SHUTDOWN_REQUEST = "http://localhost:8880/actuator/shutdown";
	
	private static MainController mainController;
	private static LuckyGuy luckyGuy;
	private static SocketPlug socketPlug;
	private static RandomFlow flow;
	
	private static Timer stayAlive = new Timer();
	private static TimerTask aliveTask;
	
	private static Preferences preferences;
	
	//random mode flag
	private static boolean random;

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
		
		MartiniBootApplication.socketPlug.
								disconnect(new CloseReason(CloseCodes.NORMAL_CLOSURE, "!!!Bye!!!"));
		
		//msg
		System.out.println("Application stopped ... ");
		
		//web server shutdown
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(SERVER_SHUTDOWN_REQUEST);
		client.execute(post);	
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

	public static void setFlow(RandomFlow flow) {
		MartiniBootApplication.flow = flow;
	}

	public static RandomFlow getFlow() {
		return flow;
	}

	public static void setRandomMode(boolean random) {
		MartiniBootApplication.random = random;
	}

	public static boolean isRandomMode() {
		return random;
	}

}

