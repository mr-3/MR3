/*
 * MR^3�̃v���O�C�����쐬���邽�߂̃N���X
 *
 */
package jp.ac.shizuoka.cs.panda.mmm.mr3.plugin;

import javax.swing.*;

import jp.ac.shizuoka.cs.panda.mmm.mr3.*;

import org.jgraph.*;

import com.hp.hpl.jena.rdf.model.*;

/**
 * @author takeshi morita
 */
public abstract class MR3Plugin {

	private MR3 mr3;
	private String menuName;

	protected MR3Plugin(String mn) {
		menuName = mn;
	}

	protected MR3Plugin() {
		menuName = "none";
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMR3(MR3 mr3) {
		this.mr3 = mr3;
	}

	/**
	 * MR3Plugin�N���X�̃T�u�N���X�Ŏ�������D
	 * File->plugins�ɒǉ�����郁�j���[�����s����ƁCexec���\�b�h�����s�����D
	 */
	public abstract void exec();

	public String toString() {
		return menuName;
	}

	/** 
	 *  Jena���񋟂���Model���CMR3��RDF�O���t�֕ϊ�����D
	 *  �ϊ�����RDF�O���t��ҏW����RDF�O���t�ƒu������D
	 */
	protected void replaceRDFModel(Model model) {
		mr3.replaceRDFModel(model);
		mr3.getGraphManager().applyTreeLayout();
	}

	/** 
	 *  Jena���񋟂���Model���CMR3��RDF�O���t�֕ϊ�����D
	 *  �ϊ�����RDF�O���t��ҏW����RDF�O���t�Ƀ}�[�W����D
	 */
	protected void mergeRDFModel(Model model) {
		mr3.mergeRDFModel(model);
		mr3.getGraphManager().applyTreeLayout();
	}

	/** 
	 *  Jena���񋟂���Model���CMR3��RDFS�O���t�֕ϊ�����D
	 *  �ϊ�����RDFS�O���t��ҏW����RDFS�O���t�Ƀ}�[�W����D
	 */
	protected void mergeRDFSModel(Model model) {
		mr3.mergeRDFSModel(model);
		mr3.getGraphManager().applyTreeLayout();
	}

	/**
	 * Jena���񋟂���Model���CMR^3�̃v���W�F�N�g�֕ϊ�����D
	 * @param model MR^3�̃v���W�F�N�g�t�@�C���D 
	 */
	protected void replaceProjectModel(Model model) {
		mr3.replaceProjectModel(model);
	}

	/**
	 * MR3��RDF�O���t��Jena��Model�ɕϊ�����D
	 */
	protected Model getRDFModel() {
		return mr3.getRDFModel();
	}

	/**
	 * �I������Ă���MR3��RDF�O���t��Jena��Model�ɕϊ�����D
	 */
	protected Model getSelectedRDFModel() {
		return mr3.getSelectedRDFModel();
	}

	/**
	 * MR3��RDFS�O���t��Jena��Model�ɕϊ�����D
	 */
	protected Model getRDFSModel() {
		return mr3.getRDFSModel();
	}

	/**
	 * �I������Ă���MR3��RDFS�O���t��Jena��Model�ɕϊ�����D
	 */
	protected Model getSelectedRDFSModel() {
		return mr3.getSelectedRDFSModel();
	}

	/**
	 * MR3�̃N���X�O���t��Jena��Model�ɕϊ�����D
	 * @return Model
	 */
	protected Model getClassModel() {
		return mr3.getClassModel();
	}

	/**
	 * �I������Ă���MR3�̃N���X�O���t��Jena��Model�ɕϊ�����D
	 * @return Model
	 */
	protected Model getSelectedClassModel() {
		return mr3.getSelectedClassModel();
	}

	/**
	 * MR3�̃v���p�e�B�O���t��Jena��Model�ɕϊ�����D
	 * @return Model
	 */
	protected Model getPropertyModel() {
		return mr3.getPropertyModel();
	}

	/**
	 * �I������Ă���MR3�̃v���p�e�B�O���t��Jena��Model�ɕϊ�����D
	 * @return Model
	 */
	protected Model getSelectedPropertyModel() {
		return mr3.getSelectedPropertyModel();
	}

	/**
	 * �v���W�F�N�g��Jena��Model�ɕϊ�����D
	 * @return Model
	 */
	protected Model getProjectModel() {
		return mr3.getProjectModel();
	}

	/**
	 * RDF�O���t(org.jgraph.JGraph)�𓾂�D
	 * @return org.jgraph.JGraph
	 */
	protected JGraph getRDFGraph() {
		return mr3.getRDFGraph();
	}

	/**
	 * �N���X�O���t(org.jgraph.JGraph)�𓾂�D
	 * @return org.jgraph.JGraph
	 */
	protected JGraph getClassGraph() {
		return mr3.getClassGraph();
	}

	/**
	 * �v���p�e�B�O���t(org.jgraph.JGraph)�𓾂�D
	 * @return org.jgraph.JGraph
	 */
	protected JGraph getPropertyGraph() {
		return mr3.getPropertyGraph();
	}

	/**
	 * BaseURI�𓾂�D
	 * @return String
	 */
	protected String getBaseURI() {
		return mr3.getBaseURI();
	}

	/**
	 * JDesktopPane�𓾂�D�����E�B���h�E���쐬����ۂɗp����D
	 * @return JDesktopPane
	 */
	protected JDesktopPane getDesktopPane() {
		return mr3.getDesktopPane();
	}
}
