package mr3.jgraph;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import mr3.data.*;
import mr3.ui.*;

import com.jgraph.graph.*;

public class ClassGraphMarqueeHandler extends RDFGraphMarqueeHandler {

	private RDFSInfoMap rdfsMap;
	private ClassPanel classPanel;

	public ClassGraphMarqueeHandler(GraphManager manager, ClassPanel panel) {
		super(manager, manager.getClassGraph());
		classPanel = panel;
	}

	// connect���邩�ǂ����������Ő���
	public void mouseReleased(MouseEvent e) {
		if (e != null && !e.isConsumed() && port != null && firstPort != null && firstPort != port && isEllipseView(firstPort.getParentView())) {
			Port source = (Port) firstPort.getCell();
			DefaultPort target = (DefaultPort) port.getCell();
			connect(source, target, "");
			classPanel.displayRDFSInfo((DefaultGraphCell) graph.getModel().getParent(source));
			e.consume();
		} else {
			graph.repaint();
		}

		firstPort = port = null;
		start = current = null;

		super.mouseReleased(e);
	}

	public void insertResourceCell(Point pt) {
		String uri = JOptionPane.showInputDialog("Please input URI");
		if (uri == null || gmanager.isEmptyURI(uri) || gmanager.isDuplicatedWithDialog(uri, null, GraphType.CLASS)) {
			return;
		} else {
			cellMaker.insertClass(pt, uri);
			DefaultGraphCell cell = (DefaultGraphCell) graph.getSelectionCell();
			if (graph.isOneCellSelected(cell)) {
				classPanel.displayRDFSInfo(cell);
			}
		}
	}

	public void insertSubClass(Point pt, Object[] supCells) {
		String uri = JOptionPane.showInputDialog("Please input URI");
		if (uri == null || gmanager.isEmptyURI(uri) || gmanager.isDuplicatedWithDialog(uri, null, GraphType.CLASS)) {
			return;
		} else {
			pt.y += 100;
			cellMaker.insertClass(pt, uri);
			DefaultGraphCell cell = (DefaultGraphCell) graph.getSelectionCell();
			Port sourcePort = (Port)cell.getChildAt(0);
			connectSubToSups(sourcePort, supCells);
			if (graph.isOneCellSelected(cell)) {
				classPanel.displayRDFSInfo(cell);
			}
		}
	}

	//
	// PopupMenu
	//
	public JPopupMenu createPopupMenu(final Point pt, final Object cell) {
		JPopupMenu menu = new JPopupMenu();

		menu.add(new AbstractAction("Insert Class") {
			public void actionPerformed(ActionEvent ev) {
				insertResourceCell(pt);
			}
		});

		if (!graph.isSelectionEmpty()) {
			// Insert Sub Class
			menu.add(new AbstractAction("Insert Sub Class") {
				public void actionPerformed(ActionEvent e) {
					if (!graph.isSelectionEmpty()) {
						Object[] supCells = graph.getSelectionCells();
						supCells = graph.getDescendants(supCells);
						insertSubClass(pt, supCells);
					}
				}
			});

			//			Remove	
			menu.add(new AbstractAction("Remove") {
				public void actionPerformed(ActionEvent e) {
					if (!graph.isSelectionEmpty()) {
						Object[] cells = graph.getSelectionCells();
						cells = graph.getDescendants(cells);
						gmanager.removeCells(cells, graph);
					}
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
			}
		});

		return menu;
	}
}