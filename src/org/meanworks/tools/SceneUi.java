package org.meanworks.tools;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.meanworks.engine.core.Application;
import org.meanworks.engine.scene.Node;

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
public class SceneUi extends JFrame {

	/*
	 * The place we pull our data from! :D
	 */
	private Application application;

	/*
	 * The currently open frame;
	 */
	private JFrame openFrame;

	/*
	 * The scene tree
	 */
	private JTree sceneTree;

	/*
	 * The node that contains all scene objects
	 */
	private DefaultMutableTreeNode sceneNode;

	/**
	 * Construct a new scene ui
	 */
	public SceneUi(Application application) {
		super("MeanWorks Scene UI");

		this.application = application;

		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
		sceneNode = new DefaultMutableTreeNode("Scene");
		root.add(sceneNode);

		sceneTree = new JTree(root);
		sceneTree.setRootVisible(false);

		refreshSceneNodes();

		add(new JScrollPane(sceneTree));

		setSize(300, 600);
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		MouseListener ml = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int selRow = sceneTree.getRowForLocation(e.getX(), e.getY());
				TreePath selPath = sceneTree.getPathForLocation(e.getX(),
						e.getY());
				if (selRow != -1) {
					if (e.getClickCount() == 1) {
						// mySingleClick(selRow, selPath);
					} else if (e.getClickCount() == 2) {
						nodeClicked(selRow, selPath);
					}
				}
			}
		};
		sceneTree.addMouseListener(ml);
	}

	/**
	 * Make sure we close any open frames
	 */
	@Override
	public void dispose() {
		if (openFrame != null) {
			openFrame.dispose();
		}
		super.dispose();
	}

	/**
	 * Called when a node is clicked
	 * 
	 * @param selRow
	 * @param selPath
	 */
	public void nodeClicked(int selRow, TreePath selPath) {
		DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) sceneTree
				.getLastSelectedPathComponent();
		if (dmtn == null) {
			return;
		}
		Object obj = dmtn.getUserObject();
		if (obj == null) {
			return;
		}
		if (obj instanceof Node) {
			if (openFrame != null) {
				openFrame.dispose();
			}
			openFrame = new NodeViewer((Node) obj);
		}
	}

	/**
	 * Used for recursively looking at the scene graph tree
	 * 
	 * @param parentNode
	 * @param sn
	 */
	private void recursiveRefreshSceneNodes(DefaultMutableTreeNode parentNode,
			Node sn) {
		DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode(sn);
		parentNode.add(dmtn);
		List<Node> nodes = sn.getChildren();
		for (Node n : nodes) {
			recursiveRefreshSceneNodes(dmtn, n);
		}
	}

	/**
	 * Refresh the graph
	 */
	public void refreshSceneNodes() {
		sceneNode.removeAllChildren();
		Node node = application.getScene().getRootNode();
		recursiveRefreshSceneNodes(sceneNode, node);
	}

}
