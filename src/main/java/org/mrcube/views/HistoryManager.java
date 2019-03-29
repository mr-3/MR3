/*
 * Project Name: MR^3 (Meta-Model Management based on RDFs Revision Reflection)
 * Project Website: http://mrcube.org/
 *
 * Copyright (C) 2003-2019 Yamaguchi Laboratory, Keio University. All rights reserved.
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

package org.mrcube.views;

import org.jgraph.graph.GraphCell;
import org.jgraph.graph.GraphConstants;
import org.mrcube.MR3;
import org.mrcube.io.MR3Reader;
import org.mrcube.io.MR3Writer;
import org.mrcube.jgraph.GraphManager;
import org.mrcube.jgraph.RDFGraph;
import org.mrcube.models.*;
import org.mrcube.models.MR3Constants.HistoryType;
import org.mrcube.utils.Translator;
import org.mrcube.utils.Utilities;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.logging.*;

/**
 * @author Takeshi Morita
 */
public class HistoryManager extends JDialog implements ActionListener {

    private static MR3 mr3;
    private static MR3Writer mr3Writer;
    private static MR3Reader mr3Reader;
    private static Map<Date, HistoryModel> dateHistoryDataMap;
    private static DefaultTableModel historyTableModel;
    private static JTable historyTable;

    private static GraphManager gmanager;

    private final JButton openHistoryButton;
    private final JButton cancelButton;

    private static final int WINDOW_WIDTH = 600;
    private static final int WINDOW_HEIGHT = 400;

    public static final String DEFAULT_LOG_FILE_NAME = "mr3_log.txt";

    private static Logger logger;
    private static FileHandler logFileHandler;

    static class CustomLogFormatter extends SimpleFormatter {
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        public String format(LogRecord logRecord) {
            final StringBuilder stringBuffer = new StringBuilder();

            stringBuffer.append(this.dateFormat.format(new Date(logRecord.getMillis())));
            stringBuffer.append(" ");

            Level level = logRecord.getLevel();
            if (level == Level.FINEST) {
                stringBuffer.append("FINEST");
            } else if (level == Level.FINER) {
                stringBuffer.append("FINER ");
            } else if (level == Level.FINE) {
                stringBuffer.append("FINE ");
            } else if (level == Level.CONFIG) {
                stringBuffer.append("CONFIG");
            } else if (level == Level.INFO) {
                stringBuffer.append("INFO ");
            } else if (level == Level.WARNING) {
                stringBuffer.append("WARN ");
            } else if (level == Level.SEVERE) {
                stringBuffer.append("SEVERE");
            } else {
                stringBuffer.append(logRecord.getLevel().intValue());
                stringBuffer.append(" ");
            }
            stringBuffer.append(" ");
            stringBuffer.append(logRecord.getLoggerName());
            stringBuffer.append(" - ");
            stringBuffer.append(logRecord.getMessage());
            stringBuffer.append(System.lineSeparator());

            return stringBuffer.toString();
        }
    }

    public static void initLogger(String logFilePath) {
        logger = Logger.getGlobal();
        logger.setLevel(Level.INFO);
        resetFileAppender(logFilePath);
    }

    public static void closeLogFile() {
        if (logFileHandler != null) {
            logFileHandler.close();
        }
    }

    public static void saveMessage(HistoryType historyType, String message) {
        logger.info(message);
        System.out.println(message);
        switch (historyType) {
            case OPEN_PROJECT:
            case NEW_PROJECT:
            case LOAD_HISTORY:
                break;
            default:
                HistoryModel data = new HistoryModel(historyType, mr3Writer.getProjectModel());
                dateHistoryDataMap.put(data.getDate(), data);
                historyTableModel.insertRow(0, new Object[]{data.getDate(), data.getHistoryType()});
                break;
        }
    }

