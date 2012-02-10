package org.talend.designer.codegen.translators.databases.teradata;

import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;

public class TTeradataFastLoadEndJava
{
  protected static String nl;
  public static synchronized TTeradataFastLoadEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TTeradataFastLoadEndJava result = new TTeradataFastLoadEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "if(pstmtUpdate_";
  protected final String TEXT_3 = " != null){" + NL + "" + NL + "        pstmtUpdate_";
  protected final String TEXT_4 = ".executeBatch();" + NL + "\t\tpstmtUpdate_";
  protected final String TEXT_5 = ".clearBatch();" + NL + "\t\tpstmtUpdate_";
  protected final String TEXT_6 = ".close();" + NL + "\t" + NL + "} " + NL + "if(pstmtInsert_";
  protected final String TEXT_7 = " != null){" + NL + "" + NL + "        pstmtInsert_";
  protected final String TEXT_8 = ".executeBatch();" + NL + "\t\tpstmtInsert_";
  protected final String TEXT_9 = ".clearBatch();" + NL + "\t\tpstmtInsert_";
  protected final String TEXT_10 = ".close();" + NL + "\t" + NL + "}" + NL + "if(pstmt_";
  protected final String TEXT_11 = " != null) {" + NL + "" + NL + "        pstmt_";
  protected final String TEXT_12 = ".executeBatch();" + NL + "\t\tpstmt_";
  protected final String TEXT_13 = ".clearBatch();" + NL + "\t\tpstmt_";
  protected final String TEXT_14 = ".close();" + NL + "\t" + NL + "}" + NL + "conn_";
  protected final String TEXT_15 = ".commit();" + NL + "conn_";
  protected final String TEXT_16 = " .close();" + NL;
  protected final String TEXT_17 = NL + "if (commitCounter_";
  protected final String TEXT_18 = " > 0) {" + NL + "\tpstmt_";
  protected final String TEXT_19 = ".executeBatch();" + NL + "\tpstmt_";
  protected final String TEXT_20 = ".clearBatch();" + NL + "\tcommitCounter_";
  protected final String TEXT_21 = "=0;" + NL + "}" + NL + "pstmt_";
  protected final String TEXT_22 = ".close();" + NL + "conn_";
  protected final String TEXT_23 = ".commit();" + NL + "conn_";
  protected final String TEXT_24 = " .close();";
  protected final String TEXT_25 = NL + "\tnb_line_inserted_";
  protected final String TEXT_26 = "=nb_line_inserted_";
  protected final String TEXT_27 = " + insertedCount_";
  protected final String TEXT_28 = ";" + NL + "" + NL + "globalMap.put(\"";
  protected final String TEXT_29 = "_NB_LINE\",nb_line_";
  protected final String TEXT_30 = ");" + NL + "globalMap.put(\"";
  protected final String TEXT_31 = "_NB_LINE_INSERTED\",nb_line_inserted_";
  protected final String TEXT_32 = ");";
  protected final String TEXT_33 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	
	String cid = node.getUniqueName();
	
	String dataAction = "INSERT";
	
	String commitEvery = ElementParameterParser.getValue(node, "__COMMIT_EVERY__");
	
	if(("INSERT_OR_UPDATE").equals(dataAction)||("UPDATE_OR_INSERT").equals(dataAction)){


    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    	
	}else{

    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    
	}

    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(TEXT_33);
    return stringBuffer.toString();
  }
}
