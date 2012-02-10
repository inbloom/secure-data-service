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

public class TPaloCubeBeginJava
{
  protected static String nl;
  public static synchronized TPaloCubeBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TPaloCubeBeginJava result = new TPaloCubeBeginJava();
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
  protected final String TEXT_19 = NL + NL + "org.talend.jpalo.palodatabases pDBs_";
  protected final String TEXT_20 = " = pConn_";
  protected final String TEXT_21 = ".getDatabases();" + NL + "org.talend.jpalo.palodatabase pDB_";
  protected final String TEXT_22 = " = pDBs_";
  protected final String TEXT_23 = ".getDatabase(";
  protected final String TEXT_24 = ");" + NL + "" + NL + "if(pDB_";
  protected final String TEXT_25 = " == null){" + NL + "\t throw new RuntimeException (\"Database '\" + ";
  protected final String TEXT_26 = " + \"' not found. exiting...\");" + NL + "}" + NL;
  protected final String TEXT_27 = NL + "\torg.talend.jpalo.palocubes pCUBs_";
  protected final String TEXT_28 = " = pDB_";
  protected final String TEXT_29 = ".getCubes(org.talend.jpalo.palocubes.";
  protected final String TEXT_30 = ");" + NL + "\tpCUBs_";
  protected final String TEXT_31 = ".createCube(";
  protected final String TEXT_32 = ", new String[]{";
  protected final String TEXT_33 = "}, org.talend.jpalo.palocubes.";
  protected final String TEXT_34 = ");";
  protected final String TEXT_35 = NL + "\torg.talend.jpalo.palocubes pCUBs_";
  protected final String TEXT_36 = " = pDB_";
  protected final String TEXT_37 = ".getCubes(org.talend.jpalo.palocubes.";
  protected final String TEXT_38 = ");" + NL + "\tif(null==pCUBs_";
  protected final String TEXT_39 = ".getCube(";
  protected final String TEXT_40 = ")){" + NL + "\t\tpCUBs_";
  protected final String TEXT_41 = ".createCube(";
  protected final String TEXT_42 = ", new String[]{";
  protected final String TEXT_43 = "}, org.talend.jpalo.palocubes.";
  protected final String TEXT_44 = ");" + NL + "\t}";
  protected final String TEXT_45 = NL + "\torg.talend.jpalo.palocubes pCUBs_";
  protected final String TEXT_46 = " = pDB_";
  protected final String TEXT_47 = ".getCubes(org.talend.jpalo.palocubes.";
  protected final String TEXT_48 = ");" + NL + "\tif(null!=pCUBs_";
  protected final String TEXT_49 = ".getCube(";
  protected final String TEXT_50 = ")) pCUBs_";
  protected final String TEXT_51 = ".deleteCube(";
  protected final String TEXT_52 = ");\t" + NL + "\tpCUBs_";
  protected final String TEXT_53 = ".createCube(";
  protected final String TEXT_54 = ", new String[]{";
  protected final String TEXT_55 = "}, org.talend.jpalo.palocubes.";
  protected final String TEXT_56 = ");";
  protected final String TEXT_57 = NL + "\torg.talend.jpalo.palocubes pCUBs_";
  protected final String TEXT_58 = " = pDB_";
  protected final String TEXT_59 = ".getCubes(org.talend.jpalo.palocubes.";
  protected final String TEXT_60 = ");" + NL + "\tpCUBs_";
  protected final String TEXT_61 = ".deleteCube(";
  protected final String TEXT_62 = ");\t";
  protected final String TEXT_63 = NL + "\torg.talend.jpalo.palocubes pCUBs_";
  protected final String TEXT_64 = " = pDB_";
  protected final String TEXT_65 = ".getCubes(org.talend.jpalo.palocubes.";
  protected final String TEXT_66 = ");" + NL + "\tpCUBs_";
  protected final String TEXT_67 = ".getCube(";
  protected final String TEXT_68 = ").clear();\t";
  protected final String TEXT_69 = NL + "globalMap.put(\"";
  protected final String TEXT_70 = "_CUBENAME\",";
  protected final String TEXT_71 = ");";
  protected final String TEXT_72 = NL;

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
    String sCubeType = ElementParameterParser.getValue(node,"__CUBE_TYPE__");
    String sCubeAction = ElementParameterParser.getValue(node,"__CUBE_ACTION__");

    List<Map<String, String>> tCubeDimensions = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__CUBE_DIMENSIONS__");
    

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
    stringBuffer.append(sDatabaseName);
    stringBuffer.append(TEXT_26);
    

int iNbOfCubeDimensions = tCubeDimensions.size();
StringBuilder sbX=new StringBuilder();
for(int i=0; i<iNbOfCubeDimensions; i++){
	Map<String, String> CubeDimension = tCubeDimensions.get(i);
	String strDimensionName = CubeDimension.get("DIMENSION_NAME");
	if(i>0) sbX.append(",");
	sbX.append(strDimensionName);
}


if(("CREATE").equals(sCubeAction)){

    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(sCubeType);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(sCubeName);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(sbX.toString());
    stringBuffer.append(TEXT_33);
    stringBuffer.append(sCubeType);
    stringBuffer.append(TEXT_34);
    
}else if(("CREATE_IF_NOT_EXISTS").equals(sCubeAction)){

    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(sCubeType);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(sCubeName);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(sCubeName);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(sbX.toString());
    stringBuffer.append(TEXT_43);
    stringBuffer.append(sCubeType);
    stringBuffer.append(TEXT_44);
    
}else if(("DELETE_IF_EXISTS_AND_CREATE").equals(sCubeAction)){

    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_47);
    stringBuffer.append(sCubeType);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_49);
    stringBuffer.append(sCubeName);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_51);
    stringBuffer.append(sCubeName);
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_53);
    stringBuffer.append(sCubeName);
    stringBuffer.append(TEXT_54);
    stringBuffer.append(sbX.toString());
    stringBuffer.append(TEXT_55);
    stringBuffer.append(sCubeType);
    stringBuffer.append(TEXT_56);
    
}else if(("DELETE").equals(sCubeAction)){

    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_59);
    stringBuffer.append(sCubeType);
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_61);
    stringBuffer.append(sCubeName);
    stringBuffer.append(TEXT_62);
    	
} else if(("CLEAR").equals(sCubeAction)){

    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_65);
    stringBuffer.append(sCubeType);
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_67);
    stringBuffer.append(sCubeName);
    stringBuffer.append(TEXT_68);
    	
}

    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_70);
    stringBuffer.append(sCubeName);
    stringBuffer.append(TEXT_71);
    stringBuffer.append(TEXT_72);
    return stringBuffer.toString();
  }
}
