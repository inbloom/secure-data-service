package org.talend.designer.codegen.translators.databases.vertica;

import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;

public class TVerticaOutputEndJava
{
  protected static String nl;
  public static synchronized TVerticaOutputEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TVerticaOutputEndJava result = new TVerticaOutputEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = "                " + NL + "                try {" + NL + "\t\t\t\t\t\tpstmt_";
  protected final String TEXT_3 = ".executeBatch();                \t" + NL + "            \t    \t";
  protected final String TEXT_4 = NL + "            \t    \t\tinsertedCount_";
  protected final String TEXT_5 = " +=pstmt_";
  protected final String TEXT_6 = ".getUpdateCount(); " + NL + "            \t    \t";
  protected final String TEXT_7 = NL + "            \t    \t\tupdatedCount_";
  protected final String TEXT_8 = " += pstmt_";
  protected final String TEXT_9 = ".getUpdateCount();" + NL + "            \t    \t";
  protected final String TEXT_10 = NL + "            \t    \t    deletedCount_";
  protected final String TEXT_11 = " += pstmt_";
  protected final String TEXT_12 = ".getUpdateCount();" + NL + "            \t    \t";
  protected final String TEXT_13 = "            \t    " + NL + "                }catch (java.sql.BatchUpdateException e){" + NL + "                \t";
  protected final String TEXT_14 = NL + "                \t\tthrow(e);" + NL + "                \t";
  protected final String TEXT_15 = NL + "                \tSystem.out.println(e.getMessage());" + NL + "                \t";
  protected final String TEXT_16 = "                \t" + NL + "            \t}                                  ";
  protected final String TEXT_17 = "    " + NL + "\t\t\t\t\tif (stmtBuilder_";
  protected final String TEXT_18 = " != null && stmtBuilder_";
  protected final String TEXT_19 = ".length() != 0 ) {" + NL + "\t\t\t\t\t    java.io.ByteArrayInputStream bais_";
  protected final String TEXT_20 = " = new ByteArrayInputStream(stmtBuilder_";
  protected final String TEXT_21 = ".toString().getBytes());" + NL + "\t\t\t\t\t\tif (globalMap.get(\"";
  protected final String TEXT_22 = "_COPY_STARTED\") == null) {" + NL + "\t\t\t\t\t\t    ((com.vertica.PGStatement)stmt_";
  protected final String TEXT_23 = ").executeCopyIn(template_";
  protected final String TEXT_24 = ", bais_";
  protected final String TEXT_25 = ");" + NL + "\t\t\t\t\t\t    globalMap.put(\"";
  protected final String TEXT_26 = "_COPY_STARTED\",Boolean.TRUE);" + NL + "\t\t\t\t\t\t} else {" + NL + "\t\t\t\t\t\t\t((com.vertica.PGStatement)stmt_";
  protected final String TEXT_27 = ").addStreamToCopyIn(bais_";
  protected final String TEXT_28 = ");" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\tstmtBuilder_";
  protected final String TEXT_29 = " = null;" + NL + "\t\t\t\t\t\tbais_";
  protected final String TEXT_30 = " = null;" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t\t" + NL + "\t\t\t\t" + NL + "\t\t\t    ";
  protected final String TEXT_31 = NL + "        pstmt_";
  protected final String TEXT_32 = " = conn_";
  protected final String TEXT_33 = ".prepareStatement(\"COMMIT;\");              " + NL + "        pstmtInsert_";
  protected final String TEXT_34 = " = conn_";
  protected final String TEXT_35 = ".prepareStatement(\"COMMIT;\");   " + NL + "        pstmtUpdate_";
  protected final String TEXT_36 = " = conn_";
  protected final String TEXT_37 = ".prepareStatement(\"COMMIT;\");   " + NL + "        pstmt_";
  protected final String TEXT_38 = ".executeUpdate();" + NL + "        pstmtInsert_";
  protected final String TEXT_39 = ".executeUpdate();" + NL + "        pstmtUpdate_";
  protected final String TEXT_40 = ".executeUpdate();";
  protected final String TEXT_41 = "         " + NL + "        pstmtInsert_";
  protected final String TEXT_42 = " = conn_";
  protected final String TEXT_43 = ".prepareStatement(\"COMMIT;\");   " + NL + "        pstmtUpdate_";
  protected final String TEXT_44 = " = conn_";
  protected final String TEXT_45 = ".prepareStatement(\"COMMIT;\");    " + NL + "        pstmtInsert_";
  protected final String TEXT_46 = ".executeUpdate();" + NL + "        pstmtUpdate_";
  protected final String TEXT_47 = ".executeUpdate();      ";
  protected final String TEXT_48 = "   " + NL + "    \t";
  protected final String TEXT_49 = NL + "        pstmt_";
  protected final String TEXT_50 = " = conn_";
  protected final String TEXT_51 = ".prepareStatement(\"COMMIT;\");    " + NL + "        pstmt_";
  protected final String TEXT_52 = ".executeUpdate();";
  protected final String TEXT_53 = NL + "        if(pstmtUpdate_";
  protected final String TEXT_54 = " != null){" + NL + "" + NL + "            pstmtUpdate_";
  protected final String TEXT_55 = ".close();" + NL + "            " + NL + "        } " + NL + "        if(pstmtInsert_";
  protected final String TEXT_56 = " != null){" + NL + "" + NL + "            pstmtInsert_";
  protected final String TEXT_57 = ".close();" + NL + "            " + NL + "        }" + NL + "        if(pstmt_";
  protected final String TEXT_58 = " != null) {" + NL + "" + NL + "            pstmt_";
  protected final String TEXT_59 = ".close();" + NL + "            " + NL + "        }        ";
  protected final String TEXT_60 = NL + "        if(pstmtUpdate_";
  protected final String TEXT_61 = " != null){" + NL + "" + NL + "            pstmtUpdate_";
  protected final String TEXT_62 = ".close();" + NL + "            " + NL + "        } " + NL + "        if(pstmtInsert_";
  protected final String TEXT_63 = " != null){" + NL + "" + NL + "            pstmtInsert_";
  protected final String TEXT_64 = ".close();" + NL + "            " + NL + "        }        ";
  protected final String TEXT_65 = NL + "\t        if(pstmt_";
  protected final String TEXT_66 = " != null) {" + NL + "\t" + NL + "\t            pstmt_";
  protected final String TEXT_67 = ".close();" + NL + "\t            " + NL + "\t        }        ";
  protected final String TEXT_68 = NL + "\t        conn_";
  protected final String TEXT_69 = ".commit();" + NL + "\t        ";
  protected final String TEXT_70 = NL + "\t     conn_";
  protected final String TEXT_71 = " .close();" + NL + "\t";
  protected final String TEXT_72 = NL + NL + "    nb_line_deleted_";
  protected final String TEXT_73 = "=nb_line_deleted_";
  protected final String TEXT_74 = "+ deletedCount_";
  protected final String TEXT_75 = ";" + NL + "    nb_line_update_";
  protected final String TEXT_76 = "=nb_line_update_";
  protected final String TEXT_77 = " + updatedCount_";
  protected final String TEXT_78 = ";" + NL + "    nb_line_inserted_";
  protected final String TEXT_79 = "=nb_line_inserted_";
  protected final String TEXT_80 = " + insertedCount_";
  protected final String TEXT_81 = ";" + NL + "    nb_line_rejected_";
  protected final String TEXT_82 = "=nb_line_rejected_";
  protected final String TEXT_83 = " + rejectedCount_";
  protected final String TEXT_84 = ";" + NL + "" + NL + "    globalMap.put(\"";
  protected final String TEXT_85 = "_NB_LINE\",nb_line_";
  protected final String TEXT_86 = ");" + NL + "    globalMap.put(\"";
  protected final String TEXT_87 = "_NB_LINE_UPDATED\",nb_line_update_";
  protected final String TEXT_88 = ");" + NL + "    globalMap.put(\"";
  protected final String TEXT_89 = "_NB_LINE_INSERTED\",nb_line_inserted_";
  protected final String TEXT_90 = ");" + NL + "    globalMap.put(\"";
  protected final String TEXT_91 = "_NB_LINE_DELETED\",nb_line_deleted_";
  protected final String TEXT_92 = ");" + NL + "    globalMap.put(\"";
  protected final String TEXT_93 = "_NB_LINE_REJECTED\",nb_line_rejected_";
  protected final String TEXT_94 = ");";
  protected final String TEXT_95 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
    CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
    INode node = (INode)codeGenArgument.getArgument();
    
