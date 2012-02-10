package org.talend.designer.codegen.translators.databases.sybase;

import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import java.util.List;

public class TSybaseOutputEndJava
{
  protected static String nl;
  public static synchronized TSybaseOutputEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSybaseOutputEndJava result = new TSybaseOutputEndJava();
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
  protected final String TEXT_14 = NL + "\t";
  protected final String TEXT_15 = "                " + NL + "    try {" + NL + "    \t";
  protected final String TEXT_16 = NL + "\t\t\tif ( batchSizeCounter_";
  protected final String TEXT_17 = " > 0 ) { // do not commit empty batch, this is a jdbc error" + NL + "\t\t";
  protected final String TEXT_18 = NL + "\t\t\tif ( commitCounter_";
  protected final String TEXT_19 = " > 0 ) { // do not commit empty batch, this is a jdbc error" + NL + "\t\t";
  protected final String TEXT_20 = NL + "\t\t\t{" + NL + "\t\t";
  protected final String TEXT_21 = NL + "\t\t\tint countSum_";
  protected final String TEXT_22 = " = 0;" + NL + "\t\t\tfor(int countEach_";
  protected final String TEXT_23 = ": pstmt_";
  protected final String TEXT_24 = ".executeBatch()) {" + NL + "\t\t\t\tcountSum_";
  protected final String TEXT_25 = " += (countEach_";
  protected final String TEXT_26 = " < 0 ? 0 : countEach_";
  protected final String TEXT_27 = ");" + NL + "\t\t\t}                \t" + NL + "\t    \t";
  protected final String TEXT_28 = NL + "\t    \t\tinsertedCount_";
  protected final String TEXT_29 = " += countSum_";
  protected final String TEXT_30 = "; " + NL + "\t    \t";
  protected final String TEXT_31 = NL + "\t    \t\tupdatedCount_";
  protected final String TEXT_32 = " += countSum_";
  protected final String TEXT_33 = ";" + NL + "\t    \t";
  protected final String TEXT_34 = NL + "\t    \t    deletedCount_";
  protected final String TEXT_35 = " += countSum_";
  protected final String TEXT_36 = ";" + NL + "\t    \t";
  protected final String TEXT_37 = "  " + NL + "\t    }          \t    " + NL + "    }catch (java.sql.BatchUpdateException e){" + NL + "    \t";
  protected final String TEXT_38 = NL + "    \t\tthrow(e);" + NL + "    \t";
  protected final String TEXT_39 = NL + "    \tint countSum_";
  protected final String TEXT_40 = " = 0;" + NL + "\t\tfor(int countEach_";
  protected final String TEXT_41 = ": e.getUpdateCounts()) {" + NL + "\t\t\tcountSum_";
  protected final String TEXT_42 = " += (countEach_";
  protected final String TEXT_43 = " < 0 ? 0 : countEach_";
  protected final String TEXT_44 = ");" + NL + "\t\t}" + NL + "\t\t";
  protected final String TEXT_45 = NL + "    \t\tinsertedCount_";
  protected final String TEXT_46 = " += countSum_";
  protected final String TEXT_47 = "; " + NL + "    \t";
  protected final String TEXT_48 = NL + "    \t\tupdatedCount_";
  protected final String TEXT_49 = " += countSum_";
  protected final String TEXT_50 = ";" + NL + "    \t";
  protected final String TEXT_51 = NL + "    \t    deletedCount_";
  protected final String TEXT_52 = " += countSum_";
  protected final String TEXT_53 = ";" + NL + "    \t";
  protected final String TEXT_54 = NL + "    \tSystem.out.println(e.getMessage());" + NL + "    \t";
  protected final String TEXT_55 = "                \t" + NL + "\t}                                  ";
  protected final String TEXT_56 = "   " + NL + "        if(pstmt_";
  protected final String TEXT_57 = " != null) {" + NL + "" + NL + "            pstmt_";
  protected final String TEXT_58 = ".close();" + NL + "            " + NL + "        }        ";
  protected final String TEXT_59 = NL + "        stmt_";
  protected final String TEXT_60 = ".execute(\"SET IDENTITY_INSERT \"+ ";
  protected final String TEXT_61 = " +\" OFF\");" + NL + "        stmt_";
  protected final String TEXT_62 = ".close();";
  protected final String TEXT_63 = NL + "\t\t    conn_";
  protected final String TEXT_64 = ".commit();" + NL + "\t\t    ";
  protected final String TEXT_65 = NL + "\t\tconn_";
  protected final String TEXT_66 = " .close();" + NL + "\t\t";
  protected final String TEXT_67 = NL + NL + "\tnb_line_deleted_";
  protected final String TEXT_68 = "=nb_line_deleted_";
  protected final String TEXT_69 = "+ deletedCount_";
  protected final String TEXT_70 = ";" + NL + "\tnb_line_update_";
  protected final String TEXT_71 = "=nb_line_update_";
  protected final String TEXT_72 = " + updatedCount_";
  protected final String TEXT_73 = ";" + NL + "\tnb_line_inserted_";
  protected final String TEXT_74 = "=nb_line_inserted_";
  protected final String TEXT_75 = " + insertedCount_";
  protected final String TEXT_76 = ";" + NL + "\tnb_line_rejected_";
  protected final String TEXT_77 = "=nb_line_rejected_";
  protected final String TEXT_78 = " + rejectedCount_";
  protected final String TEXT_79 = ";" + NL + "\t" + NL + "    globalMap.put(\"";
  protected final String TEXT_80 = "_NB_LINE\",nb_line_";
  protected final String TEXT_81 = ");" + NL + "    globalMap.put(\"";
  protected final String TEXT_82 = "_NB_LINE_UPDATED\",nb_line_update_";
  protected final String TEXT_83 = ");" + NL + "    globalMap.put(\"";
  protected final String TEXT_84 = "_NB_LINE_INSERTED\",nb_line_inserted_";
  protected final String TEXT_85 = ");" + NL + "    globalMap.put(\"";
  protected final String TEXT_86 = "_NB_LINE_DELETED\",nb_line_deleted_";
  protected final String TEXT_87 = ");" + NL + "    globalMap.put(\"";
  protected final String TEXT_88 = "_NB_LINE_REJECTED\",nb_line_rejected_";
  protected final String TEXT_89 = ");";
  protected final String TEXT_90 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	
	String cid = node.getUniqueName();
	
