package mr3.editor;
import java.io.*;

import javax.swing.*;
import javax.swing.text.*;

import mr3.data.*;
import mr3.jgraph.*;
import mr3.ui.*;

import com.hp.hpl.mesa.rdf.jena.common.*;
import com.hp.hpl.mesa.rdf.jena.model.*;
import com.hp.hpl.mesa.rdf.jena.vocabulary.*;
import com.jgraph.event.*;
import com.jgraph.graph.*;

/**
 * @author takeshi morita
 *
 */
public class RDFEditor extends Editor {

	private RDFResourcePanel resPanel;
	private RDFPropertyPanel propPanel;
	private RDFLiteralPanel litPanel;

	public RDFEditor(AttributeDialog pw, GraphManager manager) {
		graph = manager.getRDFGraph();
		graph.setMarqueeHandler(new RDFGraphMarqueeHandler(manager, graph));
		initEditor(manager.getRDFGraph(), manager, pw);
	}

	protected void initField(AttributeDialog pw, GraphManager manager) {
		super.initField(pw, manager);
		resPanel = new RDFResourcePanel(gmanager, pw);
		propPanel = new RDFPropertyPanel(gmanager, pw);
		litPanel = new RDFLiteralPanel(gmanager, pw);
	}

	public void convertNTripleSRC(JTextComponent area) {
		try {
			Model model = graphToRDF.getRDFModel();
			Writer output = new StringWriter();
			RDFWriter writer = new NTripleWriter();
			writeModel(model, output, writer);
			//			model.write(output, "N-TRIPLE");
			area.setText(output.toString());
		} catch (RDFException e) {
			e.printStackTrace();
		}
	}

	public void exportRDFFile(String type) {
		Model model = graphToRDF.getRDFModel();
		JFileChooser fc = new JFileChooser();
		FileWriter output = null;
		int fd = fc.showSaveDialog(null);
		try {
			if (fd == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				output = new FileWriter(file);
				if (type.equals("RDF")) {
					RDFWriter writer = new RDFWriterFImpl().getWriter("RDF/XML-ABBREV");
					writeModel(model, output, writer);
				} else if (type.equals("N-Triple")) {
					RDFWriter writer = new RDFWriterFImpl().getWriter("N-TRIPLE");
					writeModel(model, output, writer);
				}
				output.close();
			} else {
				System.out.println("Can not open File");
			}
		} catch (Exception ex) {
		}
	}

	public void convertRDFSRC(JTextComponent area) {
		try {
			Model model = graphToRDF.getRDFModel();
			Writer output = new StringWriter();
			RDFWriter writer = new RDFWriterFImpl().getWriter("RDF/XML-ABBREV");
			writeModel(model, output, writer);
			area.setText(output.toString());
		} catch (RDFException e) {
			e.printStackTrace();
		}
	}

	private Object getDomainType(Edge edge) {
		Object source = graph.getSourceVertex(edge);
		RDFResourceInfo sourceInfo = resInfoMap.getCellInfo(source);
		if (sourceInfo == null || sourceInfo.getTypeCell() == null) {
			return gmanager.getClassCell(RDFS.Resource, true);
		} else {
			return sourceInfo.getTypeCell();
		}
	}

	private Object getRangeType(Edge edge) {
		Object target = graph.getTargetVertex(edge);
		RDFResourceInfo resInfo = resInfoMap.getCellInfo(target);
		Literal litInfo = litInfoMap.getCellInfo(target);
		if (litInfo != null) { // info��Literal�Ȃ��
			return gmanager.getClassCell(RDFS.Literal, true);
		} else if (litInfo == null || resInfo.getTypeCell() == null) { // TypeCell���Ȃ���΍��D
			return gmanager.getClassCell(RDFS.Resource, true);
		} else {
			return resInfo.getTypeCell();
		}
	}

	private void selectResource(GraphCell cell) {
		resPanel.displayResInfo(cell);
		propWindow.setContentPane(resPanel);
	}

	private void selectProperty(GraphCell cell) {
		propPanel.dspPropertyInfo(cell);
		Edge edge = (Edge) cell;
		Object domainType = getDomainType(edge);
		Object rangeType = getRangeType(edge);
		propPanel.setPropertyList(gmanager.getPropertyList());
		propPanel.setValidPropertyList(gmanager.getValidPropertyList(domainType, rangeType));
		propWindow.setContentPane(propPanel);
	}

	private void selectLiteral(GraphCell cell) {
		litPanel.dspLiteralInfo(cell);
		propWindow.setContentPane(litPanel);
	}

	// From GraphSelectionListener Interface
	public void valueChanged(GraphSelectionEvent e) {
		setToolStatus();
		changeAttrPanel();
		propWindow.validate(); // validate���\�b�h���Ă΂Ȃ��ƍĕ`�悪���܂������Ȃ�
	}

	private void changeAttrPanel() {
		GraphCell cell = (GraphCell) graph.getSelectionCell();
		if (graph.isOneCellSelected(cell)) {
			if (graph.isResourceCell(cell)) {
				selectResource(cell);
			} else if (graph.isPropertyCell(cell)) {
				selectProperty(cell);
			} else if (graph.isLiteralCell(cell)) {
				selectLiteral(cell);
			}
		} else {
			propWindow.setNullPanel();
		}
	}
}