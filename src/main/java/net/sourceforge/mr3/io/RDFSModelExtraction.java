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
public class RDFSModelExtraction {

	private GraphManager gmanager;

	public RDFSModelExtraction(GraphManager gm) {
		gmanager = gm;
	}

	public Model extractClassModel(Model orgModel) {
		Model classModel = ModelFactory.createDefaultModel();
		addInnerClassModel(orgModel);
		Set<Resource> classClassList = gmanager.getClassClassList();
		Set<Resource> findClassClassList = new HashSet<Resource>();
		for (Resource clsClass : classClassList) {
			gmanager.findMetaClass(orgModel, clsClass, findClassClassList);
		}
		classClassList.addAll(findClassClassList);
		// System.out.println(classClassList);
		gmanager.setClassClassList(classClassList);
		Model removeModel = ModelFactory.createDefaultModel();
		for (Resource clsClass : classClassList) {
			for (ResIterator i = orgModel.listSubjectsWithProperty(RDF.type, clsClass); i.hasNext();) {
				Resource classResource = i.nextResource();
				ClassInfo info = getClassResInfo(classResource);
				setDefaultSubClassOf(classResource, info);
				removeModel.add(extractRDFSModel(classModel, classResource, info));
			}
		}
		orgModel.remove(removeModel);
		return classModel;
	}

	// subPropertyOfが省略されたプロパティは，Propertyクラスのサブクラスとみなす、
	public void setDefaultSubPropertyOf(Resource property) {
		if (!property.hasProperty(RDFS.subPropertyOf)) {
			RDFSInfoMap rdfsInfoMap = gmanager.getCurrentRDFSInfoMap();
			rdfsInfoMap.addRootProperties(property);
		}
	}

	// subClassOfで指定されているクラスをオリジナルに追加．（定義されていないかもしれないので）
	private void addInnerClassModel(Model orgModel) {
		Model tmpModel = ModelFactory.createDefaultModel();
		for (ResIterator i = orgModel.listSubjectsWithProperty(RDF.type, RDFS.Class); i.hasNext();) {
			Resource classRes = i.nextResource();
			for (StmtIterator j = classRes.listProperties(); j.hasNext();) {
				Statement stmt = j.nextStatement();
				if (stmt.getPredicate().equals(RDFS.subClassOf)) {
					tmpModel.add(ResourceFactory.createStatement((Resource) stmt.getObject(),
							RDF.type, RDFS.Class));
				}
			}
		}
		orgModel.add(tmpModel);
	}

	// subPropertyOfで指定されているプロパティとdomain, rangeで指定されているクラスをオリジナルに追加
	// 先にプロパティを読み込むことで，domain, rangeに指定されたクラスを処理できる
	private void addInnerPropertyModel(Model orgModel) {
		Model tmpModel = ModelFactory.createDefaultModel();
		for (ResIterator i = orgModel.listSubjectsWithProperty(RDF.type, RDF.Property); i.hasNext();) {
			Resource propRes = i.nextResource();
			for (StmtIterator j = propRes.listProperties(); j.hasNext();) {
				Statement stmt = j.nextStatement();
				if (stmt.getPredicate().equals(RDFS.domain)
						|| stmt.getPredicate().equals(RDFS.range)) {
					tmpModel.add(ResourceFactory.createStatement((Resource) stmt.getObject(),
							RDF.type, RDFS.Class));
				} else if (stmt.getPredicate().equals(RDFS.subPropertyOf)) {
					tmpModel.add(ResourceFactory.createStatement((Resource) stmt.getObject(),
							RDF.type, RDF.Property));
				}
			}
		}
		orgModel.add(tmpModel);
	}

	public Model extractPropertyModel(Model orgModel) {
		Model propertyModel = ModelFactory.createDefaultModel();
		addInnerPropertyModel(orgModel);
		Set<Resource> propClassList = gmanager.getPropertyClassList();
		Set<Resource> findPropClassList = new HashSet<Resource>();
		for (Resource propClass : propClassList) {
			gmanager.findMetaClass(orgModel, propClass, findPropClassList);
		}
		propClassList.addAll(findPropClassList);
		// System.out.println(propClassList);
		gmanager.setPropertyClassList(propClassList);
		Model removeModel = ModelFactory.createDefaultModel();
		for (Resource propClass : propClassList) {
			for (ResIterator i = orgModel.listSubjectsWithProperty(RDF.type, propClass); i
					.hasNext();) {
				Resource propClassRes = i.nextResource();
				PropertyInfo info = getPropertyResInfo(propClassRes);
				setDefaultSubPropertyOf(propClassRes);
				removeModel.add(extractRDFSModel(propertyModel, propClassRes, info));
				info.setMetaClass(propClass.toString());
			}
		}
		orgModel.remove(removeModel);

		return propertyModel;
	}

	public Model extractRDFSModel(Model rdfsModel, Resource metaResource, RDFSInfo info) {
		Model removeModel = ModelFactory.createDefaultModel();
		for (StmtIterator i = metaResource.listProperties(); i.hasNext();) {
			Statement stmt = i.nextStatement();
			if (setRDFSInfo(stmt, info)) {
				rdfsModel.add(stmt);
			} else {
				info.addStatement(stmt); // RDFS以外のプロパティを持つ文を保存
			}
			removeModel.add(stmt);
		}

		info.setURI(metaResource.toString());
		RDFSInfoMap rdfsInfoMap = gmanager.getCurrentRDFSInfoMap();
		rdfsInfoMap.putResourceInfo(metaResource, info);

		return removeModel;
	}

