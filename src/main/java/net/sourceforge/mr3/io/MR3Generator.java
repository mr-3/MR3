/*
 * Project Name: MR^3 (Meta-Model Management based on RDFs Revision Reflection)
 * Project Website: http://mr3.sourceforge.net/
 * 
 * Copyright (C) 2003-2015 Yamaguchi Laboratory, Keio University. All rights reserved. 
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

package net.sourceforge.mr3.io;

import java.util.*;

import net.sourceforge.mr3.*;
import net.sourceforge.mr3.data.*;
import net.sourceforge.mr3.jgraph.*;

import org.jgraph.graph.*;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;

/*
 * 
 * @author Takeshi Morita
 * 
 */
public class MR3Generator {

	private GraphManager gmanager;

	public MR3Generator(GraphManager gm) {
		gmanager = gm;
	}

	public Model getPropertyModel(boolean isSelected) {
		RDFGraph graph = gmanager.getCurrentPropertyGraph();
		Object[] cells = null;
		if (isSelected) {
			cells = graph.getAllSelectedCells();
		} else {
			cells = graph.getAllCells();
		}
		Model propertyModel = ModelFactory.createDefaultModel();
		createPropertyModel(graph, cells, propertyModel);
		return propertyModel;
	}

	private void createPropertyModel(RDFGraph graph, Object[] cells, Model propertyModel) {
		for (Object cell : cells) {
			if (RDFGraph.isRDFSPropertyCell(cell)) {
				PropertyInfo info = (PropertyInfo) GraphConstants.getValue(((GraphCell) cell)
						.getAttributes());
				Set<GraphCell> supProperties = graph.getTargetCells((DefaultGraphCell) cell);
				info.setSuperRDFS(supProperties);
				if (!info.getURI().equals(MR3Resource.Property)) {
					propertyModel.add(info.getModel());
				}
			}
		}
	}

	public Model getClassModel(boolean isSelected) {
		RDFGraph graph = gmanager.getCurrentClassGraph();
		Object[] cells = null;
		if (isSelected) {
			cells = graph.getAllSelectedCells();
		} else {
			cells = graph.getAllCells();
		}
		Model classModel = ModelFactory.createDefaultModel();
		createClassModel(graph, cells, classModel);
		return classModel;
	}

	private void createClassModel(RDFGraph graph, Object[] cells, Model classModel) {
		for (Object cell : cells) {
			if (RDFGraph.isRDFSClassCell(cell)) {
				DefaultGraphCell classCell = (DefaultGraphCell) cell;
				ClassInfo info = (ClassInfo) GraphConstants.getValue(classCell.getAttributes());
				Set<GraphCell> supClasses = graph.getTargetCells(classCell);
				info.setSuperRDFS(supClasses);
				classModel.add(info.getModel());
			}
		}
	}

	private void setResourceType(Model rdfModel, GraphCell cell) {
		RDFResourceInfo resInfo = (RDFResourceInfo) GraphConstants.getValue(cell.getAttributes());
		if (resInfo.hasType()) {
			rdfModel.add(rdfModel.createStatement(resInfo.getURI(), RDF.type, resInfo.getType()));
		}
	}

	/**
	 * Edgeのリストを得て，rdf:typeのStatementsを作成．
	 */
	private Object[] getEdges(Model rdfModel, Object[] cells) {
		if (cells != null) {
			List<GraphCell> result = new ArrayList<GraphCell>();
			for (int i = 0; i < cells.length; i++) {
				GraphCell cell = (GraphCell) cells[i];
				if (RDFGraph.isEdge(cell)) {
					result.add(cell);
				} else if (!RDFGraph.isTypeCell(cell) && RDFGraph.isRDFResourceCell(cell)) {
					setResourceType(rdfModel, cell);
				}
			}
			return result.toArray();
		}
		return null;
	}

	public Model getRDFModel(boolean isSelected) {
		Object[] edges = null;
		Object[] cells = null;
		RDFGraph graph = gmanager.getCurrentRDFGraph();
		Model rdfModel = ModelFactory.createDefaultModel();
		if (isSelected) {
			edges = getEdges(rdfModel, graph.getAllSelectedCells());
			cells = graph.getAllSelectedCells();
		} else {
			edges = getEdges(rdfModel, graph.getAllCells());
			cells = graph.getAllCells();
		}
		createRDFModel(graph, rdfModel, edges);
		addRDFModel(cells, rdfModel);

		return rdfModel;
	}

	private void addRDFModel(Object[] cells, Model rdfModel) {
		for (Object cell : cells) {
			if (RDFGraph.isRDFResourceCell(cell)) {
				RDFResourceInfo info = (RDFResourceInfo) GraphConstants.getValue(((GraphCell) cell)
						.getAttributes());
				rdfModel.add(info.getModel(info.getURI()));
			}
		}
	}

	private Property getRDFProperty(Edge edge) {
		if (edge.getAttributes() == null
				|| !(GraphConstants.getValue(edge.getAttributes()) instanceof RDFSInfo)) {
			return MR3Resource.Nil;
		}

		RDFSInfo propInfo = (RDFSInfo) GraphConstants.getValue(edge.getAttributes());
		Property property = null;
		if (propInfo == null) {
			property = MR3Resource.Nil;
		} else {
			property = ResourceFactory.createProperty(propInfo.getURI().getURI());
		}
		return property;
	}

	/*
	 * JenaのRDFModelにrdf:liを含むStatementを与えるとエラーが表示されるため，
	 * subjectとなるリソースごとにrdf:liの番号を割り振る
	 */
	private Property getRDFLIProperty(Resource subject, Property property,
			Map<Resource, Integer> containerNumMap) {
		if (property.getURI().equals(RDF.getURI() + "li")) {
			if (containerNumMap.get(subject) != null) {
				Integer num = containerNumMap.get(subject);
				num = new Integer(num.intValue() + 1);
				// System.out.println(num);
				property = RDF.li(num.intValue());
				containerNumMap.put(subject, num);
			} else {
				containerNumMap.put(subject, new Integer(1));
				property = RDF.li(1);
			}
		}
		return property;
	}

	private void createRDFModel(RDFGraph graph, Model rdfModel, Object[] edges) {
		// rdf:liを利用していた場合に，subjectとなるリソースごとに番号を割り振るために利用
		Map<Resource, Integer> containerNumMap = new HashMap<Resource, Integer>();
		for (int i = 0; i < edges.length; i++) {
			Edge edge = (Edge) edges[i];
			GraphCell sourceCell = (GraphCell) graph.getSourceVertex(edge);
			RDFResourceInfo info = (RDFResourceInfo) GraphConstants.getValue(sourceCell
					.getAttributes());
			Resource subject = info.getURI();
			Property property = getRDFProperty(edge);
			property = getRDFLIProperty(subject, property, containerNumMap);
			GraphCell targetCell = (GraphCell) graph.getTargetVertex(edge);

			if (RDFGraph.isRDFResourceCell(targetCell)) {
				info = (RDFResourceInfo) GraphConstants.getValue(targetCell.getAttributes());
				rdfModel.add(rdfModel.createStatement(subject, property, info.getURI()));
			} else if (RDFGraph.isRDFLiteralCell(targetCell)) {
				// MR3Literal to Literal
				MR3Literal literal = (MR3Literal) GraphConstants.getValue(targetCell
						.getAttributes());
				rdfModel.add(rdfModel.createStatement(subject, property, literal.getLiteral()));
			}
		}
	}

}
