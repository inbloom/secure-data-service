package org.talend.designer.codegen.translators.file.management;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import java.util.List;
import java.util.Map;

public class TFileDeleteMainJava
{
  protected static String nl;
  public static synchronized TFileDeleteMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileDeleteMainJava result = new TFileDeleteMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "class DeleteFolder";
  protected final String TEXT_2 = "{" + NL + "\t /**" + NL + "     * delete all the sub-files in 'file'" + NL + "     * " + NL + "     * @param file" + NL + "     */" + NL + "\tpublic boolean delete(java.io.File file) {" + NL + "        java.io.File[] files = file.listFiles();" + NL + "        for (int i = 0; i < files.length; i++) {" + NL + "            if (files[i].isFile()) {" + NL + "                files[i].delete();" + NL + "            } else if (files[i].isDirectory()) {" + NL + "                if (!files[i].delete()) {" + NL + "                    delete(files[i]);" + NL + "                }" + NL + "            }" + NL + "        }" + NL + "        deleteDirectory(file);" + NL + "        return file.delete();" + NL + "    }" + NL + "" + NL + "    /**" + NL + "     * delete all the sub-folders in 'file'" + NL + "     * " + NL + "     * @param file" + NL + "     */" + NL + "    private void deleteDirectory(java.io.File file) {" + NL + "        java.io.File[] filed = file.listFiles();" + NL + "        for (int i = 0; i < filed.length; i++) {" + NL + "        \tif(filed[i].isDirectory()) {" + NL + "            \tdeleteDirectory(filed[i]);" + NL + "            }" + NL + "            filed[i].delete();" + NL + "        }" + NL + "    }" + NL + "" + NL + "}";
  protected final String TEXT_3 = NL + "\tjava.io.File path_";
  protected final String TEXT_4 = "=new java.io.File(";
  protected final String TEXT_5 = ");" + NL + "\tif(path_";
  protected final String TEXT_6 = ".exists()){" + NL + "\t\tif(path_";
  protected final String TEXT_7 = ".isFile()){" + NL + "\t    \tif(path_";
  protected final String TEXT_8 = ".delete()){" + NL + "\t    \t\tglobalMap.put(\"";
  protected final String TEXT_9 = "_CURRENT_STATUS\", \"File deleted.\");" + NL + "\t    \t}else{" + NL + "\t    \t\tglobalMap.put(\"";
  protected final String TEXT_10 = "_CURRENT_STATUS\", \"No file deleted.\");" + NL + "\t    \t}" + NL + "\t\t}else if(path_";
  protected final String TEXT_11 = ".isDirectory()){ " + NL + "\t    \tDeleteFolder";
  protected final String TEXT_12 = " df";
  protected final String TEXT_13 = " = new DeleteFolder";
  protected final String TEXT_14 = "();" + NL + "\t    \tif(df";
  protected final String TEXT_15 = ".delete(path_";
  protected final String TEXT_16 = ")){" + NL + "\t    \t\tglobalMap.put(\"";
  protected final String TEXT_17 = "_CURRENT_STATUS\", \"Path deleted.\");" + NL + "\t    \t}else{" + NL + "\t    \t\tglobalMap.put(\"";
  protected final String TEXT_18 = "_CURRENT_STATUS\", \"No path deleted.\");" + NL + "\t    \t}" + NL + "\t\t}" + NL + "    }else{" + NL + "\t\tglobalMap.put(\"";
  protected final String TEXT_19 = "_CURRENT_STATUS\", \"File or path does not exists or is invalid.\");" + NL;
  protected final String TEXT_20 = NL + "    \t\tthrow new RuntimeException(\"File or path does not exists or is invalid.\");";
  protected final String TEXT_21 = NL + "    }" + NL + "    globalMap.put(\"";
  protected final String TEXT_22 = "_DELETE_PATH\",";
  protected final String TEXT_23 = ");";
  protected final String TEXT_24 = NL + NL + "\tjava.io.File file";
  protected final String TEXT_25 = " = new java.io.File(";
  protected final String TEXT_26 = ");" + NL + "" + NL + "\tif(file";
  protected final String TEXT_27 = ".exists()&& file";
  protected final String TEXT_28 = ".isDirectory()){" + NL + "\t\tDeleteFolder";
  protected final String TEXT_29 = " df";
  protected final String TEXT_30 = " = new DeleteFolder";
  protected final String TEXT_31 = "();" + NL + "\t\tif(df";
  protected final String TEXT_32 = ".delete(file";
  protected final String TEXT_33 = ")){" + NL + "    \t\tglobalMap.put(\"";
  protected final String TEXT_34 = "_CURRENT_STATUS\", \"Path deleted.\");" + NL + "    \t}else{" + NL + "    \t\tglobalMap.put(\"";
  protected final String TEXT_35 = "_CURRENT_STATUS\", \"No path deleted.\");" + NL + "    \t}" + NL + "\t}else{" + NL + "\t\tglobalMap.put(\"";
  protected final String TEXT_36 = "_CURRENT_STATUS\", \"Path does not exists or is invalid.\");" + NL;
  protected final String TEXT_37 = NL + "    \t\tthrow new RuntimeException(\"Path does not exists or is invalid.\");";
  protected final String TEXT_38 = NL + "    }" + NL + "    globalMap.put(\"";
  protected final String TEXT_39 = "_DELETE_PATH\",";
  protected final String TEXT_40 = ");";
  protected final String TEXT_41 = NL + "    java.io.File file_";
  protected final String TEXT_42 = "=new java.io.File(";
  protected final String TEXT_43 = ");" + NL + "    if(file_";
  protected final String TEXT_44 = ".exists()&& file_";
  protected final String TEXT_45 = ".isFile()){" + NL + "    \tif(file_";
  protected final String TEXT_46 = ".delete()){" + NL + "    \t\tglobalMap.put(\"";
  protected final String TEXT_47 = "_CURRENT_STATUS\", \"File deleted.\");" + NL + "    \t}else{" + NL + "    \t\tglobalMap.put(\"";
  protected final String TEXT_48 = "_CURRENT_STATUS\", \"No file deleted.\");" + NL + "    \t}" + NL + "    }else{" + NL + "    \tglobalMap.put(\"";
  protected final String TEXT_49 = "_CURRENT_STATUS\", \"File does not exists or is invalid.\");";
  protected final String TEXT_50 = NL + "    \t\tthrow new RuntimeException(\"File does not exists or is invalid.\");";
  protected final String TEXT_51 = NL + "\t}" + NL + "\tglobalMap.put(\"";
  protected final String TEXT_52 = "_DELETE_PATH\",";
  protected final String TEXT_53 = ");";
  protected final String TEXT_54 = NL + "    " + NL + "     " + NL + " ";
  protected final String TEXT_55 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String fileName = ElementParameterParser.getValue(node, "__FILENAME__");
String dirName = ElementParameterParser.getValue(node, "__DIRECTORY__");
String path = ElementParameterParser.getValue(node, "__PATH__");
String failon = ElementParameterParser.getValue(node, "__FAILON__");
boolean ifFolder = ("true").equals(ElementParameterParser.getValue(node, "__FOLDER__"));
boolean either = ("true").equals(ElementParameterParser.getValue(node, "__FOLDER_FILE__"));

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    
if(either){

    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(path);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
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
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    
		if(("true").equals(failon)){

    stringBuffer.append(TEXT_20);
    
		}

    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(path);
    stringBuffer.append(TEXT_23);
    
}else if(ifFolder){

    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(dirName);
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
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    
		if(("true").equals(failon)){

    stringBuffer.append(TEXT_37);
    
		}

    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(dirName);
    stringBuffer.append(TEXT_40);
    
}else{

    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(fileName);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_49);
    
		if(("true").equals(failon)){

    stringBuffer.append(TEXT_50);
    
		}

    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_52);
    stringBuffer.append(fileName);
    stringBuffer.append(TEXT_53);
    
}

    stringBuffer.append(TEXT_54);
    stringBuffer.append(TEXT_55);
    return stringBuffer.toString();
  }
}