	// Resourceクラス以外のsubClassOfが省略されたクラスは，
	// Resourceクラスのサブクラスとみなす
	public void setDefaultSubClassOf(Resource rdfsResource, ClassInfo info) {
		if (!rdfsResource.equals(RDFS.Resource) && !rdfsResource.hasProperty(RDFS.subClassOf)) {
			ClassInfo supResInfo = getClassResInfo(RDFS.Resource);
			supResInfo.addSubClass(rdfsResource);
			info.addSupClass(RDFS.Resource);
			RDFSInfoMap rdfsInfoMap = gmanager.getCurrentRDFSInfoMap();
			rdfsInfoMap.putResourceInfo(RDFS.Resource, supResInfo);
		}
	}

	private boolean setClassInfo(Statement stmt, ClassInfo info) {
		Resource subject = stmt.getSubject();
		Property predicate = stmt.getPredicate();
		RDFNode object = stmt.getObject();

		if (setBaseInfo(stmt, info)) {
			return true;
		}

		if (predicate.equals(RDFS.subClassOf)) { // rdfs:subClassOf
			// subject < object
			// info -> subject info supInfo -> object info
			ClassInfo supResInfo = getClassResInfo((Resource) object);
			supResInfo.addSubClass(subject);
			info.addSupClass((Resource) object);
			RDFSInfoMap rdfsInfoMap = gmanager.getCurrentRDFSInfoMap();
			rdfsInfoMap.putResourceInfo((Resource) object, supResInfo);
		} else {
			return false;
		}

		return true;
	}

	private boolean setPropertyInfo(Statement stmt, PropertyInfo info) {
		Resource subject = stmt.getSubject();
		Property predicate = stmt.getPredicate();
		RDFNode object = stmt.getObject();

		if (setBaseInfo(stmt, info)) {
			return true;
		}

		Resource objectResource = null;
		if (object instanceof Resource) {
			objectResource = (Resource) object;
		}

		if (predicate.equals(RDFS.domain)) { // rdfs:domain
			GraphCell cell = gmanager.getClassCell(objectResource, false);
			info.addDomain(cell);
		} else if (predicate.equals(RDFS.range)) { // rdfs:range
			GraphCell cell = gmanager.getClassCell(objectResource, false);
			info.addRange(cell);
		} else if (predicate.equals(RDFS.subPropertyOf)) { // rdfs:subPropertyOf
			// subject < object
			// info -> subject info supInfo -> object info
			PropertyInfo supResInfo = getPropertyResInfo(objectResource);
			// rdfsInfoMap.addRootProperties((Resource)object); //一時しのぎ
			supResInfo.addSubProperty(subject);
			RDFSInfoMap rdfsInfoMap = gmanager.getCurrentRDFSInfoMap();
			rdfsInfoMap.putResourceInfo(objectResource, supResInfo);
			info.addSupProperty(object);
		} else {
			return false;
		}
		return true;
	}

	private boolean setBaseInfo(Statement stmt, RDFSInfo info) {
		Property predicate = stmt.getPredicate();
		RDFNode object = stmt.getObject();

		if (predicate.equals(RDFS.label)) { // rdfs:label
			MR3Literal literal = new MR3Literal((Literal) object);
			info.addLabel(literal);
		} else if (predicate.equals(RDFS.comment)) { // rdfs:comment
			MR3Literal literal = new MR3Literal((Literal) object);
			info.addComment(literal);
		} else if (predicate.equals(RDF.type)) {
			info.setMetaClass(object.toString());
		} else if (predicate.getURI().matches(MR3Resource.conceptLabel.getURI() + ".*")) {
			// subClassOfやsubPropertyOfと同時に使った場合にのみ有効
			RDFSInfoMap rdfsInfoMap = gmanager.getCurrentRDFSInfoMap();
			rdfsInfoMap.addPropertyLabelModel(stmt);
		} else {
			return false;
		}
		return true;
	}

	private boolean setRDFSInfo(Statement stmt, RDFSInfo info) {
		if (info instanceof ClassInfo) {
			return setClassInfo(stmt, (ClassInfo) info);
		} else if (info instanceof PropertyInfo) {
			return setPropertyInfo(stmt, (PropertyInfo) info);
		} else {
			return false;
		}
	}

	public ClassInfo getClassResInfo(Resource resource) {
		RDFSInfoMap rdfsInfoMap = gmanager.getCurrentRDFSInfoMap();
		ClassInfo info = (ClassInfo) rdfsInfoMap.getResourceInfo(resource);
		if (info == null)
			info = new ClassInfo("");
		return info;
	}

	public PropertyInfo getPropertyResInfo(Resource resource) {
		RDFSInfoMap rdfsInfoMap = gmanager.getCurrentRDFSInfoMap();
		PropertyInfo info = (PropertyInfo) rdfsInfoMap.getResourceInfo(resource);
		if (info == null)
			info = new PropertyInfo("");
		return info;
	}
}
