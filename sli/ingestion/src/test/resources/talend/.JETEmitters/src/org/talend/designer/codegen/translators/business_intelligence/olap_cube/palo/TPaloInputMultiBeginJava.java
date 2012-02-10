package org.talend.designer.codegen.translators.business_intelligence.olap_cube.palo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TPaloInputMultiBeginJava
{
  protected static String nl;
  public static synchronized TPaloInputMultiBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TPaloInputMultiBeginJava result = new TPaloInputMultiBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "org.talend.jpalo.palo p_";
  protected final String TEXT_3 = " = null;" + NL + "org.talend.jpalo.paloconnection pConn_";
  protected final String TEXT_4 = " = null;" + NL;
  protected final String TEXT_5 = NL + "\t// Use Existing" + NL + "\tp_";
  protected final String TEXT_6 = " = (org.talend.jpalo.palo)globalMap.get(\"";
  protected final String TEXT_7 = "\");" + NL + "\tpConn_";
  protected final String TEXT_8 = " =  (org.talend.jpalo.paloconnection)globalMap.get(\"";
  protected final String TEXT_9 = "\");";
  protected final String TEXT_10 = NL + "\t// Initialize jpalo" + NL + "\tp_";
  protected final String TEXT_11 = " = new org.talend.jpalo.palo(";
  protected final String TEXT_12 = ");" + NL + "\t// Open the connection" + NL + "\tpConn_";
  protected final String TEXT_13 = " = p_";
  protected final String TEXT_14 = ".connect(";
  protected final String TEXT_15 = ", ";
  protected final String TEXT_16 = ", ";
  protected final String TEXT_17 = ", ";
  protected final String TEXT_18 = ");";
  protected final String TEXT_19 = NL + NL + NL + "org.talend.jpalo.palodatabases pDBs_";
  protected final String TEXT_20 = " = pConn_";
  protected final String TEXT_21 = ".getDatabases();" + NL + "org.talend.jpalo.palodatabase pDB_";
  protected final String TEXT_22 = " = pDBs_";
  protected final String TEXT_23 = ".getDatabase(";
  protected final String TEXT_24 = ");" + NL + "" + NL + "org.talend.jpalo.palocubes pCBs_";
  protected final String TEXT_25 = " = pDB_";
  protected final String TEXT_26 = ".getCubes(org.talend.jpalo.palocubes.";
  protected final String TEXT_27 = ");" + NL + "org.talend.jpalo.palocube pCB_";
  protected final String TEXT_28 = " = pCBs_";
  protected final String TEXT_29 = ".getCube(";
  protected final String TEXT_30 = ");" + NL;
  protected final String TEXT_31 = NL + "String strDimensionElementArray_";
  protected final String TEXT_32 = "[][] = new String[";
  protected final String TEXT_33 = "][];";
  protected final String TEXT_34 = NL + "\tstrDimensionElementArray_";
  protected final String TEXT_35 = "[";
  protected final String TEXT_36 = "] = new String[]{";
  protected final String TEXT_37 = "};";
  protected final String TEXT_38 = NL + "\tjava.util.ArrayList<org.talend.jpalo.paloelements> alPaloElements_";
  protected final String TEXT_39 = "= new java.util.ArrayList<org.talend.jpalo.paloelements>();" + NL + "\tfor(org.talend.jpalo.palodimension plDim_";
  protected final String TEXT_40 = " : pCB_";
  protected final String TEXT_41 = ".getDimensions().getDimensions()){" + NL + "\t\talPaloElements_";
  protected final String TEXT_42 = ".add(plDim_";
  protected final String TEXT_43 = ".getElements());" + NL + "\t}" + NL + "\torg.talend.jpalo.palodata pDT_";
  protected final String TEXT_44 = " = new org.talend.jpalo.palodata(pConn_";
  protected final String TEXT_45 = ",pDB_";
  protected final String TEXT_46 = ",pCB_";
  protected final String TEXT_47 = ",alPaloElements_";
  protected final String TEXT_48 = ", strDimensionElementArray_";
  protected final String TEXT_49 = ", ";
  protected final String TEXT_50 = ");" + NL + "\tint iRowCount_";
  protected final String TEXT_51 = "=0;" + NL + "\twhile(pDT_";
  protected final String TEXT_52 = ".getResults()){" + NL + "\t";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();

    boolean useExistingConnection = "true".equals(ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__"));


    String sServer = ElementParameterParser.getValue(node, "__SERVER__");
    String sServerport = ElementParameterParser.getValue(node, "__SERVERPORT__");
    String sUsername = ElementParameterParser.getValue(node, "__USERNAME__");
    String sPassword = ElementParameterParser.getValue(node, "__PASS__");

    String sDatabaseName = ElementParameterParser.getValue(node,"__DATABASE__");
    String sCubeName = ElementParameterParser.getValue(node,"__CUBE__");
    //String sCubeType = ElementParameterParser.getValue(node,"__CUBE_TYPE__");
    String sCubeType = "CUBE_NORMAL";

    String sCommitSize = ElementParameterParser.getValue(node,"__COMMITSIZE__");
    int iCommitSize = Integer.valueOf(sCommitSize).intValue();	
    List<Map<String, String>> tDimensionElements = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__DIMENSION_ELEMENTS__");
    
    String sDeploypalolibs = ElementParameterParser.getValue(node, "__DEPLOY_PALO_LIBS__");
    boolean bDeploypalolibs=false;
    if(sDeploypalolibs.equals("true"))bDeploypalolibs=true;


    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    
if(useExistingConnection){
	String connection = ElementParameterParser.getValue(node,"__CONNECTION__");
	String p = "p_" + connection;
	String pConn = "pConn_" + connection;


    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(p);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(pConn);
    stringBuffer.append(TEXT_9);
    
}else{

    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(bDeploypalolibs);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(sUsername);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(sPassword);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(sServer);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(sServerport);
    stringBuffer.append(TEXT_18);
    
}

    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(sDatabaseName);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(sCubeType);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(sCubeName);
    stringBuffer.append(TEXT_30);
    
	String strOutputConnectionName="";
	List< ? extends IConnection> conns = node.getOutgoingConnections();
 	if(conns!=null){
		if (conns.size()>0){
                IConnection conn =conns.get(0);
		    strOutputConnectionName=conn.getName();
		}
	}

    
int iNbOfDimensionElements = tDimensionElements.size();
int iNbOfColumnSize =iNbOfDimensionElements-2;

    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(iNbOfColumnSize);
    stringBuffer.append(TEXT_33);
    
for(int i=0; i<iNbOfDimensionElements-2; i++){
	Map<String, String> DimensionElements = tDimensionElements.get(i);
	String strDimensionName = DimensionElements.get("DIMENSIONS");
	String strElementName = DimensionElements.get("ELEMENTS");

    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(strElementName);
    stringBuffer.append(TEXT_37);
    
}

if(null!=strOutputConnectionName && strOutputConnectionName.length()>0){

    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid );
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
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_49);
    stringBuffer.append(iCommitSize);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_52);
    
}

    return stringBuffer.toString();
  }
}