	String dataAction = ElementParameterParser.getValue(node,"__DATA_ACTION__");
	
	String commitEvery = ElementParameterParser.getValue(node, "__COMMIT_EVERY__");
	
	boolean useExistingConn = ("true").equals(ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__"));
	
	String tableName = ElementParameterParser.getValue(node,"__TABLE__");
	
	String identityInsert = ElementParameterParser.getValue(node, "__IDENTITY_INSERT__");
	
	boolean useBatchSize = ("true").equals(ElementParameterParser.getValue(node,"__USE_BATCH_SIZE__"));
	String batchSize=ElementParameterParser.getValue(node,"__BATCH_SIZE__");
	
	String dieOnError = ElementParameterParser.getValue(node, "__DIE_ON_ERROR__");
	
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
    
    } else {
        
    stringBuffer.append(TEXT_14);
    if ((rejectConnName==null && useBatchSize) && (("INSERT").equals(dataAction) || ("UPDATE").equals(dataAction) || ("DELETE").equals(dataAction))) {
    
    stringBuffer.append(TEXT_15);
    	
		if(useBatchSize && !("").equals(batchSize) && !("0").equals(batchSize)) {
		
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    if (("INSERT").equals(dataAction)) {
	    	
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    
	    	}else if (("UPDATE").equals(dataAction)) {
	    	
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    
	    	}else if (("DELETE").equals(dataAction)) {
	    	
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    
	    	}
    stringBuffer.append(TEXT_37);
    if(("true").equals(dieOnError)) {
    	
    stringBuffer.append(TEXT_38);
    
    	}else {
    	
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    if (("INSERT").equals(dataAction)) {
    	
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    
    	}else if (("UPDATE").equals(dataAction)) {
    	
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_50);
    
    	}else if (("DELETE").equals(dataAction)) {
    	
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_53);
    
    	}
    stringBuffer.append(TEXT_54);
    
    	}
    stringBuffer.append(TEXT_55);
    
    }
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_58);
    
    } 
    
    if(("true").equals(identityInsert)){
        
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_60);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_62);
    
	}
    
	if(!useExistingConn){
		if(!("").equals(commitEvery) && !("0").equals(commitEvery)){
		    
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_64);
    
		}
		
    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_66);
    
	}
	
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_73);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_74);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_76);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_78);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_82);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_83);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_86);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_87);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_89);
    stringBuffer.append(TEXT_90);
    return stringBuffer.toString();
  }
}
