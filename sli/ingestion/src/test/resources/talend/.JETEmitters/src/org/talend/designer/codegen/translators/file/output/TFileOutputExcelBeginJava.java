package org.talend.designer.codegen.translators.file.output;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;
import java.util.Map;

public class TFileOutputExcelBeginJava
{
  protected static String nl;
  public static synchronized TFileOutputExcelBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileOutputExcelBeginJava result = new TFileOutputExcelBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\tint nb_line_";
  protected final String TEXT_3 = " = 0;" + NL;
  protected final String TEXT_4 = "\t\t" + NL + "\t\tjava.io.File file_";
  protected final String TEXT_5 = " = new java.io.File(";
  protected final String TEXT_6 = ");" + NL + "\t\t";
  protected final String TEXT_7 = NL + "//create directory only if not exists\t\t  " + NL + "          java.io.File parentFile_";
  protected final String TEXT_8 = " = file_";
  protected final String TEXT_9 = ".getParentFile();" + NL + "          if (parentFile_";
  protected final String TEXT_10 = " != null && !parentFile_";
  protected final String TEXT_11 = ".exists()) {" + NL + "             parentFile_";
  protected final String TEXT_12 = ".mkdirs();" + NL + "          }";
  protected final String TEXT_13 = "\t\t" + NL + "\t\t" + NL + "\t\tjxl.write.WritableWorkbook writeableWorkbook_";
  protected final String TEXT_14 = " = null;" + NL + "\t\tjxl.write.WritableSheet writableSheet_";
  protected final String TEXT_15 = " = null;" + NL + "\t\t" + NL + "\t\tjxl.WorkbookSettings workbookSettings_";
  protected final String TEXT_16 = " = new jxl.WorkbookSettings();" + NL + "        workbookSettings_";
  protected final String TEXT_17 = ".setEncoding(";
  protected final String TEXT_18 = ");";
  protected final String TEXT_19 = NL + "\t\twriteableWorkbook_";
  protected final String TEXT_20 = " = new jxl.write.biff.WritableWorkbookImpl(" + NL + "            \t\tnew java.io.BufferedOutputStream(";
  protected final String TEXT_21 = "), " + NL + "            \t\tfalse, workbookSettings_";
  protected final String TEXT_22 = ");";
  protected final String TEXT_23 = "  " + NL + "        if (file_";
  protected final String TEXT_24 = ".exists()) {" + NL + "        jxl.Workbook workbook_";
  protected final String TEXT_25 = " = jxl.Workbook.getWorkbook(file_";
  protected final String TEXT_26 = ",workbookSettings_";
  protected final String TEXT_27 = ");" + NL + "        writeableWorkbook_";
  protected final String TEXT_28 = " = new jxl.write.biff.WritableWorkbookImpl(" + NL + "                \tnew java.io.BufferedOutputStream(new java.io.FileOutputStream(file_";
  protected final String TEXT_29 = ", false)), " + NL + "                \tworkbook_";
  protected final String TEXT_30 = ", " + NL + "                \ttrue," + NL + "                    workbookSettings_";
  protected final String TEXT_31 = ");" + NL + "        }else{        " + NL + "\t\twriteableWorkbook_";
  protected final String TEXT_32 = " = new jxl.write.biff.WritableWorkbookImpl(" + NL + "            \t\tnew java.io.BufferedOutputStream(new java.io.FileOutputStream(";
  protected final String TEXT_33 = ")), " + NL + "            \t\ttrue, " + NL + "            \t\tworkbookSettings_";
  protected final String TEXT_34 = ");        " + NL + "        }     ";
  protected final String TEXT_35 = NL + "\t\twriteableWorkbook_";
  protected final String TEXT_36 = " = new jxl.write.biff.WritableWorkbookImpl(" + NL + "            \t\tnew java.io.BufferedOutputStream(new java.io.FileOutputStream(";
  protected final String TEXT_37 = ")), " + NL + "            \t\ttrue, " + NL + "            \t\tworkbookSettings_";
  protected final String TEXT_38 = ");";
  protected final String TEXT_39 = "       " + NL + "" + NL + "        writableSheet_";
  protected final String TEXT_40 = " = writeableWorkbook_";
  protected final String TEXT_41 = ".getSheet(";
  protected final String TEXT_42 = ");" + NL + "        if(writableSheet_";
  protected final String TEXT_43 = " == null){" + NL + "        \twritableSheet_";
  protected final String TEXT_44 = " = writeableWorkbook_";
  protected final String TEXT_45 = ".createSheet(";
  protected final String TEXT_46 = ", writeableWorkbook_";
  protected final String TEXT_47 = ".getNumberOfSheets());" + NL + "\t\t}" + NL + "\t\t";
  protected final String TEXT_48 = NL + "        else {" + NL + "" + NL + "            String[] sheetNames_";
  protected final String TEXT_49 = " = writeableWorkbook_";
  protected final String TEXT_50 = ".getSheetNames();" + NL + "            for (int i = 0; i < sheetNames_";
  protected final String TEXT_51 = ".length; i++) {" + NL + "                if (sheetNames_";
  protected final String TEXT_52 = "[i].equals(";
  protected final String TEXT_53 = ")) {" + NL + "                    writeableWorkbook_";
  protected final String TEXT_54 = ".removeSheet(i);" + NL + "                    break;" + NL + "                }" + NL + "            }" + NL + "" + NL + "\t\t\twritableSheet_";
  protected final String TEXT_55 = " = writeableWorkbook_";
  protected final String TEXT_56 = ".createSheet(";
  protected final String TEXT_57 = ", writeableWorkbook_";
  protected final String TEXT_58 = ".getNumberOfSheets());" + NL + "        }";
  protected final String TEXT_59 = NL + NL + "        //modif start";
  protected final String TEXT_60 = NL + "\t\tint startRowNum_";
  protected final String TEXT_61 = " = ";
  protected final String TEXT_62 = ";";
  protected final String TEXT_63 = NL + "        int startRowNum_";
  protected final String TEXT_64 = " = writableSheet_";
  protected final String TEXT_65 = ".getRows();";
  protected final String TEXT_66 = NL + "\t\t//modif end" + NL + "\t\t" + NL + "\t\tint[] fitWidth_";
  protected final String TEXT_67 = " = new int[";
  protected final String TEXT_68 = "];" + NL + "\t\tjava.util.Arrays.fill(fitWidth_";
  protected final String TEXT_69 = ",2);" + NL + "\t\t";
  protected final String TEXT_70 = NL + "\t\t" + NL + "\t\tjxl.write.WritableFont wf_";
  protected final String TEXT_71 = " = new jxl.write.WritableFont(jxl.write.WritableFont.";
  protected final String TEXT_72 = ", 10, jxl.write.WritableFont.NO_BOLD, false);" + NL + "        jxl.write.WritableCellFormat format_";
  protected final String TEXT_73 = "  = new jxl.write.WritableCellFormat(wf_";
  protected final String TEXT_74 = "); ";
  protected final String TEXT_75 = NL;
  protected final String TEXT_76 = NL + "    \t\t\t\t\tfinal jxl.write.WritableCellFormat cell_format_";
  protected final String TEXT_77 = "_";
  protected final String TEXT_78 = "=new jxl.write.WritableCellFormat(wf_";
  protected final String TEXT_79 = " ,new jxl.write.DateFormat(";
  protected final String TEXT_80 = "));";
  protected final String TEXT_81 = "\t\t\t\t\t" + NL + "\t\t\t\t\t\tfinal jxl.write.WritableCellFormat cell_format_";
  protected final String TEXT_82 = "_";
  protected final String TEXT_83 = "=new jxl.write.WritableCellFormat(new jxl.write.DateFormat(";
  protected final String TEXT_84 = "));";
  protected final String TEXT_85 = NL + "\t\tif(true){" + NL + "\t\t\tthrow new RuntimeException(\"Date pattern must be set for column ";
  protected final String TEXT_86 = " in the schema of component ";
  protected final String TEXT_87 = "!\");" + NL + "\t\t}";
  protected final String TEXT_88 = "\t\t" + NL + NL;
  protected final String TEXT_89 = NL + "\tboolean needDel_";
  protected final String TEXT_90 = " = false;";
  protected final String TEXT_91 = NL + "\t\tif (startRowNum_";
  protected final String TEXT_92 = " == ";
  protected final String TEXT_93 = "){";
  protected final String TEXT_94 = NL + "\t\tif (startRowNum_";
  protected final String TEXT_95 = " == 0){";
  protected final String TEXT_96 = NL + "\t//modif end";
  protected final String TEXT_97 = NL + "\t\t//modif start";
  protected final String TEXT_98 = NL + "\t\t\twritableSheet_";
  protected final String TEXT_99 = ".addCell(new jxl.write.Label(";
  protected final String TEXT_100 = " + ";
  protected final String TEXT_101 = ", startRowNum_";
  protected final String TEXT_102 = ", \"";
  protected final String TEXT_103 = "\"";
  protected final String TEXT_104 = NL + "\t\t\t\t\t,format_";
  protected final String TEXT_105 = NL + "\t\t\t));\t\t";
  protected final String TEXT_106 = NL + "\t\t\twritableSheet_";
  protected final String TEXT_107 = ".addCell(new jxl.write.Label(";
  protected final String TEXT_108 = ", nb_line_";
  protected final String TEXT_109 = ", \"";
  protected final String TEXT_110 = "\"";
  protected final String TEXT_111 = NL + "\t\t\t\t\t,format_";
  protected final String TEXT_112 = NL + "\t\t\t));";
  protected final String TEXT_113 = NL + "\t\t//modif end" + NL + "\t\tfitWidth_";
  protected final String TEXT_114 = "[";
  protected final String TEXT_115 = "]=fitWidth_";
  protected final String TEXT_116 = "[";
  protected final String TEXT_117 = "]>";
  protected final String TEXT_118 = "?fitWidth_";
  protected final String TEXT_119 = "[";
  protected final String TEXT_120 = "]:";
  protected final String TEXT_121 = ";";
  protected final String TEXT_122 = NL + "\t\tneedDel_";
  protected final String TEXT_123 = " = true;";
  protected final String TEXT_124 = NL + "\t\tnb_line_";
  protected final String TEXT_125 = "++;" + NL + "\t}";
  protected final String TEXT_126 = NL + "\t\t";
  protected final String TEXT_127 = "\t" + NL + "\t\tint nb_line_";
  protected final String TEXT_128 = " = 0;" + NL + "\t\torg.talend.ExcelTool xlsxTool_";
  protected final String TEXT_129 = " = new org.talend.ExcelTool();" + NL + "\t\txlsxTool_";
  protected final String TEXT_130 = ".setSheet(";
  protected final String TEXT_131 = ");" + NL + "\t\txlsxTool_";
  protected final String TEXT_132 = ".setAppend(";
  protected final String TEXT_133 = ",";
  protected final String TEXT_134 = ");" + NL + "\t\txlsxTool_";
  protected final String TEXT_135 = ".setXY(";
  protected final String TEXT_136 = ",";
  protected final String TEXT_137 = ",";
  protected final String TEXT_138 = ",";
  protected final String TEXT_139 = ");" + NL + "\t\t";
  protected final String TEXT_140 = NL + "\t\txlsxTool_";
  protected final String TEXT_141 = ".prepareXlsxFile(";
  protected final String TEXT_142 = ");" + NL + "\t\t";
  protected final String TEXT_143 = NL + "\t\txlsxTool_";
  protected final String TEXT_144 = ".prepareStream();" + NL + "\t\t";
  protected final String TEXT_145 = NL + "\t\txlsxTool_";
  protected final String TEXT_146 = ".setFont(\"";
  protected final String TEXT_147 = "\");" + NL + "\t\t";
  protected final String TEXT_148 = NL + "\t\tboolean needDel_";
  protected final String TEXT_149 = " = false;" + NL + "\t\t";
  protected final String TEXT_150 = NL + "\t\txlsxTool_";
  protected final String TEXT_151 = ".addRow();" + NL + "\t\t";
  protected final String TEXT_152 = NL + "\t\txlsxTool_";
  protected final String TEXT_153 = ".addCellValue(\"";
  protected final String TEXT_154 = "\");" + NL + "\t\t";
  protected final String TEXT_155 = NL + "\t\tneedDel_";
  protected final String TEXT_156 = " = true;" + NL + "\t\t";
  protected final String TEXT_157 = NL + "\t\tnb_line_";
  protected final String TEXT_158 = "++;" + NL + "\t\t";
  protected final String TEXT_159 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
boolean version07 = ("true").equals(ElementParameterParser.getValue(node,"__VERSION_2007__"));
	
boolean useStream = ("true").equals(ElementParameterParser.getValue(node,"__USESTREAM__"));
String outStream = ElementParameterParser.getValue(node,"__STREAMNAME__");

String filename = ElementParameterParser.getValue(node, "__FILENAME__");
String sheetname = ElementParameterParser.getValue(node, "__SHEETNAME__");
boolean firstCellYAbsolute = ("true").equals(ElementParameterParser.getValue(node, "__FIRST_CELL_Y_ABSOLUTE__"));
String firstCellXStr = ElementParameterParser.getValue(node, "__FIRST_CELL_X__");
String firstCellYStr = ElementParameterParser.getValue(node, "__FIRST_CELL_Y__");
boolean keepCellFormating = ("true").equals(ElementParameterParser.getValue(node, "__KEEP_CELL_FORMATING__"));
String font = ElementParameterParser.getValue(node, "__FONT__");
boolean isDeleteEmptyFile = ElementParameterParser.getValue(node, "__DELETE_EMPTYFILE__").equals("true");
boolean isIncludeHeader = ("true").equals(ElementParameterParser.getValue(node, "__INCLUDEHEADER__"));
boolean isAppendFile = ("true").equals(ElementParameterParser.getValue(node, "__APPEND_FILE__" ));
boolean isAppendSheet = ("true").equals(ElementParameterParser.getValue(node, "__APPEND_SHEET__" ));

List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null) {
    	List<IMetadataColumn> columns = metadata.getListColumns();
    	if(!version07){//version judgement

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    
		if(!useStream){ // the part of the file path

    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_6);
    
			if(("true").equals(ElementParameterParser.getValue(node,"__CREATE__"))){

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
    
			}
		}

    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_16);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(ElementParameterParser.getValue(node,"__ENCODING__") );
    stringBuffer.append(TEXT_18);
    
		if(useStream){ // the part of the output stream support

    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(outStream );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    
		}else{
			if(isAppendFile){

    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(filename);
    stringBuffer.append(TEXT_33);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_34);
    
			} else {

    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(filename);
    stringBuffer.append(TEXT_37);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_38);
    
			}
		}

    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(sheetname );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_45);
    stringBuffer.append(sheetname );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_47);
    if(!useStream && isAppendFile && !isAppendSheet){
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_52);
    stringBuffer.append(sheetname );
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_56);
    stringBuffer.append(sheetname );
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_58);
    }
    stringBuffer.append(TEXT_59);
    if(firstCellYAbsolute){
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_61);
    stringBuffer.append(firstCellYStr);
    stringBuffer.append(TEXT_62);
    }else{
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_65);
    }
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_67);
    stringBuffer.append(columns.size());
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_69);
    if(font !=null && font.length()!=0){
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_71);
    stringBuffer.append(font);
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_73);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_74);
    }
    stringBuffer.append(TEXT_75);
    
    	for (int i = 0; i < columns.size(); i++) {
    		IMetadataColumn column = columns.get(i);
    		JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
    		if (javaType == JavaTypesManager.DATE){
    			String pattern = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
    			if(pattern != null && pattern.trim().length() != 0){
    				if(font !=null && font.length()!=0){

    stringBuffer.append(TEXT_76);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_77);
    stringBuffer.append( cid);
    stringBuffer.append(TEXT_78);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_79);
    stringBuffer.append(pattern );
    stringBuffer.append(TEXT_80);
    
					}else{

    stringBuffer.append(TEXT_81);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_82);
    stringBuffer.append( cid);
    stringBuffer.append(TEXT_83);
    stringBuffer.append(pattern );
    stringBuffer.append(TEXT_84);
    	
					}
				}else{

    stringBuffer.append(TEXT_85);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_86);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_87);
    				}
			}
		
	    }

    stringBuffer.append(TEXT_88);
    
