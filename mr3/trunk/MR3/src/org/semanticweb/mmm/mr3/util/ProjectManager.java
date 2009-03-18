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

package org.semanticweb.mmm.mr3.util;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import org.jgraph.graph.*;
import org.semanticweb.mmm.mr3.*;
import org.semanticweb.mmm.mr3.data.*;
import org.semanticweb.mmm.mr3.jgraph.*;
import org.semanticweb.mmm.mr3.layout.*;
import org.semanticweb.mmm.mr3.ui.*;
import org.semanticweb.mmm.mr3.ui.NameSpaceTableDialog.*;

import com.hp.hpl.jena.datatypes.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;

/**
 * @author takeshi morita
 */
public class ProjectManager {

    private GraphManager gmanager;
    private NSTableModel nsTableModel;
    private NameSpaceTableDialog nsTableDialog;

    public ProjectManager(GraphManager gm) {
        gmanager = gm;
        nsTableDialog = gmanager.getNSTableDialog();
        nsTableModel = nsTableDialog.getNSTableModel();
    }

    /*
     * RDF���f���̃v���W�F�N�g�̕ۑ��Ɋւ���l��ۑ�
     * 
     */
    private void addRDFProjectModel(Model projectModel) throws RDFException {
        int literal_cnt = 0;
        RDFGraph graph = gmanager.getCurrentRDFGraph();
        Object[] cells = graph.getAllCells();
        for (int i = 0; i < cells.length; i++) {
            GraphCell cell = (GraphCell) cells[i];
            if (RDFGraph.isRDFResourceCell(cell)) {
                addRDFResourceProjectModel(projectModel, cell);
            } else if (RDFGraph.isRDFPropertyCell(cell)) {
                literal_cnt = addRDFLiteralProjectModel(projectModel, literal_cnt, cell);
            }
        }
    }

    /*
     * x,y,width,height��ۑ��D type���Ȃ��ꍇ�ɂ́CEmpty�Ƃ���D
     */
    private void addRDFResourceProjectModel(Model projectModel, GraphCell cell) throws RDFException {
        Rectangle2D rec = GraphConstants.getBounds(cell.getAttributes());
        RDFResourceInfo info = (RDFResourceInfo) GraphConstants.getValue(cell.getAttributes());
        projectModel.add(info.getURI(), MR3Resource.PointX, rec.getX());
        projectModel.add(info.getURI(), MR3Resource.PointY, rec.getY());
        projectModel.add(info.getURI(), MR3Resource.NodeWidth, rec.getWidth());
        projectModel.add(info.getURI(), MR3Resource.NodeHeight, rec.getHeight());
        if (info.getTypeCell() == null) {
            projectModel.add(info.getURI(), RDF.type, MR3Resource.Empty);
        }
    }

    /*
     * ���e�����̏��̕ۑ��D
     * 
     */
    private int addRDFLiteralProjectModel(Model projectModel, int literal_cnt, GraphCell cell) throws RDFException {
        Edge edge = (Edge) cell;
        RDFGraph graph = gmanager.getCurrentRDFGraph();
        GraphCell sourceCell = (GraphCell) graph.getSourceVertex(edge);
        GraphCell targetCell = (GraphCell) graph.getTargetVertex(edge);
        if (RDFGraph.isRDFLiteralCell(targetCell)) {
            RDFResourceInfo info = (RDFResourceInfo) GraphConstants.getValue(sourceCell.getAttributes());

            RDFSInfo propInfo = (RDFSInfo) GraphConstants.getValue(edge.getAttributes());
            Resource litRes = ResourceFactory.createResource(MR3Resource.Literal + Integer.toString(literal_cnt++));
            projectModel.add(litRes, MR3Resource.HasLiteralResource, info.getURI());
            if (propInfo == null) {
                projectModel.add(litRes, MR3Resource.LiteralProperty, MR3Resource.Nil);
            } else {
                projectModel.add(litRes, MR3Resource.LiteralProperty, propInfo.getURI());
            }

            MR3Literal litInfo = (MR3Literal) GraphConstants.getValue(targetCell.getAttributes());
            projectModel.add(litRes, MR3Resource.LiteralLang, litInfo.getLanguage());
            if (litInfo.getDatatype() != null) {
                projectModel.add(litRes, MR3Resource.LiteralDatatype, litInfo.getDatatype().getURI());
            }
            projectModel.add(litRes, MR3Resource.LiteralString, litInfo.getString());

            Rectangle2D rec = GraphConstants.getBounds(targetCell.getAttributes());
            projectModel.add(litRes, MR3Resource.PointX, rec.getX());
            projectModel.add(litRes, MR3Resource.PointY, rec.getY());
            projectModel.add(litRes, MR3Resource.NodeWidth, rec.getWidth());
            projectModel.add(litRes, MR3Resource.NodeHeight, rec.getHeight());
        }
        return literal_cnt;
    }

