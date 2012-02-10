package org.talend.designer.codegen.translators.custom_code;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import java.util.List;
import java.util.Map;

public class TLibraryLoadBeginJava
{
  protected static String nl;
  public static synchronized TLibraryLoadBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TLibraryLoadBeginJava result = new TLibraryLoadBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL;
  protected final String TEXT_3 = NL + NL + "java.net.URLClassLoader sysloader_";
  protected final String TEXT_4 = " = (java.net.URLClassLoader) ClassLoader.getSystemClassLoader();" + NL + "java.lang.reflect.Method method_";
  protected final String TEXT_5 = " = java.net.URLClassLoader.class.getDeclaredMethod(\"addURL\", new Class[] { java.net.URL.class });" + NL + "method_";
  protected final String TEXT_6 = ".setAccessible(true);" + NL + "" + NL + "String[] libPaths_";
  protected final String TEXT_7 = " = new String[] { ";
  protected final String TEXT_8 = " ";
  protected final String TEXT_9 = ", ";
  protected final String TEXT_10 = " };" + NL + "for(String lib_";
  protected final String TEXT_11 = ":libPaths_";
  protected final String TEXT_12 = " ){" + NL + "\tString separator_";
  protected final String TEXT_13 = " = System.getProperty(\"path.separator\");" + NL + "\tString[] jarFiles_";
  protected final String TEXT_14 = " = lib_";
  protected final String TEXT_15 = ".split(separator_";
  protected final String TEXT_16 = ");\t" + NL + "\tfor(String jarFile_";
  protected final String TEXT_17 = ":jarFiles_";
  protected final String TEXT_18 = "){\t\t" + NL + "\t\tmethod_";
  protected final String TEXT_19 = ".invoke(sysloader_";
  protected final String TEXT_20 = ", new Object[] { new java.io.File(jarFile_";
  protected final String TEXT_21 = ").toURL() });" + NL + "\t}" + NL + "}" + NL;
  protected final String TEXT_22 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
    CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
    INode node = (INode)codeGenArgument.getArgument();
    String cid = node.getUniqueName();
    List<Map<String, String>> hotLibs = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__HOTLIBS__");

    stringBuffer.append(TEXT_2);
     if(hotLibs.size() > 0){
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
     for(Map<String, String> item : hotLibs){
    stringBuffer.append(TEXT_8);
    stringBuffer.append(item.get("LIBPATH") );
    stringBuffer.append(TEXT_9);
    }
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    }
    stringBuffer.append(TEXT_22);
    return stringBuffer.toString();
  }
}
