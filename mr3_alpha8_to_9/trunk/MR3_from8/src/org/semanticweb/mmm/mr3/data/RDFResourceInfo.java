package org.semanticweb.mmm.mr3.data;
import java.io.*;
import java.util.*;

import org.jgraph.graph.*;

import com.hp.hpl.jena.mem.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.model.impl.*;

public class RDFResourceInfo implements Serializable {

	private Object typeCell; // RDFS Class�ɑΉ�����Cell��ێ�����
	private Set typeCells; // RDF���\�[�X�̃^�C�v�𕡐��ێ�����D
	private GraphCell typeViewCell; // RDF Resource�ɂ���`��Cell��ێ�����

	private String uri;
	private URIType uriType;
	private String uriTypeStr;
	transient private RDFSInfoMap rdfsInfoMap = RDFSInfoMap.getInstance();
	private static final long serialVersionUID = -2998293866936983365L;

	public RDFResourceInfo(URIType type, String uri, GraphCell typeCell) {
		setURIType(type);
		this.uri = uri;
		this.typeViewCell = typeCell;
	}

	public RDFResourceInfo(RDFResourceInfo info) {
		uri = info.getURIStr();
		setURIType(info.getURIType());
		typeViewCell = (GraphCell) info.getTypeViewCell();
		typeCell = info.getTypeCell();
	}

	private static Model anonModel = new ModelMem();

	/** RDF��Type�̌�������ς��� */
	public void setTypeCellValue(GraphCell cell) {
		Object value = null;
		if (cell != null) {
			Map map = cell.getAttributes();
			value = GraphConstants.getValue(map);
		} else {
			value = "";
		}

		Map typeMap = typeViewCell.getAttributes();
		GraphConstants.setValue(typeMap, value);
		typeViewCell.changeAttributes(typeMap);
	}

	public boolean equals(Object o) {
		String uriStr = (String) o;
		return uriStr.equals(uri);
	}

	public void setTypeCellValue() {
		if (typeViewCell != null) { // �����[�g����鎞�CtypeViewCell�����Ȃ�����
			setTypeCellValue((GraphCell) typeCell);
		}
	}

	public void setTypeCell(Object type) {
		this.typeCell = type;
		setTypeCellValue();
	}

	public Resource getType() {
		RDFSInfo info = rdfsInfoMap.getCellInfo(typeCell);
		if (info == null || info.getURI() == null) {
			return new ResourceImpl("");
		} else {
			return info.getURI();
		}
	}

	public Object getTypeCell() {
		return typeCell;
	}

	public void setTypeViewCell(GraphCell cell) {
		typeViewCell = cell;
		setTypeCellValue();
	}

	public Object getTypeViewCell() {
		return typeViewCell;
	}

	public void setURIType(URIType type) {
		uriType = type;
		uriTypeStr = type.toString();
	}

	public URIType getURIType() {
		return uriType;
	}

	public void setURI(String str) {
		uri = str;
	}

	public Resource getURI() {
		if (uriType == URIType.ANONYMOUS) {
			return new ResourceImpl(new AnonId(uri));
		} else {
			return new ResourceImpl(uri);
		}
	}

	public String getURIStr() {
		return uri;
	}

	public String toString() {
		String msg = "URIType: " + uriType + "\n";
		msg += "URI: " + uri + "\n";
		msg += "Type: " + typeCell + "\n";
		return msg;
	}
}
