package org.talend.designer.codegen.translators.databases.firebird;

import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;

public class TFirebirdOutputEndJava
{
  protected static String nl;
  public static synchronized TFirebirdOutputEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFirebirdOutputEndJava result = new TFirebirdOutputEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "        if(pstmtUpdate_";
  protected final String TEXT_3 = " != null){" + NL + "" + NL + "            pstmtUpdate_";
  protected final String TEXT_4 = ".close();" + NL + "            " + NL + "        } " + NL + "        if(pstmtInsert_";
  protected final String TEXT_5 = " != null){" + NL + "" + NL + "            pstmtInsert_";
  protected final String TEXT_6 = ".close();" + NL + "            " + NL + "        }" + NL + "        if(pstmt_";
  protected final String TEXT_7 = " != null) {" + NL + "" + NL + "            pstmt_";
  protected final String TEXT_8 = ".close();" + NL + "            " + NL + "        }        ";
  protected final String TEXT_9 = NL + "        if(pstmtUpdate_";
  protected final String TEXT_10 = " != null){" + NL + "" + NL + "            pstmtUpdate_";
  protected final String TEXT_11 = ".close();" + NL + "            " + NL + "        } " + NL + "        if(pstmtInsert_";
  protected final String TEXT_12 = " != null){" + NL + "" + NL + "            pstmtInsert_";
  protected final String TEXT_13 = ".close();" + NL + "            " + NL + "        }        ";
  protected final String TEXT_14 = NL + "        if(pstmt_";
  protected final String TEXT_15 = " != null) {" + NL + "" + NL + "            pstmt_";
  protected final String TEXT_16 = ".close();" + NL + "            " + NL + "        }        ";
  protected final String TEXT_17 = NL + "    ";
  protected final String TEXT_18 = NL + "        if(conn_";
  protected final String TEXT_19 = " != null && !conn_";
  protected final String TEXT_20 = ".isClosed()) {";
  protected final String TEXT_21 = NL + "                conn_";
  protected final String TEXT_22 = ".commit();";
  protected final String TEXT_23 = NL + "            conn_";
  protected final String TEXT_24 = " .close();" + NL + "        }";
  protected final String TEXT_25 = "    " + NL + "    " + NL + "\tnb_line_deleted_";
  protected final String TEXT_26 = "=nb_line_deleted_";
  protected final String TEXT_27 = "+ deletedCount_";
  protected final String TEXT_28 = ";" + NL + "\tnb_line_update_";
  protected final String TEXT_29 = "=nb_line_update_";
  protected final String TEXT_30 = " + updatedCount_";
  protected final String TEXT_31 = ";" + NL + "\tnb_line_inserted_";
  protected final String TEXT_32 = "=nb_line_inserted_";
  protected final String TEXT_33 = " + insertedCount_";
  protected final String TEXT_34 = ";" + NL + "\tnb_line_rejected_";
  protected final String TEXT_35 = "=nb_line_rejected_";
  protected final String TEXT_36 = " + rejectedCount_";
  protected final String TEXT_37 = ";" + NL + "" + NL + "    globalMap.put(\"";
  protected final String TEXT_38 = "_NB_LINE\",nb_line_";
  protected final String TEXT_39 = ");" + NL + "    globalMap.put(\"";
  protected final String TEXT_40 = "_NB_LINE_UPDATED\",nb_line_update_";
  protected final String TEXT_41 = ");" + NL + "    globalMap.put(\"";
  protected final String TEXT_42 = "_NB_LINE_INSERTED\",nb_line_inserted_";
  protected final String TEXT_43 = ");" + NL + "    globalMap.put(\"";
  protected final String TEXT_44 = "_NB_LINE_DELETED\",nb_line_deleted_";
  protected final String TEXT_45 = ");" + NL + "    globalMap.put(\"";
  protected final String TEXT_46 = "_NB_LINE_REJECTED\",nb_line_rejected_";
  protected final String TEXT_47 = ");";
  protected final String TEXT_48 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	
	String cid = node.getUniqueName();
	
	String dataAction = ElementParameterParser.getValue(node,"__DATA_ACTION__");
	
	String commitEvery = ElementParameterParser.getValue(node, "__COMMIT_EVERY__");
	
	String useExistingConn = ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__");
	
    if(("INSERT_OR_UPDATE").equals(dataAction)) {
        
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
    
    } else if(("UPDATE_OR_INSERT").equals(dataAction)) {
        
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    
    } else {
        
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    
    }
    
    stringBuffer.append(TEXT_17);
    
    if(!("true").equals(useExistingConn)) {
        
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    
            if(!("").equals(commitEvery) && !("0").equals(commitEvery)) {
                
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    
            }    
            commitEvery = "0";
            
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(TEXT_48);
    return stringBuffer.toString();
  }
}
