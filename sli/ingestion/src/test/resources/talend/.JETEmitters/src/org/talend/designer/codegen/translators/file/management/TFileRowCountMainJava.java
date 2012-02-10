package org.talend.designer.codegen.translators.file.management;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TFileRowCountMainJava
{
  protected static String nl;
  public static synchronized TFileRowCountMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileRowCountMainJava result = new TFileRowCountMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "String tmp_";
  protected final String TEXT_3 = " = ";
  protected final String TEXT_4 = ";" + NL + "int emptyLineCount_";
  protected final String TEXT_5 = " = 0;" + NL + "if (tmp_";
  protected final String TEXT_6 = ".toLowerCase().endsWith(\".xlsx\")) throw new RuntimeException(\"not support excel 2007\");  " + NL + "int lineCount_";
  protected final String TEXT_7 = " = 0;" + NL + "if (tmp_";
  protected final String TEXT_8 = ".toLowerCase().endsWith(\".xls\")){" + NL + "  final jxl.WorkbookSettings wbs_";
  protected final String TEXT_9 = " = new jxl.WorkbookSettings();" + NL + "  wbs_";
  protected final String TEXT_10 = ".setEncoding(";
  protected final String TEXT_11 = ");" + NL + "  final jxl.Workbook wb_";
  protected final String TEXT_12 = " = jxl.Workbook.getWorkbook(" + NL + "      new java.io.BufferedInputStream(" + NL + "          new java.io.FileInputStream(";
  protected final String TEXT_13 = ")), wbs_";
  protected final String TEXT_14 = ");" + NL + "" + NL + "  java.util.List<jxl.Sheet> sheets_";
  protected final String TEXT_15 = " = new java.util.ArrayList<jxl.Sheet>();;" + NL + "  sheets_";
  protected final String TEXT_16 = " = java.util.Arrays.<jxl.Sheet> asList(wb_";
  protected final String TEXT_17 = ".getSheets());" + NL + "  " + NL + "  if (sheets_";
  protected final String TEXT_18 = ".size() <= 0){" + NL + "    wb_";
  protected final String TEXT_19 = ".close();" + NL + "    throw new RuntimeException(\"Special sheets not exist!\");" + NL + "  } " + NL + " " + NL + "  for (jxl.Sheet sheet_";
  protected final String TEXT_20 = " : sheets_";
  protected final String TEXT_21 = ") {" + NL + "    int one_sheet_rows = sheet_";
  protected final String TEXT_22 = ".getRows();" + NL + "    lineCount_";
  protected final String TEXT_23 = " += one_sheet_rows;" + NL + "  " + NL + "    for (int i_";
  protected final String TEXT_24 = " = 0; i_";
  protected final String TEXT_25 = " < one_sheet_rows; i_";
  protected final String TEXT_26 = "++){" + NL + "      jxl.Cell[] sheet_row = sheet_";
  protected final String TEXT_27 = ".getRow(i_";
  protected final String TEXT_28 = ");" + NL + "    " + NL + "      boolean bIsEmptyRow = true;" + NL + "      for (jxl.Cell cell: sheet_row){" + NL + "        if (cell.getContents() != null){" + NL + "          bIsEmptyRow = false;" + NL + "          break;" + NL + "        }" + NL + "      }" + NL + "      emptyLineCount_";
  protected final String TEXT_29 = " += bIsEmptyRow ? 1 : 0;" + NL + "    }" + NL + "  }  " + NL + "  wb_";
  protected final String TEXT_30 = ".close();" + NL + "  " + NL + "} else{        \t\t\t\t\t" + NL + "  java.io.BufferedReader br_";
  protected final String TEXT_31 = " = new java.io.BufferedReader(new java.io.InputStreamReader(new java.io.FileInputStream(";
  protected final String TEXT_32 = "), ";
  protected final String TEXT_33 = "));\t\t\t" + NL + "  String rowSeparator_";
  protected final String TEXT_34 = " = ";
  protected final String TEXT_35 = ";" + NL + "  byte[] bytes_";
  protected final String TEXT_36 = " = rowSeparator_";
  protected final String TEXT_37 = ".getBytes();" + NL + "  int index_";
  protected final String TEXT_38 = " = 0, oneChar_";
  protected final String TEXT_39 = " = 0, tipEmptyLineCount_";
  protected final String TEXT_40 = " = 0; " + NL + "  boolean bTipEmptyFlagOpen_";
  protected final String TEXT_41 = " = true, bReadyEOF_";
  protected final String TEXT_42 = " = false;" + NL + "\t\t" + NL + "  if(bytes_";
  protected final String TEXT_43 = ".length > 0) {" + NL + "    while ((oneChar_";
  protected final String TEXT_44 = " = br_";
  protected final String TEXT_45 = ".read()) != -1) {" + NL + "      if (oneChar_";
  protected final String TEXT_46 = " == bytes_";
  protected final String TEXT_47 = "[index_";
  protected final String TEXT_48 = "]) {" + NL + "      " + NL + "        if (index_";
  protected final String TEXT_49 = " < bytes_";
  protected final String TEXT_50 = ".length - 1){" + NL + "          index_";
  protected final String TEXT_51 = " ++ ;" + NL + "          continue; // match next char" + NL + "        }" + NL + "        " + NL + "        if (index_";
  protected final String TEXT_52 = " == bytes_";
  protected final String TEXT_53 = ".length - 1) {                  " + NL + "          lineCount_";
  protected final String TEXT_54 = "++;" + NL + "          if(bTipEmptyFlagOpen_";
  protected final String TEXT_55 = ") {" + NL + "            tipEmptyLineCount_";
  protected final String TEXT_56 = " ++;" + NL + "            emptyLineCount_";
  protected final String TEXT_57 = " ++;" + NL + "          }" + NL + "          bReadyEOF_";
  protected final String TEXT_58 = " = false; // next row must be have char(or EOF flag)" + NL + "          bTipEmptyFlagOpen_";
  protected final String TEXT_59 = " = true; " + NL + "          index_";
  protected final String TEXT_60 = " = 0;" + NL + "        }" + NL + "        " + NL + "      }else{      " + NL + "        bReadyEOF_";
  protected final String TEXT_61 = " = true;" + NL + "        bTipEmptyFlagOpen_";
  protected final String TEXT_62 = " = false;" + NL + "        tipEmptyLineCount_";
  protected final String TEXT_63 = " = 0;" + NL + "        index_";
  protected final String TEXT_64 = " = 0;        " + NL + "      }" + NL + "    }" + NL + "    " + NL + "    if (bReadyEOF_";
  protected final String TEXT_65 = ") lineCount_";
  protected final String TEXT_66 = " ++ ; // add last row if not end by row separator" + NL + "    " + NL + "    if (bTipEmptyFlagOpen_";
  protected final String TEXT_67 = ") {" + NL + "      lineCount_";
  protected final String TEXT_68 = " -= tipEmptyLineCount_";
  protected final String TEXT_69 = ";" + NL + "      emptyLineCount_";
  protected final String TEXT_70 = " -= tipEmptyLineCount_";
  protected final String TEXT_71 = ";" + NL + "    }          " + NL + "  }" + NL + "  br_";
  protected final String TEXT_72 = ".close();" + NL + "} ";
  protected final String TEXT_73 = NL + "  lineCount_";
  protected final String TEXT_74 = " -= emptyLineCount_";
  protected final String TEXT_75 = ";";
  protected final String TEXT_76 = "   " + NL + "  globalMap.put(\"";
  protected final String TEXT_77 = "_COUNT\",lineCount_";
  protected final String TEXT_78 = ");    " + NL;
  protected final String TEXT_79 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

String filename = ElementParameterParser.getValue(
    node,
    "__FILENAME__"
);

String rowSeparator = ElementParameterParser.getValue(
    node,
    "__ROWSEPARATOR__"
);
String encoding = ElementParameterParser.getValue(node,"__ENCODING__");

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(filename);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(filename);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(rowSeparator );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_72);
    
  if ("true".equals(ElementParameterParser.getValue(node, "__IGNORE_EMPTY_ROW__")) ){

    stringBuffer.append(TEXT_73);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_74);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_75);
    }
    stringBuffer.append(TEXT_76);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_78);
    stringBuffer.append(TEXT_79);
    return stringBuffer.toString();
  }
}
