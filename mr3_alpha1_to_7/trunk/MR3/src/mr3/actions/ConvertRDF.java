/*
 * Created on 2003/07/19
 *
 */
package mr3.actions;

import java.awt.event.*;
import java.io.*;

import mr3.*;

import com.hp.hpl.mesa.rdf.jena.common.*;
import com.hp.hpl.mesa.rdf.jena.model.*;

/**
 * @author takeshi morita
 *
 */
public class ConvertRDF extends AbstractActionFile {

	public ConvertRDF(MR3 mr3, String name) {
		super(mr3, name);
	}

	public void convertRDFSRC(boolean isSelected) {
		try {
			Model model = null;
			if (isSelected) {
				model = mr3.getSelectedRDFModel();
			} else {
				model = mr3.getRDFModel();
			}
			Writer output = new StringWriter();
			RDFWriter writer = new RDFWriterFImpl().getWriter("RDF/XML-ABBREV");
			writeModel(model, output, writer);
			mr3.getSourceArea().setText(output.toString());
		} catch (RDFException e) {
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("RDF/XML")) {
			convertRDFSRC(false);
		} else {
			convertRDFSRC(true);
		}
		showSrcView();
	}

}
