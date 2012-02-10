package org.talend.designer.codegen.translators.dotnet;

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
import java.util.Set;

public class TDotNETRowMainJava
{
  protected static String nl;
  public static synchronized TDotNETRowMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TDotNETRowMainJava result = new TDotNETRowMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "    //i = ";
  protected final String TEXT_2 = NL + "                        ";
  protected final String TEXT_3 = ".";
  protected final String TEXT_4 = " = ";
  protected final String TEXT_5 = ".";
  protected final String TEXT_6 = ";";
  protected final String TEXT_7 = NL + "    ";
  protected final String TEXT_8 = " = new ";
  protected final String TEXT_9 = "Struct();";
  protected final String TEXT_10 = NL + "    class TypeConverter_";
  protected final String TEXT_11 = " {" + NL + "       void doConversion(Object val, Object targetStruct, String colName) {" + NL + "            try {" + NL + "       \t\t\tjava.lang.reflect.Field f = targetStruct.getClass().getField(colName);" + NL + "       \t\t\tf.setAccessible(true);" + NL + "       \t\t\tf.set(targetStruct,val);" + NL + "       \t\t} catch (Exception ex) {" + NL + "       \t\t\tthrow new RuntimeException(\"Can not convert value of type: \"+val.getClass().getName()+\" to target column\");" + NL + "       \t\t}" + NL + "       }" + NL + "    }";
  protected final String TEXT_12 = NL + "    Object[] parameters_";
  protected final String TEXT_13 = " = new Object[] {";
  protected final String TEXT_14 = NL + "       ";
  protected final String TEXT_15 = NL + "       ,";
  protected final String TEXT_16 = NL + "   }; ";
  protected final String TEXT_17 = NL + "   Object[] parameterList_";
  protected final String TEXT_18 = " = new Object[] {";
  protected final String TEXT_19 = NL + "     ";
  protected final String TEXT_20 = NL + "        ,";
  protected final String TEXT_21 = "   " + NL + "   };";
  protected final String TEXT_22 = NL + "   org.talend.net.Object netObject_";
  protected final String TEXT_23 = " = ";
  protected final String TEXT_24 = ";" + NL + "   globalMap.put(\"";
  protected final String TEXT_25 = "_INSTANCE\",netObject_";
  protected final String TEXT_26 = ");" + NL + "  ";
  protected final String TEXT_27 = NL + "       globalMap.put(\"";
  protected final String TEXT_28 = "_INSTANCE\",globalMap.get(\"";
  protected final String TEXT_29 = "_INSTANCE\"));";
  protected final String TEXT_30 = NL + "   org.talend.net.Object instance_";
  protected final String TEXT_31 = " = (org.talend.net.Object)globalMap.get(\"";
  protected final String TEXT_32 = "_INSTANCE\");";
  protected final String TEXT_33 = NL + "   ";
  protected final String TEXT_34 = " ";
  protected final String TEXT_35 = NL + "   ";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
     
    CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
    INode node = (INode)codeGenArgument.getArgument();
    String cid = node.getUniqueName();
    String dllLoad = ElementParameterParser.getValue(node,"__LIBRARY__");    
    String clazz = ElementParameterParser.getValue(node,"__CLASS_NAME__");
    boolean useExistingConnection = "true".equals((String)ElementParameterParser.getValue(node,"__USE_EXISTING_INSTANCE__"));
    boolean useStatic = "true".equals(ElementParameterParser.getValue(node,"__USE_STATIC_METHOD__"));    
    List<Map<String,String>> values = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node,"__PARAMETERS__");
    boolean onRow = "true".equals(ElementParameterParser.getValue(node,"__INSTANTIATE_ON_ROW__"));
    String instance = ElementParameterParser.getValue(node,"__INSTANCE__");
    List<Map<String,String>> parameters = 
    	(List<Map<String,String>>)ElementParameterParser.getObjectValue(node,"__METHOD_PARAMETERS__");
    List<Map<String,String>> customParameters = 
    	(List<Map<String,String>>)ElementParameterParser.getObjectValue(node,"__ADD_PARAMS__");
    boolean returnsDotNetObj = "true".equals(ElementParameterParser.getValue(node,"__IS_RETURN_NET_OBJ__"));
    boolean isVoid = "true".equals(ElementParameterParser.getValue(node,"__IS_VOID_METHOD__"));
    String methodName = ElementParameterParser.getValue(node,"__METHOD_NAME__");
    String outputCol = ElementParameterParser.getValue(node,"__OUTPUT_COLUMN__");
    List<IMetadataTable> metadatas = node.getMetadataList();
    for (int i = 0; i < metadatas.size(); i++) {
    
    stringBuffer.append(TEXT_1);
    stringBuffer.append(i);
    
    }
    IMetadataTable metadata = metadatas.get(0);
    List<? extends IConnection> connsout = node.getOutgoingConnections();
    boolean storeResult = "true".equals(ElementParameterParser.getValue(node,"__STORE_RETURNED_INSTANCE__"));
    boolean propagateData = "true".equals(ElementParameterParser.getValue(node,"__PASS_EXISTING_DATA__"));
    String connName = null;
    if (node.getIncomingConnections().size() == 1) {
        IConnection conn = node.getIncomingConnections().get(0);
        connName = conn.getName();
    }
    List<IMetadataColumn> columnsout = metadata.getListColumns();
    String outName = null;
     if (connsout != null) {
        for (int i = 0; i < connsout.size(); i++) {
            IConnection connout = connsout.get(i);
            if (connout.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
               outName = connout.getName();
               if (propagateData) {
               for (int j = 0; j < columnsout.size(); j++) {
                    IMetadataColumn columnout = columnsout.get(j); 
                    if (!columnout.getLabel().equals(outputCol) ) {                       
    
    stringBuffer.append(TEXT_2);
    stringBuffer.append(outName);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(columnout.getLabel());
    stringBuffer.append(TEXT_4);
    stringBuffer.append(connName);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(columnout.getLabel());
    stringBuffer.append(TEXT_6);
    
					}
                }
                }
            }
        }
    }
    
    if (node.getIncomingConnections().size() == 0 && outName != null) {
    
    stringBuffer.append(TEXT_7);
    stringBuffer.append(outName);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(outName);
    stringBuffer.append(TEXT_9);
    
    }    
    
    String call = "";
    String args = "";
    if (useStatic) {
       call = "org.talend.net.Object.invokeStatic";
       args = dllLoad+","+clazz+",";
    } else {
       call = "instance_"+cid+".invoke";
    }
    args += methodName+", parameters_"+cid;
    if (!returnsDotNetObj) {
       call += "Generic";
    }
    if (!isVoid && !storeResult) {
    call = "Object rc_"+cid+" = "+call+"("+args+");\nglobalMap.put(\""+cid+"_CALL_RESULT\",rc_"+cid+");\nnew TypeConverter_"+cid+"().doConversion(rc_"+cid+","+outName+",\""+outputCol+"\");";
    
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    
    } else {
    call = call+"("+args+");";
    	if (storeResult) {
    		call = "Object rc_"+cid+" = "+call+"\nglobalMap.put(\""+cid+"_INSTANCE\",rc_"+cid+");";
    	}
    }
    
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
       
     for (int i = 0; i < parameters.size(); i++) {
         Map<String,String> parameter = parameters.get(i); 
         
   
    stringBuffer.append(TEXT_14);
    stringBuffer.append(parameter.get("PREV_COL").replaceAll("input_row",connName).replaceAll("output_row",outName));
    
       if (i < parameters.size() - 1) {
       
    stringBuffer.append(TEXT_15);
    
       }
     
   }
   
    stringBuffer.append(TEXT_16);
    
    if (onRow && !useExistingConnection && !useStatic) {
String cCall = "org.talend.net.Object.createInstance("+dllLoad+","+clazz+")";
        if (values.size() > 0) {
            cCall = "org.talend.net.Object.createInstance("+dllLoad+","+clazz+",parameterList_"+cid+")";

    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    
     for (int i = 0; i < values.size(); i++) {
         Map<String,String> line = values.get(i);
     
    stringBuffer.append(TEXT_19);
    stringBuffer.append(line.get("PARAMETER") );
    
        if (i < values.size() - 1) {
        
    stringBuffer.append(TEXT_20);
    
        }
     }
   
    stringBuffer.append(TEXT_21);
     } 
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cCall);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    
   } else if (!onRow && useExistingConnection && !useStatic) {
   
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(instance);
    stringBuffer.append(TEXT_29);
    
   } 

   if (!useStatic) {    
   
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    
   }
   
    stringBuffer.append(TEXT_33);
    stringBuffer.append(call);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(TEXT_35);
    return stringBuffer.toString();
  }
}
