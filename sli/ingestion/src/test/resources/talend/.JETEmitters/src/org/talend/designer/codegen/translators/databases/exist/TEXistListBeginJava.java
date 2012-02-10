package org.talend.designer.codegen.translators.databases.exist;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import java.util.Map;

public class TEXistListBeginJava
{
  protected static String nl;
  public static synchronized TEXistListBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TEXistListBeginJava result = new TEXistListBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\torg.xmldb.api.base.Collection col_";
  protected final String TEXT_3 = " = (org.xmldb.api.base.Collection)globalMap.get(\"";
  protected final String TEXT_4 = "\");";
  protected final String TEXT_5 = NL + "\t\tClass cl_";
  protected final String TEXT_6 = " = Class.forName(";
  protected final String TEXT_7 = ");" + NL + "\t    org.xmldb.api.base.Database database_";
  protected final String TEXT_8 = " = (org.xmldb.api.base.Database) cl_";
  protected final String TEXT_9 = ".newInstance();" + NL + "\t    database_";
  protected final String TEXT_10 = ".setProperty(\"create-database\", \"true\");" + NL + "\t    org.xmldb.api.DatabaseManager.registerDatabase(database_";
  protected final String TEXT_11 = ");" + NL + "\t    org.xmldb.api.base.Collection col_";
  protected final String TEXT_12 = " = org.xmldb.api.DatabaseManager.getCollection(";
  protected final String TEXT_13 = " + ";
  protected final String TEXT_14 = ",";
  protected final String TEXT_15 = ",";
  protected final String TEXT_16 = ");";
  protected final String TEXT_17 = NL + "\tint nb_file_";
  protected final String TEXT_18 = " = 0;" + NL + "    java.util.List<java.util.Map<String,String>> list_";
  protected final String TEXT_19 = " = new java.util.ArrayList<java.util.Map<String,String>>();";
  protected final String TEXT_20 = NL + "\t\tjava.util.Map<String,String> map_";
  protected final String TEXT_21 = "_";
  protected final String TEXT_22 = " = new java.util.HashMap<String,String>();" + NL + "\t\tmap_";
  protected final String TEXT_23 = "_";
  protected final String TEXT_24 = ".put(";
  protected final String TEXT_25 = ",";
  protected final String TEXT_26 = ");\t\t" + NL + " \t\tlist_";
  protected final String TEXT_27 = ".add(map_";
  protected final String TEXT_28 = "_";
  protected final String TEXT_29 = ");";
  protected final String TEXT_30 = "\t" + NL + "" + NL + "\t\tfor(java.util.Map<String, String> map_";
  protected final String TEXT_31 = " : list_";
  protected final String TEXT_32 = "){" + NL + "\t\t" + NL + "\t\t   \tjava.util.Set<String> keySet_";
  protected final String TEXT_33 = " = map_";
  protected final String TEXT_34 = ".keySet();" + NL + "\t\t  \tfor(String key_";
  protected final String TEXT_35 = " : keySet_";
  protected final String TEXT_36 = "){" + NL + "\t\t\t\tString filemask_";
  protected final String TEXT_37 = " = key_";
  protected final String TEXT_38 = "; " + NL + "\t\t\t\tString dir_";
  protected final String TEXT_39 = " = null;\t" + NL + "\t\t\t\tString mask_";
  protected final String TEXT_40 = " = filemask_";
  protected final String TEXT_41 = ".replaceAll(\"\\\\\\\\\", \"/\") ;\t" + NL + "\t\t\t\tint i_";
  protected final String TEXT_42 = " = mask_";
  protected final String TEXT_43 = ".lastIndexOf('/');" + NL + "\t\t  \t\tif (i_";
  protected final String TEXT_44 = "!=-1){" + NL + "\t\t\t\t\tdir_";
  protected final String TEXT_45 = " = mask_";
  protected final String TEXT_46 = ".substring(0, i_";
  protected final String TEXT_47 = "); " + NL + "\t\t\t\t\tmask_";
  protected final String TEXT_48 = " = mask_";
  protected final String TEXT_49 = ".substring(i_";
  protected final String TEXT_50 = "+1);" + NL + "\t\t\t\t}" + NL + "\t\t\t\tmask_";
  protected final String TEXT_51 = " = mask_";
  protected final String TEXT_52 = ".replaceAll(\"\\\\.\", \"\\\\\\\\.\").replaceAll(\"\\\\*\", \".*\");" + NL + "\t\t\t\tfinal String finalMask_";
  protected final String TEXT_53 = " = mask_";
  protected final String TEXT_54 = ";";
  protected final String TEXT_55 = NL + "\t\t\t\t\tfor(String contentName_";
  protected final String TEXT_56 = " : col_";
  protected final String TEXT_57 = ".listResources()){";
  protected final String TEXT_58 = NL + "\t\t\t\t\tfor(String contentName_";
  protected final String TEXT_59 = " : col_";
  protected final String TEXT_60 = ".listChildCollections()){";
  protected final String TEXT_61 = NL + "\t\t\t\t\tString[] collentions_";
  protected final String TEXT_62 = " = col_";
  protected final String TEXT_63 = ".listChildCollections();" + NL + "\t\t\t\t\tString[] resources_";
  protected final String TEXT_64 = " = col_";
  protected final String TEXT_65 = ".listResources();" + NL + "\t\t\t\t\tString[] dest_";
  protected final String TEXT_66 = " = new String[resources_";
  protected final String TEXT_67 = ".length + collentions_";
  protected final String TEXT_68 = ".length];" + NL + "\t\t\t\t\tSystem.arraycopy(resources_";
  protected final String TEXT_69 = ", 0, dest_";
  protected final String TEXT_70 = ", 0, resources_";
  protected final String TEXT_71 = ".length);" + NL + "        \t\t\tSystem.arraycopy(collentions_";
  protected final String TEXT_72 = ", 0, dest_";
  protected final String TEXT_73 = ", resources_";
  protected final String TEXT_74 = ".length, collentions_";
  protected final String TEXT_75 = ".length);" + NL + "\t\t\t\t\tfor(String contentName_";
  protected final String TEXT_76 = " : dest_";
  protected final String TEXT_77 = "){";
  protected final String TEXT_78 = NL + "\t\t\t\t\t\tif(contentName_";
  protected final String TEXT_79 = ".matches(finalMask_";
  protected final String TEXT_80 = ")){" + NL + "\t\t\t\t\t        globalMap.put(\"";
  protected final String TEXT_81 = "_CURRENT_FILE\", contentName_";
  protected final String TEXT_82 = ");" + NL + "\t\t\t\t\t        globalMap.put(\"";
  protected final String TEXT_83 = "_CURRENT_FILEPATH\", ";
  protected final String TEXT_84 = " + contentName_";
  protected final String TEXT_85 = ");" + NL + "\t\t\t\t\t\t}";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	String uri = ElementParameterParser.getValue(node, "__URI__");
	String driver = ElementParameterParser.getValue(node, "__DRIVER__");
	String collection = ElementParameterParser.getValue(node, "__COLLECTION__");
	String user = ElementParameterParser.getValue(node, "__USERNAME__");
	String pass = ElementParameterParser.getValue(node, "__PASSWORD__");
	String remotedir = ElementParameterParser.getValue(node, "__REMOTEDIR__");
	List<Map<String, String>> files = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__FILES__");
	String targetType = ElementParameterParser.getValue(node, "__TARGET_TYPE__");
	String connection = ElementParameterParser.getValue(node, "__CONNECTION__");
	String useExistingConn = ElementParameterParser.getValue(node, "__USE_EXISTING_CONNECTION__");
	if(("true").equals(useExistingConn)){
		String col= "col_" + connection;

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(col);
    stringBuffer.append(TEXT_4);
    
	}else{

    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(driver);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(uri);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(collection);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(user);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(pass);
    stringBuffer.append(TEXT_16);
    
	}

    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    
    for(int i=0; i<files.size(); i++){
		Map<String, String> line = files.get(i);

    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(line.get("FILEMASK"));
    stringBuffer.append(TEXT_25);
    stringBuffer.append(line.get("NEWNAME"));
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_29);
    
	}

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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid );
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
    
				if("RESOURCE".equals(targetType)){

    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_57);
    
				}else if("COLLECTION".equals(targetType)){

    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_60);
    
				}else{

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
    
				}

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
    stringBuffer.append(collection);
    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_85);
    return stringBuffer.toString();
  }
}