    private void addRDFSProjectModel(Model projectModel, RDFGraph graph) throws RDFException {
        Object[] cells = graph.getAllCells();
        for (int i = 0; i < cells.length; i++) {
            if (RDFGraph.isRDFSCell(cells[i])) {
                GraphCell cell = (GraphCell) cells[i];
                Rectangle2D rec = GraphConstants.getBounds(cell.getAttributes());
                RDFSInfo info = (RDFSInfo) GraphConstants.getValue(cell.getAttributes());
                projectModel.add(info.getURI(), MR3Resource.PointX, rec.getX());
                projectModel.add(info.getURI(), MR3Resource.PointY, rec.getY());
                projectModel.add(info.getURI(), MR3Resource.NodeWidth, rec.getWidth());
                projectModel.add(info.getURI(), MR3Resource.NodeHeight, rec.getHeight());
            }
        }
    }

    private static final int IS_AVAILABLE_COLUMN = 0;
    private static final int PREFIX_COLUMN = 1;
    private static final int NS_COLUMN = 2;

    private void addPrefixNSProjectModel(Model projectModel) throws RDFException {
        for (int i = 0; i < nsTableModel.getRowCount(); i++) {
            Boolean isAvailable = (Boolean) nsTableModel.getValueAt(i, IS_AVAILABLE_COLUMN);
            String prefix = (String) nsTableModel.getValueAt(i, PREFIX_COLUMN);
            String nameSpace = (String) nsTableModel.getValueAt(i, NS_COLUMN);
            projectModel.add(ResourceFactory.createResource(nameSpace), MR3Resource.IsPrefixAvailable, isAvailable
                    .toString());
            projectModel.add(ResourceFactory.createResource(nameSpace), MR3Resource.Prefix, prefix);
        }
    }

    private void addDefaultLangModel(Model projectModel) throws RDFException {
        projectModel.add(MR3Resource.DefaultURI, MR3Resource.DefaultLang, GraphManager.getDefaultLang());
    }

    /*
     * RDF�C�N���X�C�v���p�e�B�̂��ꂼ��̃v���W�F�N�g�ۑ��ɕK�v�Ȓl���q�c�e���f�� �Ƃ��ĕۑ�����D�i�w�C�x���W�Ȃǁj
     */
    public Model getProjectModel() {
        Model projectModel = ModelFactory.createDefaultModel();
        try {
            addDefaultLangModel(projectModel);
            addRDFProjectModel(projectModel);
            addRDFSProjectModel(projectModel, gmanager.getCurrentClassGraph());
            addRDFSProjectModel(projectModel, gmanager.getCurrentPropertyGraph());
            addPrefixNSProjectModel(projectModel);
        } catch (RDFException e) {
            e.printStackTrace();
        }
        return projectModel;
    }

    /*
     * RDF�̃��f������C���e���������X�e�[�g�����g�W���̃��f���𓾂�
     */
    public Model getLiteralModel(Model model) {
        Model literalModel = ModelFactory.createDefaultModel();
        try {
            for (StmtIterator i = model.listStatements(); i.hasNext();) {
                Statement stmt = i.nextStatement();
                Property predicate = stmt.getPredicate();
                RDFNode object = stmt.getObject();
                if (object instanceof Literal && !(predicate.equals(RDFS.label) || predicate.equals(RDFS.comment))) {
                    literalModel.add(stmt);
                }
            }
        } catch (RDFException e) {
            e.printStackTrace();
        }
        return literalModel;
    }

    private boolean hasProjectPredicate(Statement stmt) {
        return (stmt.getPredicate().equals(MR3Resource.DefaultLang) || stmt.getPredicate().equals(MR3Resource.PointX)
                || stmt.getPredicate().equals(MR3Resource.PointY) || stmt.getPredicate().equals(MR3Resource.NodeWidth)
                || stmt.getPredicate().equals(MR3Resource.NodeHeight)
                || stmt.getPredicate().equals(MR3Resource.LiteralProperty)
                || stmt.getPredicate().equals(MR3Resource.HasLiteralResource)
                || stmt.getPredicate().equals(MR3Resource.LiteralLang)
                || stmt.getPredicate().equals(MR3Resource.LiteralDatatype)
                || stmt.getPredicate().equals(MR3Resource.LiteralString)
                || stmt.getPredicate().equals(MR3Resource.Prefix) || stmt.getPredicate().equals(
                MR3Resource.IsPrefixAvailable));
    }

