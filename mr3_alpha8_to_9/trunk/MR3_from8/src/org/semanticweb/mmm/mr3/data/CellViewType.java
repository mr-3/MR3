/*
 * @(#) CellViewType.java
 *
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

package org.semanticweb.mmm.mr3.data;

/**
 * Cell��URI�ŕ\�����邩�CID�ŕ\�����邩�C���x���ŕ\�����邩�����߂�D
 *
 * @author takeshi morita
 */
public class CellViewType {
	private String type;
    
	private CellViewType(String t) {
		type = t;
	}

	public static final CellViewType URI = new CellViewType("URI");
	public static final CellViewType ID = new CellViewType("ID");
	public static final CellViewType LABEL = new CellViewType("Label");
	
	public String toString() {
		return type;
	}
}
