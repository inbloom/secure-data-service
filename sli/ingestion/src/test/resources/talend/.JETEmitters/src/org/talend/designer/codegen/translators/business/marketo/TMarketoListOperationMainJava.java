package org.talend.designer.codegen.translators.business.marketo;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;
import java.util.Map;

public class TMarketoListOperationMainJava
{
  protected static String nl;
  public static synchronized TMarketoListOperationMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMarketoListOperationMainJava result = new TMarketoListOperationMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t" + NL + "\t\t";
  protected final String TEXT_2 = " = null;\t\t\t" + NL + "\t";
  protected final String TEXT_3 = NL + "\tif(firstList_";
  protected final String TEXT_4 = "){" + NL + "\t\tlistTypeFlag_";
  protected final String TEXT_5 = " = ";
  protected final String TEXT_6 = ".ListKeyType;" + NL + "\t\tlistValueFlag_";
  protected final String TEXT_7 = " = ";
  protected final String TEXT_8 = ".ListKeyValue;" + NL + "\t\tfirstList_";
  protected final String TEXT_9 = " = false;" + NL + "\t}" + NL + "\tif(!listTypeFlag_";
  protected final String TEXT_10 = ".equals(";
  protected final String TEXT_11 = ".ListKeyType) || !listValueFlag_";
  protected final String TEXT_12 = ".equals(";
  protected final String TEXT_13 = ".ListKeyValue)){" + NL + "\t\ttry{" + NL + "\t\t\tclient_";
  protected final String TEXT_14 = ".listOperation(\"";
  protected final String TEXT_15 = "\",listTypeFlag_";
  protected final String TEXT_16 = ",listValueFlag_";
  protected final String TEXT_17 = ",leadKeyList_";
  protected final String TEXT_18 = ".toArray(new com.marketo.www.mktows.LeadKey[leadKeyList_";
  protected final String TEXT_19 = ".size()]),";
  protected final String TEXT_20 = ");" + NL + "\t\t\tglobalMap.put(\"";
  protected final String TEXT_21 = "_NB_CALL\",++nb_call_";
  protected final String TEXT_22 = ");\t\t" + NL + "\t\t}catch(Exception ex_";
  protected final String TEXT_23 = "){" + NL + "\t\t\tif(ex_";
  protected final String TEXT_24 = " instanceof org.apache.axis.AxisFault){" + NL + "    \t\t\tif(!client_";
  protected final String TEXT_25 = ".isSystemError((org.apache.axis.AxisFault)ex_";
  protected final String TEXT_26 = ")){" + NL + "\t\t\t\t\tglobalMap.put(\"";
  protected final String TEXT_27 = "_NB_CALL\",++nb_call_";
  protected final String TEXT_28 = ");\t\t" + NL + "\t\t\t\t}" + NL + "    \t\t}" + NL + "\t\t\t";
  protected final String TEXT_29 = NL + "\t            throw(ex_";
  protected final String TEXT_30 = ");" + NL + "\t        ";
  protected final String TEXT_31 = NL + "\t        \tSystem.err.println(ex_";
  protected final String TEXT_32 = ".getMessage());" + NL + "\t        ";
  protected final String TEXT_33 = NL + "\t\t}" + NL + "\t\tleadKeyList_";
  protected final String TEXT_34 = ".clear();" + NL + "\t\tlistTypeFlag_";
  protected final String TEXT_35 = " = ";
  protected final String TEXT_36 = ".ListKeyType;" + NL + "\t\tlistValueFlag_";
  protected final String TEXT_37 = " = ";
  protected final String TEXT_38 = ".ListKeyValue;" + NL + "\t}" + NL + "\tleadKeyList_";
  protected final String TEXT_39 = ".add(client_";
  protected final String TEXT_40 = ".getLeadKey(";
  protected final String TEXT_41 = ".LeadKeyType,";
  protected final String TEXT_42 = ".LeadKeyValue));";
  protected final String TEXT_43 = " " + NL + "\tleadKeyList_";
  protected final String TEXT_44 = ".add(client_";
  protected final String TEXT_45 = ".getLeadKey(";
  protected final String TEXT_46 = ".LeadKeyType,";
  protected final String TEXT_47 = ".LeadKeyValue));" + NL + "\ttry{" + NL + "\t\twhetherReject_";
  protected final String TEXT_48 = " = false;" + NL + "\t\tresultListOperation_";
  protected final String TEXT_49 = " = client_";
  protected final String TEXT_50 = ".listOperation(\"";
  protected final String TEXT_51 = "\",";
  protected final String TEXT_52 = ".ListKeyType,";
  protected final String TEXT_53 = ".ListKeyValue,leadKeyList_";
  protected final String TEXT_54 = ".toArray(new com.marketo.www.mktows.LeadKey[leadKeyList_";
  protected final String TEXT_55 = ".size()]),";
  protected final String TEXT_56 = ");" + NL + "\t\tglobalMap.put(\"";
  protected final String TEXT_57 = "_NB_CALL\",++nb_call_";
  protected final String TEXT_58 = ");" + NL + "\t}catch(Exception ex_";
  protected final String TEXT_59 = "){" + NL + "\t\tif(ex_";
  protected final String TEXT_60 = " instanceof org.apache.axis.AxisFault){" + NL + "\t\t\tif(!client_";
  protected final String TEXT_61 = ".isSystemError((org.apache.axis.AxisFault)ex_";
  protected final String TEXT_62 = ")){" + NL + "\t\t\t\tglobalMap.put(\"";
  protected final String TEXT_63 = "_NB_CALL\",++nb_call_";
  protected final String TEXT_64 = ");\t\t" + NL + "\t\t\t}" + NL + "\t\t}" + NL + "\t\twhetherReject_";
  protected final String TEXT_65 = " = true;" + NL + "\t\t";
  protected final String TEXT_66 = NL + "            throw(ex_";
  protected final String TEXT_67 = ");";
  protected final String TEXT_68 = NL + "            \t";
  protected final String TEXT_69 = " = new ";
  protected final String TEXT_70 = "Struct();";
  protected final String TEXT_71 = NL + "                ";
  protected final String TEXT_72 = ".ListKeyType = ";
  protected final String TEXT_73 = ".ListKeyType;";
  protected final String TEXT_74 = NL + "                ";
  protected final String TEXT_75 = ".ListKeyValue = ";
  protected final String TEXT_76 = ".ListKeyValue;";
  protected final String TEXT_77 = NL + "                ";
  protected final String TEXT_78 = ".LeadKeyType = ";
  protected final String TEXT_79 = ".LeadKeyType;";
  protected final String TEXT_80 = NL + "                ";
  protected final String TEXT_81 = ".LeadKeyValue = ";
  protected final String TEXT_82 = ".LeadKeyValue;" + NL + "            \t";
  protected final String TEXT_83 = ".ERROR_MSG = ex_";
  protected final String TEXT_84 = ".getMessage();";
  protected final String TEXT_85 = NL + "        \t\tSystem.err.println(ex_";
  protected final String TEXT_86 = ".getMessage());" + NL + "        \t";
  protected final String TEXT_87 = NL + "\t}" + NL + "\tleadKeyList_";
  protected final String TEXT_88 = ".clear();";
  protected final String TEXT_89 = "\t\t" + NL + "\t\t";
  protected final String TEXT_90 = NL + "\t\t\tif(!whetherReject_";
  protected final String TEXT_91 = "){" + NL + "\t\t";
  protected final String TEXT_92 = NL + "\t\t\t\t";
  protected final String TEXT_93 = " = new ";
  protected final String TEXT_94 = "Struct();" + NL + "\t\t\t\t";
  protected final String TEXT_95 = NL + "\t\t\t\t\t";
  protected final String TEXT_96 = ".Success = resultListOperation_";
  protected final String TEXT_97 = ".isSuccess();" + NL + "\t\t\t\t";
  protected final String TEXT_98 = NL + "\t\t\t\t";
  protected final String TEXT_99 = "\t\t\t" + NL + "\t\t   \t\t\t";
  protected final String TEXT_100 = ".";
  protected final String TEXT_101 = " = ";
  protected final String TEXT_102 = ".";
  protected final String TEXT_103 = "; \t\t\t" + NL + "\t\t\t\t";
  protected final String TEXT_104 = NL + "\t\t";
  protected final String TEXT_105 = NL + "\t\t\t}" + NL + "\t\t";
  protected final String TEXT_106 = NL;
  protected final String TEXT_107 = NL + NL;
  protected final String TEXT_108 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
     
    CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
    INode node = (INode)codeGenArgument.getArgument();
    String cid = node.getUniqueName();	
    
	String operation = ElementParameterParser.getValue(node, "__OPERATION__");
	boolean mutipleOperation = ("true").equals(ElementParameterParser.getValue(node,"__MUTIPLE_OPERATION__"));
	boolean isMutiple = false;
	if(mutipleOperation&&!operation.equals("ISMEMBEROFLIST")){
		isMutiple = true;
	}
	boolean strict = ("true").equals(ElementParameterParser.getValue(node,"__STRICT__"));
	
	String rejectConnName = null;
    List<? extends IConnection> rejectConns = node.getOutgoingConnections("REJECT");
    if(rejectConns != null && rejectConns.size() > 0) {
        IConnection rejectConn = rejectConns.get(0);
        rejectConnName = rejectConn.getName();
    }
    
    String mainConnName = null;
    List<? extends IConnection> mainConns = node.getOutgoingConnections("OUTPUT_MAIN");
    if(mainConns != null && mainConns.size() > 0) {
        IConnection mainConn = mainConns.get(0);
        mainConnName = mainConn.getName();
    }
	
	List<? extends IConnection> outgoingConns = node.getOutgoingSortedConnections();
	if(outgoingConns!=null){
		for (int i=0;i<outgoingConns.size();i++) {
	    IConnection outgoingConn = outgoingConns.get(i);
	    	if (outgoingConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
	
    stringBuffer.append(TEXT_1);
    stringBuffer.append(outgoingConn.getName() );
    stringBuffer.append(TEXT_2);
    
	    	}
	    }
	} 
	
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {//1
    IMetadataTable metadata = metadatas.get(0);
    
    if (metadata!=null) {//2
    	List< ? extends IConnection> conns = node.getIncomingConnections();
    	
    	for (IConnection conn : conns) {//3
    		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {//4

    if(isMutiple){
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(operation);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(strict);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
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
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    
	        if (strict) {
	        
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    
	        } else {
	        
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    
	        }
	        
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_41);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_42);
    }else{
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_46);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(operation);
    stringBuffer.append(TEXT_51);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_52);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_55);
    stringBuffer.append(strict);
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_65);
    
        if (strict) {
        
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_67);
    
        } else {
        	if(rejectConnName != null) {
            
    stringBuffer.append(TEXT_68);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_69);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_70);
    stringBuffer.append(TEXT_71);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_72);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_73);
    stringBuffer.append(TEXT_74);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_75);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_76);
    stringBuffer.append(TEXT_77);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_78);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_79);
    stringBuffer.append(TEXT_80);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_81);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_82);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_83);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_84);
    
            } else {
            
    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_86);
    
        	}
        }
        
    stringBuffer.append(TEXT_87);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_88);
    }		
	 if(mainConnName != null){

    stringBuffer.append(TEXT_89);
    if(!isMutiple){
    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_91);
    }
    stringBuffer.append(TEXT_92);
    stringBuffer.append(mainConnName);
    stringBuffer.append(TEXT_93);
    stringBuffer.append(mainConnName);
    stringBuffer.append(TEXT_94);
    if(!isMutiple){
    stringBuffer.append(TEXT_95);
    stringBuffer.append(mainConnName);
    stringBuffer.append(TEXT_96);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_97);
    }
    stringBuffer.append(TEXT_98);
    
				for (IMetadataColumn column: metadata.getListColumns()) {
					if(!isMutiple){
						if("Success".equals(column.getLabel()))
							continue;
					}
					
    stringBuffer.append(TEXT_99);
    stringBuffer.append(mainConnName);
    stringBuffer.append(TEXT_100);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_101);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_102);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_103);
     					 
				}
				
    stringBuffer.append(TEXT_104);
    if(!isMutiple){
    stringBuffer.append(TEXT_105);
    
		}
	}

    stringBuffer.append(TEXT_106);
     
			}//4
		}//3
	}//2
}//1

    stringBuffer.append(TEXT_107);
    stringBuffer.append(TEXT_108);
    return stringBuffer.toString();
  }
}
