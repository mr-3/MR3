/*
 * @(#) ReplaceRDFS.java
 *
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

package org.semanticweb.mmm.mr3.actions;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.semanticweb.mmm.mr3.*;
import org.semanticweb.mmm.mr3.jgraph.*;
import org.semanticweb.mmm.mr3.util.*;

import com.hp.hpl.jena.rdf.model.*;

/**
 * @author takeshi morita
 */
public class ReplaceRDFS extends AbstractActionFile {

	public static final String REPLACE_RDFS_FILE = Translator.getString("Component.File.Import.Replace.RDFS/XML(File).Text");
	public static final String REPLACE_RDFS_URI = Translator.getString("Component.File.Import.Replace.RDFS/XML(URI).Text");
	public static final String REPLACE_N_TRIPLE_FILE = Translator.getString("Component.File.Import.Replace.RDFS/N-Triple(File).Text");
	public static final String REPLACE_N_TRIPLE_URI = Translator.getString("Component.File.Import.Replace.RDFS/N-Triple(URI).Text");

	public ReplaceRDFS(MR3 mr3, String title) {
		super(mr3, title);
	}

	public void actionPerformed(ActionEvent e) {
		Model model = null;
		Component desktop = mr3.getDesktopPane();
		GraphManager gmanager = mr3.getGraphManager();
		gmanager.setIsImporting(true);
		if (e.getActionCommand().equals(REPLACE_RDFS_FILE)) {
			model = readModel(getReader("rdfs", null), gmanager.getBaseURI(), "RDF/XML");
		} else if (e.getActionCommand().equals(REPLACE_N_TRIPLE_FILE)) {
			model = readModel(getReader("rdfs", null), gmanager.getBaseURI(), "N-Triple");
		} else if (e.getActionCommand().equals(REPLACE_RDFS_URI)) {
			String uri = JOptionPane.showInternalInputDialog(desktop, "Open URI ( exp. http://slashdot.jp/slashdot.rdf )");
			model = readModel(getReader(uri), gmanager.getBaseURI(), "RDF/XML");
		} else if (e.getActionCommand().equals(REPLACE_N_TRIPLE_URI)) {
			String uri = JOptionPane.showInternalInputDialog(desktop, "Open URI ( exp. http://slashdot.jp/slashdot.rdf )");
			model = readModel(getReader(uri), gmanager.getBaseURI(), "N-Triple");
		}
		if (model == null) {
			gmanager.setIsImporting(false);
			return;
		}
		mr3.replaceRDFSModel(model);
		gmanager.setIsImporting(false);
	}

}
