/*
 * @(#) InsertRDFSResDialog.java
 *
 * Copyright (C) 2003 The MMM Project
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

package org.semanticweb.mmm.mr3.ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.semanticweb.mmm.mr3.data.*;
import org.semanticweb.mmm.mr3.jgraph.*;
import org.semanticweb.mmm.mr3.util.*;

/**
 * @author takeshi morita
 */
public class InsertRDFSResDialog extends JDialog implements ActionListener {

	private JButton confirm;
	private JButton cancel;
	private JTextField idField;
	private JLabel nsLabel;
	private JComboBox uriPrefixBox;

	private boolean isConfirm;

	private static final int LIST_WIDTH = 300;
	private static final int LIST_HEIGHT = 40;
	private static final int BOX_WIDTH = 120;
	private static final int BOX_HEIGHT = 50;
	private static final int ID_WIDTH = 180;

	public InsertRDFSResDialog(String title, GraphManager gmanager) {
		super(gmanager.getRoot(), title, true);
		Container contentPane = getContentPane();
		PrefixNSUtil.setPrefixNSInfoSet(gmanager.getPrefixNSInfoSet());

		idField = new JTextField();
		idField.setText("");
		Utilities.initComponent(idField, "ID", ID_WIDTH, LIST_HEIGHT);

		uriPrefixBox = new JComboBox();
		Utilities.initComponent(uriPrefixBox, MR3Constants.PREFIX, BOX_WIDTH, BOX_HEIGHT);
		uriPrefixBox.setModel(new DefaultComboBoxModel(PrefixNSUtil.getPrefixes().toArray()));
		uriPrefixBox.addActionListener(new ChangePrefixAction());

		nsLabel = new JLabel("");
		Utilities.initComponent(nsLabel, MR3Constants.NAME_SPACE, LIST_WIDTH, LIST_HEIGHT);

		uriPrefixBox.setSelectedItem(PrefixNSUtil.getBaseURIPrefix(gmanager.getBaseURI()));
		PrefixNSUtil.replacePrefix((String) uriPrefixBox.getSelectedItem(), nsLabel);

		JPanel uriPanel = new JPanel();
		uriPanel.add(uriPrefixBox);
		uriPanel.add(idField);

		confirm = new JButton(MR3Constants.OK);
		confirm.addActionListener(this);
		cancel = new JButton(MR3Constants.CANCEL);
		cancel.addActionListener(this);
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(confirm);
		buttonPanel.add(cancel);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(uriPanel, BorderLayout.NORTH);
		panel.add(nsLabel, BorderLayout.CENTER);
		panel.add(buttonPanel, BorderLayout.SOUTH);
		contentPane.add(panel);

		setLocation(300, 300);
		setSize(new Dimension(400, 170));
		setResizable(false);
		setVisible(true);
	}

	class ChangePrefixAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			PrefixNSUtil.replacePrefix((String) uriPrefixBox.getSelectedItem(), nsLabel);
		}
	}

	public boolean isConfirm() {
		return isConfirm;
	}

	public String getURI() {
		return nsLabel.getText() + idField.getText();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == confirm) {
			isConfirm = true;
		} else {
			isConfirm = false;
		}
		setVisible(false);
	}
}
