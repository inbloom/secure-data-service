package org.talend.designer.codegen.translators.file.output;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.utils.TalendQuoteUtils;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.talend.core.model.metadata.IMetadataTable;

public class TFileOutputXMLEndJava
{
  protected static String nl;
  public static synchronized TFileOutputXMLEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileOutputXMLEndJava result = new TFileOutputXMLEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = "out_";
  protected final String TEXT_3 = ".write(\"</\"+";
  protected final String TEXT_4 = "+\">\");" + NL + "" + NL + "out_";
  protected final String TEXT_5 = ".newLine();" + NL;
  protected final String TEXT_6 = "\tout_";
  protected final String TEXT_7 = ".write(footers_";
  protected final String TEXT_8 = "[";
  protected final String TEXT_9 = "]);" + NL + "" + NL + "\tout_";
  protected final String TEXT_10 = ".newLine();" + NL + "\t";
  protected final String TEXT_11 = "out_";
  protected final String TEXT_12 = ".close();" + NL;
  protected final String TEXT_13 = NL + "\tif(currentRowCount_";
  protected final String TEXT_14 = " == 0){\t\t" + NL + "\t\tfile_";
  protected final String TEXT_15 = ".delete();" + NL + "\t}";
  protected final String TEXT_16 = "globalMap.put(\"";
  protected final String TEXT_17 = "_NB_LINE\",nb_line_";
  protected final String TEXT_18 = ");";
  protected final String TEXT_19 = NL + "\t";
  protected final String TEXT_20 = NL + "\tif(nb_line_";
  protected final String TEXT_21 = " == 0){" + NL + "\t\tfile_";
  protected final String TEXT_22 = ".delete();" + NL + "\t}\t\t";
  protected final String TEXT_23 = "\t";
  protected final String TEXT_24 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

String filename = ElementParameterParser.getValue(node, "__FILENAME__");		
boolean isDeleteEmptyFile = ("true").equals(ElementParameterParser.getValue(node, "__DELETE_EMPTYFILE__"));		

List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null) {
String split = ElementParameterParser.getValue(node, "__SPLIT__");
String fileName = ElementParameterParser.getValue(node, "__FILENAME__");
List rootTags = (List)ElementParameterParser.getObjectValue(node, "__ROOT_TAGS__");
if (rootTags.size()==0) {
    Map<String, String> defaultRootNode = new HashMap<String, String>();
    defaultRootNode.put("TAG","\"root\"");
    rootTags.add(defaultRootNode);
}
    int footers = rootTags.size();
String useDynamicGrouping = ElementParameterParser.getValue(
            node,
            "__USE_DYNAMIC_GROUPING__"
        );
List<Map<String, String>> columnMapping = 
    		(List<Map<String,String>>)ElementParameterParser.getObjectValue(
                node,
                "__MAPPING__"
            );
List<Map<String, String>> groupBys =
            (List<Map<String,String>>)ElementParameterParser.getObjectValue(
                node,
                "__GROUP_BY__"
            );
if (("false").equals(useDynamicGrouping)) {
    groupBys.clear();
}


String groupby[][] = new String[groupBys.size()][3];
for(int i = 0; i < groupBys.size(); i++){
    groupby[i][0] = groupBys.get(i).get("COLUMN");
    groupby[i][1] = groupBys.get(i).get("LABEL");
}
for(int i = 0; i < groupby.length; i++){
	for(int j = 0; j < columnMapping.size(); j++){
		Map<String, String> map = columnMapping.get(j);
		String col = metadata.getListColumns().get(j).getLabel();
		if(groupby[i][0].equals(col)){
			if(("true").equals(map.get("SCHEMA_COLUMN_NAME"))){
    			groupby[i][2] = col;
    			groupby[i][2] = TalendQuoteUtils.addQuotes(groupby[i][2]);
    		}else{
    			groupby[i][2] = map.get("LABEL");
    		}
    		break;
		}
	}
}

    
for(int i = groupby.length -1; i >= 0; i--){

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(groupby[i][2] );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    
}
	for(int i = footers - 1; i >= 0;i--){

    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    
	}

    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    
if(("true").equals(split)){

    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    
}

    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    
	}
}
	
    stringBuffer.append(TEXT_19);
    if(isDeleteEmptyFile){
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    }
    stringBuffer.append(TEXT_23);
    stringBuffer.append(TEXT_24);
    return stringBuffer.toString();
  }
}
