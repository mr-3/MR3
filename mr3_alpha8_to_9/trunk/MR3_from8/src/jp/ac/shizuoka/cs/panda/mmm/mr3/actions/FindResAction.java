/*
 * Created on 2003/09/17
 *
 */
package jp.ac.shizuoka.cs.panda.mmm.mr3.actions;

import java.awt.event.*;

import javax.swing.*;

import jp.ac.shizuoka.cs.panda.mmm.mr3.jgraph.*;
import jp.ac.shizuoka.cs.panda.mmm.mr3.ui.*;
import jp.ac.shizuoka.cs.panda.mmm.mr3.util.*;

/**
 * @author takeshi morita
 */
public class FindResAction extends AbstractAction {

	private RDFGraph graph;
	private FindResourceDialog findResDialog;

	public FindResAction(RDFGraph g, FindResourceDialog frd, String title) {
		super(title, Utilities.getImageIcon("find.gif"));
		setValues();
		graph = g;
		findResDialog = frd;
	}

	private void setValues() {
		putValue(SHORT_DESCRIPTION, "Find Resource");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.ALT_MASK));
	}

	public void actionPerformed(ActionEvent e) {
		findResDialog.setSearchArea(graph.getType());
		findResDialog.setVisible(true);
	}
}
