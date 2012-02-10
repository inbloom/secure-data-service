package org.talend.designer.codegen.translators.talendmdm;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.IMetadataTable;
import java.util.List;
import java.util.Map;

public class TMDMViewSearchBeginJava
{
  protected static String nl;
  public static synchronized TMDMViewSearchBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMDMViewSearchBeginJava result = new TMDMViewSearchBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "       " + NL + "\t\t\t\tint nb_line_";
  protected final String TEXT_2 = " = 0;" + NL + "\t\t\t\t";
  protected final String TEXT_3 = NL + "\t\t\t\t\torg.talend.mdm.webservice.XtentisServiceLocator xtentisService_";
  protected final String TEXT_4 = " = new org.talend.mdm.webservice.XtentisServiceLocator();" + NL + "\t\t\t\t\txtentisService_";
  protected final String TEXT_5 = ".setXtentisPortEndpointAddress(";
  protected final String TEXT_6 = ");" + NL + "\t\t\t\t\torg.talend.mdm.webservice.XtentisPort xtentisWS_";
  protected final String TEXT_7 = " = xtentisService_";
  protected final String TEXT_8 = ".getXtentisPort();    " + NL + "\t\t\t\t\torg.talend.mdm.webservice.XtentisBindingStub stub_";
  protected final String TEXT_9 = " = (org.talend.mdm.webservice.XtentisBindingStub)xtentisWS_";
  protected final String TEXT_10 = ";" + NL + "\t" + NL + "\t\t\t\t\t// Authentification" + NL + "\t\t\t\t\t";
  protected final String TEXT_11 = NL + "\t\t\t\t\t\tstub_";
  protected final String TEXT_12 = ".setUsername(";
  protected final String TEXT_13 = ");" + NL + "\t\t\t\t\t";
  protected final String TEXT_14 = NL + "\t\t\t\t\t\tstub_";
  protected final String TEXT_15 = ".setUsername(";
  protected final String TEXT_16 = " + \"/\" + ";
  protected final String TEXT_17 = ");" + NL + "\t\t\t\t\t";
  protected final String TEXT_18 = NL + "\t\t\t\t\tstub_";
  protected final String TEXT_19 = ".setPassword(";
  protected final String TEXT_20 = ");" + NL + "\t\t\t\t";
  protected final String TEXT_21 = NL + "\t\t\t\t\torg.talend.mdm.webservice.XtentisPort xtentisWS_";
  protected final String TEXT_22 = " = (org.talend.mdm.webservice.XtentisPort)globalMap.get(\"";
  protected final String TEXT_23 = "\");" + NL + "\t\t\t\t";
  protected final String TEXT_24 = NL + "\t\t\t\t" + NL + "\t\t\t\torg.talend.mdm.webservice.WSDataClusterPK dataCluster_";
  protected final String TEXT_25 = " = new org.talend.mdm.webservice.WSDataClusterPK(";
  protected final String TEXT_26 = ");" + NL + "\t\t\t\t" + NL + "\t\t\t\torg.talend.mdm.webservice.WSWhereCondition wc_";
  protected final String TEXT_27 = " = null;" + NL + "\t\t\t\tjava.util.List<org.talend.mdm.webservice.WSWhereItem> conditions_";
  protected final String TEXT_28 = " = new java.util.ArrayList<org.talend.mdm.webservice.WSWhereItem>();" + NL + "\t\t\t\t";
  protected final String TEXT_29 = NL + "\t\t\t\t\twc_";
  protected final String TEXT_30 = " =new org.talend.mdm.webservice.WSWhereCondition(";
  protected final String TEXT_31 = ",org.talend.mdm.webservice.WSWhereOperator.";
  protected final String TEXT_32 = ", ";
  protected final String TEXT_33 = ", org.talend.mdm.webservice.WSStringPredicate.";
  protected final String TEXT_34 = ",false);" + NL + "\t\t\t\t\torg.talend.mdm.webservice.WSWhereItem wsItem_";
  protected final String TEXT_35 = "=new org.talend.mdm.webservice.WSWhereItem(wc_";
  protected final String TEXT_36 = ",null,null);" + NL + "\t\t\t\t\tconditions_";
  protected final String TEXT_37 = ".add(wsItem_";
  protected final String TEXT_38 = ");" + NL + "\t\t\t\t";
  protected final String TEXT_39 = NL + "\t\t\t\torg.talend.mdm.webservice.WSWhereItem wsItem_";
  protected final String TEXT_40 = " = null;" + NL + "\t\t\t\tif(conditions_";
  protected final String TEXT_41 = ".size() > 0){" + NL + "\t\t\t\t\twsItem_";
  protected final String TEXT_42 = " =new org.talend.mdm.webservice.WSWhereItem(null,conditions_";
  protected final String TEXT_43 = ".toArray(new org.talend.mdm.webservice.WSWhereItem[conditions_";
  protected final String TEXT_44 = ".size()]),null);" + NL + "\t\t\t\t}" + NL + "\t\t\t\t" + NL + "\t\t\t\torg.talend.mdm.webservice.WSViewSearch wsViewSearch_";
  protected final String TEXT_45 = " = null;" + NL + "\t\t\t\t" + NL + "\t\t\t\tint limit_";
  protected final String TEXT_46 = " = ";
  protected final String TEXT_47 = ";" + NL + "\t\t\t\tint recordsPerPage_";
  protected final String TEXT_48 = " = ";
  protected final String TEXT_49 = ";" + NL + "\t\t\t\tint skip_";
  protected final String TEXT_50 = " = ";
  protected final String TEXT_51 = ";" + NL + "\t\t\t\t" + NL + "\t\t\t\tif (skip_";
  protected final String TEXT_52 = " < 0)" + NL + "\t                skip_";
  protected final String TEXT_53 = " = 0;" + NL + "\t" + NL + "\t            if (recordsPerPage_";
  protected final String TEXT_54 = " <= 0)" + NL + "\t                recordsPerPage_";
  protected final String TEXT_55 = " = -1;// read whole records in one time" + NL + "\t" + NL + "\t            if (limit_";
  protected final String TEXT_56 = " <= 0)" + NL + "\t                limit_";
  protected final String TEXT_57 = " = -1;// no limit" + NL + "\t\t\t\t" + NL + "\t            int retrievedCount_";
  protected final String TEXT_58 = " = 0;" + NL + "       \t\t\tint readRecordsSize_";
  protected final String TEXT_59 = " = limit_";
  protected final String TEXT_60 = " == -1 ? recordsPerPage_";
  protected final String TEXT_61 = " : (recordsPerPage_";
  protected final String TEXT_62 = " == -1 ? limit_";
  protected final String TEXT_63 = " : (recordsPerPage_";
  protected final String TEXT_64 = " > limit_";
  protected final String TEXT_65 = " ? limit_";
  protected final String TEXT_66 = NL + "                : recordsPerPage_";
  protected final String TEXT_67 = "));" + NL + "" + NL + "\t\t\t\torg.talend.mdm.webservice.WSGetItems getitems_";
  protected final String TEXT_68 = "=null;" + NL + "\t\t\t\t" + NL + "        \t\tboolean continueFlag_";
  protected final String TEXT_69 = " = true;" + NL + "        \t\twhile (continueFlag_";
  protected final String TEXT_70 = ") {" + NL + "\t\t\t\t\twsViewSearch_";
  protected final String TEXT_71 = " = new org.talend.mdm.webservice.WSViewSearch();       \t\t\t\t\t                " + NL + "\t                wsViewSearch_";
  protected final String TEXT_72 = ".setWsDataClusterPK(dataCluster_";
  protected final String TEXT_73 = ");" + NL + "\t\t\t\t\twsViewSearch_";
  protected final String TEXT_74 = ".setWsViewPK(new org.talend.mdm.webservice.WSViewPK(";
  protected final String TEXT_75 = "));" + NL + "\t\t\t\t\t" + NL + "\t\t\t\t\twsViewSearch_";
  protected final String TEXT_76 = ".setSpellTreshold(";
  protected final String TEXT_77 = ");" + NL + "\t\t\t\t\twsViewSearch_";
  protected final String TEXT_78 = ".setSkip(skip_";
  protected final String TEXT_79 = " + retrievedCount_";
  protected final String TEXT_80 = ");" + NL + "\t\t\t\t\twsViewSearch_";
  protected final String TEXT_81 = ".setMaxItems(readRecordsSize_";
  protected final String TEXT_82 = ");" + NL + "\t\t\t\t\t" + NL + "\t\t\t\t\t";
  protected final String TEXT_83 = NL + "\t\t\t\t\t\twsViewSearch_";
  protected final String TEXT_84 = ".setOrderBy(";
  protected final String TEXT_85 = "); // OPTIONAL" + NL + "\t\t\t\t\t\twsViewSearch_";
  protected final String TEXT_86 = ".setDirection(\"";
  protected final String TEXT_87 = "\"); // OPTIONAL" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_88 = NL + "\t\t\t\t\twsViewSearch_";
  protected final String TEXT_89 = ".setWhereItem(wsItem_";
  protected final String TEXT_90 = "); // OPTIONAL" + NL + "\t\t\t\t\t" + NL + "\t\t\t\t\tString[] items_";
  protected final String TEXT_91 = " = xtentisWS_";
  protected final String TEXT_92 = ".viewSearch(wsViewSearch_";
  protected final String TEXT_93 = ");" + NL + "\t\t\t\t\t" + NL + "\t\t\t\t\tretrievedCount_";
  protected final String TEXT_94 = " += items_";
  protected final String TEXT_95 = ".length - 1; // for header" + NL + "\t\t\t\t\tif (recordsPerPage_";
  protected final String TEXT_96 = " == -1 || items_";
  protected final String TEXT_97 = ".length < readRecordsSize_";
  protected final String TEXT_98 = " || limit_";
  protected final String TEXT_99 = " == retrievedCount_";
  protected final String TEXT_100 = ") {" + NL + "\t                    continueFlag_";
  protected final String TEXT_101 = " = false;" + NL + "\t                } else if (limit_";
  protected final String TEXT_102 = " != -1 && limit_";
  protected final String TEXT_103 = " < retrievedCount_";
  protected final String TEXT_104 = " + readRecordsSize_";
  protected final String TEXT_105 = ") {" + NL + "\t                    readRecordsSize_";
  protected final String TEXT_106 = " = limit_";
  protected final String TEXT_107 = " - retrievedCount_";
  protected final String TEXT_108 = ";" + NL + "\t                }" + NL + "\t\t\t\t" + NL + "\t\t\t\t\tfor(int i_";
  protected final String TEXT_109 = "=1;i_";
  protected final String TEXT_110 = " < items_";
  protected final String TEXT_111 = ".length; i_";
  protected final String TEXT_112 = "++){" + NL + "\t\t\t\t\t\tnb_line_";
  protected final String TEXT_113 = " ++;" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_114 = ".";
  protected final String TEXT_115 = " = items_";
  protected final String TEXT_116 = "[i_";
  protected final String TEXT_117 = "];\t" + NL + "\t\t\t";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();

List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas != null) && (metadatas.size() > 0)) {
	IMetadataTable metadata = metadatas.get(0);
	
	if (metadata != null) {
	
		List<IMetadataColumn> columnList = metadata.getListColumns();
		List<? extends IConnection> outgoingConns = node.getOutgoingSortedConnections();

		// if output columns are defined
		if (outgoingConns != null && outgoingConns.size() > 0){
		
			IConnection outgoingConn = outgoingConns.get(0);
			if(outgoingConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) { // start 1
	
				boolean useExistingConn = ("true").equals(ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__"));
				String connection = ElementParameterParser.getValue(node,"__CONNECTION__");
				String conn = "xtentisWS_" + connection;
				String mdmUrl = ElementParameterParser.getValue(node, "__MDMURL__");
				String username = ElementParameterParser.getValue(node, "__USERNAME__");
				String password = ElementParameterParser.getValue(node, "__PASSWORD__");
				String universe = ElementParameterParser.getValue(node, "__UNIVERSE__");
				//String concept = ElementParameterParser.getValue(node, "__CONCEPT__");
				String xmlField = ElementParameterParser.getValue(node, "__XMLFIELD__");
				
				String dataCluster = ElementParameterParser.getValue(node, "__DATACLUSTER__");
				String viewName = ElementParameterParser.getValue(node, "__VIEWNAME__");
				
				List<Map<String, String>> operations = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__OPERATIONS__");
				List<Map<String, String>> orders = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__ORDER_TABLE__");
				
				String spellThrehold = ElementParameterParser.getValue(node, "__SPELLTHREHOLD__");
				String skip = ElementParameterParser.getValue(node, "__SKIP__");
				String limit = ElementParameterParser.getValue(node, "__MAXVALUE__");
				String bufferSize = ElementParameterParser.getValue(node, "__FETCHSIZE__");
				if(spellThrehold==null || ("").equals(spellThrehold)){
					spellThrehold = "-1";
				}
				if(skip==null || ("").equals(skip)){
					skip = "0";
				}
				
				if(limit==null || ("").equals(limit)){
					limit = "-1";
				}
				
    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_2);
    if(!useExistingConn){
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(mdmUrl );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    if(universe==null || ("").equals(universe.trim())){
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(username );
    stringBuffer.append(TEXT_13);
    }else{
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(universe);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(username );
    stringBuffer.append(TEXT_17);
    }
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(password );
    stringBuffer.append(TEXT_20);
    }else{
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(conn);
    stringBuffer.append(TEXT_23);
    }
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(dataCluster );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    
				for(int i=0; i < operations.size(); i++){
					Map<String, String> map= operations.get(i);
					String xpath=map.get("XPATH");
					String operator=map.get("FUNCTION");
					String value = map.get("VALUE");
					String predicate = map.get("PREDICATE");
					
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(xpath );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(operator );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(value );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(predicate );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_38);
    
				}//for
				
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(limit);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(bufferSize);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(skip);
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid );
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
    stringBuffer.append(viewName);
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_76);
    stringBuffer.append(spellThrehold);
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
    
					for(int i=0; i< orders.size(); i++){ // only the first one will be effective.
						Map<String, String> map= orders.get(i);
						
    stringBuffer.append(TEXT_83);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_84);
    stringBuffer.append(map.get("XPATH"));
    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_86);
    stringBuffer.append(map.get("ORDER"));
    stringBuffer.append(TEXT_87);
    
						break;
					}
					
    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_92);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_93);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_95);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_96);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_97);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_98);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_99);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_100);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_101);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_102);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_103);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_104);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_105);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_106);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_107);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_108);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_109);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_110);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_111);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_112);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_113);
    stringBuffer.append(outgoingConn.getName() );
    stringBuffer.append(TEXT_114);
    stringBuffer.append(xmlField );
    stringBuffer.append(TEXT_115);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_116);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_117);
    
			}
		}
	}
}

    return stringBuffer.toString();
  }
}
