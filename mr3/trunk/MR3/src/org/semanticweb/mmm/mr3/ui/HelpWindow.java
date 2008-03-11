/*
 * Project Name: MR^3 (Meta-Model Management based on RDFs Revision Reflection)
 * Project Website: http://mr3.sourceforge.net/
 * 
 * Copyright (C) 2003-2008 Yamaguchi Laboratory, Keio University. All rights reserved. 
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

package org.semanticweb.mmm.mr3.ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.semanticweb.mmm.mr3.util.*;

/**
 * @author takeshi morita
 */
public class HelpWindow extends JWindow {

    private static final int FONT_SIZE = 14;
    private static final String TOOL_NAME = "Project Name: MR<sup>3</sup> <br>";
    private static final String VERSION = "   Version: 1.0 SP1<br>";
    private static final String LAST_UPDATE = "Last Update: 2008-03-12<br>";
    private static final String MR3_URL = " Project Website:   http://mr3.sourceforge.net/<br>";
    private static final String COPY_RIGHT = "   Copyright (C) 2003-2008 Yamaguchi Laboratory, Keio University.<br>";
    private static final String LICENSE = "License: GPL<br>";
    private static final Color HELP_BACK_COLOR = Color.WHITE;

    public HelpWindow(Frame root, ImageIcon logo) {
        super(root);
        JLabel logoLabel = new JLabel("", logo, SwingConstants.LEFT);
        logoLabel.setFont(logoLabel.getFont().deriveFont(Font.PLAIN, FONT_SIZE));

        JEditorPane editor = new JEditorPane("text/html", "");
        editor.setEditable(false);
        StringBuilder builder = new StringBuilder();
        builder.append("<font face=TimesNewRoman>");
        builder.append(TOOL_NAME);
        builder.append(VERSION);
        builder.append(LAST_UPDATE);
        builder.append(COPY_RIGHT);
        builder.append("Contact: t_morita@ae.keio.ac.jp<br>");
        builder.append(LICENSE);
        builder.append(MR3_URL);
        builder.append("</font>");
        editor.setText(builder.toString());

        JPanel helpPanel = new JPanel();
        helpPanel.setBackground(HELP_BACK_COLOR);
        helpPanel.setLayout(new BorderLayout());
        helpPanel.add(logoLabel, BorderLayout.CENTER);
        helpPanel.add(editor, BorderLayout.EAST);
        helpPanel.setBorder(BorderFactory.createEtchedBorder());

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(helpPanel, BorderLayout.CENTER);

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose();
            }
        });
        pack();
        Utilities.center(this);
        setVisible(true);
    }
}