package org.meanworks.engine.util;

import java.util.LinkedList;
import java.util.List;

/**
 * Copyright (C) 2013 Steffen Evensen
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 * @author Meanz
 */
public class QueuedLinkedList<T> {

	/*
	 * The elements of this list
	 */
	private LinkedList<T> elements = new LinkedList<T>();

	/*
	 * The add queue of this list
	 */
	private LinkedList<T> addQueue = new LinkedList<T>();

	/*
	 * The remove queue of this list
	 */
	private LinkedList<T> removeQueue = new LinkedList<T>();

	/**
	 * Add an element directly to the list
	 * 
	 * @param t
	 */
	public void directAdd(T t) {
		if (t == null) {
			return;
		}
		elements.add(t);
	}

	/**
	 * Remove an element directly from the list
	 * 
	 * @param t
	 */
	public void directRemove(T t) {
		if (t == null) {
			return;
		}
		elements.remove(t);
	}

	/**
	 * Add an element to this list's add queue
	 * 
	 * @param t
	 */
	public void add(T t) {
		if (t == null) {
			return;
		}
		addQueue.add(t);
	}

	/**
	 * Add an element to this list's remove queue
	 * 
	 * @param t
	 */
	public void remove(T t) {
		if (t == null) {
			return;
		}
		removeQueue.add(t);
	}

	/**
	 * Get an element from the list
	 * 
	 * @param index
	 * @return
	 */
	public T get(int index) {
		return elements.get(index);
	}

	/**
	 * Get the list of this list
	 * 
	 * @return
	 */
	public List<T> list() {
		return elements;
	}

	/**
	 * Processes the queues of this list
	 */
	public void processQueues() {

		if (!addQueue.isEmpty()) {
			for (T t : addQueue) {
				elements.add(t);
			}
			addQueue.clear();
		}

		if (!removeQueue.isEmpty()) {
			for (T t : removeQueue) {
				elements.remove(t);
			}
			removeQueue.clear();
		}

	}
}
