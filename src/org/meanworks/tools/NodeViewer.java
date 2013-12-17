package org.meanworks.tools;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;

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
public class NodeViewer extends JFrame {

	/*
	 * The node we are viewing
	 */
	private Node node;
	
	/*
	 * Components for the ui
	 */
	private JLabel lblName;
	private JLabel txtName;

	/**
	 * Construct a new node viewer
	 * 
	 * @param node
	 */
	public NodeViewer(Node node) {
		super(node != null ? node.getName() : "null");
		if (node == null) {
			this.dispose();
			return;
		}
		this.node = node;
		setSize(250, 250);
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		GridLayout layout = new GridLayout(2, 2);
		setLayout(layout);
		
		lblName = new JLabel("Node Name: ");
		lblName.setVerticalTextPosition(JLabel.TOP);
		lblName.setHorizontalAlignment(JLabel.LEFT);
		txtName = new JLabel(node.getName());
		lblName.setVerticalAlignment(JLabel.TOP);
		lblName.setHorizontalAlignment(JLabel.LEFT);
		add(lblName);
		add(txtName);
		
	}

}
