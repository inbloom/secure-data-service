package org.talend.designer.codegen.translators.databases.oledb;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.metadata.builder.database.ExtractMetaDataUtils;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;
import java.util.Map;

public class TOleDbInputBeginJava
{
  protected static String nl;
  public static synchronized TOleDbInputBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TOleDbInputBeginJava result = new TOleDbInputBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t" + NL + "org.talend.net.Object conn_";
  protected final String TEXT_2 = " = org.talend.net.Object.createInstance(";
  protected final String TEXT_3 = ",";
  protected final String TEXT_4 = ", " + NL + "new java.lang.Object[] { ";
  protected final String TEXT_5 = NL;
  protected final String TEXT_6 = " " + NL + "});" + NL + "conn_";
  protected final String TEXT_7 = ".invokeGeneric(\"Open\");" + NL + "org.talend.net.Object cmd_";
  protected final String TEXT_8 = " = org.talend.net.Object.createInstance(";
  protected final String TEXT_9 = ",\"System.Data.OleDb.OleDbCommand\"," + NL + "new java.lang.Object[] { ";
  protected final String TEXT_10 = NL;
  protected final String TEXT_11 = NL + ", " + NL + "conn_";
  protected final String TEXT_12 = " " + NL + "});" + NL + "org.talend.net.Object reader_";
  protected final String TEXT_13 = " = cmd_";
  protected final String TEXT_14 = ".invoke(\"ExecuteReader\");";
  protected final String TEXT_15 = NL + "               //";
  protected final String TEXT_16 = NL + "               Integer id";
  protected final String TEXT_17 = "_";
  protected final String TEXT_18 = " = (Integer)reader_";
  protected final String TEXT_19 = ".invokeGeneric(\"GetOrdinal\"," + NL + "               new java.lang.Object[] { \"";
  protected final String TEXT_20 = "\" });" + NL + "                ";
  protected final String TEXT_21 = NL + "class TypeConverter_";
  protected final String TEXT_22 = " {" + NL + "    void doConversion(Object val, Object targetStruct, String colName) {" + NL + "            try {" + NL + "       \t\t\tjava.lang.reflect.Field f = targetStruct.getClass().getField(colName);" + NL + "       \t\t\tf.setAccessible(true);" + NL + "       \t\t\tf.set(targetStruct,val);" + NL + "       \t\t} catch (Exception ex) {" + NL + "       \t\t\tthrow new RuntimeException(\"Can not convert value of type: \"+val.getClass().getName()+\" to target column\");" + NL + "       \t\t}" + NL + "    }" + NL + "}" + NL + "Boolean oBool_";
  protected final String TEXT_23 = " = (Boolean)reader_";
  protected final String TEXT_24 = ".invokeGeneric(\"Read\");" + NL + "boolean bool_";
  protected final String TEXT_25 = " = oBool_";
  protected final String TEXT_26 = " != null && oBool_";
  protected final String TEXT_27 = ".booleanValue();" + NL + "java.lang.Object val_";
  protected final String TEXT_28 = ";" + NL + "int nb_line_";
  protected final String TEXT_29 = " = 0;" + NL + "while (bool_";
  protected final String TEXT_30 = ") {" + NL + "    nb_line_";
  protected final String TEXT_31 = "++;";
  protected final String TEXT_32 = NL + "     ";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
   CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
    INode node = (INode)codeGenArgument.getArgument();
    String cid = node.getUniqueName();
    String connUrl = ElementParameterParser.getValue(node,"__DBNAME__");
    String query = ElementParameterParser.getValue(node,"__QUERY__");
    query = query.replaceAll("\n","");
    query = query.replaceAll("\r","");
    String encoding = ElementParameterParser.getValue(node,"__ENCODING__");
    String assemblyName = ElementParameterParser.getValue(node,"__ASSEMBLY_NAME__");
    String className = ElementParameterParser.getValue(node,"__CLASS_NAME__");
     boolean whetherTrimAllCol = ("true").equals(ElementParameterParser.getValue(node, "__TRIM_ALL_COLUMN__"));
    List<Map<String, String>> trimColumnList = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__TRIM_COLUMN__");
    List<IMetadataTable> metadatas = node.getMetadataList();

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(assemblyName);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(className);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(connUrl);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(assemblyName);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(query);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    
IMetadataTable metadata = metadatas.get(0);
    List<? extends IConnection> connsout = node.getOutgoingConnections();
    String connName = null;
    if (node.getIncomingConnections().size() == 1) {
        IConnection conn = node.getIncomingConnections().get(0);
        connName = conn.getName();
    }
    List<IMetadataColumn> columnsout = metadata.getListColumns();
    String populateOutput = "";
    String outName = null;
     if (connsout != null) {
        for (int i = 0; i < connsout.size(); i++) {
            IConnection connout = connsout.get(i);
            if (connout.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
               outName = connout.getName();               
               for (int j = 0; j < columnsout.size(); j++) {
                    IMetadataColumn columnout = columnsout.get(j);
               
    stringBuffer.append(TEXT_15);
    stringBuffer.append(columnout.getTalendType());
    stringBuffer.append(TEXT_16);
    stringBuffer.append(columnout.getLabel());
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(columnout.getLabel());
    stringBuffer.append(TEXT_20);
    
               populateOutput += "val_"+cid+" = reader_"+cid+".invokeGeneric(\"GetValue\","
               		+ "new java.lang.Object[] { id"+columnout.getLabel()+"_"+cid+" });\nnew TypeConverter_"+cid+"().doConversion(val_"+cid+","+connout.getName()+",\""+columnout.getLabel()+"\");\n";
               		
				if (columnout.getTalendType().equals("id_String")) {
				    boolean whetherTrimCol = false;
		            if((trimColumnList != null && trimColumnList.size() > 0) && !whetherTrimAllCol) {
		                for(Map<String, String> trimColumn : trimColumnList) {
		                    if(columnout.getLabel().equals(trimColumn.get("SCHEMA_COLUMN"))) {
		                        if(("true").equals(trimColumn.get("TRIM"))) {
		                            whetherTrimCol = true;
		                            break;
		                        }
		                    }
		                }
		            }
		            
		            if(whetherTrimAllCol || whetherTrimCol) {
		                populateOutput += connout.getName()+"."+columnout.getLabel()+" = "+connout.getName()+"."+columnout.getLabel()+".trim();\n";
		            }
		            
				}               		
               }
             }
         }
     }     

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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(populateOutput);
    return stringBuffer.toString();
  }
}