    public static void resetFileAppender(String logFilePath) {
        if (logFileHandler != null) {
            logFileHandler.close();
            logger.removeHandler(logFileHandler);
        }
        try {
            logFileHandler = new FileHandler(logFilePath, true);
            logFileHandler.setFormatter(new CustomLogFormatter());
            logger.addHandler(logFileHandler);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private static final Object[] columnNames = new Object[]{
            Translator.getString("HistoryManager.Date"),
            Translator.getString("HistoryManager.Type")
    };

    public HistoryManager(Frame root, MR3 mr3) {
        super(root, Translator.getString("HistoryManager.Title"));
        this.mr3 = mr3;
        mr3Reader = mr3.getMR3Reader();
        mr3Writer = mr3.getMR3Writer();
        dateHistoryDataMap = new HashMap<>();
        historyTableModel = new DefaultTableModel(columnNames, 0);
        historyTable = new JTable(historyTableModel);
        TableColumnModel tcModel = historyTable.getColumnModel();
        tcModel.getColumn(0).setPreferredWidth(50);
        tcModel.getColumn(1).setPreferredWidth(100);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane historyTableScroll = new JScrollPane(historyTable);

        openHistoryButton = new JButton(Translator.getString("HistoryManager.Open"));
        openHistoryButton.setMnemonic('a');
        openHistoryButton.addActionListener(this);
        cancelButton = new JButton(MR3Constants.CANCEL);
        cancelButton.setMnemonic('c');
        cancelButton.addActionListener(this);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2, 5, 5));
        buttonPanel.add(openHistoryButton);
        buttonPanel.add(cancelButton);

        add(historyTableScroll, BorderLayout.CENTER);
        add(Utilities.createEastPanel(buttonPanel), BorderLayout.SOUTH);

        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(root);
        setVisible(false);
    }

