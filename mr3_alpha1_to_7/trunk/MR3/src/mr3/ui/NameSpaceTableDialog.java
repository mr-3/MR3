package mr3.ui;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import mr3.data.*;
import mr3.jgraph.*;

import com.hp.hpl.mesa.rdf.jena.vocabulary.*;

/**
 *  2002/12/29 NameSpaceTableDialog
 *
 * ���O��ԂƐړ����̑Ή��t�����e�[�u���ōs��
 * �`�F�b�N�ɂ��CClass, Property, Resource�̖��O��Ԃ�ړ���
 * �Œu��������
 * ���O��Ԃ́CRDFResource, Class, Property�̂��ꂼ��Ŏg�p
 * ����Ă�����̂�I���ł���D
 * �e�[�u���ɒǉ��������O��Ԃ͑I��������폜�����
 * �e�[�u������Ή��\���폜����ƁC�I��������폜����Ă������O���
 * ��I���ł���悤�ɂȂ�
 * �ړ����̖��O�ύX�̓e�[�u������s��
 *
 * @auther takeshi morita
 */

public class NameSpaceTableDialog extends JInternalFrame implements ActionListener, TableModelListener, Serializable {

	private Map prefixNSMap;
	private JTable nsTable;
	private NSTableModel nsTableModel;
	
	private static final long serialVersionUID = 5974381131839067739L;		
		
	transient private JButton addNSButton;
	transient private JButton removeNSButton;
	transient private JButton getNSButton;
	transient private JButton closeButton;
	transient private JTextField prefixField;
	transient private JLabel nsLabel;

	transient private GridBagLayout gbLayout;
	transient private GridBagConstraints gbc;
	transient private JPanel inlinePanel;

	transient private GraphManager gmanager;
	transient private SelectNameSpaceDialog nsDialog;
	transient private JCheckBoxMenuItem showNSTable;
	