    String cid = node.getUniqueName();
    
    String dataAction = ElementParameterParser.getValue(node,"__DATA_ACTION__");
    
    String commitEvery = ElementParameterParser.getValue(node, "__COMMIT_EVERY__");
    
    boolean batchMode = ("true").equals(ElementParameterParser.getValue(node, "__BATCH_MODE__"));
    
    String dieOnError = ElementParameterParser.getValue(node, "__DIE_ON_ERROR__");
    
     boolean useExistingConnection = "true".equals(ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__"));

    
	//batch mode
	if (("INSERT").equals(dataAction) || ("UPDATE").equals(dataAction) || ("DELETE").equals(dataAction)) {

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    if (("INSERT").equals(dataAction)) {
            	    	
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    
            	    	}else if (("UPDATE").equals(dataAction)) {
            	    	
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    
            	    	}else if (("DELETE").equals(dataAction)) {
            	    	
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    
            	    	}
    stringBuffer.append(TEXT_13);
    if(("true").equals(dieOnError)) {
                	
    stringBuffer.append(TEXT_14);
    
                	}else {
                	
    stringBuffer.append(TEXT_15);
    
                	}
    stringBuffer.append(TEXT_16);
    
				} else if ("COPY".equals(dataAction)) {
					boolean abortOnError = "true".equals(ElementParameterParser.getValue(node,"__ABORT_ON_ERROR__"));
					String rejectMax = ElementParameterParser.getValue(node,"__REJECT_MAX__");
					String exceptionsPath = ElementParameterParser.getValue(node,"__EXCEPTIONS_PATH__");
					String rejectsPath = ElementParameterParser.getValue(node,"__REJECTED_DATA__");
					boolean noCommit = "true".equals(ElementParameterParser.getValue(node,"__NO_COMMIT__"));
					String exceptionNode = ElementParameterParser.getValue(node,"__EXCEPTIONS_NODE__");
					String rejectsNode = ElementParameterParser.getValue(node,"__REJECTED_NODE__");	
					rejectMax = rejectMax == null || "".equals(rejectMax.trim()) ? "" : " REJECTMAX "+rejectMax+" ";
					exceptionsPath = exceptionsPath == null || "".equals(exceptionsPath.trim()) ? "" : " EXCEPTIONS '"+exceptionsPath+"' ";
					exceptionNode = exceptionNode == null || "".equals(exceptionNode.trim()) ? "" : " ON "+exceptionNode+" ";
					rejectsPath = rejectsPath == null || "".equals(rejectsPath.trim()) ? "" : " REJECTED DATA '"+rejectsPath+"' ";
					rejectsNode = rejectsNode == null || "".equals(rejectsNode.trim()) ? "" : " ON "+rejectsNode+" ";
					String exceptionString = !"".equals(exceptionsPath) ? exceptionsPath + exceptionNode : "";
					String rejectString = !"".equals(rejectsPath) ? rejectsPath + rejectsNode : "";
					String noComm = !noCommit ? "" : " NO COMMIT ";
					String abort = abortOnError ? " ABORT ON ERROR " : "";
				
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
    stringBuffer.append(cid);
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
    
				}
				
    if(("INSERT_OR_UPDATE").equals(dataAction)){
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    }else if(("UPDATE_OR_INSERT").equals(dataAction)){
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
    }else{
    stringBuffer.append(TEXT_48);
       
    	 if(!useExistingConnection && !"COPY".equals(dataAction))	{
    	 
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_52);
    
    	}
    }

    
    if(("INSERT_OR_UPDATE").equals(dataAction)) {
        
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_59);
    
    } else if(("UPDATE_OR_INSERT").equals(dataAction)) {
        
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_64);
    
    } else {
       if (!"COPY".equals(dataAction)) {
        
    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_67);
    
		}
    }   

    if(!useExistingConnection)
    {
	    if(!("").equals(commitEvery)&&!("0").equals(commitEvery)){
	        
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_69);
    
	    }
	   
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_71);
    
    }
   
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_82);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_83);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_86);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_87);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_92);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_93);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_94);
    stringBuffer.append(TEXT_95);
    return stringBuffer.toString();
  }
}
