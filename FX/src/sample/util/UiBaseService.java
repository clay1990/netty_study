package sample.util;

import javafx.application.Platform;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum UiBaseService {

	INSTANCE;

	private StageController stageController = new StageController();

	private Map<String,Object> componetMap = new ConcurrentHashMap<>();

	public StageController getStageController() {
		return stageController;
	}

	public Map<String, Object> getComponetMap() {
		return componetMap;
	}

	/**
	 * 将任务转移给fxapplication线程延迟执行
	 * @param task
	 */
	public void runTaskInFxThread(Runnable task){
		Platform.runLater(task);
	}

}
