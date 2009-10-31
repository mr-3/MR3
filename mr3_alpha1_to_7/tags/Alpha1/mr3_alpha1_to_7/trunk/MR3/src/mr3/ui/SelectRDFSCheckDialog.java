package mr3.ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import mr3.data.*;

/**
 * @author takeshi morita
 *
 */
public class SelectRDFSCheckDialog extends JDialog implements ActionListener {

	private JLabel label;
	private JRadioButton renameButton;
	private JRadioButton createButton;
	private JButton okButton;
	private JButton cancelButton;
	private CreateRDFSType type;

	SelectRDFSCheckDialog(String title) {
		super((Frame) null, title, true);
		label = new JLabel("Not defined. Choose one select.");
		ButtonGroup group = new ButtonGroup();
		renameButton = new JRadioButton("Rename");
		createButton = new JRadioButton("Create");
		createButton.setSelected(true);
		group.add(renameButton);
		group.add(createButton);
		okButton = new JButton("OK");
		okButton.addActionListener(this);
		cancelButton = new JButton("CANCEL");
		cancelButton.addActionListener(this);
		createPane();
		setLocation(300, 300);
		setSize(new Dimension(200, 100));
		setVisible(true);
	}

	private void createPane() {
		Container contentPane = getContentPane();
		JPanel inline = new JPanel();
		inline.setLayout(new BoxLayout(inline, BoxLayout.Y_AXIS));
		inline.add(renameButton);
		inline.add(createButton);
		contentPane.add(inline, BorderLayout.CENTER);
		contentPane.add(okButton, BorderLayout.SOUTH);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cancelButton) {
			type = null;
		} else {
			if (renameButton.isSelected()) {
				type = CreateRDFSType.RENAME;				
			} else if (createButton.isSelected()) {
				type = CreateRDFSType.CREATE;
			} 
		}
		setVisible(false);
	}

	public Object getValue() {
		return type;
	}
}
