package org.meanworks.testgame.world;

import java.util.LinkedList;

import org.meanworks.engine.EngineLogger;
import org.meanworks.engine.core.Application;
import org.meanworks.engine.gui.impl.PerformanceGraph;

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
					synchronized (nextQueue) {
						regionTasks.clear();
					}
				}
				Thread.sleep(200); // Sleep for 200ms while waiting for new
									// tasks
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
