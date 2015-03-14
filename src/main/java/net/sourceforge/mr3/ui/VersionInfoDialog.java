/*
 * Project Name: MR^3 (Meta-Model Management based on RDFs Revision Reflection)
 * Project Website: http://mr3.sourceforge.net/
 * 
 * Copyright (C) 2003-2009 Yamaguchi Laboratory, Keio University. All rights reserved. 
 * 
 * This file is part of MR^3.
 * 
 * MR^3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * MR^3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with MR^3.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package net.sourceforge.mr3.ui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import net.sourceforge.mr3.util.Translator;
import net.sourceforge.mr3.util.Utilities;

/**
 * @author takeshi morita
 */
public class VersionInfoDialog extends JDialog implements HyperlinkListener {

	public VersionInfoDialog(Frame root, String title, ImageIcon icon) {
		super(root, title);
		setIconImage(icon.getImage());
		JEditorPane htmlPane = new JEditorPane();
		htmlPane.addHyperlinkListener(this);
		htmlPane.setEditable(false);
		htmlPane.setContentType("text/html; charset=utf-8");
		URL versionInfoURL = Utilities.class.getClassLoader().getResource(
				Translator.RESOURCE_DIR + "version_info.html");
		try {
			htmlPane.setPage(versionInfoURL);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		setLayout(new BorderLayout());
		add(new JScrollPane(htmlPane), BorderLayout.CENTER);
		add(Utilities.createEastPanel(okButton), BorderLayout.SOUTH);
		setSize(500, 400);
		setLocationRelativeTo(root);
		setVisible(true);
	}

	public void hyperlinkUpdate(HyperlinkEvent ae) {
		try {
			if (ae.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				Desktop.getDesktop().browse(ae.getURL().toURI());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
