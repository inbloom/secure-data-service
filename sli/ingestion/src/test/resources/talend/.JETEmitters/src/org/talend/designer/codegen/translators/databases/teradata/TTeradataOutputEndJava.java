package org.talend.designer.codegen.translators.databases.teradata;

import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import java.util.List;

public class TTeradataOutputEndJava
{
  protected static String nl;
  public static synchronized TTeradataOutputEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TTeradataOutputEndJava result = new TTeradataOutputEndJava();
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
  protected final String TEXT_14 = NL + "        ";
  protected final String TEXT_15 = NL + "                try {" + NL + "        \t\t\t";
  protected final String TEXT_16 = NL + "\t\t\t\t\t\tif ( batchSizeCounter_";
  protected final String TEXT_17 = " > 0 ) { // do not commit empty batch, this is a jdbc error" + NL + "\t\t\t\t\t";
  protected final String TEXT_18 = NL + "\t\t\t\t\t\tif ( commitCounter_";
  protected final String TEXT_19 = " > 0 ) { // do not commit empty batch, this is a jdbc error" + NL + "\t\t\t\t\t";
  protected final String TEXT_20 = NL + "\t\t\t\t\t\t{" + NL + "\t\t\t\t\t";
  protected final String TEXT_21 = "\t\t" + NL + "\t\t\t\t\t\tpstmt_";
  protected final String TEXT_22 = ".executeBatch();                \t" + NL + "            \t    \t";
  protected final String TEXT_23 = NL + "            \t    \t\tinsertedCount_";
  protected final String TEXT_24 = " +=pstmt_";
  protected final String TEXT_25 = ".getUpdateCount(); " + NL + "            \t    \t";
  protected final String TEXT_26 = NL + "            \t    \t\tupdatedCount_";
  protected final String TEXT_27 = " += pstmt_";
  protected final String TEXT_28 = ".getUpdateCount();" + NL + "            \t    \t";
  protected final String TEXT_29 = NL + "            \t    \t    deletedCount_";
  protected final String TEXT_30 = " += pstmt_";
  protected final String TEXT_31 = ".getUpdateCount();" + NL + "            \t    \t";
  protected final String TEXT_32 = NL + "            \t    \t" + NL + "\t\t\t\t\t\t}" + NL + "                //}catch (java.sql.BatchUpdateException e){" + NL + "\t\t\t\t}catch (java.sql.SQLException e){" + NL + "\t\t\t\t\t";
  protected final String TEXT_33 = NL + "\t\t\t\t\tjava.sql.SQLException e2 = e;" + NL + "\t\t\t\t\twhile (e2 != null) {" + NL + "\t\t\t\t\t\tSystem.out.println(e.getMessage());" + NL + "\t\t\t\t\t\te2 = e2.getNextException();" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_34 = NL + "                \t";
  protected final String TEXT_35 = NL + "                \t\t//throw(e);" + NL + "                \tboolean isFailure = false;" + NL + "                \tint errorCode = 0;" + NL + "\t\t\t\t\tString sqlState = \"\";" + NL + "\t\t\t\t\tString message = \"\";" + NL + "            \t    while(e != null){" + NL + "            \t    \tif(e.getErrorCode() == -2801){" + NL + "\t\t\t\t            System.out.println(\"[Message]: \" + e.getMessage());" + NL + "            \t    \t\te.printStackTrace();" + NL + "            \t    \t\tSystem.out.println();" + NL + "            \t    \t}" + NL + "            \t    \telse if(e.getErrorCode() == 1339){" + NL + "            \t    \t\tisFailure = true;" + NL + "            \t    \t}" + NL + "            \t    \telse{" + NL + "            \t    \t\t" + NL + " \t\t\t\t            System.out.println(\"[Message]: \" + e.getMessage());" + NL + "            \t    \t\te.printStackTrace();" + NL + "            \t    \t}" + NL + "            \t    \te = e.getNextException();" + NL + "            \t    }" + NL + "            \t    if(isFailure){" + NL + "\t\t\t\t         System.out.println(\"[Message]: \" + e.getMessage());" + NL + "            \t    \t e.printStackTrace();" + NL + "            \t    }" + NL + "                \t";
  protected final String TEXT_36 = NL + "                \tSystem.err.println(e.getMessage());" + NL + "                \t";
  protected final String TEXT_37 = "                \t" + NL + "            \t}                                  ";
  protected final String TEXT_38 = NL + "        " + NL + "        if(pstmt_";
  protected final String TEXT_39 = " != null) {" + NL + "" + NL + "            pstmt_";
  protected final String TEXT_40 = ".close();" + NL + "            pstmt_";
  protected final String TEXT_41 = " = null;" + NL + "        }        ";
  protected final String TEXT_42 = NL + "\t\t\tif (commitCounter_";
  protected final String TEXT_43 = " > 0)" + NL + "\t            conn_";
  protected final String TEXT_44 = ".commit();" + NL + "\t        ";
  protected final String TEXT_45 = NL + "\t    conn_";
  protected final String TEXT_46 = " .close();" + NL + "\t    ";
  protected final String TEXT_47 = NL + NL + "\tnb_line_deleted_";
  protected final String TEXT_48 = "=nb_line_deleted_";
  protected final String TEXT_49 = "+ deletedCount_";
  protected final String TEXT_50 = ";" + NL + "\tnb_line_update_";
  protected final String TEXT_51 = "=nb_line_update_";
  protected final String TEXT_52 = " + updatedCount_";
  protected final String TEXT_53 = ";" + NL + "\tnb_line_inserted_";
  protected final String TEXT_54 = "=nb_line_inserted_";
  protected final String TEXT_55 = " + insertedCount_";
  protected final String TEXT_56 = ";" + NL + "\tnb_line_rejected_";
  protected final String TEXT_57 = "=nb_line_rejected_";
  protected final String TEXT_58 = " + rejectedCount_";
  protected final String TEXT_59 = ";" + NL + "" + NL + "    globalMap.put(\"";
  protected final String TEXT_60 = "_NB_LINE\",nb_line_";
  protected final String TEXT_61 = ");" + NL + "    globalMap.put(\"";
  protected final String TEXT_62 = "_NB_LINE_UPDATED\",nb_line_update_";
  protected final String TEXT_63 = ");" + NL + "    globalMap.put(\"";
  protected final String TEXT_64 = "_NB_LINE_INSERTED\",nb_line_inserted_";
  protected final String TEXT_65 = ");" + NL + "    globalMap.put(\"";
  protected final String TEXT_66 = "_NB_LINE_DELETED\",nb_line_deleted_";
  protected final String TEXT_67 = ");" + NL + "    globalMap.put(\"";
  protected final String TEXT_68 = "_NB_LINE_REJECTED\",nb_line_rejected_";
  protected final String TEXT_69 = ");";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	
	String cid = node.getUniqueName();
	
