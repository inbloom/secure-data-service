package org.talend.designer.codegen.translators.databases.javadb;

import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;

public class TJavaDBOutputEndJava
{
  protected static String nl;
  public static synchronized TJavaDBOutputEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TJavaDBOutputEndJava result = new TJavaDBOutputEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "        conn_";
  protected final String TEXT_2 = ".commit();";
  protected final String TEXT_3 = NL + "        if(pstmtUpdate_";
  protected final String TEXT_4 = " != null){" + NL + "" + NL + "            pstmtUpdate_";
  protected final String TEXT_5 = ".close();" + NL + "            " + NL + "        } " + NL + "        if(pstmtInsert_";
  protected final String TEXT_6 = " != null){" + NL + "" + NL + "            pstmtInsert_";
  protected final String TEXT_7 = ".close();" + NL + "            " + NL + "        }" + NL + "        if(pstmt_";
  protected final String TEXT_8 = " != null) {" + NL + "" + NL + "            pstmt_";
  protected final String TEXT_9 = ".close();" + NL + "            " + NL + "        }        ";
  protected final String TEXT_10 = NL + "        if(pstmtUpdate_";
  protected final String TEXT_11 = " != null){" + NL + "" + NL + "            pstmtUpdate_";
  protected final String TEXT_12 = ".close();" + NL + "            " + NL + "        } " + NL + "        if(pstmtInsert_";
  protected final String TEXT_13 = " != null){" + NL + "" + NL + "            pstmtInsert_";
  protected final String TEXT_14 = ".close();" + NL + "            " + NL + "        }        ";
  protected final String TEXT_15 = NL + "        if(pstmt_";
  protected final String TEXT_16 = " != null) {" + NL + "" + NL + "            pstmt_";
  protected final String TEXT_17 = ".close();" + NL + "            " + NL + "        }        ";
  protected final String TEXT_18 = NL + "    conn_";
  protected final String TEXT_19 = " .close();" + NL;
  protected final String TEXT_20 = NL + "    \ttry" + NL + "    \t{" + NL + "    \t\tjava.sql.DriverManager.getConnection(\"jdbc:derby:;shutdown=true\");" + NL + "    \t}" + NL + "    \tcatch(java.sql.SQLException se)" + NL + "    \t{" + NL + "            /*" + NL + "               In embedded mode, an application should shut down Derby." + NL + "               If the application fails to shut down Derby explicitly," + NL + "               the Derby does not perform a checkpoint when the JVM shuts down, which means" + NL + "               that the next connection will be slower." + NL + "               Explicitly shutting down Derby with the URL is preferred." + NL + "               This style of shutdown will always throw an \"exception\"." + NL + "             */" + NL + "    \t}\t";
  protected final String TEXT_21 = NL + "    \tserverControl_";
  protected final String TEXT_22 = ".shutdown();" + NL + "    \t";
  protected final String TEXT_23 = NL + NL + "\tnb_line_deleted_";
  protected final String TEXT_24 = "=nb_line_deleted_";
  protected final String TEXT_25 = "+ deletedCount_";
  protected final String TEXT_26 = ";" + NL + "\tnb_line_update_";
  protected final String TEXT_27 = "=nb_line_update_";
  protected final String TEXT_28 = " + updatedCount_";
  protected final String TEXT_29 = ";" + NL + "\tnb_line_inserted_";
  protected final String TEXT_30 = "=nb_line_inserted_";
  protected final String TEXT_31 = " + insertedCount_";
  protected final String TEXT_32 = ";" + NL + "" + NL + "    globalMap.put(\"";
  protected final String TEXT_33 = "_NB_LINE\",nb_line_";
  protected final String TEXT_34 = ");" + NL + "    globalMap.put(\"";
  protected final String TEXT_35 = "_NB_LINE_UPDATED\",nb_line_update_";
  protected final String TEXT_36 = ");" + NL + "    globalMap.put(\"";
  protected final String TEXT_37 = "_NB_LINE_INSERTED\",nb_line_inserted_";
  protected final String TEXT_38 = ");" + NL + "    globalMap.put(\"";
  protected final String TEXT_39 = "_NB_LINE_DELETED\",nb_line_deleted_";
  protected final String TEXT_40 = ");";
  protected final String TEXT_41 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	
	String cid = node.getUniqueName();
	
	String dataAction = ElementParameterParser.getValue(node,"__DATA_ACTION__");
	
	String commitEvery = ElementParameterParser.getValue(node, "__COMMIT_EVERY__");
	
	String frameworkType = ElementParameterParser.getValue(node,"__FRAMEWORK_TYPE__");
	
	String connectionFlag = ElementParameterParser.getValue(node, "__CONNECTION_FLAG__");	
	
    if(!("").equals(commitEvery)&&!("0").equals(commitEvery)){
        
    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    
    }
    
    if(("INSERT_OR_UPDATE").equals(dataAction)) {
        
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
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
    
    } else if(("UPDATE_OR_INSERT").equals(dataAction)) {
        
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    
    } else {
        
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    
    }   
    
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    
    if(("EMBEDED").equals(frameworkType))
    {
    stringBuffer.append(TEXT_20);
    
    }
    else
    {
    	if(("false").equals(connectionFlag))
    	{
    	
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    
    	}
    }
    
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(TEXT_41);
    return stringBuffer.toString();
  }
}