	public NameSpaceTableDialog(GraphManager manager) {
		super("NameSpace Table", false, true, false);

		gmanager = manager;
		prefixNSMap = new HashMap();
		nsDialog = new SelectNameSpaceDialog(gmanager, prefixNSMap);
		initTable();
		inlinePanel = new JPanel();
		initGridLayout();
		setTableLayout();
		setInputLayout();
		showNSTable = new JCheckBoxMenuItem("Show NameSpace Table", true);
		showNSTable.addActionListener(new CloseNSTableAction());
		getContentPane().add(inlinePanel);

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		//����Ƃ��ɁCsetVisible(false�j�ɂ���
		addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosing(InternalFrameEvent e) {
				setVisible(false);
			}
		});

		setDefaultNSPrefix();
		setSize(new Dimension(780, 170));
		setLocation(10, 350);
		setVisible(false);
	}

	public void setDefaultNSPrefix() {
		addNameSpaceTable(new Boolean(true), "rdf", RDF.getURI());
		addNameSpaceTable(new Boolean(true), "rdfs", RDFS.getURI());
		changeCellView();
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
			addNameSpaceTable((Boolean) model.getValueAt(i, 0), (String) model.getValueAt(i, 1), (String) model.getValueAt(i, 2));
		}
		// ������prefixNSMap��ݒ肵�Ȃ��ƁC��̓��e�����ɖ߂����Ƃ��ł��Ȃ��D(non valid�ƂȂ�j
		prefixNSMap.putAll(map);
		changeCellView();
	}

	class CloseNSTableAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			setVisible(showNSTable.getState());
		}
	}

	public void setVisible(boolean b) {
		super.setVisible(b);
		if (showNSTable != null) {
			showNSTable.setState(b);
		}
	}

	public JCheckBoxMenuItem getShowNSTable() {
		return showNSTable;
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
		Object[] columnNames = new Object[] { "available", "prefix", "URI" };
		nsTableModel = new NSTableModel(columnNames, 0);
		nsTableModel.addTableModelListener(this);
		nsTable = new JTable(nsTableModel);
		nsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		TableColumnModel tcModel = nsTable.getColumnModel();
		tcModel.getColumn(0).setPreferredWidth(50);
		tcModel.getColumn(1).setPreferredWidth(100);
		tcModel.getColumn(2).setPreferredWidth(500);
	}

	private void initGridLayout() {
		gbLayout = new GridBagLayout();
		gbc = new GridBagConstraints();
		inlinePanel.setLayout(gbLayout);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
	}

	private void setTableLayout() {
		JScrollPane nsTableScroll = new JScrollPane(nsTable);
		nsTableScroll.setPreferredSize(new Dimension(750, 80));
		nsTableScroll.setMinimumSize(new Dimension(750, 80));
		gbLayout.setConstraints(nsTableScroll, gbc);
		inlinePanel.add(nsTableScroll);
	}

	private void setInputLayout() {
		addNSButton = new JButton("�{");
		addNSButton.addActionListener(this);

		removeNSButton = new JButton("�\");
		removeNSButton.addActionListener(this);

		getNSButton = new JButton("GetNS");
		getNSButton.addActionListener(this);

		closeButton = new JButton("Close");
		closeButton.addActionListener(this);

		prefixField = new JTextField(8);
		prefixField.setBorder(BorderFactory.createTitledBorder("Prefix"));
		prefixField.setPreferredSize(new Dimension(50, 40));
		prefixField.setMinimumSize(new Dimension(50, 40));

		nsLabel = new JLabel();
		nsLabel.setBorder(BorderFactory.createTitledBorder("NameSpace"));
		nsLabel.setPreferredSize(new Dimension(400, 40));
		nsLabel.setMinimumSize(new Dimension(400, 40));

		JPanel inline = new JPanel();
		inline.add(prefixField);
		inline.add(nsLabel);
		inline.add(addNSButton);
		inline.add(removeNSButton);
		inline.add(getNSButton);
		inline.add(closeButton);
		gbLayout.setConstraints(inline, gbc);
		inlinePanel.add(inline);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addNSButton) {
			addNameSpaceTable(new Boolean(true), prefixField.getText(), nsLabel.getText());
			changeCellView();
		} else if (e.getSource() == removeNSButton) {
			removeNameSpaceTable();
		} else if (e.getSource() == getNSButton) {
			getNameSpace();
		} else if (e.getSource() == closeButton) {
			setVisible(false);
		}
	}

	/** prefix ����łȂ����C���łɓo�^����Ă�����̂ł͂Ȃ��ꍇtrue */
	private boolean isValidPrefix(String prefix) {
		Set keySet = prefixNSMap.keySet();
		return (!keySet.contains(prefix) && !prefix.equals(""));
	}

	/** ns����ł�null�ł��Ȃ��ꍇ */
	private boolean isValidNS(String ns) {
		return (ns != null && !ns.equals(""));
	}

	private void addNameSpaceTable(Boolean isAvailable, String prefix, String ns) {
		if (isValidPrefix(prefix) && isValidNS(ns)) {
			prefixNSMap.put(prefix, ns);
			Object[] list = new Object[] { isAvailable, prefix, ns };
			nsTableModel.insertRow(nsTableModel.getRowCount(), list);
			prefixField.setText("");
			nsLabel.setText("");
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
		prefixNSMap.remove(nsTableModel.getValueAt(row, 1));
		nsTableModel.removeRow(row);
		changeCellView();
	}

	private void changeCellView() {
		gmanager.setPrefixNSInfoSet(getPrefixNSInfoSet());
		gmanager.changeCellView();
	}

	private void getNameSpace() {
		nsDialog.setVisible(true);
		String uri = (String) nsDialog.getValue();
		if (uri != null) {
			nsLabel.setText(uri);
			nsLabel.setToolTipText(uri);
		}
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

	class NSTableModel extends DefaultTableModel implements Serializable {
		
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
				if (isValidPrefix(prefix)) {
					String oldPrefix = (String) nsTableModel.getValueAt(rowIndex, columnIndex);
					prefixNSMap.remove(oldPrefix);
					String ns = (String) nsTableModel.getValueAt(rowIndex, 2);
					prefixNSMap.put(prefix, ns);
				} else {
					return;
				}
			}
			super.setValueAt(aValue, rowIndex, columnIndex);
			changeCellView();
		}
	}
}
