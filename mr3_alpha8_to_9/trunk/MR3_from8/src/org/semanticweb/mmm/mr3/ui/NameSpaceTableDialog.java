package org.semanticweb.mmm.mr3.ui;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import org.semanticweb.mmm.mr3.data.*;
import org.semanticweb.mmm.mr3.jgraph.*;
import org.semanticweb.mmm.mr3.util.*;

import com.hp.hpl.jena.vocabulary.*;

/**
 *
 * ���O��ԂƐړ����̑Ή��t�����e�[�u���ōs��
 * �`�F�b�N�ɂ��CClass, Property, Resource�̖��O��Ԃ�ړ����Œu��������
 * �ړ����̖��O�ύX�̓e�[�u������s�����Ƃ��ł���
 *
 * @author takeshi morita
 */

public class NameSpaceTableDialog extends JInternalFrame implements ActionListener, TableModelListener, Serializable {

	private Map prefixNSMap;
	private JTable nsTable;
	private NSTableModel nsTableModel;

	private static final long serialVersionUID = 5974381131839067739L;

	transient private JButton addNSButton;
	transient private JButton removeNSButton;
	transient private JButton closeButton;
	transient private JTextField prefixField;
	transient private JTextField nsField;
	transient private JPanel inlinePanel;

	transient private GraphManager gmanager;

	private static final String WARNING=Translator.getString("Warning");
	private static final ImageIcon ICON = Utilities.getImageIcon(Translator.getString("NameSpaceTable.Icon"));

