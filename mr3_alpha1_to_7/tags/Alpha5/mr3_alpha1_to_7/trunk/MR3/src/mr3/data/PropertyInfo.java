package mr3.data;
import java.util.*;

import com.hp.hpl.mesa.rdf.jena.common.*;
import com.hp.hpl.mesa.rdf.jena.model.*;
import com.hp.hpl.mesa.rdf.jena.vocabulary.*;

/**
 * @author user
 *
 */
public class PropertyInfo extends RDFSInfo {

	private Set domain;
	private Set range;
	transient private Set subProperties;
	transient private Set supProperties;

	public PropertyInfo(String uri, URIType type) {
		super(uri, type);
		domain = new HashSet();
		range = new HashSet();
		subProperties = new HashSet();
		supProperties = new HashSet();
	}

	public PropertyInfo(PropertyInfo info) {
		super(info);
		domain = new HashSet(info.getDomain());
		range = new HashSet(info.getRange());
		subProperties = new HashSet();
		supProperties = new HashSet();
	}

	public Set getRDFSSubList() {
		return Collections.unmodifiableSet(subProperties);
	}

	public Model getModel(String baseURI) {
		try {
			Model tmpModel = super.getModel();
			Resource res = null;

			if (uriType == URIType.URI) {
				res = new ResourceImpl(uri);
			} else {
				res = new ResourceImpl(baseURI + uri);
			}

			tmpModel.add(tmpModel.createStatement(res, RDF.type, RDF.Property));
			for (Iterator i = supRDFS.iterator(); i.hasNext();) {
				RDFSInfo propInfo = rdfsInfoMap.getCellInfo(i.next());
				if (!propInfo.getURI().equals(MR3Resource.Property)) { // MRCUBE.Property�́C���[�g�ɂȂ��Ă��邾���Ȃ̂ŁD
					if (propInfo.getURIType() == URIType.URI) {
						tmpModel.add(tmpModel.createStatement(res, RDFS.subPropertyOf, propInfo.getURI()));
					} else {
						tmpModel.add(tmpModel.createStatement(res, RDFS.subPropertyOf, new ResourceImpl(baseURI + propInfo.getURIStr())));
					}
				}
			}
			for (Iterator i = domain.iterator(); i.hasNext();) {
				RDFSInfo classInfo = rdfsInfoMap.getCellInfo(i.next());
				if (classInfo.getURIType() == URIType.URI) {
					tmpModel.add(tmpModel.createStatement(res, RDFS.domain, classInfo.getURI()));
				} else {
					tmpModel.add(tmpModel.createStatement(res, RDFS.domain, new ResourceImpl(baseURI + classInfo.getURIStr())));
				}
			}
			for (Iterator i = range.iterator(); i.hasNext();) {
				RDFSInfo classInfo = rdfsInfoMap.getCellInfo(i.next());
				if (classInfo.getURIType() == URIType.URI) {
					tmpModel.add(tmpModel.createStatement(res, RDFS.range, classInfo.getURI()));
				} else {
					tmpModel.add(tmpModel.createStatement(res, RDFS.domain, new ResourceImpl(baseURI + classInfo.getURIStr())));
				}
			}
			return tmpModel;
		} catch (RDFException e) {
			e.printStackTrace();
		}
		return model;
	}

	public void addDomain(Object resource) {
		domain.add(resource);
	}

	public void addAllDomain(Set set) {
		domain.addAll(set);
	}

	public void removeNullDomain() {
		for (Iterator i = domain.iterator(); i.hasNext();) {
			Object cell = i.next();
			if (rdfsInfoMap.getCellInfo(cell) == null) {
				domain.remove(cell);
			}
		}
	}

	public void removeDomain(Object obj) {
		domain.remove(obj);
	}

	public Set getDomain() {
		return Collections.unmodifiableSet(domain);
	}

	public void addRange(Object resource) {
		range.add(resource);
	}

	public void addAllRange(Set set) {
		range.addAll(set);
	}

	public void removeNullRange() {
		for (Iterator i = range.iterator(); i.hasNext();) {
			Object cell = i.next();
			if (rdfsInfoMap.getCellInfo(cell) == null) {
				range.remove(cell);
			}
		}
	}

	public void removeRange(Object obj) {
		range.remove(obj);
	}

	public Set getRange() {
		return Collections.unmodifiableSet(range);
	}

	public void addSubProperty(Object resource) {
		subProperties.add(resource);
	}

	public void addSupProperty(Object resource) {
		supProperties.add(resource);
	}

	public Set getSupProperties() {
		return Collections.unmodifiableSet(supProperties);
	}

	public String toString() {
		String msg = super.toString();

		if (domain.size() > 0) {
			msg += "domain: " + domain.toString() + "\n";
		}
		if (range.size() > 0) {
			msg += "range: " + range.toString() + "\n";
		}
		if (subProperties.size() > 0) {
			msg += "SubProperty: " + subProperties.toString() + "\n";
		}
		if (supProperties.size() > 0) {
			msg += "SuperProperty: " + supProperties.toString() + "\n";
		}
		msg += getModelString();
		return msg;
	}
}