    public static void setGraphManager(GraphManager gm) {
        gmanager = gm;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == openHistoryButton) {
            loadHistory();
        } else if (e.getSource() == cancelButton) {
            setVisible(false);
        }
    }

    private static final String MODEL = "[Model]";
    private static final String META_MODEL = "[Meta Model]";

    public synchronized static void saveHistory(HistoryType historyType, RDFSModel info, GraphCell sourceCell, GraphCell targetCell) {
        switch (historyType) {
            case INSERT_PROPERTY:
                RDFResourceModel resInfo = (RDFResourceModel) GraphConstants.getValue(sourceCell.getAttributes());
                String sourceStr = String.format("Source: RDF Resource: %s\tRDF Resource Type: %s\n", resInfo.getURIStr(), resInfo.getType());
                String targetStr;
                if (RDFGraph.isRDFLiteralCell(targetCell)) {
                    MR3Literal literal = (MR3Literal) GraphConstants.getValue(targetCell.getAttributes());
                    targetStr = String.format("Target: Language: %s\tDatatype: %s\tString: %s\n", literal.getLanguage(), literal.getDatatype(), literal.getString());
                } else {
                    resInfo = (RDFResourceModel) GraphConstants.getValue(targetCell.getAttributes());
                    targetStr = String.format("Target: RDF Resource: %s\tRDF Resource Type: %s\n", resInfo.getURIStr(), resInfo.getType());
                }
                var message = String.format("%s[%s]\nInsert Property: %s\n%s%s", MODEL, historyType, info.getURIStr(), sourceStr, targetStr);
                saveMessage(historyType, message);
                break;
            case CONNECT_SUP_SUB_CLASS:
                RDFSModel subInfo = (RDFSModel) GraphConstants.getValue(sourceCell.getAttributes());
                String subStr = String.format("Sub Class: %s\tMeta Class: %s\n", subInfo.getURIStr(), subInfo.getMetaClass());
                RDFSModel supInfo = (RDFSModel) GraphConstants.getValue(targetCell.getAttributes());
                String supStr = String.format("Super Class: %s\tMeta Class: %s\n", supInfo.getURIStr(), supInfo.getMetaClass());
                message = String.format("%s[%s]\n%s%s", META_MODEL, historyType, subStr, supStr);
                saveMessage(historyType, message);
                break;
            case CONNECT_SUP_SUB_PROPERTY:
                subInfo = (RDFSModel) GraphConstants.getValue(sourceCell.getAttributes());
                subStr = String.format("Sub Property: %s\tMeta Class: %s\n", subInfo.getURIStr(), subInfo.getMetaClass());
                supInfo = (RDFSModel) GraphConstants.getValue(targetCell.getAttributes());
                supStr = String.format("Super Property: %s\tMeta Class: %s\n", supInfo.getURIStr(), supInfo.getMetaClass());
                message = String.format("%s[%s]\n%s%s", META_MODEL, historyType, subStr, supStr);
                saveMessage(historyType, message);
                break;
        }
    }

    public synchronized static void saveHistory(HistoryType historyType, Object[] removeCells) {
        StringBuilder buf = new StringBuilder("DELETE: \n");
        for (Object removeCell : removeCells) {
            GraphCell cell = (GraphCell) removeCell;
            if (RDFGraph.isRDFResourceCell(cell)) {
                RDFResourceModel resInfo = (RDFResourceModel) GraphConstants.getValue(cell.getAttributes());
                buf.append("RDF Resource: ").append(resInfo.getURIStr()).append("\t");
                buf.append("RDF Resource Type: ").append(resInfo.getType()).append("\n");
            } else if (historyType == HistoryType.DELETE_RDF && RDFGraph.isRDFPropertyCell(cell)) {
                RDFSModel rdfsModel = (RDFSModel) GraphConstants.getValue(cell.getAttributes());
                buf.append("RDF Property: ").append(rdfsModel.getURIStr()).append("\n");
            } else if (RDFGraph.isRDFLiteralCell(cell)) {
                MR3Literal literal = (MR3Literal) GraphConstants.getValue(cell.getAttributes());
                buf.append("RDF Literal: \n");
                buf.append("Language: ").append(literal.getLanguage()).append("\t");
                buf.append("Datatype: ").append(literal.getDatatype()).append("\t");
                buf.append("String: ").append(literal.getString()).append("\n");
            } else if (RDFGraph.isRDFSClassCell(cell)) {
                RDFSModel rdfsModel = (RDFSModel) GraphConstants.getValue(cell.getAttributes());
                buf.append("Ont Class: ").append(rdfsModel.getURIStr()).append("\t");
                buf.append("Meta Class: ").append(rdfsModel.getMetaClass()).append("\n");
            } else if (RDFGraph.isRDFSPropertyCell(cell)) {
                RDFSModel rdfsModel = (RDFSModel) GraphConstants.getValue(cell.getAttributes());
                buf.append("Ont Property: ").append(rdfsModel.getURIStr()).append("\t");
                buf.append("Meta Property: ").append(rdfsModel.getMetaClass()).append("\n");
            }
        }

        switch (historyType) {
            case DELETE_RDF:
                var message = String.format("%s[%s]\n%s", MODEL, historyType, buf.toString());
                saveMessage(historyType, message);
                break;
            case DELETE_CLASS:
            case DELETE_ONT_PROPERTY:
                message = String.format("%s[%s]\n%s", META_MODEL, historyType, buf.toString());
                saveMessage(historyType, message);
                break;
        }
    }

    public synchronized static void saveHistory(HistoryType historyType, List<MR3Literal> beforeMR3LiteralList, List<MR3Literal> afterMR3LiteralList) {
        StringBuilder beforeBuf = new StringBuilder();
        for (MR3Literal lit : beforeMR3LiteralList) {
            beforeBuf.append("Language: ").append(lit.getLanguage()).append("\tString: ").append(lit.getString()).append("\n");
        }
        StringBuilder afterBuf = new StringBuilder();
        for (MR3Literal lit : afterMR3LiteralList) {
            afterBuf.append("Language: ").append(lit.getLanguage()).append("\tString: ").append(lit.getString()).append("\n");
        }

        switch (historyType) {
            case EDIT_RESOURCE_LABEL:
            case EDIT_RESOURCE_LABEL_WITH_GRAPH:
            case ADD_RESOURCE_LABEL:
            case DELETE_RESOURCE_LABEL:
                var message = String.format("%s[%s]\nBefore Label: \n%sAfter Label: \n%s",
                        MODEL, historyType, beforeBuf.toString(), afterBuf.toString());
                saveMessage(historyType, message);
                break;
            case EDIT_RESOURCE_COMMENT:
            case ADD_RESOURCE_COMMENT:
            case DELETE_RESOURCE_COMMENT:
                message = String.format("%s[%s]\nBefore Comment: \n%sAfter Comment: \n%s",
                        MODEL, historyType, beforeBuf.toString(), afterBuf.toString());
                saveMessage(historyType, message);
                break;
            case EDIT_CLASS_LABEL:
            case EDIT_CLASS_LABEL_WITH_GRAPH:
            case ADD_CLASS_LABEL:
            case DELETE_CLASS_LABEL:
            case EDIT_ONT_PROPERTY_LABEL:
            case EDIT_ONT_PROPERTY_LABEL_WITH_GRAPH:
            case ADD_ONT_PROPERTY_LABEL:
            case DELETE_ONT_PROPERTY_LABEL:
                message = String.format("%s[%s]\nBefore Label: \n%sAfter Label: \n%s",
                        META_MODEL, historyType, beforeBuf.toString(), afterBuf.toString());
                saveMessage(historyType, message);
                break;
            case EDIT_CLASS_COMMENT:
            case ADD_CLASS_COMMENT:
            case DELETE_CLASS_COMMENT:
            case EDIT_ONT_PROPERTY_COMMENT:
            case ADD_ONT_PROPERTY_COMMENT:
            case DELETE_ONT_PROPERTY_COMMENT:
                message = String.format("%s[%s]\nBefore Comment: \n%sAfter Comment: \n%s",
                        META_MODEL, historyType, beforeBuf.toString(), afterBuf.toString());
                saveMessage(historyType, message);
                break;

        }
    }

    public synchronized static void saveHistory(HistoryType historyType, RDFResourceModel beforeInfo, RDFResourceModel afterInfo) {
        if (beforeInfo.isSameInfo(afterInfo)) {
            return;
        }
        switch (historyType) {
            case EDIT_RESOURCE_WITH_DIALOG:
            case EDIT_RESOURCE_WITH_GRAPH:
                var message = String.format("%s[%s]\nBefore RDF Resource URI Type: %s\nBefore RDF Resource: %s\n" +
                                "Before RDF Resource Type: %s\nAfter RDF Resource URI Type: %s\nAfter RDF Resource: %s\nAfter RDF Resource Type: %s\n",
                        MODEL, historyType, beforeInfo.getURIType(), beforeInfo.getURIStr(), beforeInfo.getType(),
                        afterInfo.getURIType(), afterInfo.getURIStr(), afterInfo.getType());
                saveMessage(historyType, message);
                break;
        }
    }

    public synchronized static void saveHistory(HistoryType historyType, String beforeProperty, String afterProperty) {
        if (beforeProperty.equals(afterProperty)) {
            return;
        }
        switch (historyType) {
            case EDIT_PROPERTY_WITH_DIAGLOG:
            case EDIT_PROPERTY_WITH_GRAPH:
                var message = String.format("%s[%s]\nBefore Property: %s\nAfter Property: %s\n",
                        MODEL, historyType, beforeProperty, afterProperty);
                saveMessage(historyType, message);
                break;
        }
    }

    public synchronized static void saveHistory(HistoryType historyType, MR3Literal beforeLiteral,
                                                MR3Literal afterLiteral) {
        if (beforeLiteral.equals(afterLiteral)) {
            return;
        }
        switch (historyType) {
            case EDIT_LITERAL_WITH_DIAGLOG:
            case EDIT_LITERAL_WITH_GRAPH:
                var message = String.format("%s[%s]\nBefore Language%s\nBefore String: %s\nBefore Data type: %s" +
                                "\nAfter Language: %s\nAfter String: %s\nAfter Data type: %s\n",
                        MODEL, historyType, beforeLiteral.getLanguage(), beforeLiteral.getString(), beforeLiteral.getDatatype(),
                        afterLiteral.getLanguage(), afterLiteral.getString(), afterLiteral.getDatatype());
                saveMessage(historyType, message);
                break;
        }
    }

    public synchronized static void saveHistory(HistoryType historyType, Set beforeRegion, Set afterRegion, String uri) {
        switch (historyType) {
            case ADD_ONT_PROPERTY_DOMAIN:
            case DELETE_ONT_PROPERTY_DOMAIN:
                var message = String.format("%s[%s]\nOnt Property: %s\nBefore ONT Property Domain: %s\n" +
                                "After ONT Property Domain: %s\n",
                        META_MODEL, historyType, uri, beforeRegion, afterRegion);
                saveMessage(historyType, message);
                break;
            case ADD_ONT_PROPERTY_RANGE:
            case DELETE_ONT_PROPERTY_RANGE:
                message = String.format("%s[%s]\nOnt Property: %s\nBefore ONT Property Range: %s\n" +
                                "After ONT Property Range: %s\n",
                        META_MODEL, historyType, uri, beforeRegion, afterRegion);
                saveMessage(historyType, message);
                break;
        }
    }

    public synchronized static void saveHistory(HistoryType historyType, RDFSModel beforeInfo, RDFSModel afterInfo) {
        if (beforeInfo.isSameInfo(afterInfo)) {
            return;
        }
        switch (historyType) {
            case EDIT_CLASS_WITH_DIAGLOG:
            case EDIT_CLASS_WITH_GRAPH:
                var message = String.format("%s[%s]\nBefore ONT Class: %s\nBefore ONT Class Type: %s\nAfter ONT Class: %s" +
                                "\nAfter ONT Class Type: %s\n", META_MODEL, historyType, beforeInfo.getURIStr(), beforeInfo.getMetaClass(),
                        afterInfo.getURIStr(), afterInfo.getMetaClass());
                saveMessage(historyType, message);
                if (!MR3.OFF_META_MODEL_MANAGEMENT) {
                    ClassModel clsInfo = (ClassModel) afterInfo;
                    Set<RDFResourceModel> instanceInfoSet = gmanager.getClassInstanceInfoSet(clsInfo);
                    if (0 < instanceInfoSet.size()) {
                        StringBuilder instanceInfoStr = new StringBuilder();
                        for (RDFResourceModel resInfo : instanceInfoSet) {
                            instanceInfoStr.append("RDF Resource: ").append(resInfo.getURIStr()).append("\n");
                        }
                        message = String.format("%s[%s]\n%s", META_MODEL, HistoryType.META_MODEL_MANAGEMNET_REPLACE_CLASS, instanceInfoStr);
                        saveMessage(historyType, message);
                    }
                }
                break;
            case EDIT_ONT_PROPERTY_WITH_DIAGLOG:
            case EDIT_ONT_PROPERTY_WITH_GRAPH:
                message = String.format("%s[%s]\nBefore ONT Property: %s\nBefore ONT Property Type: %s\nAfter ONT Property: %s" +
                                "\nAfter ONT Property Type: %s\n", META_MODEL, historyType, beforeInfo.getURIStr(), beforeInfo.getMetaClass(),
                        afterInfo.getURIStr(), afterInfo.getMetaClass());
                saveMessage(historyType, message);
                if (!MR3.OFF_META_MODEL_MANAGEMENT) {
                    PropertyModel propInfo = (PropertyModel) afterInfo;
                    Set instanceSet = gmanager.getPropertyInstanceInfoSet(propInfo);
                    if (0 < instanceSet.size()) {
                        StringBuilder instanceInfoStr = new StringBuilder();
                        for (Object cell : instanceSet) {
                            RDFResourceModel resInfo = (RDFResourceModel) GraphConstants.getValue(((GraphCell) cell).getAttributes());
                            instanceInfoStr.append("Source RDF Resource: ").append(resInfo.getURIStr()).append("\n");
                        }
                        // TODO: 正確にやるなら，RDFプロパティのグラフセルのセットから，sourcevertex, targetvertexを得て，Source Resource, Target Resourceを表示するようにすべき
                        message = String.format("%s[%s]\n%s", META_MODEL, HistoryType.META_MODEL_MANAGEMNET_REPLACE_ONT_PROPERTY, instanceInfoStr);
                        saveMessage(historyType, message);
                    }
                }
                break;
        }
    }

    public synchronized static void saveHistory(HistoryType historyType, GraphCell insertCell) {
        switch (historyType) {
            case INSERT_RESOURCE:
            case INSERT_CONNECTED_RESOURCE:
                RDFResourceModel info = (RDFResourceModel) GraphConstants.getValue(insertCell.getAttributes());
                var message = String.format("%s[%s]\nRDF Resource URI Type: %s \nRDF Resource: %s \nRDF Resource Type: %s\n",
                        MODEL, historyType, info.getURIType(), info.getURIStr(), info.getType());
                saveMessage(historyType, message);
                break;
            case INSERT_LITERAL:
            case INSERT_CONNECTED_LITERAL:
                MR3Literal literal = (MR3Literal) GraphConstants.getValue(insertCell.getAttributes());
                message = String.format("%s[%s]\nLanguage: %s \nString: %s \nData type: %s\n",
                        MODEL, historyType, literal.getLanguage(), literal.getString(), literal.getDatatype());
                saveMessage(historyType, message);
                break;
            case INSERT_ONT_PROPERTY:
            case INSERT_CONNECTED_ONT_PROPERTY:
                RDFSModel rdfsModel = (RDFSModel) GraphConstants.getValue(insertCell.getAttributes());
                message = String.format("%s[%s]\nONT Property: %s\nONT Property Type: %s\n",
                        META_MODEL, historyType, rdfsModel.getURIStr(), rdfsModel.getMetaClass());
                saveMessage(historyType, message);
                break;
            case INSERT_CLASS:
                rdfsModel = (RDFSModel) GraphConstants.getValue(insertCell.getAttributes());
                message = String.format("%s[%s]\nONT Class: %s\nONT Class Type: %s\n",
                        META_MODEL, historyType, rdfsModel.getURIStr(), rdfsModel.getMetaClass());
                saveMessage(historyType, message);
                break;
        }
    }

    public synchronized static void saveHistory(HistoryType historyType, String path) {
        switch (historyType) {
            case OPEN_PROJECT:
            case SAVE_PROJECT:
            case SAVE_PROJECT_AS:
                var message = String.format("[---][%s]\nPath: %s\n", historyType, path);
                saveMessage(historyType, message);
                break;
        }
    }

    public synchronized static void saveHistory(HistoryType historyType) {
        switch (historyType) {
            case NEW_PROJECT:
                var message = String.format("[---] [%s]\n", historyType);
                saveMessage(historyType, message);
                break;
            case COPY_CLASS_GRAPH:
            case CUT_CLASS_GRAPH:
            case PASTE_CLASS_GRAPH:
            case COPY_PROPERTY_GRAPH:
            case CUT_PROPERTY_GRAPH:
            case PASTE_PROPERTY_GRAPH:
                message = String.format("%s[%s]\n", META_MODEL, historyType);
                saveMessage(historyType, message);
                break;
            case META_MODEL_MANAGEMNET_REPLACE_RESOURCE_TYPE_WITH_CREATE_CLASS:
            case META_MODEL_MANAGEMNET_REPLACE_RESOURCE_TYPE_WITH_REPLACE_CLASS:
            case META_MODEL_MANAGEMNET_REPLACE_PROPERTY_WITH_CREATE_ONT_PROPERTY:
            case META_MODEL_MANAGEMNET_REPLACE_PROPERTY_WITH_REPLACE_ONT_PROPERTY:
            case COPY_RDF_GRAPH:
            case CUT_RDF_GRAPH:
            case PASTE_RDF_GRAPH:
                message = String.format("%s[%s]\n", MODEL, historyType);
                saveMessage(historyType, message);
                break;
        }
    }

    private void loadHistory() {
        if (historyTable.getSelectedRowCount() == 1) {
            Date date = (Date) historyTableModel.getValueAt(historyTable.getSelectedRow(), 0);
            HistoryModel data = dateHistoryDataMap.get(date);
            mr3.newProject();
            mr3Reader.replaceProjectModel(data.getProjectModel());
            saveHistory(HistoryType.LOAD_HISTORY);
        }
    }
}