    public Model extractProjectModel(Model model) {
        Model extractModel = ModelFactory.createDefaultModel();
        try {
            for (StmtIterator i = model.listStatements(); i.hasNext();) {
                Statement stmt = i.nextStatement();
                if (hasProjectPredicate(stmt)) {
                    extractModel.add(stmt);
                }
            }
            model.remove(extractModel);
        } catch (RDFException e) {
            e.printStackTrace();
        }

        setCellLayoutMap(extractModel);
        return extractModel;
    }

    private void changeNSModel(Map<String, String> uriPrefixMap, Map<String, Boolean> uriIsAvailableMap) {
        Set<String> existNSSet = new HashSet<String>();
        for (int i = 0; i < nsTableModel.getRowCount(); i++) {
            String nameSpace = (String) nsTableModel.getValueAt(i, NS_COLUMN);
            String prefix = uriPrefixMap.get(nameSpace);
            Boolean isAvailable = uriIsAvailableMap.get(nameSpace);

            if (prefix == null || isAvailable == null) {
                continue;
            }

            if (!nsTableModel.getValueAt(i, PREFIX_COLUMN).equals(prefix)) {
                nsTableModel.setValueAt(prefix, i, PREFIX_COLUMN);
            }
            nsTableModel.setValueAt(isAvailable, i, IS_AVAILABLE_COLUMN);
            existNSSet.add(nameSpace);
        }

        Collection<String> notExistNSSet = uriPrefixMap.keySet();
        notExistNSSet.removeAll(existNSSet);
        for (String nameSpace : notExistNSSet) {
            String prefix = uriPrefixMap.get(nameSpace);
            Boolean isAvailable = uriIsAvailableMap.get(nameSpace);
            nsTableDialog.addNameSpaceTable(isAvailable, prefix, nameSpace);
        }
    }

    public void removeEmptyClass() {
        RDFGraph graph = gmanager.getCurrentRDFGraph();
        Object[] cells = graph.getAllCells();
        for (int i = 0; i < cells.length; i++) {
            GraphCell cell = (GraphCell) cells[i];
            if (RDFGraph.isRDFResourceCell(cell)) {
                RDFResourceInfo info = (RDFResourceInfo) GraphConstants.getValue(cell.getAttributes());
                if (info.getType().equals(MR3Resource.Empty)) {
                    info.setTypeCell(null, gmanager.getCurrentRDFGraph());
                    GraphConstants.setValue(cell.getAttributes(), info);
                }
            }
        }
        graph = gmanager.getCurrentClassGraph();
        RDFSInfoMap rdfsInfoMap = gmanager.getCurrentRDFSInfoMap();
        Object cell = rdfsInfoMap.getClassCell(MR3Resource.Empty);
        graph.clearSelection();
        graph.setSelectionCell(cell);
        gmanager.removeAction(graph);
    }

    private int getStmtCount(Model model) {
        int total = 0;
        for (Iterator i = model.listStatements(); i.hasNext();) {
            i.next();
            total++;
        }
        return total;
    }

    private static Map<RDFNode, GraphLayoutData> layoutMap;

    public static Map<RDFNode, GraphLayoutData> getLayoutMap() {
        return layoutMap;
    }

    public void initLayoutMap() {
        layoutMap = null;
    }

    public void setCellLayoutMap(Model model) {
        layoutMap = new HashMap<RDFNode, GraphLayoutData>();
        try {
            for (StmtIterator i = model.listStatements(); i.hasNext();) {
                Statement stmt = i.nextStatement();
                GraphLayoutData data = layoutMap.get(stmt.getSubject());
                if (data == null) {
                    data = new GraphLayoutData(stmt.getSubject());
                }
                if (stmt.getPredicate().equals(MR3Resource.PointX)) {
                    int x = (int) Float.parseFloat(stmt.getObject().toString());
                    Point2D.Double point = data.getPosition();
                    data.setPosition(x, point.y);
                } else if (stmt.getPredicate().equals(MR3Resource.PointY)) {
                    int y = (int) Float.parseFloat(stmt.getObject().toString());
                    Point2D.Double point = data.getPosition();
                    data.setPosition(point.x, y);
                } else if (stmt.getPredicate().equals(MR3Resource.NodeWidth)) {
                    int width = (int) Float.parseFloat(stmt.getObject().toString());
                    Dimension dimension = data.getBoundingBox();
                    data.setBoundingBox(width, dimension.height);
                } else if (stmt.getPredicate().equals(MR3Resource.NodeHeight)) {
                    int height = (int) Float.parseFloat(stmt.getObject().toString());
                    Dimension dimension = data.getBoundingBox();
                    data.setBoundingBox(dimension.width, height);
                }
                layoutMap.put(stmt.getSubject(), data);
            }
        } catch (RDFException e) {
            e.printStackTrace();
        }
    }

