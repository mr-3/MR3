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
		if (e != null && !e.isConsumed() && port != null && firstPort != null && firstPort != port) {
			Port source = (Port) firstPort.getCell();
			DefaultPort target = (DefaultPort) port.getCell();
			connect(source, target, "");
			classPanel.showRDFSInfo((DefaultGraphCell) graph.getModel().getParent(source));
			e.consume();
		} else {
			graph.repaint();
		}

		firstPort = port = null;
		start = current = null;

		super.mouseReleased(e);
	}

	public GraphCell insertResourceCell(Point pt) {
		InsertRDFSResDialog ird = new InsertRDFSResDialog("Input Resource");
		if (!ird.isConfirm()) {
			return null;
		}
		String uri = ird.getURI();
		URIType uriType = ird.getURIType();
		
		String tmpURI = getAddedBaseURI(uri, uriType);
		if (uri == null || gmanager.isEmptyURI(tmpURI) || gmanager.isDuplicatedWithDialog(tmpURI, null, GraphType.CLASS)) {
			return null;
		} else {
			return cellMaker.insertClass(pt, uri, uriType);
		}
	}

	public void insertSubClass(Point pt, Object[] supCells) {
		InsertRDFSResDialog ird = new InsertRDFSResDialog("Input Resource");
		if (!ird.isConfirm()) {
			return;
		}
		String uri = ird.getURI();
		URIType uriType = ird.getURIType();
		
		String tmpURI = getAddedBaseURI(uri, uriType);
		if (uri == null || gmanager.isEmptyURI(tmpURI) || gmanager.isDuplicatedWithDialog(tmpURI, null, GraphType.CLASS)) {
			return;
		} else {
			pt.y += 100;
			cellMaker.insertClass(pt, uri, uriType);
			DefaultGraphCell cell = (DefaultGraphCell) graph.getSelectionCell();
			Port sourcePort = (Port) cell.getChildAt(0);
			connectSubToSups(sourcePort, supCells);
			graph.setSelectionCell(cell);
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

		// cell != null�ɂ��Ȃ��ƁC������Z����I�������Ƃ��ɁC���j���[���\������Ȃ��D�Ȃ��H
		if (cell != null || !graph.isSelectionEmpty()) { // Insert Sub Class
			menu.add(new AbstractAction("Insert Sub Class") {
				public void actionPerformed(ActionEvent e) {
					if (!graph.isSelectionEmpty()) {
						Object[] supCells = graph.getSelectionCells();
						supCells = graph.getDescendants(supCells);
						insertSubClass(pt, supCells);
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

		menu.add(new AbstractAction("Attribute Dialog") {
			public void actionPerformed(ActionEvent e) {
				gmanager.setVisibleAttrDialog(true);
			}
		});

		return menu;
	}
}
