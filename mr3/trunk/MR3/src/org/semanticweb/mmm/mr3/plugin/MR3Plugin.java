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

package org.semanticweb.mmm.mr3.plugin;

import java.util.*;

import javax.swing.*;
import javax.swing.tree.*;

import org.jgraph.*;
import org.jgraph.graph.*;
import org.semanticweb.mmm.mr3.*;
import org.semanticweb.mmm.mr3.actions.*;
import org.semanticweb.mmm.mr3.data.*;
import org.semanticweb.mmm.mr3.jgraph.*;
import org.semanticweb.mmm.mr3.layout.*;
import org.semanticweb.mmm.mr3.ui.*;
import org.semanticweb.mmm.mr3.util.*;

import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;

/**
 * MR3�̃v���O�C�����쐬���邽�߂̃N���X
 * 
 * @author takeshi morita
 */
public abstract class MR3Plugin {

    private MR3 mr3;
    private String menuName;

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
     * 
     * MR�R�Őݒ肵���o�̓G���R�[�f�B���O�̕������Ԃ�
     * 
     * @return MR3�Őݒ肵���o�̓G���R�[�f�B���O�̕�����iexp. SJIS, EUC_JP, etc)
     */
    protected String getOutputEncoding() {
        return mr3.getUserPrefs().get(PrefConstants.OutputEncoding, "SJIS");
    }

    /**
     * Jena���񋟂���Model���CMR3��RDF�O���t�֕ϊ�����D �ϊ�����RDF�O���t��ҏW����RDF�O���t�ƒu������D
     */
    protected void replaceRDFModel(Model model) {
        mr3.getMR3Reader().replaceRDFModel(model);
    }

    /**
     * Jena���񋟂���Model���CMR3��RDF�O���t�֕ϊ�����D �ϊ�����RDF�O���t��ҏW����RDF�O���t�Ƀ}�[�W����D
     */
    protected void mergeRDFModel(Model model) {
        mr3.getMR3Reader().mergeRDFModelThread(model);
    }

    /**
     * Jena���񋟂���Model���CMR3��RDFS�O���t�֕ϊ�����D �ϊ�����RDFS�O���t��ҏW����RDFS�O���t�Ƀ}�[�W����D
     */
    protected void mergeRDFSModel(Model model) {
        mr3.getMR3Reader().mergeRDFPlusRDFSModel(model);
    }

    /**
     * Jena���񋟂���OntModel���CMR3�̃O���t�֕ϊ�����D �ϊ������O���t��ҏW���̃O���t�Ƀ}�[�W����D
     */
    protected void mergeOntologyModel(OntModel model) {
        mr3.getMR3Reader().mergeOntologyModel(model);
        mr3.getMR3Reader().performTreeLayout();
    }

    /**
     * Jena���񋟂���Model���CMR3�̃v���W�F�N�g�֕ϊ�����D
     * 
     * @param model
     *            MR3�̃v���W�F�N�g�t�@�C���D
     * 
     */
    protected void replaceProjectModel(Model model) {
        mr3.getMR3Reader().replaceProjectModel(model);
    }

    /**
     * MR3��RDF�O���t��Jena��Model�ɕϊ�����D
     */
    protected Model getRDFModel() {
        return mr3.getMR3Writer().getRDFModel();
    }

    /**
     * �I������Ă���MR3��RDF�O���t��Jena��Model�ɕϊ�����D
     */
    protected Model getSelectedRDFModel() {
        return mr3.getMR3Writer().getSelectedRDFModel();
    }

    /**
     * MR3��RDFS�O���t��Jena��Model�ɕϊ�����D
     */
    protected Model getRDFSModel() {
        return mr3.getMR3Writer().getRDFSModel();
    }

    /**
     * �I������Ă���MR3��RDFS�O���t��Jena��Model�ɕϊ�����D
     */
    protected Model getSelectedRDFSModel() {
        return mr3.getMR3Writer().getSelectedRDFSModel();
    }

    /**
     * MR3�̃N���X�O���t��Jena��Model�ɕϊ�����D
     * 
     * @return Model
     */
    protected Model getClassModel() {
        return mr3.getMR3Writer().getClassModel();
    }

    /**
     * MR3�̃N���X�O���t��JTree��TreeModel�ɕϊ�����D
     * 
     * @return TreeModel
     */
    protected TreeModel getClassTreeModel() {
        TreeNode rootNode = MR3TreePanel.getRDFSTreeRoot(mr3.getMR3Writer().getClassModel(), RDFS.Resource,
                RDFS.subClassOf);
        return new DefaultTreeModel(rootNode);
    }

    /**
     * �I������Ă���MR3�̃N���X�O���t��Jena��Model�ɕϊ�����D
     * 
     * @return Model
     */
    protected Model getSelectedClassModel() {
        return mr3.getMR3Writer().getSelectedClassModel();
    }

    /**
     * MR3�̃v���p�e�B�O���t��Jena��Model�ɕϊ�����D
     * 
     * @return Model
     */
    protected Model getPropertyModel() {
        return mr3.getMR3Writer().getPropertyModel();
    }

    /**
     * MR3�̃v���p�e�B�O���t��JTree��TreeModel�ɕϊ�����D
     * 
     * @return TreeModel
     */
    protected TreeModel getPropertyTreeModel() {
        TreeNode rootNode = MR3TreePanel.getRDFSTreeRoot(mr3.getMR3Writer().getPropertyModel(), MR3Resource.Property,
                RDFS.subPropertyOf);
        return new DefaultTreeModel(rootNode);
    }

    /**
     * �I������Ă���MR3�̃v���p�e�B�O���t��Jena��Model�ɕϊ�����D
     * 
     * @return Model
     */
    protected Model getSelectedPropertyModel() {
        return mr3.getMR3Writer().getSelectedPropertyModel();
    }

    /**
     * �v���W�F�N�g��Jena��Model�ɕϊ�����D
     * 
     * @return Model
     */
    protected Model getProjectModel() {
        return mr3.getMR3Writer().getProjectModel();
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
     * 
     * @return JTabbedPane
     */
    protected JTabbedPane getDesktopPane() {
        return mr3.getGraphManager().getDesktopTabbedPane();
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
        for (Object cell : graph.getAllCells()) {
            if (RDFGraph.isRDFResourceCell(cell)) {
                RDFResourceInfo info = (RDFResourceInfo) GraphConstants.getValue(((GraphCell) cell).getAttributes());
                if (uri.equals(info.getURIStr())) {
                    selectionCells.add(cell);
                }
            } else if (RDFGraph.isRDFPropertyCell(cell)) {
                GraphCell propCell = (GraphCell) cell;
                RDFSInfo info = (RDFSInfo) GraphConstants.getValue(propCell.getAttributes());
                if (uri.equals(info.getURIStr())) {
                    selectionCells.add(cell);
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
        RDFSInfoMap rdfsInfoMap = mr3.getGraphManager().getCurrentRDFSInfoMap();
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
        RDFSInfoMap rdfsInfoMap = mr3.getGraphManager().getCurrentRDFSInfoMap();
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
        GroupAction.group(mr3.getRDFGraph());
    }

    /**
     * 
     * URI������̃Z�b�g���󂯎���āCRDF�G�f�B�^���̎w�肳�ꂽ�m�[�h���O���[�v������
     * 
     */
    protected void unGroupRDFNodes(Set nodes) {
        selectRDFNodes(nodes);
        UnGroupAction.ungroup(mr3.getRDFGraph());
    }

    /**
     * 
     * URI������̃Z�b�g���󂯎���āC�N���X�G�f�B�^���̎w�肳�ꂽ�m�[�h���O���[�v������
     * 
     */
    protected void groupClassNodes(Set nodes) {
        selectClassNodes(nodes);
        GroupAction.group(mr3.getClassGraph());
    }

    /**
     * 
     * URI������̃Z�b�g���󂯎���āC�N���X�G�f�B�^���̎w�肳�ꂽ�m�[�h���O���[�v������
     * 
     */
    protected void unGroupClassNodes(Set nodes) {
        selectClassNodes(nodes);
        UnGroupAction.ungroup(mr3.getClassGraph());
    }

    /**
     * 
     * URI������̃Z�b�g���󂯎���āC�v���p�e�B�G�f�B�^���̎w�肳�ꂽ�m�[�h���O���[�v������
     * 
     */
    protected void groupPropertyNodes(Set nodes) {
        selectPropertyNodes(nodes);
        GroupAction.group(mr3.getPropertyGraph());
    }

    /**
     * 
     * URI������̃Z�b�g���󂯎���āC�v���p�e�B�G�f�B�^���̎w�肳�ꂽ�m�[�h���O���[�v������
     * 
     */
    protected void unGroupPropertyNodes(Set nodes) {
        selectPropertyNodes(nodes);
        UnGroupAction.ungroup(mr3.getPropertyGraph());
    }

    /**
     * 
     * URI������̃Z�b�g���󂯎��CRDF�G�f�B�^���̃m�[�h����������
     * 
     * @param nodes
     */
    protected void emphasisRDFNodes(Set nodes) {
        selectRDFNodes(nodes);
        GraphUtilities.emphasisNodes(mr3.getRDFGraph());
    }

    /**
     * 
     * URI������̃Z�b�g���󂯎��C�N���X�G�f�B�^���̃m�[�h����������
     * 
     * @param nodes
     */
    protected void emphasisClassNodes(Set nodes) {
        selectClassNodes(nodes);
        GraphUtilities.emphasisNodes(mr3.getClassGraph());
    }

    /**
     * 
     * URI������̃Z�b�g���󂯎��C�v���p�e�B�G�f�B�^���̃m�[�h����������
     * 
     * @param nodes
     */
    protected void emphasisPropertyNodes(Set nodes) {
        selectPropertyNodes(nodes);
        GraphUtilities.emphasisNodes(mr3.getPropertyGraph());
    }

    protected void reverseClassArc() {
        GraphLayoutUtilities.reverseArc(new MR3CellMaker(mr3.getGraphManager()), mr3.getClassGraph());
    }

    protected void reversePropertyArc() {
        GraphLayoutUtilities.reverseArc(new MR3CellMaker(mr3.getGraphManager()), mr3.getPropertyGraph());
    }
}
