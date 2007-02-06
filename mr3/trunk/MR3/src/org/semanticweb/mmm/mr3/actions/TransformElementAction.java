/*
 * @(#) TransformElementAction.java
 * 
 * 
 * Copyright (C) 2003 The MMM Project
 * 
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *  
 */

package org.semanticweb.mmm.mr3.actions;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import org.jgraph.graph.*;
import org.semanticweb.mmm.mr3.data.*;
import org.semanticweb.mmm.mr3.data.MR3Constants.*;
import static org.semanticweb.mmm.mr3.data.MR3Constants.GraphType.CLASS;
import static org.semanticweb.mmm.mr3.data.MR3Constants.GraphType.PROPERTY;
import static org.semanticweb.mmm.mr3.data.MR3Constants.GraphType.RDF;
import org.semanticweb.mmm.mr3.jgraph.*;
import org.semanticweb.mmm.mr3.util.*;

/**
 * @author takeshi morita
 */
public class TransformElementAction extends AbstractAction {

    private Set<String> uriSet;
    private RDFGraph graph;
    private GraphType fromGraphType;
    private GraphType toGraphType;
    private GraphManager gmanager;

    // private RDFSInfoMap rdfsInfoMap = RDFSInfoMap.getInstance();

    public TransformElementAction(RDFGraph g, GraphManager gm, GraphType fromType, GraphType toType) {
        super(Translator.getString("Action.TransformElement." + fromType + "To" + toType + ".Text"));
        graph = g;
        gmanager = gm;
        fromGraphType = fromType;
        toGraphType = toType;
    }

    private void setURISet() {
        uriSet = new HashSet<String>();
        Object[] cells = graph.getDescendants(graph.getSelectionCells());
        for (int i = 0; i < cells.length; i++) {
            GraphCell cell = (GraphCell) cells[i];

            if (fromGraphType == GraphType.RDF && RDFGraph.isRDFResourceCell(cell)) {
                RDFResourceInfo info = (RDFResourceInfo) GraphConstants.getValue(cell.getAttributes());
                uriSet.add(info.getURIStr());
            } else if (fromGraphType == GraphType.CLASS && RDFGraph.isRDFSClassCell(cell)) {
                RDFSInfo info = (RDFSInfo) GraphConstants.getValue(cell.getAttributes());
                uriSet.add(info.getURIStr());
            } else if (fromGraphType == GraphType.PROPERTY && RDFGraph.isRDFSPropertyCell(cell)) {
                RDFSInfo info = (RDFSInfo) GraphConstants.getValue(cell.getAttributes());
                uriSet.add(info.getURIStr());
            }
            // RDFSプロパティとクラスが重複してしまうため，複雑な処理が必要．
            // else if (graph.isRDFPropertyCell(cell)) {
            // Object propCell = rdfsInfoMap.getEdgeInfo(cell);
            // RDFSInfo info = rdfsInfoMap.getCellInfo(propCell);
            // resSet.add(info.getURI());
            // }
        }
        // System.out.println(uriSet);
    }

    private void insertElements(Set uriSet) {
        Point pt = new Point(100, 100);
        MR3CellMaker cellMaker = new MR3CellMaker(gmanager);
        for (Iterator i = uriSet.iterator(); i.hasNext();) {
            String uri = (String) i.next();

            switch (toGraphType) {
            case RDF:
                cellMaker.insertRDFResource(pt, uri, null, URIType.URI);
                break;
            case CLASS:
                cellMaker.insertClass(pt, uri);
                break;
            case PROPERTY:
                cellMaker.insertProperty(pt, uri);
                break;
            }
            pt.x += 20;
            pt.y += 20;
        }
    }

    class TransformThread extends Thread {
        public void run() {
            while (gmanager.getRemoveDialog().isVisible()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
            if (isRmCellsRemoved()) {
                insertElements(uriSet);
            }
        }
    }

    private boolean isRmCellsRemoved() {
        Object[] cells = gmanager.getRemoveCells();
        for (int i = 0; i < cells.length; i++) {
            if (gmanager.getRDFGraph().getModel().contains(cells[i])
                    || gmanager.getClassGraph().getModel().contains(cells[i])
                    || gmanager.getPropertyGraph().getModel().contains(cells[i])) { return false; }
        }
        return true;
    }

    public void actionPerformed(ActionEvent e) {
        setURISet();
        gmanager.removeAction(graph);
        // 削除した時に，メタモデル管理が行われるが，その間にinsertされないようにするための仕掛け
        // モーダルにできれば，いいが．．．
        new TransformThread().start();
    }

}
