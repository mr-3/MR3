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

package org.semanticweb.mmm.mr3.layout;

import java.util.*;

import org.jgraph.graph.*;
import org.semanticweb.mmm.mr3.jgraph.*;
import org.semanticweb.mmm.mr3.util.*;

/**
 * @author takeshi morita
 */
public class JGraphTreeLayout {

    private GraphManager gmanager;
    private MR3CellMaker cellMaker;

    public JGraphTreeLayout(GraphManager gm) {
        gmanager = gm;
        cellMaker = new MR3CellMaker(gmanager);
    }

    public void performJGraphTreeLayout() {
        gmanager.removeTypeCells();
        performJGraphTreeLayout(gmanager.getCurrentRDFGraph(), GraphLayoutUtilities.getJGraphRDFLayoutDirection(), 200,
                20);
        gmanager.addTypeCells();
        performJGraphRDFSTreeLayout();
    }

    public void performJGraphRDFSTreeLayout() {
        RDFGraph classGraph = gmanager.getCurrentClassGraph();
        RDFGraph propGraph = gmanager.getCurrentPropertyGraph();
        GraphLayoutUtilities.reverseArc(cellMaker, classGraph);
        GraphLayoutUtilities.reverseArc(cellMaker, propGraph);
        performJGraphTreeLayout(classGraph, GraphLayoutUtilities.getJGraphClassLayoutDirection(), 30, 50);
        performJGraphTreeLayout(propGraph, GraphLayoutUtilities.getJGraphPropertyLayoutDirection(), 30, 50);
        GraphLayoutUtilities.reverseArc(cellMaker, classGraph);
        GraphLayoutUtilities.reverseArc(cellMaker, propGraph);
        gmanager.clearSelection();
    }

    public void performJGraphTreeLayout(RDFGraph graph, int orientation, int distance, int border) {
        Map<Object, GraphLayoutData> cellLayoutMap = new HashMap<Object, GraphLayoutData>();
        Set<GraphLayoutData> dataSet = GraphLayoutUtilities.initGraphLayoutData(graph, cellLayoutMap);
        Set<DefaultGraphCell> rootCells = new HashSet<DefaultGraphCell>();

        for (GraphLayoutData data : dataSet) {
            DefaultGraphCell cell = (DefaultGraphCell) data.getCell();
            GraphLayoutUtilities.addChild(graph, cell, data, cellLayoutMap);
            if (!data.hasParent()) {
                rootCells.add(cell);
            }
        }
        RDFGraph rdfGraph = gmanager.getCurrentRDFGraph();

        Object tmpRoot = null;
        if (rootCells.size() != 1) {
            tmpRoot = GraphLayoutUtilities.collectRoot(rdfGraph, cellMaker, rootCells, dataSet, cellLayoutMap);
        }

        TreeLayoutAlgorithm treeLayout = new TreeLayoutAlgorithm(orientation, distance, border);
        if (tmpRoot != null) {
            treeLayout.perform(graph, new Object[] { tmpRoot});
        } else {
            treeLayout.perform(graph, rootCells.toArray());
        }
        dataSet.remove(cellLayoutMap.get(tmpRoot));
        GraphLayoutUtilities.removeTemporaryRoot(rdfGraph, (DefaultGraphCell) tmpRoot);
        GraphLayoutUtilities.centralizeGraph(graph);
    }
}