package org.talend.designer.codegen.translators.data_quality;

import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;
import java.util.Map;

public class TReplaceListBeginJava
{
  protected static String nl;
  public static synchronized TReplaceListBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TReplaceListBeginJava result = new TReplaceListBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "java.util.Map<";
  protected final String TEXT_3 = "Struct, ";
  protected final String TEXT_4 = "Struct>  tMap_";
  protected final String TEXT_5 = "_";
  protected final String TEXT_6 = " = (java.util.Map<";
  protected final String TEXT_7 = "Struct, ";
  protected final String TEXT_8 = "Struct>) globalMap.get(\"tHash_";
  protected final String TEXT_9 = "\");" + NL + "java.util.Map<";
  protected final String TEXT_10 = ",";
  protected final String TEXT_11 = "> replace_";
  protected final String TEXT_12 = " = new java.util.HashMap<";
  protected final String TEXT_13 = ",";
  protected final String TEXT_14 = ">();" + NL + "" + NL + "for (Object o : tMap_";
  protected final String TEXT_15 = "_";
  protected final String TEXT_16 = ".keySet()) {" + NL + "    ";
  protected final String TEXT_17 = NL + "    ";
  protected final String TEXT_18 = " search = tMap_";
  protected final String TEXT_19 = "_";
  protected final String TEXT_20 = ".get(o).";
  protected final String TEXT_21 = ";";
  protected final String TEXT_22 = NL + "    ";
  protected final String TEXT_23 = " replacement = tMap_";
  protected final String TEXT_24 = "_";
  protected final String TEXT_25 = ".get(o).";
  protected final String TEXT_26 = ";" + NL + "    replace_";
  protected final String TEXT_27 = ".put(search,replacement);" + NL + "}" + NL;
  protected final String TEXT_28 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

List<IMetadataTable> metadatas = node.getMetadataList();
IMetadataTable metadata = metadatas.get(0);

String lookupSearchColumn = ElementParameterParser.getValue(
    node,
    "__LOOKUP_SEARCH_COLUMN__"
);
String lookupSearchName = null;


String lookupReplacementColumn = ElementParameterParser.getValue(
    node,
    "__LOOKUP_REPLACEMENT_COLUMN__"
);
String lookupReplacementName = null;
String typeToGenerate_lookup="";
String typeToGenerate_replacement="";
if (metadatas != null && metadatas.size() > 0) {
    String lookup = null;

    List<IConnection> inputConnections;
    inputConnections = (List<IConnection>) node.getIncomingConnections();
    for (IConnection connection : inputConnections) {
        if (connection == null) {
            continue;
        }
        if (connection.getLineStyle() == EConnectionType.FLOW_REF) {
            lookup = connection.getName();
            IMetadataTable lookupMetadata = connection.getSource().getMetadataList().get(0);
            int colnum = 0; 
            for(IMetadataColumn column: lookupMetadata.getListColumns()){
                if (lookupSearchColumn.equals(column.getLabel())){
                	typeToGenerate_lookup = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
                	lookupSearchName = column.getLabel();
                }
                if (lookupReplacementColumn.equals(column.getLabel())){
                	typeToGenerate_replacement = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable()); 
                	lookupReplacementName = column.getLabel();
                }
                colnum++;
            }
          
        }
    }
   

    stringBuffer.append(TEXT_2);
    stringBuffer.append(lookup );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(lookup );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(lookup);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(lookup );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(lookup );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(lookup );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(typeToGenerate_lookup);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(typeToGenerate_replacement);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(typeToGenerate_lookup);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(typeToGenerate_replacement);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(lookup);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(typeToGenerate_lookup);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(lookup);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(lookupSearchName);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(typeToGenerate_replacement);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(lookup);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(lookupReplacementName);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    
}

    stringBuffer.append(TEXT_28);
    return stringBuffer.toString();
  }
}
