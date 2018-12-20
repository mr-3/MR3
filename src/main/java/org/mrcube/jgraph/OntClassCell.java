/*
 * Project Name: MR^3 (Meta-Model Management based on RDFs Revision Reflection)
 * Project Website: http://mrcube.org/
 * 
 * Copyright (C) 2003-2018 Yamaguchi Laboratory, Keio University. All rights reserved.
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

package org.mrcube.jgraph;

import org.jgraph.graph.DefaultGraphCell;
import org.mrcube.utils.GraphUtilities;

import java.awt.*;

/**
 * @author Takeshi Morita
 */
public class OntClassCell extends DefaultGraphCell implements RDFCellStyleChanger {

	public static Color classColor = new Color(204, 255, 102);

	public OntClassCell() {
		this(null);
	}

	public OntClassCell(Object userObject) {
		super(userObject);
	}

	public void changeStyle(RDFGraph graph) {
		GraphUtilities.changeCellStyle(graph, this, GraphUtilities.selectedColor,
				GraphUtilities.selectedBorderColor);
	}

	public void changeDefaultStyle(RDFGraph graph) {
		GraphUtilities.changeDefaultCellStyle(graph, this, classColor);
	}
}