	public NameSpaceTableDialog(GraphManager manager) {
		super(Translator.getString("NameSpaceTable.Title"), true, true, false);

		gmanager = manager;
		prefixNSMap = new HashMap();
		initTable();
		inlinePanel = new JPanel();
		inlinePanel.setLayout(new BorderLayout());
		setTableLayout();
		setInputLayout();
		getContentPane().add(inlinePanel);

		setFrameIcon(ICON);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosing(InternalFrameEvent e) {
				setVisible(false);
			}
		});

		setDefaultNSPrefix();
		setSize(new Dimension(750, 210));
		setLocation(10, 100);
		setVisible(false);
	}

	//	baseURI��rdf, rdfs, mr3�̏ꍇ�����邽��
	private void addDefaultNS(String prefix, String addNS) {
		if (!isValidPrefix(prefix)) {
			prefix = getMR3Prefix(addNS);
		}
		if (isValidNS(addNS)) {
			addNameSpaceTable(new Boolean(true), prefix, addNS);
		}
	}

	public void setDefaultNSPrefix() {
		addDefaultNS("mr3", MR3Resource.getURI());
		addDefaultNS("base", gmanager.getBaseURI());
		addDefaultNS("rdf", RDF.getURI());
		addDefaultNS("rdfs", RDFS.getURI());
		changeCellView();
	}

	private String getKnownPrefix(String ns) {
		if (ns.equals("http://purl.org/dc/elements/1.1/")) {
			return "dc";
		} else if (ns.equals("http://purl.org/rss/1.0/")) {
			return "rss";
		} else if (ns.equals("http://xmlns.com/foaf/0.1/")) {
			return "foaf";
		} else if (ns.equals("http://www.w3.org/2002/07/owl#")) {
			return "owl";
		} else {
			return "prefix";
		}
	}

	private String getMR3Prefix(String ns) {
		String nextPrefix = getKnownPrefix(ns);
		for (int i = 0; true; i++) {
			String cnt = Integer.toString(i);
			if (isValidPrefix(nextPrefix + "_" + cnt)) {
				nextPrefix = nextPrefix +  "_" + cnt;
				break;
			}
		}
		return nextPrefix;
	}

	public void setCurrentNSPrefix() {
		Set allNSSet = gmanager.getAllNameSpaceSet();
		for (Iterator i = allNSSet.iterator(); i.hasNext();) {
			String ns = (String) i.next();
			if (isValidNS(ns)) {
				String knownPrefix = getKnownPrefix(ns);
				if (isValidPrefix(knownPrefix) && (!knownPrefix.equals("prefix"))) {
					addNameSpaceTable(new Boolean(true), knownPrefix, ns);
				} else {
					addNameSpaceTable(new Boolean(true), getMR3Prefix(ns), ns);
				}
			}
		}
		changeCellView();
	}

	public NSTableModel getNSTableModel() {
		return nsTableModel;
	}

	public Serializable getState() {
		ArrayList list = new ArrayList();
		list.add(prefixNSMap);
		list.add(nsTableModel);
		return list;
	}

	public void loadState(List list) {
		Map map = (Map) list.get(0);
		NSTableModel model = (NSTableModel) list.get(1);
		for (int i = 0; i < model.getRowCount(); i++) {
			Boolean isAvailable = (Boolean) model.getValueAt(i, 0);
			String prefix = (String) model.getValueAt(i, 1);
			String ns = (String) model.getValueAt(i, 2);
			if (isValidPrefix(prefix) && isValidNS(ns)) {
				addNameSpaceTable(isAvailable, prefix, ns);
			}
		}
		// ������prefixNSMap��ݒ肵�Ȃ��ƁC��̓��e�����ɖ߂����Ƃ��ł��Ȃ��D(non valid�ƂȂ�j
		prefixNSMap.putAll(map);
		changeCellView();
	}

	public void resetNSTable() {
		prefixNSMap = new HashMap();
		// ��C�ɂ��ׂč폜������@���킩��Ȃ��D
		while (nsTableModel.getRowCount() != 0) {
			nsTableModel.removeRow(nsTableModel.getRowCount() - 1);
		}
		gmanager.setPrefixNSInfoSet(new HashSet());
	}

	private void initTable() {
		Object[] columnNames = new Object[] { Translator.getString("Available"), Translator.getString("Prefix"), "URI" };
		nsTableModel = new NSTableModel(columnNames, 0);
		nsTableModel.addTableModelListener(this);
		nsTable = new JTable(nsTableModel);
		nsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		TableColumnModel tcModel = nsTable.getColumnModel();
		tcModel.getColumn(0).setPreferredWidth(50);
		tcModel.getColumn(1).setPreferredWidth(100);
		tcModel.getColumn(2).setPreferredWidth(450);
	}

	private void setTableLayout() {
		JScrollPane nsTableScroll = new JScrollPane(nsTable);
		nsTableScroll.setPreferredSize(new Dimension(700, 115));
		nsTableScroll.setMinimumSize(new Dimension(700, 115));
		inlinePanel.add(nsTableScroll, BorderLayout.CENTER);
	}

	private void setInputLayout() {
		addNSButton = new JButton(Translator.getString("Add"));
		addNSButton.addActionListener(this);

		removeNSButton = new JButton(Translator.getString("Remove"));
		removeNSButton.addActionListener(this);

		closeButton = new JButton(Translator.getString("Close"));
		closeButton.addActionListener(this);

		prefixField = new JTextField(8);
		prefixField.setBorder(BorderFactory.createTitledBorder(MR3Constants.PREFIX));
		prefixField.setPreferredSize(new Dimension(50, 40));
		prefixField.setMinimumSize(new Dimension(50, 40));

		nsField = new JTextField(30);
		nsField.setBorder(BorderFactory.createTitledBorder(MR3Constants.NAME_SPACE));
		nsField.setPreferredSize(new Dimension(400, 40));
		nsField.setMinimumSize(new Dimension(400, 40));

		JPanel inline = new JPanel();
		inline.add(prefixField);
		inline.add(nsField);
		inline.add(addNSButton);
		inline.add(removeNSButton);
		inline.add(closeButton);
		inlinePanel.add(inline, BorderLayout.SOUTH);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addNSButton) {
			addNameSpaceTable(new Boolean(true), prefixField.getText(), nsField.getText());
			changeCellView();
		} else if (e.getSource() == removeNSButton) {
			removeNameSpaceTable();
		} else if (e.getSource() == closeButton) {
			setVisible(false);
		}
	}

	private boolean isValidPrefix(String prefix) {
		Set keySet = prefixNSMap.keySet();
		return (!keySet.contains(prefix) && !prefix.equals(""));
	}

	private boolean isValidNS(String ns) {
		Collection values = prefixNSMap.values();
		return (ns != null && !ns.equals("") && !values.contains(ns));
	}

	/** prefix ����łȂ����C���łɓo�^����Ă��Ȃ��ꍇtrue */
	private boolean isValidPrefixWithWarning(String prefix) {
		if (isValidPrefix(prefix)) {
			return true;
		} else {
			JOptionPane.showInternalMessageDialog(gmanager.getDesktop(), Translator.getString("Warning.Message5"), WARNING, JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	/** ns����ł�null�ł��Ȃ��C���łɓo�^����ĂȂ��ꍇ true */
	private boolean isValidNSWithWarning(String ns) {
		if (isValidNS(ns)) {
			return true;
		} else {
			JOptionPane.showInternalMessageDialog(gmanager.getDesktop(), Translator.getString("Warning.Message6"), WARNING, JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	public void addNameSpaceTable(Boolean isAvailable, String prefix, String ns) {
		if (isValidPrefixWithWarning(prefix) && isValidNSWithWarning(ns)) {
			prefixNSMap.put(prefix, ns);
			Object[] list = new Object[] { isAvailable, prefix, ns };
			nsTableModel.insertRow(nsTableModel.getRowCount(), list);
			prefixField.setText("");
			nsField.setText("");
		}
	}

	private void removeNameSpaceTable() {
		int[] removeList = nsTable.getSelectedRows();
		int length = removeList.length;
		// �ǂ��������C������row����������̂����悭�킩��Ȃ��D
		// model������������_��row�ԍ����ς���Ă��܂��̂�����
		if (length == 0) {
			return;
		}
		int row = removeList[0];
		String rmPrefix = (String) nsTableModel.getValueAt(row, 1);
		String rmNS = (String) nsTableModel.getValueAt(row, 2);
		if (rmNS.equals(gmanager.getBaseURI())) {
			JOptionPane.showInternalMessageDialog(gmanager.getDesktop(), Translator.getString("Warning.Message7"), WARNING, JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (rmNS.equals(MR3Resource.DefaultURI.getNameSpace())) {
			JOptionPane.showInternalMessageDialog(gmanager.getDesktop(), Translator.getString("Warning.Message8"), WARNING, JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!gmanager.getAllNameSpaceSet().contains(rmNS)) {
			prefixNSMap.remove(rmPrefix);
			nsTableModel.removeRow(row);
			changeCellView();
		} else {
			JOptionPane.showInternalMessageDialog(gmanager.getDesktop(), Translator.getString("Warning.Message9"), WARNING, JOptionPane.ERROR_MESSAGE);
		}
	}

	public void changeCellView() {
		gmanager.setPrefixNSInfoSet(getPrefixNSInfoSet());
		gmanager.changeCellView();
	}

	private boolean isCheckBoxChanged(int type, int column) {
		return (type == TableModelEvent.UPDATE && column == 0);
	}

	/** �e�[�u���̃`�F�b�N�{�b�N�X���`�F�b�N���ꂽ���ǂ��� */
	private boolean isPrefixAvailable(int row, int column) {
		Boolean isPrefixAvailable = (Boolean) nsTableModel.getValueAt(row, column);
		return isPrefixAvailable.booleanValue();
	}

	public void tableChanged(TableModelEvent e) {
		int row = e.getFirstRow();
		int column = e.getColumn();
		int type = e.getType();

		if (isCheckBoxChanged(type, column)) {
			changeCellView();
		}
	}

	private Set getPrefixNSInfoSet() {
		Set infoSet = new HashSet();
		for (int i = 0; i < nsTableModel.getRowCount(); i++) {
			String prefix = (String) nsTableModel.getValueAt(i, 1);
			String ns = (String) nsTableModel.getValueAt(i, 2);
			infoSet.add(new PrefixNSInfo(prefix, ns, isPrefixAvailable(i, 0)));
		}
		return infoSet;
	}

	public class NSTableModel extends DefaultTableModel implements Serializable {

		private static final long serialVersionUID = -5977304717491874293L;

		public NSTableModel(Object[] columnNames, int rowCount) {
			super(columnNames, rowCount);
		}

		public boolean isCellEditable(int row, int col) {
			if (col == 2)
				return false;
			return true;
		}

		public Class getColumnClass(int column) {
			Vector v = (Vector) dataVector.elementAt(0);
			return v.elementAt(column).getClass();
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if (aValue instanceof String) {
				String prefix = (String) aValue;
				// ����prefix�̃`�F�b�N�͂���Ȃ��D
				String oldPrefix = (String) nsTableModel.getValueAt(rowIndex, columnIndex);
				prefixNSMap.remove(oldPrefix);
				String ns = (String) nsTableModel.getValueAt(rowIndex, 2);
				prefixNSMap.put(prefix, ns);
			}
			super.setValueAt(aValue, rowIndex, columnIndex);
			changeCellView();
		}
	}

}