package org.fractalstudio.engine.asset;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.fractalstudio.engine.EngineLogger;

public class AssetWatcher implements Runnable {

	/**
	 * The watcher that watches files
	 */
	private WatchService watcher;
	/**
	 * The callback that will receive notifications
	 */
	private LinkedList<AssetListener> listeners;
	/*
	 * A watched list so we can perform a reverse lookup for the asset watched
	 * callback
	 */
	private ArrayList<String> watchedPaths;
	private HashMap<String, String> watchedFiles;
	/**
	 * To prevent multiple notifications of a file change
	 */
	private HashMap<String, Long> timestamps;
	/**
	 * Whether or not this watcher is running
	 */
	private boolean running;

	/**
	 * Constructor
	 * 
	 * @param callback
	 */
	public AssetWatcher() {
		// Create the watched list
		watchedPaths = new ArrayList<>();
		watchedFiles = new HashMap<>();

		// Create out timestamp list
		timestamps = new HashMap<>();

		// Create our watcher
		try {
			watcher = FileSystems.getDefault().newWatchService();
		} catch (IOException iex) {
			EngineLogger.error(iex.getMessage());
			iex.printStackTrace();
		}

		listeners = new LinkedList<>();

		// Start the watcher
		running = true;
		Thread thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * 
	 * @param listener
	 */
	public void addListener(AssetListener listener) {
		listeners.add(listener);
	}

	/**
	 * Adds a watch to a file
	 * 
	 * @param file
	 */
	public void watchFile(String keyName, String fileAbsPath) {
		try {
			File file = new File(fileAbsPath);
			String fileName = file.getCanonicalPath().split("[\\\\]")[file
					.getCanonicalPath().split("[\\\\]").length - 1];
			String filePath = file.getParent();

			if (watchedFiles.containsKey(fileName)) {
				// System.err.println("File " + fileName +
				// " is already beeing watched.");
				return;
			}

			// System.err.println("Added file: " + fileName);
			watchedFiles.put(fileName, keyName);

			if (watchedPaths.contains(filePath)) {
				// System.err.println("Folder " + filePath +
				// " is already beeing watched.");
				return;
			}

			// System.err.println("Added watch directory: " + file.getParent());
			watchedPaths.add(filePath);

			Path path = FileSystems.getDefault().getPath("", file.getParent());
			path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);

		} catch (IOException iex) {
			iex.printStackTrace();
		}
	}

	/**
     *
     */
	@Override
	public void run() {
		EngineLogger.info("[Watcher] Running!");
		while (running) {
			try {
				WatchKey key = watcher.take();
				if (key != null) {
					Path path = (Path) key.watchable();
					for (WatchEvent we : key.pollEvents()) {
						// We only want to watch modifies
						// TODO: Add something for ENTRY_DELETE later
						if (we.kind() != StandardWatchEventKinds.ENTRY_MODIFY) {
							continue;
						}

						// Check if we are recieving duplicate notifications
						File f = new File(path.toString() + "/"
								+ we.context().toString());
						if (!f.exists()) {
							System.err.println("File: " + f.getAbsolutePath()
									+ " does not exist!");
							break;
						}
						Long oldTimestamp = timestamps.get(f.toString());
						// If this timestamp is not in our database
						if (oldTimestamp != null) {
							if (f.lastModified() - oldTimestamp < 100) {
								// System.err.println("Prevented mulitple notifications on file: "
								// + f.toString());
								break; // Multiple notifications prevented!
							}
						}
						// System.err.println(" lastModified: " + new
						// Date(f.lastModified()).toString() + " - " +
						// f.lastModified());
						// Put the timestamp in our database
						timestamps.put(f.toString(), f.lastModified());
						// Send notification to the callback
						String file = watchedFiles.get(we.context().toString());
						if (file != null) {
							for (AssetListener listener : listeners) {
								listener.fileModified(file, path.toString()
										+ "/" + we.context().toString());
							}
						}
					}
					key.reset();
					Thread.sleep(150);
				}
			} catch (InterruptedException iex) {
				EngineLogger.error("[Watcher]: exception (" + iex.getMessage()
						+ ")");
			}
		}
		EngineLogger.info("[Watcher] Stopped!");
	}
}