if(isIncludeHeader){
	if(isDeleteEmptyFile){

    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_90);
    
	}
	if(firstCellYAbsolute){

    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_92);
    stringBuffer.append(firstCellYStr);
    stringBuffer.append(TEXT_93);
    
	}else{

    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_95);
    
	}

    stringBuffer.append(TEXT_96);
    
	for (int i = 0; i < columns.size(); i++) {
		IMetadataColumn column = columns.get(i);

    stringBuffer.append(TEXT_97);
    
		if(firstCellYAbsolute){

    stringBuffer.append(TEXT_98);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_99);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_100);
    stringBuffer.append(firstCellXStr);
    stringBuffer.append(TEXT_101);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_102);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_103);
    
				if (font !=null && font.length()!=0) {

    stringBuffer.append(TEXT_104);
    stringBuffer.append(cid );
    
				}

    stringBuffer.append(TEXT_105);
    
		}else{

    stringBuffer.append(TEXT_106);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_107);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_108);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_109);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_110);
    
				if (font !=null && font.length()!=0) {

    stringBuffer.append(TEXT_111);
    stringBuffer.append(cid );
    
				}

    stringBuffer.append(TEXT_112);
    
		}

    stringBuffer.append(TEXT_113);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_114);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_115);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_116);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_117);
    stringBuffer.append(column.getLabel().length());
    stringBuffer.append(TEXT_118);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_119);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_120);
    stringBuffer.append(column.getLabel().length());
    stringBuffer.append(TEXT_121);
    
	}
		if(isDeleteEmptyFile){

    stringBuffer.append(TEXT_122);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_123);
    		}
    stringBuffer.append(TEXT_124);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_125);
    
}

    stringBuffer.append(TEXT_126);
    	
		}else{ //version judgement /***excel 2007 xlsx*****/

    stringBuffer.append(TEXT_127);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_128);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_129);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_130);
    stringBuffer.append(sheetname);
    stringBuffer.append(TEXT_131);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_132);
    stringBuffer.append(isAppendFile);
    stringBuffer.append(TEXT_133);
    stringBuffer.append(isAppendSheet);
    stringBuffer.append(TEXT_134);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_135);
    stringBuffer.append(firstCellYAbsolute);
    stringBuffer.append(TEXT_136);
    stringBuffer.append(firstCellXStr);
    stringBuffer.append(TEXT_137);
    stringBuffer.append(firstCellYStr);
    stringBuffer.append(TEXT_138);
    stringBuffer.append(keepCellFormating);
    stringBuffer.append(TEXT_139);
    if(!useStream){
    stringBuffer.append(TEXT_140);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_141);
    stringBuffer.append(filename);
    stringBuffer.append(TEXT_142);
    }else{
    stringBuffer.append(TEXT_143);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_144);
    }
    stringBuffer.append(TEXT_145);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_146);
    stringBuffer.append(font);
    stringBuffer.append(TEXT_147);
    
		if(isIncludeHeader){
			if(isDeleteEmptyFile){
		
    stringBuffer.append(TEXT_148);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_149);
    
			}
		
    stringBuffer.append(TEXT_150);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_151);
    
			for (int i = 0; i < columns.size(); i++) {
				IMetadataColumn column = columns.get(i);
		
    stringBuffer.append(TEXT_152);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_153);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_154);
    
			}
			if(isDeleteEmptyFile){
		
    stringBuffer.append(TEXT_155);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_156);
    		
			}
		
    stringBuffer.append(TEXT_157);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_158);
    	
		}	
		
    
		}
    }
}

    stringBuffer.append(TEXT_159);
    return stringBuffer.toString();
  }
}
