package org.fractalstudio.testgame.world;

import java.util.LinkedList;

import org.fractalstudio.engine.Application;
import org.fractalstudio.engine.EngineLogger;
import org.fractalstudio.engine.gui.PerformanceGraph;

public class WorldLoader implements Runnable {

	private LinkedList<Region> nextQueue = new LinkedList<>();

	private LinkedList<Region> regionTasks = new LinkedList<>();

	/**
	 * 
	 */
	public WorldLoader() {
		Thread thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
	}

	public void addTask(Region region) {
		if (region == null) {
			return;
		}
		synchronized (nextQueue) {
			nextQueue.add(region);
		}
	}

	/**
	 * World loader loop
	 */
	public void run() {
		EngineLogger.info("WorldLoader thread started.");
		while (true) {
			try {
				synchronized (nextQueue) {
					if (nextQueue.size() > 0) {
						regionTasks.addAll(nextQueue);
					}
					nextQueue.clear();
				}
				if (regionTasks.size() > 0) {
					// Process each task
					for (Region region : regionTasks) {
						long time = System.currentTimeMillis();
						region.buildTerrain();
						time = System.currentTimeMillis() - time;
					}
					regionTasks.clear();
				}
				Thread.sleep(200); // Sleep for 200ms while waiting for new
									// tasks
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

}
