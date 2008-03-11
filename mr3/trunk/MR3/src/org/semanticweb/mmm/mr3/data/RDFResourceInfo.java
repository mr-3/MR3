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

package org.semanticweb.mmm.mr3.data;

import java.io.*;
import java.util.*;

import org.jgraph.graph.*;
import org.semanticweb.mmm.mr3.*;
import org.semanticweb.mmm.mr3.data.MR3Constants.*;
import org.semanticweb.mmm.mr3.jgraph.*;
import org.semanticweb.mmm.mr3.util.*;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.model.impl.*;

public class RDFResourceInfo extends ResourceInfo implements Serializable {

    private GraphCell typeCell;
    // private Set typeCells; // RDF���\�[�X�̃^�C�v�𕡐��ێ�����D
    private GraphCell typeViewCell; // RDF Resource�ɂ���`��Cell��ێ�����

    private String uri;
    private URIType uriType;
    private static final long serialVersionUID = -2998293866936983365L;

    // �����p
    private Resource typeRes;

    public RDFResourceInfo(URIType type, String uri) {
        setURIType(type);
        this.uri = uri;
        labelList = new ArrayList<MR3Literal>();
        commentList = new ArrayList<MR3Literal>();
    }

    public RDFResourceInfo(RDFResourceInfo info) {
        setURIType(info.getURIType());
        if (info.getURIType() == URIType.ANONYMOUS) {
            uri = ResourceFactory.createResource().toString();
        } else {
            uri = info.getURIStr();
        }
        typeCell = info.getTypeCell();
        labelList = new ArrayList<MR3Literal>(info.getLabelList());
        commentList = new ArrayList<MR3Literal>(info.getCommentList());
    }

    public RDFSInfo getTypeInfo() {
        if (MR3.OFF_META_MODEL_MANAGEMENT) {
            if (typeRes != null) {
                ClassInfo tmpInfo = new ClassInfo("");
                tmpInfo.setURI(typeRes.getURI());
                return tmpInfo;
            }
            return NULL_INFO;
        }

        if (typeCell == null) { return NULL_INFO; }
        return (RDFSInfo) GraphConstants.getValue(typeCell.getAttributes());
    }

    public boolean equals(Object o) {
        if (o instanceof String) { return o.equals(uri); }
        RDFResourceInfo info = (RDFResourceInfo) o;
        return info.getURIStr().equals(uri);
    }

    public boolean isSameInfo(RDFResourceInfo resInfo) {
        return resInfo.getURIType().equals(uriType) && resInfo.getURIStr().equals(uri)
                && resInfo.getType().equals(getType());
    }

    private static final ClassInfo NULL_INFO = new ClassInfo("");

    public void setTypeCell(GraphCell cell) {
        if (MR3.OFF_META_MODEL_MANAGEMENT) {
            if (cell != null) {
                RDFSInfo info = (RDFSInfo) GraphConstants.getValue(cell.getAttributes());
                typeRes = info.getURI();
            } else {
                typeRes = null;
            }
            if (typeViewCell != null) { // �����[�g����鎞�CtypeViewCell�����Ȃ�����
                if (typeRes != null) {
                    GraphConstants.setValue(typeViewCell.getAttributes(), typeRes);
                } else {
                    GraphConstants.setValue(typeViewCell.getAttributes(), "");
                }
                typeViewCell.getAttributes().applyMap(typeViewCell.getAttributes());
            }
            return;
        }

        typeCell = cell;
        if (typeViewCell != null) { // �����[�g����鎞�CtypeViewCell�����Ȃ�����
            GraphConstants.setValue(typeViewCell.getAttributes(), getTypeInfo());
            typeViewCell.getAttributes().applyMap(typeViewCell.getAttributes());
        }
    }

    public boolean hasType() {
        return getTypeInfo() != NULL_INFO;
    }

    public Resource getType() {
        return getTypeInfo().getURI();
    }

    public GraphCell getTypeCell() {
        return typeCell;
    }

    public void setTypeViewCell(GraphCell cell) {
        typeViewCell = cell;
        if (typeViewCell != null) {
            GraphConstants.setValue(typeViewCell.getAttributes(), getTypeInfo());
            typeViewCell.getAttributes().applyMap(typeViewCell.getAttributes());
        }
    }

    public Object getTypeViewCell() {
        return typeViewCell;
    }

    public void setURIType(URIType type) {
        uriType = type;
    }

    public URIType getURIType() {
        return uriType;
    }

    public void setURI(String str) {
        uri = str;
    }

    public Resource getURI() {
        if (uriType == URIType.ANONYMOUS) { return new ResourceImpl(new AnonId(uri)); }
        return ResourceFactory.createResource(uri);
    }

    public String getURIStr() {
        return uri;
    }

    public String getStatus() {
        String msg = "URIType: " + uriType + "\n";
        msg += "URI: " + uri + "\n";
        msg += "Type: " + getTypeInfo().getURIStr() + "\n";
        return msg;
    }

    public String toString() {
        if (uriType == URIType.ANONYMOUS) { return ""; }
        switch (GraphManager.cellViewType) {
        case URI:
            return GraphUtilities.getNSPrefix(getURI());
        case LABEL:
            if (getDefaultLabel(GraphManager.getDefaultLang()) != null) {
                return getDefaultLabel(GraphManager.getDefaultLang()).getString();
            } else if (getFirstLabel() != null) { return getFirstLabel().getString(); }
            break;
        case ID:
            Resource resource = getURI();
            if (resource.getLocalName().length() != 0) { return resource.getLocalName(); }
            break;
        }
        return GraphUtilities.getNSPrefix(getURI());
    }
}
