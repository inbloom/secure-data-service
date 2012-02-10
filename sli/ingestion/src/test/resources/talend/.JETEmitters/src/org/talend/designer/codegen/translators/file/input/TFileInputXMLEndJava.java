package org.talend.designer.codegen.translators.file.input;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TFileInputXMLEndJava
{
  protected static String nl;
  public static synchronized TFileInputXMLEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileInputXMLEndJava result = new TFileInputXMLEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "}";
  protected final String TEXT_3 = NL + "\t}";
  protected final String TEXT_4 = NL + "\ttry {" + NL + "\t\tlooper_";
  protected final String TEXT_5 = ".handleTaskResponse();" + NL + "\t} catch(Exception e) {" + NL + "\t";
  protected final String TEXT_6 = NL + "\t\tthrow(e);" + NL + "\t";
  protected final String TEXT_7 = NL + "\t\tSystem.err.println(e.getMessage());\t" + NL + "\t";
  protected final String TEXT_8 = NL + "\t}";
  protected final String TEXT_9 = NL + "\tglobalMap.put(\"";
  protected final String TEXT_10 = "_NB_LINE\",nb_line_";
  protected final String TEXT_11 = ");";
  protected final String TEXT_12 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();

String cid = node.getUniqueName();

String dieOnErrorStr = ElementParameterParser.getValue(node, "__DIE_ON_ERROR__");
boolean dieOnError = (dieOnErrorStr!=null&&!("").equals(dieOnErrorStr))?("true").equals(dieOnErrorStr):false;

String mode = ElementParameterParser.getValue(node, "__GENERATION_MODE__");
if(("Xerces").equals(mode) || ("Dom4j").equals(mode)){

    stringBuffer.append(TEXT_2);
    }
    stringBuffer.append(TEXT_3);
    if(("SAX").equals(mode)){
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
     if(dieOnError) { 
    stringBuffer.append(TEXT_6);
     } else { 
    stringBuffer.append(TEXT_7);
     } 
    stringBuffer.append(TEXT_8);
     } 
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(TEXT_12);
    return stringBuffer.toString();
  }
}
