/*
 * @(#) MR3Plugin.java
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

package org.semanticweb.mmm.mr3.plugin;

import java.util.*;

import javax.swing.*;

import org.jgraph.*;
import org.semanticweb.mmm.mr3.*;
import org.semanticweb.mmm.mr3.actions.*;
import org.semanticweb.mmm.mr3.data.*;
import org.semanticweb.mmm.mr3.jgraph.*;
import org.semanticweb.mmm.mr3.layout.*;
import org.semanticweb.mmm.mr3.util.*;

import com.hp.hpl.jena.rdf.model.*;

/**
 * MR^3�̃v���O�C�����쐬���邽�߂̃N���X
 * 
 * @author takeshi morita
 */
public abstract class MR3Plugin {

	private MR3 mr3;
	private String menuName;
	private RDFSInfoMap rdfsInfoMap = RDFSInfoMap.getInstance();
	private RDFResourceInfoMap resInfoMap = RDFResourceInfoMap.getInstance();

	protected MR3Plugin(String mn) {
		menuName = mn;
	}

	protected MR3Plugin() {
		menuName = "none";
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMR3(MR3 mr3) {
		this.mr3 = mr3;
	}

	/**
	 * MR3Plugin�N���X�̃T�u�N���X�Ŏ�������D File->plugins�ɒǉ�����郁�j���[�����s����ƁCexec���\�b�h�����s�����D
	 */
	public abstract void exec();

	public String toString() {
		return menuName;
	}

	/**
	 * Jena���񋟂���Model���CMR3��RDF�O���t�֕ϊ�����D �ϊ�����RDF�O���t��ҏW����RDF�O���t�ƒu������D
	 */
	protected void replaceRDFModel(Model model) {
		mr3.replaceRDFModel(model);
		mr3.performTreeLayout();
	}

	/**
	 * Jena���񋟂���Model���CMR3��RDF�O���t�֕ϊ�����D �ϊ�����RDF�O���t��ҏW����RDF�O���t�Ƀ}�[�W����D
	 */
	protected void mergeRDFModel(Model model) {
		mr3.mergeRDFModel(model);
	}

	/**
	 * Jena���񋟂���Model���CMR3��RDFS�O���t�֕ϊ�����D �ϊ�����RDFS�O���t��ҏW����RDFS�O���t�Ƀ}�[�W����D
	 */
	protected void mergeRDFSModel(Model model) {
		mr3.mergeRDFSModel(model);
		mr3.performTreeLayout();
	}

	/**
	 * Jena���񋟂���Model���CMR^3�̃v���W�F�N�g�֕ϊ�����D
	 * 
	 * @param model
	 *                MR^3�̃v���W�F�N�g�t�@�C���D
	 */
	protected void replaceProjectModel(Model model) {
		mr3.replaceProjectModel(model);
	}

	/**
	 * MR3��RDF�O���t��Jena��Model�ɕϊ�����D
	 */
	protected Model getRDFModel() {
		return mr3.getRDFModel();
	}

	/**
	 * �I������Ă���MR3��RDF�O���t��Jena��Model�ɕϊ�����D
	 */
	protected Model getSelectedRDFModel() {
		return mr3.getSelectedRDFModel();
	}

	/**
	 * MR3��RDFS�O���t��Jena��Model�ɕϊ�����D
	 */
	protected Model getRDFSModel() {
		return mr3.getRDFSModel();
	}

	/**
	 * �I������Ă���MR3��RDFS�O���t��Jena��Model�ɕϊ�����D
	 */
	protected Model getSelectedRDFSModel() {
		return mr3.getSelectedRDFSModel();
	}

	/**
	 * MR3�̃N���X�O���t��Jena��Model�ɕϊ�����D
	 * 
	 * @return Model
	 */
	protected Model getClassModel() {
		return mr3.getClassModel();
	}

	/**
	 * �I������Ă���MR3�̃N���X�O���t��Jena��Model�ɕϊ�����D
	 * 
	 * @return Model
	 */
	protected Model getSelectedClassModel() {
		return mr3.getSelectedClassModel();
	}

	/**
	 * MR3�̃v���p�e�B�O���t��Jena��Model�ɕϊ�����D
	 * 
	 * @return Model
	 */
	protected Model getPropertyModel() {
		return mr3.getPropertyModel();
	}

	/**
	 * �I������Ă���MR3�̃v���p�e�B�O���t��Jena��Model�ɕϊ�����D
	 * 
	 * @return Model
	 */
	protected Model getSelectedPropertyModel() {
		return mr3.getSelectedPropertyModel();
	}

	/**
	 * �v���W�F�N�g��Jena��Model�ɕϊ�����D
	 * 
	 * @return Model
	 */
	protected Model getProjectModel() {
		return mr3.getProjectModel();
	}

	/**
	 * RDF�O���t(org.jgraph.JGraph)�𓾂�D
	 * 
	 * @return org.jgraph.JGraph
	 */
	protected JGraph getRDFGraph() {
		return mr3.getRDFGraph();
	}

	/**
	 * �N���X�O���t(org.jgraph.JGraph)�𓾂�D
	 * 
	 * @return org.jgraph.JGraph
	 */
	protected JGraph getClassGraph() {
		return mr3.getClassGraph();
	}

	/**
	 * �v���p�e�B�O���t(org.jgraph.JGraph)�𓾂�D
	 * 
	 * @return org.jgraph.JGraph
	 */
	protected JGraph getPropertyGraph() {
		return mr3.getPropertyGraph();
	}

	/**
	 * BaseURI�𓾂�D
	 * 
	 * @return String
	 */
	protected String getBaseURI() {
		return mr3.getBaseURI();
	}

	/**
	 * JDesktopPane�𓾂�D�����E�B���h�E���쐬����ۂɗp����D
	 * 
	 * @return JDesktopPane
	 */
	protected JDesktopPane getDesktopPane() {
		return mr3.getDesktopPane();
	}

	/**
	 * 
	 * URI������̃Z�b�g���󂯎���āCRDF�G�f�B�^���̎w�肳�ꂽ�m�[�h��I������
	 *  
	 */
	protected void selectRDFNodes(Set nodes) {
		Set selectionCells = new HashSet();
		RDFGraph graph = mr3.getRDFGraph();

		for (Iterator node = nodes.iterator(); node.hasNext();) {
			String uri = (String) node.next();
			addRDFNode(graph, uri, selectionCells);
		}
		graph.setSelectionCells(selectionCells.toArray());
	}

	private void addRDFNode(RDFGraph graph, String uri, Set selectionCells) {
		Object[] cells = graph.getAllCells();
		for (int i = 0; i < cells.length; i++) {
			if (graph.isRDFResourceCell(cells[i])) {
				RDFResourceInfo info = resInfoMap.getCellInfo(cells[i]);
				if (uri.equals(info.getURIStr())) {
					selectionCells.add(cells[i]);
				}
			} else if (graph.isRDFPropertyCell(cells[i])) {
				Object propCell = rdfsInfoMap.getEdgeInfo(cells[i]);
				RDFSInfo info = rdfsInfoMap.getCellInfo(propCell);
				if (uri.equals(info.getURIStr())) {
					selectionCells.add(cells[i]);
				}
			}
		}
	}

	/**
	 * 
	 * URI������̃Z�b�g���󂯎���āC�N���X�G�f�B�^���̎w�肳�ꂽ�m�[�h��I������
	 *  
	 */
	protected void selectClassNodes(Set nodes) {
		Set selectionCells = new HashSet();
		RDFGraph graph = mr3.getClassGraph();
		graph.clearSelection();
		for (Iterator i = nodes.iterator(); i.hasNext();) {
			Object cell = rdfsInfoMap.getClassCell(ResourceFactory.createResource((String) i.next()));
			if (cell != null) {
				selectionCells.add(cell);
			}
		}
		graph.setSelectionCells(selectionCells.toArray());
	}

	/**
	 * 
	 * URI������̃Z�b�g���󂯎���āC�v���p�e�B�G�f�B�^���̎w�肳�ꂽ�m�[�h��I������
	 *  
	 */
	protected void selectPropertyNodes(Set nodes) {
		Set selectionCells = new HashSet();
		RDFGraph graph = mr3.getPropertyGraph();
		graph.clearSelection();
		for (Iterator i = nodes.iterator(); i.hasNext();) {
			Object cell = rdfsInfoMap.getPropertyCell(ResourceFactory.createResource((String) i.next()));
			if (cell != null) {
				selectionCells.add(cell);
			}
		}
		graph.setSelectionCells(selectionCells.toArray());
	}

	/**
	 * 
		 * URI������̃Z�b�g���󂯎���āCRDF�G�f�B�^���̎w�肳�ꂽ�m�[�h���O���[�v������
	 *  
	 */
	protected void groupRDFNodes(Set nodes) {
		selectRDFNodes(nodes);
		RDFGraph graph = mr3.getRDFGraph();
		GroupAction.group(graph, graph.getSelectionCells());
	}

	/**
	 * 
	 * URI������̃Z�b�g���󂯎���āCRDF�G�f�B�^���̎w�肳�ꂽ�m�[�h���O���[�v������
	 *  
	 */
	protected void unGroupRDFNodes(Set nodes) {
		selectRDFNodes(nodes);
		RDFGraph graph = mr3.getRDFGraph();
		UnGroupAction.ungroup(graph, graph.getSelectionCells());
	}

	/**
	 * 
	 * URI������̃Z�b�g���󂯎���āC�N���X�G�f�B�^���̎w�肳�ꂽ�m�[�h���O���[�v������
	 *  
	 */
	protected void groupClassNodes(Set nodes) {
		selectClassNodes(nodes);
		RDFGraph graph = mr3.getClassGraph();
		GroupAction.group(graph, graph.getSelectionCells());
	}

	/**
	 * 
	 * URI������̃Z�b�g���󂯎���āC�N���X�G�f�B�^���̎w�肳�ꂽ�m�[�h���O���[�v������
	 *  
	 */
	protected void unGroupClassNodes(Set nodes) {
		selectClassNodes(nodes);
		RDFGraph graph = mr3.getClassGraph();
		UnGroupAction.ungroup(graph, graph.getSelectionCells());
	}

	/**
	 * 
	 * URI������̃Z�b�g���󂯎���āC�v���p�e�B�G�f�B�^���̎w�肳�ꂽ�m�[�h���O���[�v������
	 *  
	 */
	protected void groupPropertyNodes(Set nodes) {
		selectPropertyNodes(nodes);
		RDFGraph graph = mr3.getPropertyGraph();
		GroupAction.group(graph, graph.getSelectionCells());
	}

	/**
	 * 
	 * URI������̃Z�b�g���󂯎���āC�v���p�e�B�G�f�B�^���̎w�肳�ꂽ�m�[�h���O���[�v������
	 *  
	 */
	protected void unGroupPropertyNodes(Set nodes) {
		selectPropertyNodes(nodes);
		RDFGraph graph = mr3.getPropertyGraph();
		UnGroupAction.ungroup(graph, graph.getSelectionCells());
	}

	protected void reverseClassArc() {
		GraphLayoutUtilities.reverseArc(new RDFCellMaker(mr3.getGraphManager()), mr3.getClassGraph());
	}

	protected void reversePropertyArc() {
		GraphLayoutUtilities.reverseArc(new RDFCellMaker(mr3.getGraphManager()), mr3.getPropertyGraph());
	}
}
