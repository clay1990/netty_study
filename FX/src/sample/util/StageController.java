package sample.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class StageController {

	private Map<String, Stage> stages = new HashMap<>();

	private Map<String, ControlledStage> controllers = new HashMap<>();

	public void addStage(String name, Stage stage) {
		this.stages.put(name, stage);
	}

	public Stage getStageBy(String name) {
		return this.stages.get(name);
	}

	public void setPrimaryStage(String name, Stage stage) {
		this.addStage(name, stage);
	}

	public Stage loadStage(String name, String resource,String title, StageStyle... styles) {
		Stage result = null;
		try{
//            URL url = IndexApplication.class.getClass().getClassLoader().getResource(resource);
            URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
			FXMLLoader loader = new FXMLLoader(url);
//			FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
//			loader.setResources(ResourceBundle.getBundle("i18n/message"));
			Pane tmpPane = (Pane)loader.load();
			ControlledStage controlledStage = (ControlledStage)loader.getController();
			this.controllers.put(name, controlledStage);
			Scene tmpScene = new Scene(tmpPane);
			result = new Stage();
			result.setScene(tmpScene);
			result.setTitle(title);

			for (StageStyle style:styles) {
				result.initStyle(style);
			}
			this.addStage(name, result);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public <T> T load(String resource, Class<T> clazz) {
		try{
			URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
			FXMLLoader loader = new FXMLLoader(url);
			return (T)loader.load();
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T> T load(String resource, Class<T> clazz, ResourceBundle resources) {
		try{
			URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
			return (T)FXMLLoader.load(url, resources);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public Stage setStage(String name) {
		Stage stage = this.getStageBy(name);
		if (stage == null) {
			return null;
		}
		stage.show();
		return stage;
	}

	public boolean switchStage(String toShow, String toClose) {
		getStageBy(toClose).close();
		setStage(toShow);

		return true;
	}

	public void closeStage(String name) {
		Stage target = getStageBy(name);
		target.close();
	}

	public boolean unloadStage(String name) {
		return this.stages.remove(name) != null;
	}

	public ControlledStage getController(String name) {
		return this.controllers.get(name);
	}

}
