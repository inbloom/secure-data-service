package org.talend.designer.codegen.translators.databases.exist;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import java.util.Map;

public class TEXistDeleteMainJava
{
  protected static String nl;
  public static synchronized TEXistDeleteMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TEXistDeleteMainJava result = new TEXistDeleteMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "   \tjava.util.Set<String> keySet_";
  protected final String TEXT_3 = " = map_";
  protected final String TEXT_4 = ".keySet();" + NL + "  \tfor(String key_";
  protected final String TEXT_5 = " : keySet_";
  protected final String TEXT_6 = "){" + NL + "\t\tString filemask_";
  protected final String TEXT_7 = " = key_";
  protected final String TEXT_8 = "; " + NL + "\t\tString dir_";
  protected final String TEXT_9 = " = null;\t" + NL + "\t\tString mask_";
  protected final String TEXT_10 = " = filemask_";
  protected final String TEXT_11 = ".replaceAll(\"\\\\\\\\\", \"/\") ;\t" + NL + "\t\tint i_";
  protected final String TEXT_12 = " = mask_";
  protected final String TEXT_13 = ".lastIndexOf('/');" + NL + "  \t\tif (i_";
  protected final String TEXT_14 = "!=-1){" + NL + "\t\t\tdir_";
  protected final String TEXT_15 = " = mask_";
  protected final String TEXT_16 = ".substring(0, i_";
  protected final String TEXT_17 = "); " + NL + "\t\t\tmask_";
  protected final String TEXT_18 = " = mask_";
  protected final String TEXT_19 = ".substring(i_";
  protected final String TEXT_20 = "+1);" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\tmask_";
  protected final String TEXT_21 = " = mask_";
  protected final String TEXT_22 = ".replaceAll(\"\\\\.\", \"\\\\\\\\.\").replaceAll(\"\\\\*\", \".*\");" + NL + "\t\tfinal String finalMask_";
  protected final String TEXT_23 = " = mask_";
  protected final String TEXT_24 = ";";
  protected final String TEXT_25 = NL + "\t\t\tfor(String resourceName_";
  protected final String TEXT_26 = " : col_";
  protected final String TEXT_27 = ".listResources()){" + NL + "\t\t\t\tif(resourceName_";
  protected final String TEXT_28 = ".matches(finalMask_";
  protected final String TEXT_29 = ")){" + NL + "\t\t\t        org.xmldb.api.base.Resource resource_";
  protected final String TEXT_30 = " = col_";
  protected final String TEXT_31 = ".getResource(resourceName_";
  protected final String TEXT_32 = ");" + NL + "\t\t\t        col_";
  protected final String TEXT_33 = ".removeResource(resource_";
  protected final String TEXT_34 = ");" + NL + "\t\t\t\t}" + NL + "\t\t\t}";
  protected final String TEXT_35 = NL + "\t\t\tfor(String subCol_";
  protected final String TEXT_36 = " : col_";
  protected final String TEXT_37 = ".listChildCollections()){" + NL + "\t\t\t\tif(subCol_";
  protected final String TEXT_38 = ".matches(finalMask_";
  protected final String TEXT_39 = ")){" + NL + "\t\t\t\t\tmgtService_";
  protected final String TEXT_40 = ".removeCollection(subCol_";
  protected final String TEXT_41 = ");" + NL + "\t\t\t\t}" + NL + "\t\t\t}";
  protected final String TEXT_42 = NL + "\t\t\tfor(String resourceName_";
  protected final String TEXT_43 = " : col_";
  protected final String TEXT_44 = ".listResources()){" + NL + "\t\t\t\tif(resourceName_";
  protected final String TEXT_45 = ".matches(finalMask_";
  protected final String TEXT_46 = ")){" + NL + "\t\t\t        org.xmldb.api.base.Resource resource_";
  protected final String TEXT_47 = " = col_";
  protected final String TEXT_48 = ".getResource(resourceName_";
  protected final String TEXT_49 = ");" + NL + "\t\t\t        col_";
  protected final String TEXT_50 = ".removeResource(resource_";
  protected final String TEXT_51 = ");" + NL + "\t\t\t\t}" + NL + "\t\t\t}" + NL + "\t\t\tfor(String subCol_";
  protected final String TEXT_52 = " : col_";
  protected final String TEXT_53 = ".listChildCollections()){" + NL + "\t\t\t\tif(subCol_";
  protected final String TEXT_54 = ".matches(finalMask_";
  protected final String TEXT_55 = ")){" + NL + "\t\t\t\t\tmgtService_";
  protected final String TEXT_56 = ".removeCollection(subCol_";
  protected final String TEXT_57 = ");" + NL + "\t\t\t\t}" + NL + "\t\t\t}";
  protected final String TEXT_58 = NL + "\t\tnb_file_";
  protected final String TEXT_59 = "++;" + NL + " " + NL + "    }";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	String uri = ElementParameterParser.getValue(node, "__URI__");
	String driver = ElementParameterParser.getValue(node, "__DRIVER__");
	String user = ElementParameterParser.getValue(node, "__USERNAME__");
	String pass = ElementParameterParser.getValue(node, "__PASSWORD__");
	String remotedir = ElementParameterParser.getValue(node, "__REMOTEDIR__");
	String targetType = ElementParameterParser.getValue(node, "__TARGETTYPE__");
	List<Map<String, String>> files = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__FILES__");

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
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
    
		if("RESOURCE".equals(targetType)){

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
    
		}else if("COLLECTION".equals(targetType)){

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
    
		}else{

    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
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
    
		}

    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_59);
    return stringBuffer.toString();
  }
}
