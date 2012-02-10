package org.talend.designer.codegen.translators.file.management;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;

public class TFileCopyMainJava
{
  protected static String nl;
  public static synchronized TFileCopyMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileCopyMainJava result = new TFileCopyMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t";
  protected final String TEXT_2 = "     " + NL + "\t\tString srcFileFolderPath_";
  protected final String TEXT_3 = " = ";
  protected final String TEXT_4 = ";" + NL + "" + NL + "\t\tString desFileFolderPath_";
  protected final String TEXT_5 = " = ";
  protected final String TEXT_6 = ";" + NL + "" + NL + "\t\tjava.io.File srcFileFolder_";
  protected final String TEXT_7 = " = new java.io.File(srcFileFolderPath_";
  protected final String TEXT_8 = ");" + NL + "" + NL + "\t\tjava.io.File desFileFolder_";
  protected final String TEXT_9 = " = new java.io.File(desFileFolderPath_";
  protected final String TEXT_10 = ");" + NL + "" + NL + "\t\tint srcFilePathLength_";
  protected final String TEXT_11 = " = srcFileFolder_";
  protected final String TEXT_12 = ".getPath().length();" + NL + "" + NL + "\t\tString srcFolderName_";
  protected final String TEXT_13 = " = srcFileFolder_";
  protected final String TEXT_14 = ".getName();" + NL + "" + NL + "\t\tjava.io.File newDesFileFolder_";
  protected final String TEXT_15 = " = new java.io.File(desFileFolder_";
  protected final String TEXT_16 = ", srcFolderName_";
  protected final String TEXT_17 = ");" + NL + "      " + NL + "\t\tclass CopyDirectory_";
  protected final String TEXT_18 = " {\t\t" + NL + "" + NL + "\t\t\tpublic void copyAllFiles(java.io.File rootFolder, int pathLength, java.io.File newDesFileFolder) {" + NL + "" + NL + "\t\t\t\tjava.io.File[] fileList = rootFolder.listFiles();" + NL + "" + NL + "\t\t\t\tif (fileList.length > 0) {" + NL + "\t\t\t\t" + NL + "\t\t\t\t\tfor (java.io.File file : fileList) {\t\t\t\t" + NL + "" + NL + "\t\t\t\t\t\tif (file.isDirectory()) {" + NL + "\t\t\t\t\t\t\tcopyAllFiles(file, pathLength, newDesFileFolder);" + NL + "\t\t\t\t\t\t} else {" + NL + "\t\t\t\t\t\t" + NL + "\t\t\t\t\t\t\tString srcFilePath = file.getPath();" + NL + "" + NL + "\t\t\t\t\t\t\tString temFileName = srcFilePath.substring(pathLength);" + NL + "" + NL + "\t\t\t\t\t\t\tjava.io.File desFile = new java.io.File(newDesFileFolder, temFileName);" + NL + "" + NL + "\t\t\t\t\t\t\tString desFilePath = desFile.getPath();" + NL + "" + NL + "\t\t\t\t\t\t\tjava.io.File parentFile = desFile.getParentFile();" + NL + "" + NL + "\t\t\t\t\t\t\tif (!parentFile.exists()) {" + NL + "\t\t\t\t\t\t\t\tparentFile.mkdirs();" + NL + "\t\t\t\t\t\t\t}" + NL + "" + NL + "\t\t\t\t\t\t\ttry\t{\t\t\t\t" + NL + "\t\t\t\t\t\t\t\torg.talend.FileCopy.copyFile(srcFilePath, desFilePath, false);" + NL + "\t\t\t\t\t\t\t} catch (Exception e){ \t\t\t\t" + NL + "\t\t\t\t\t\t\t\te.printStackTrace();" + NL + "\t\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\t}" + NL + "" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t} else{ //it is an empty folder.        " + NL + "        " + NL + "\t\t\t\t\tString srcFolderPath = rootFolder.getPath();" + NL + "" + NL + "\t\t\t\t\tString temFolderName = srcFolderPath.substring(pathLength);" + NL + "" + NL + "\t\t\t\t\tjava.io.File desFolder = new java.io.File(newDesFileFolder, temFolderName);" + NL + "\t\t\t\t\tdesFolder.mkdirs();     " + NL + "\t\t\t\t} " + NL + "        " + NL + "\t\t\t}" + NL + "\t\t}" + NL + "    " + NL + "\t\tCopyDirectory_";
  protected final String TEXT_19 = " copyDir_";
  protected final String TEXT_20 = " = new CopyDirectory_";
  protected final String TEXT_21 = "();\t  " + NL + "\t\tcopyDir_";
  protected final String TEXT_22 = ".copyAllFiles(srcFileFolder_";
  protected final String TEXT_23 = ", srcFilePathLength_";
  protected final String TEXT_24 = ",newDesFileFolder_";
  protected final String TEXT_25 = ");" + NL + "" + NL + "\t\tglobalMap.put(\"";
  protected final String TEXT_26 = "_SOURCE_DIRECTORY\", srcFileFolderPath_";
  protected final String TEXT_27 = ");" + NL + "\t\tglobalMap.put(\"";
  protected final String TEXT_28 = "_DESTINATION_DIRECTORY\", desFileFolderPath_";
  protected final String TEXT_29 = ");" + NL;
  protected final String TEXT_30 = NL + NL + "\t\tString srcFileName_";
  protected final String TEXT_31 = " = ";
  protected final String TEXT_32 = ";" + NL + "" + NL + "\t\tjava.io.File srcFile_";
  protected final String TEXT_33 = " = new java.io.File(srcFileName_";
  protected final String TEXT_34 = ");" + NL + "" + NL + "\t\t// here need check first, before mkdirs()." + NL + "\t\tif (!srcFile_";
  protected final String TEXT_35 = ".exists() || !srcFile_";
  protected final String TEXT_36 = ".isFile()) {" + NL + "\t\t\tthrow new RuntimeException(\"The source File \\\"\" + srcFileName_";
  protected final String TEXT_37 = " + \"\\\" does not exist or is not a file.\");" + NL + "\t\t}" + NL + "" + NL + "\t\tString desDirName_";
  protected final String TEXT_38 = " = ";
  protected final String TEXT_39 = ";" + NL + "" + NL + "\t\tString desFileName_";
  protected final String TEXT_40 = " = ";
  protected final String TEXT_41 = " ";
  protected final String TEXT_42 = " ";
  protected final String TEXT_43 = " srcFile_";
  protected final String TEXT_44 = ".getName() ";
  protected final String TEXT_45 = ";" + NL + "" + NL + "\t\tif (desFileName_";
  protected final String TEXT_46 = " != null && (\"\").equals(desFileName_";
  protected final String TEXT_47 = ".trim())){" + NL + "\t\t\tdesFileName_";
  protected final String TEXT_48 = " = \"NewName.temp\";" + NL + "\t\t}" + NL + "" + NL + "\t\tjava.io.File desFile_";
  protected final String TEXT_49 = " = new java.io.File(desDirName_";
  protected final String TEXT_50 = ", desFileName_";
  protected final String TEXT_51 = ");" + NL + "" + NL + "\t\tif (!srcFile_";
  protected final String TEXT_52 = ".getPath().equals(desFile_";
  protected final String TEXT_53 = ".getPath()) ";
  protected final String TEXT_54 = " && !desFile_";
  protected final String TEXT_55 = ".exists() ";
  protected final String TEXT_56 = " ) {";
  protected final String TEXT_57 = NL + "\t\t\t\tjava.io.File parentFile_";
  protected final String TEXT_58 = " = desFile_";
  protected final String TEXT_59 = ".getParentFile();" + NL + "" + NL + "\t\t\t\tif (parentFile_";
  protected final String TEXT_60 = " != null && !parentFile_";
  protected final String TEXT_61 = ".exists()) {" + NL + "\t\t\t\t\tparentFile_";
  protected final String TEXT_62 = ".mkdirs();" + NL + "\t\t\t\t}";
  protected final String TEXT_63 = "           " + NL + "\t\t\torg.talend.FileCopy.copyFile(srcFile_";
  protected final String TEXT_64 = ".getPath(), desFile_";
  protected final String TEXT_65 = ".getPath(), ";
  protected final String TEXT_66 = ");";
  protected final String TEXT_67 = NL + "\t\t\t\tjava.io.File isRemoved_";
  protected final String TEXT_68 = " = new java.io.File(";
  protected final String TEXT_69 = ");" + NL + "\t\t\t\tif(isRemoved_";
  protected final String TEXT_70 = ".exists()) {" + NL + "\t\t\t\t\tSystem.err.println(\"The source file could not be removed from the folder because it is open or you only have read-only rights.\\n\");" + NL + "\t\t\t\t}" + NL + "\t\t\t";
  protected final String TEXT_71 = NL + NL + "\t\t}" + NL + "" + NL + "        String desFilePath_";
  protected final String TEXT_72 = "=desFile_";
  protected final String TEXT_73 = ".getPath().replaceAll(\"\\\\\\\\\", \"/\");" + NL + "\t\tglobalMap.put(\"";
  protected final String TEXT_74 = "_DESTINATION_FILEPATH\",desFilePath_";
  protected final String TEXT_75 = ");" + NL + "\t\tglobalMap.put(\"";
  protected final String TEXT_76 = "_DESTINATION_FILENAME\",desFile_";
  protected final String TEXT_77 = ".getName());" + NL + "\t\tString  srcFilePathFolder_";
  protected final String TEXT_78 = "=srcFileName_";
  protected final String TEXT_79 = ".replaceAll(\"\\\\\\\\\", \"/\");" + NL + "\t\tdesDirName_";
  protected final String TEXT_80 = "=desDirName_";
  protected final String TEXT_81 = ".replaceAll(\"\\\\\\\\\", \"/\");" + NL + "\t\tsrcFilePathFolder_";
  protected final String TEXT_82 = "=srcFilePathFolder_";
  protected final String TEXT_83 = ".substring(0,srcFilePathFolder_";
  protected final String TEXT_84 = ".lastIndexOf(\"/\")+1);" + NL + "\t\tglobalMap.put(\"";
  protected final String TEXT_85 = "_SOURCE_DIRECTORY\", srcFilePathFolder_";
  protected final String TEXT_86 = ");" + NL + "\t\tglobalMap.put(\"";
  protected final String TEXT_87 = "_DESTINATION_DIRECTORY\", desDirName_";
  protected final String TEXT_88 = ");";
  protected final String TEXT_89 = "        " + NL + "        ";
  protected final String TEXT_90 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;

	INode node = (INode)codeGenArgument.getArgument();

	String cid = node.getUniqueName();

	String fileName = ElementParameterParser.getValue(node, "__FILENAME__");	

	String destination  = ElementParameterParser.getValue(node, "__DESTINATION__");
	
	boolean rename = ("true").equals(ElementParameterParser.getValue(node, "__RENAME__"));
	
	String destination_filename  = ElementParameterParser.getValue(node, "__DESTINATION_RENAME__");

	boolean isCopyADir = ("true").equals(ElementParameterParser.getValue(node, "__ENABLE_COPY_DIRECTORY__"));
	
	boolean reFile = ("true").equals(ElementParameterParser.getValue(node, "__REMOVE_FILE__"));

	boolean rpFile = ("true").equals(ElementParameterParser.getValue(node,"__REPLACE_FILE__"));

	boolean creatDir = ("true").equals(ElementParameterParser.getValue(node,"__CREATE_DIRECTORY__"));
	
	if (isCopyADir){ // copy a directory

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(ElementParameterParser.getValue(node, "__SOURCE_DERECTORY__"));
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(ElementParameterParser.getValue(node, "__DESTINATION__"));
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
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
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
     
	} else { //copy a file

    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(fileName );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(destination );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_40);
    if(rename){
    stringBuffer.append(TEXT_41);
    stringBuffer.append(destination_filename );
    stringBuffer.append(TEXT_42);
    }else{
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    }
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_53);
    if (!rpFile){
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_55);
    }
    stringBuffer.append(TEXT_56);
    
			if (creatDir){

    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_62);
    
			}

    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_65);
    stringBuffer.append(reFile );
    stringBuffer.append(TEXT_66);
    
			if(reFile) {

    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_68);
    stringBuffer.append(fileName);
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_70);
    			}

    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_73);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_74);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_76);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_78);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_82);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_83);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_86);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_87);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_88);
    
	}

    stringBuffer.append(TEXT_89);
    stringBuffer.append(TEXT_90);
    return stringBuffer.toString();
  }
}