    public void loadProject(Model model) {
        Map<Resource, MR3Literal> uriNodeInfoMap = new HashMap<Resource, MR3Literal>(); // ���\�[�X�̂t�q�h��MR3Literal�̃}�b�v
        Map<String, String> uriPrefixMap = new HashMap<String, String>(); // URI�ƃv���t�B�b�N�X�̃}�b�v
        Map<String, Boolean> uriIsAvailableMap = new HashMap<String, Boolean>(); // URI��isAvailable(boolean)�̃}�b�v

        try {
            MR3.STATUS_BAR.initNormal(getStmtCount(model));
            for (StmtIterator i = model.listStatements(); i.hasNext();) {
                Statement stmt = i.nextStatement();
                MR3Literal rec = uriNodeInfoMap.get(stmt.getSubject());
                if (rec == null) {
                    rec = new MR3Literal();
                    uriNodeInfoMap.put(stmt.getSubject(), rec);
                }
                if (stmt.getPredicate().equals(MR3Resource.DefaultLang)) {
                    gmanager.setDefaultLang(stmt.getObject().toString());
                } else if (stmt.getPredicate().equals(MR3Resource.PointX)) {
                    setPositionX(uriNodeInfoMap, stmt, rec);
                } else if (stmt.getPredicate().equals(MR3Resource.PointY)) {
                    setPositionY(uriNodeInfoMap, stmt, rec);
                } else if (stmt.getPredicate().equals(MR3Resource.NodeWidth)) {
                    setNodeWidth(uriNodeInfoMap, stmt, rec);
                } else if (stmt.getPredicate().equals(MR3Resource.NodeHeight)) {
                    setNodeHeight(uriNodeInfoMap, stmt, rec);
                } else if (stmt.getPredicate().equals(MR3Resource.LiteralLang)) {
                    rec.setLanguage(stmt.getObject().toString());
                    uriNodeInfoMap.put(stmt.getSubject(), rec);
                } else if (stmt.getPredicate().equals(MR3Resource.LiteralDatatype)) {
                    rec.setDatatype((RDFDatatype) stmt.getObject());
                    uriNodeInfoMap.put(stmt.getSubject(), rec);
                } else if (stmt.getPredicate().equals(MR3Resource.LiteralString)) {
                    rec.setString(stmt.getObject().toString());
                    uriNodeInfoMap.put(stmt.getSubject(), rec);
                } else if (stmt.getPredicate().equals(MR3Resource.Prefix)) {
                    uriPrefixMap.put(stmt.getSubject().getURI(), stmt.getObject().toString());
                } else if (stmt.getPredicate().equals(MR3Resource.HasLiteralResource)) {
                    rec.setResource(stmt.getObject().toString());
                    uriNodeInfoMap.put(stmt.getSubject(), rec);
                } else if (stmt.getPredicate().equals(MR3Resource.LiteralProperty)) {
                    rec.setProperty(stmt.getObject().toString());
                    uriNodeInfoMap.put(stmt.getSubject(), rec);
                } else if (stmt.getPredicate().equals(MR3Resource.IsPrefixAvailable)) {
                    if (stmt.getObject().toString().equals("true")) {
                        uriIsAvailableMap.put(stmt.getSubject().getURI(), new Boolean(true));
                    } else {
                        uriIsAvailableMap.put(stmt.getSubject().getURI(), new Boolean(false));
                    }
                }
                MR3.STATUS_BAR.addValue();
            }
        } catch (RDFException e) {
            e.printStackTrace();
        }
        gmanager.setNodeBounds(uriNodeInfoMap);
        changeNSModel(uriPrefixMap, uriIsAvailableMap);
        MR3.STATUS_BAR.hideProgressBar();
        initLayoutMap();
    }

    private void setNodeWidth(Map<Resource, MR3Literal> uriNodeInfoMap, Statement stmt, MR3Literal rec) {
        int width = (int) Float.parseFloat(stmt.getObject().toString());
        rec.setWidth(width);
        uriNodeInfoMap.put(stmt.getSubject(), rec);
    }

    private void setNodeHeight(Map<Resource, MR3Literal> uriNodeInfoMap, Statement stmt, MR3Literal rec) {
        int height = (int) Float.parseFloat(stmt.getObject().toString());
        rec.setHeight(height);
        uriNodeInfoMap.put(stmt.getSubject(), rec);
    }

    private void setPositionY(Map<Resource, MR3Literal> uriNodeInfoMap, Statement stmt, MR3Literal rec) {
        int y = (int) Float.parseFloat(stmt.getObject().toString());
        rec.setY(y);
        uriNodeInfoMap.put(stmt.getSubject(), rec);
    }

    private void setPositionX(Map<Resource, MR3Literal> uriNodeInfoMap, Statement stmt, MR3Literal rec) {
        int x = (int) Float.parseFloat(stmt.getObject().toString());
        rec.setX(x);
        uriNodeInfoMap.put(stmt.getSubject(), rec);
    }

}
