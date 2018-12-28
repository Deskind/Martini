package com.deskind.martiniboot;

import java.util.Timer;
import java.util.TimerTask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.deskind.martiniboot.connection.SocketPlug;
import com.deskind.martiniboot.entities.LuckyGuy;
import com.deskind.martiniboot.fxcontrollers.MainController;
import com.deskind.martiniboot.runnables.AliveTask;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

@SpringBootApplication
public class MartiniBootApplication extends Application{
	
	private static MainController mainController;
	private static LuckyGuy luckyGuy;
	private static SocketPlug socketPlug;
	
	private static Timer stayAlive = new Timer();
	
	private static TimerTask aliveTask;

	public static void main(String[] args) {
		
		//start boot application
		SpringApplication.run(MartiniBootApplication.class, args);
		
		//initialize web socket connection
		socketPlug = new SocketPlug();
		socketPlug.connect();
		
		//start alive task
		aliveTask = new AliveTask(socketPlug);
		stayAlive.schedule(aliveTask, 5000, 5000);
		
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
		
		FXMLLoader loader = new FXMLLoader();
		
		loader.setLocation(MartiniBootApplication.class.getResource("/fxml/main.fxml"));
		loader.setClassLoader(MartiniBootApplication.class.getClassLoader());
		Parent parent = (Parent)loader.load(MartiniBootApplication.class.getResourceAsStream("/fxml/main.fxml"));
		
		stage.setScene(new Scene(parent));
		
		stage.show();
		
	}
	
	@Override
	public void stop() throws Exception {
		super.stop();
		
		//cancel stay alive timer
		stayAlive.cancel();
		
		//msg
		System.out.println("Application stopped ... ");
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
}

