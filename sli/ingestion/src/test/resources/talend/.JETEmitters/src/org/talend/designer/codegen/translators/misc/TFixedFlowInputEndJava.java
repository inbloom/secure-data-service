package org.talend.designer.codegen.translators.misc;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnectionCategory;
import java.util.List;
import java.util.Map;

public class TFixedFlowInputEndJava
{
  protected static String nl;
  public static synchronized TFixedFlowInputEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFixedFlowInputEndJava result = new TFixedFlowInputEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "        }" + NL + "        globalMap.put(\"";
  protected final String TEXT_3 = "_NB_LINE\", ";
  protected final String TEXT_4 = ");        ";
  protected final String TEXT_5 = NL + "\t\t}" + NL + "\t}" + NL + "\t" + NL + "\tcacheList_";
  protected final String TEXT_6 = ".clear();" + NL + "\tglobalMap.put(\"";
  protected final String TEXT_7 = "_NB_LINE\", nb_line_";
  protected final String TEXT_8 = ");";
  protected final String TEXT_9 = NL + "\t\t}" + NL + "\t  fid_";
  protected final String TEXT_10 = ".close();" + NL + "\t}" + NL + "" + NL + "\tglobalMap.put(\"";
  protected final String TEXT_11 = "_NB_LINE\", nb_line_";
  protected final String TEXT_12 = ");";
  protected final String TEXT_13 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
    CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
    INode node = (INode)codeGenArgument.getArgument();
    String cid = node.getUniqueName();
    
    String nbRows = ElementParameterParser.getValue(node, "__NB_ROWS__");
    
    boolean use_singleMode = ("true").equals(ElementParameterParser.getValue(node, "__USE_SINGLEMODE__"));
    boolean use_inTable = ("true").equals(ElementParameterParser.getValue(node, "__USE_INTABLE__"));
    boolean use_inlineContent = ("true").equals(ElementParameterParser.getValue(node, "__USE_INLINECONTENT__"));
    
    List<Map<String, String>> tableValues =
    (List<Map<String,String>>)ElementParameterParser.getObjectValue(
        node,
        "__VALUES__"
    );

	List<Map<String, String>> inTableValues =
    (List<Map<String,String>>)ElementParameterParser.getObjectValue(
        node,
        "__INTABLE__"
    );
    
    List< ? extends IConnection> conns = node.getOutgoingSortedConnections();
    if(use_singleMode){
    	if(tableValues != null && tableValues.size() > 0 && conns != null && conns.size() > 0) {
        
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(nbRows);
    stringBuffer.append(TEXT_4);
    
    	}
//********************the new part*****************************************************    	
    }else if(use_inTable){
    	if(inTableValues != null && inTableValues.size() > 0 && conns != null && conns.size() > 0) {
    		String firstConnName = null;
    	    for(IConnection conn : conns) {
                if(conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
             		firstConnName = conn.getName();
             		break;
                }
            }
            if(firstConnName!=null && !firstConnName.equals("")){

    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    
    	    }
    	}
//********************the new part*****************************************************
    } else if(use_inlineContent){

    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    
	}

    stringBuffer.append(TEXT_13);
    return stringBuffer.toString();
  }
}
