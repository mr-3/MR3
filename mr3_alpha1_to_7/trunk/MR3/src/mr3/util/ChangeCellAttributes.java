/*
 * Created on 2003/05/25
 *
 */
package mr3.util;

import java.awt.*;
import java.util.*;

import com.jgraph.*;
import com.jgraph.graph.*;

/**
 * @author takeshi morita
 */
public class ChangeCellAttributes {

	/**
	 *  
	 * �Z���̐F��ύX���邽�߂̃��\�b�h
	 * @param graph
	 * @param cell
	 * @param color
	 */
	public static void changeCellColor(JGraph graph, GraphCell cell, Color color) {
		if (cell != null) {
			Map map = cell.getAttributes();
			GraphConstants.setBackground(map, color);
			map = GraphConstants.cloneMap(map);
			map.remove(GraphConstants.BOUNDS);
			map.remove(GraphConstants.POINTS);

			Map nested = new HashMap();
			nested.put(cell, GraphConstants.cloneMap(map));
			graph.getGraphLayoutCache().edit(nested, null, null, null);
		}
	}

}