	String dataAction = ElementParameterParser.getValue(node,"__DATA_ACTION__");
	
	String commitEvery = ElementParameterParser.getValue(node, "__COMMIT_EVERY__");
	
	String useBatchSize = ElementParameterParser.getValue(node, "__USE_BATCH_SIZE__");
	
	boolean useExistingConn = ("true").equals(ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__"));
	
	String dieOnError = ElementParameterParser.getValue(node, "__DIE_ON_ERROR__");
    
	boolean isEnableDebug = ("true").equals(ElementParameterParser.getValue(node,"__ENABLE_DEBUG_MODE__"));
	
	String batchSize=ElementParameterParser.getValue(node,"__BATCH_SIZE__");
	
    String rejectConnName = null;
    List<? extends IConnection> rejectConns = node.getOutgoingConnections("REJECT");
    if(rejectConns != null && rejectConns.size() > 0) {
        IConnection rejectConn = rejectConns.get(0);
        rejectConnName = rejectConn.getName();
    }
    
    
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
    
    } else { // dataAction equals "INSERT" or "UPDATE" or "DELETE" 
        
    stringBuffer.append(TEXT_14);
    if (  (rejectConnName==null) && (("INSERT").equals(dataAction) || ("UPDATE").equals(dataAction) || ("DELETE").equals(dataAction))) {
        
    stringBuffer.append(TEXT_15);
    	
					if("true".equals(useBatchSize) && !("").equals(batchSize) && !("0").equals(batchSize)) {
					
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    
					} else if (!useExistingConn &&!("").equals(commitEvery) && !("0").equals(commitEvery)) {
					
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    
					} else {
					
    stringBuffer.append(TEXT_20);
    
					} 
					
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    if (("INSERT").equals(dataAction)) {
            	    	
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    
            	    	}else if (("UPDATE").equals(dataAction)) {
            	    	
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    
            	    	}else if (("DELETE").equals(dataAction)) {
            	    	
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    
            	    	}
    stringBuffer.append(TEXT_32);
     if (isEnableDebug) { 
    stringBuffer.append(TEXT_33);
     } 
    stringBuffer.append(TEXT_34);
    if(("true").equals(dieOnError)) {
                	
    stringBuffer.append(TEXT_35);
    
                	}else {
                	
    stringBuffer.append(TEXT_36);
    
                	}
    stringBuffer.append(TEXT_37);
    
        }
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_41);
    
    }
    
    
	if(!useExistingConn){
	    if(!("").equals(commitEvery) && !("0").equals(commitEvery)) {
	        
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    
	    }
	    
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_46);
    
	}
    
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_69);
    return stringBuffer.toString();
  }
}
