package jp.ac.shizuoka.cs.panda.mmm.mr3.jgraph;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import jp.ac.shizuoka.cs.panda.mmm.mr3.data.*;
import jp.ac.shizuoka.cs.panda.mmm.mr3.ui.*;

import com.jgraph.graph.*;

/**
 *
 * @author takeshi morita
 */
public class PropertyGraphMarqueeHandler extends RDFGraphMarqueeHandler {

	private PropertyPanel propertyPanel;
	private RDFSInfoMap rdfsMap = RDFSInfoMap.getInstance();

	public PropertyGraphMarqueeHandler(GraphManager manager, PropertyPanel panel) {
		super(manager, manager.getPropertyGraph());
		propertyPanel = panel;
	}

	// ������connect���邩�ǂ����𐧌䂷��
	public void mouseReleased(MouseEvent e) {
		if (e != null && !e.isConsumed() && port != null && firstPort != null && firstPort != port && isEllipseView(firstPort.getParentView())) {
			Port source = (Port) firstPort.getCell();
			DefaultPort target = (DefaultPort) port.getCell();
			cellMaker.connect(source, target, "", graph);
			propertyPanel.showRDFSInfo((DefaultGraphCell) graph.getModel().getParent(source));
			e.consume();
		} else {
			graph.repaint();
		}

		firstPort = port = null;
		start = current = null;
		super.mouseReleased(e);
	}

	public GraphCell insertResourceCell(Point pt) {
		InsertRDFSResDialog ird = new InsertRDFSResDialog("Input Resource", gmanager);
		if (!ird.isConfirm()) {
			return null;
		}
		String uri = ird.getURI();
		if (uri == null || gmanager.isEmptyURI(uri) || gmanager.isDuplicatedWithDialog(uri, null, GraphType.PROPERTY)) {
			return null;
		} else {
			return cellMaker.insertProperty(pt, uri);
		}
	}

	public void insertSubProperty(Point pt, Object[] supCells) {
		InsertRDFSResDialog ird = new InsertRDFSResDialog("Input Resource", gmanager);
		if (!ird.isConfirm()) {
			return;
		}
		String uri = ird.getURI();
		if (uri == null || gmanager.isEmptyURI(uri) || gmanager.isDuplicatedWithDialog(uri, null, GraphType.CLASS)) {
			return;
		} else {
			cellMaker.insertProperty(pt, uri);
			DefaultGraphCell cell = (DefaultGraphCell) graph.getSelectionCell();
			Port subPort = (Port) cell.getChildAt(0);
			cellMaker.connectSubToSups(subPort, supCells, graph);
			graph.setSelectionCell(cell);
		}
	}

	//
	// PopupMenu
	//
	public JPopupMenu createPopupMenu(final Point pt, final Object cell) {
		JPopupMenu menu = new JPopupMenu();

		menu.add(new AbstractAction("Insert Property") {
			public void actionPerformed(ActionEvent ev) {
				insertResourceCell(pt);
			}
		});

		if (cell != null || !graph.isSelectionEmpty()) {
			// Insert Sub Property
			menu.add(new AbstractAction("Insert Sub Property") {
				public void actionPerformed(ActionEvent e) {
					if (!graph.isSelectionEmpty()) {
						Object[] supCells = graph.getSelectionCells();
						supCells = graph.getDescendants(supCells);
						insertSubProperty(pt, supCells);
					}
				}
			});
		}

		menu.addSeparator();

		menu.add(new AbstractAction("Copy") {
			public void actionPerformed(ActionEvent e) {
				graph.copy(pt);
			}
		});

		menu.add(new AbstractAction("Cut") {
			public void actionPerformed(ActionEvent e) {
				graph.copy(pt);
				gmanager.removeAction(graph);
			}
		});

		menu.add(new AbstractAction("Paste") {
			public void actionPerformed(ActionEvent e) {
				graph.paste(pt);
			}
		});

		if (cell != null || !graph.isSelectionEmpty()) {
			menu.add(new AbstractAction("Remove") {
				public void actionPerformed(ActionEvent e) {
					gmanager.removeAction(graph);
				}
			});
		}

		menu.addSeparator();

		menu.add(new AbstractAction("Connect mode") {
			public void actionPerformed(ActionEvent e) {
				connectAction();
			}
		});

		menu.addSeparator();
		menu.add(new AbstractAction("Attribute Dialog") {
			public void actionPerformed(ActionEvent e) {
				gmanager.setVisibleAttrDialog(true);
				graph.setSelectionCell(graph.getSelectionCell());
			}
		});

		return menu;
	}
}