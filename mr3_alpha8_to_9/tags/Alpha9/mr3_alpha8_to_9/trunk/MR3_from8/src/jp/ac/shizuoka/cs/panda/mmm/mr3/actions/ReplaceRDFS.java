/*
 * Created on 2003/09/23
 *
 */
package jp.ac.shizuoka.cs.panda.mmm.mr3.actions;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import jp.ac.shizuoka.cs.panda.mmm.mr3.*;
import jp.ac.shizuoka.cs.panda.mmm.mr3.jgraph.*;
import jp.ac.shizuoka.cs.panda.mmm.mr3.util.*;

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