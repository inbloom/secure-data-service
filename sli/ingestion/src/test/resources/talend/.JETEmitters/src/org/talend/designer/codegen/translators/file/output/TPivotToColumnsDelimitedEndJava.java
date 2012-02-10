package org.talend.designer.codegen.translators.file.output;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import java.util.Map;

public class TPivotToColumnsDelimitedEndJava
{
  protected static String nl;
  public static synchronized TPivotToColumnsDelimitedEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TPivotToColumnsDelimitedEndJava result = new TPivotToColumnsDelimitedEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL;
  protected final String TEXT_3 = NL + "  ////////////////////////////////  Delimited  /////////  ";
  protected final String TEXT_4 = NL + "    out";
  protected final String TEXT_5 = ".write(\"";
  protected final String TEXT_6 = "\");" + NL + "    " + NL + "    out";
  protected final String TEXT_7 = ".write(";
  protected final String TEXT_8 = ");";
  protected final String TEXT_9 = NL + "    " + NL + "    out";
  protected final String TEXT_10 = ".write(pivot_Keys";
  protected final String TEXT_11 = ".substring(0,pivot_Keys";
  protected final String TEXT_12 = ".length()<=0?0:pivot_Keys";
  protected final String TEXT_13 = ".length()-1));" + NL + "    " + NL + "    out";
  protected final String TEXT_14 = ".write(";
  protected final String TEXT_15 = ");" + NL + "" + NL + "    for(int i = 0;group_Keys_Split";
  protected final String TEXT_16 = "!=null && i<group_Keys_Split";
  protected final String TEXT_17 = ".length;i++){" + NL + "        " + NL + "        out";
  protected final String TEXT_18 = ".write(group_Keys_Split";
  protected final String TEXT_19 = "[i]);" + NL + "        " + NL + "        out";
  protected final String TEXT_20 = ".write(";
  protected final String TEXT_21 = ");" + NL + "        " + NL + "        String aggOut";
  protected final String TEXT_22 = " = (String)aggregation";
  protected final String TEXT_23 = ".get(group_Keys_Split";
  protected final String TEXT_24 = "[i]);" + NL + "" + NL + "        int gap";
  protected final String TEXT_25 = " = routines.system.StringUtils.splitNotRegex(aggOut";
  protected final String TEXT_26 = ",";
  protected final String TEXT_27 = ").length - routines.system.StringUtils.splitNotRegex(pivot_Keys";
  protected final String TEXT_28 = ",";
  protected final String TEXT_29 = ").length;" + NL + "        " + NL + "        if(gap";
  protected final String TEXT_30 = " < 0){" + NL + "        " + NL + "        \tfor(int k=0;k<-1-gap";
  protected final String TEXT_31 = ";k++)" + NL + "        \t   \t" + NL + "        \t\taggOut";
  protected final String TEXT_32 = " = aggOut";
  protected final String TEXT_33 = " + ";
  protected final String TEXT_34 = ";" + NL + "\t" + NL + "        }else if(gap";
  protected final String TEXT_35 = " == 0){" + NL + "        " + NL + "        \taggOut";
  protected final String TEXT_36 = " = aggOut";
  protected final String TEXT_37 = ".substring(0,aggOut";
  protected final String TEXT_38 = ".lastIndexOf(";
  protected final String TEXT_39 = "));" + NL + "        " + NL + "        }" + NL + "" + NL + "        out";
  protected final String TEXT_40 = ".write(aggOut";
  protected final String TEXT_41 = ");" + NL + "        " + NL + "        out";
  protected final String TEXT_42 = ".write(";
  protected final String TEXT_43 = ");" + NL + "" + NL + "    }" + NL + "" + NL + "    out";
  protected final String TEXT_44 = ".flush();" + NL + "    " + NL + "    out";
  protected final String TEXT_45 = ".close();" + NL + "    " + NL;
  protected final String TEXT_46 = "  ////////////////////////////////   CSV    /////////    " + NL + "" + NL + "    int rowSize";
  protected final String TEXT_47 = " = routines.system.StringUtils.splitNotRegex(pivot_Keys";
  protected final String TEXT_48 = ",";
  protected final String TEXT_49 = ").length + ";
  protected final String TEXT_50 = ";" + NL + "    " + NL + "    String[] finalStr";
  protected final String TEXT_51 = " = new String[rowSize";
  protected final String TEXT_52 = "];" + NL;
  protected final String TEXT_53 = NL + "    " + NL + "    finalStr";
  protected final String TEXT_54 = "[";
  protected final String TEXT_55 = "] = \"";
  protected final String TEXT_56 = "\";" + NL;
  protected final String TEXT_57 = NL + NL + "    for (int i=0; pivot_Keys_Split";
  protected final String TEXT_58 = "!=null && i<pivot_Keys_Split";
  protected final String TEXT_59 = ".length; i++) {" + NL + "" + NL + "        finalStr";
  protected final String TEXT_60 = "[i+";
  protected final String TEXT_61 = "] = pivot_Keys_Split";
  protected final String TEXT_62 = "[i];" + NL + "" + NL + "   }  " + NL + "    " + NL + "    " + NL + "    CsvWriter";
  protected final String TEXT_63 = ".writeRecord(finalStr";
  protected final String TEXT_64 = ");" + NL + "    " + NL + "    CsvWriter";
  protected final String TEXT_65 = ".flush();" + NL + "" + NL + "    for(int i = 0;group_Keys_Split";
  protected final String TEXT_66 = "!=null && i<group_Keys_Split";
  protected final String TEXT_67 = ".length;i++){" + NL + "        " + NL + "        finalStr";
  protected final String TEXT_68 = " = new String[rowSize";
  protected final String TEXT_69 = "];" + NL + "" + NL + "        String[] gkSplit";
  protected final String TEXT_70 = " = routines.system.StringUtils.splitNotRegex(group_Keys_Split";
  protected final String TEXT_71 = "[i],";
  protected final String TEXT_72 = ");" + NL + "        ";
  protected final String TEXT_73 = NL + "        " + NL + "        finalStr";
  protected final String TEXT_74 = "[";
  protected final String TEXT_75 = "] = gkSplit";
  protected final String TEXT_76 = "[";
  protected final String TEXT_77 = "];" + NL;
  protected final String TEXT_78 = NL + "        " + NL + "        String aggOut";
  protected final String TEXT_79 = " = (String)aggregation";
  protected final String TEXT_80 = ".get(group_Keys_Split";
  protected final String TEXT_81 = "[i]);" + NL + "" + NL + "        String[] aggSplit";
  protected final String TEXT_82 = " = routines.system.StringUtils.splitNotRegex(aggOut";
  protected final String TEXT_83 = ",";
  protected final String TEXT_84 = ");    " + NL + "        " + NL + "  for (int j=0; j<aggSplit";
  protected final String TEXT_85 = ".length; j++) {" + NL + "" + NL + "        finalStr";
  protected final String TEXT_86 = "[j+";
  protected final String TEXT_87 = "] =  aggSplit";
  protected final String TEXT_88 = "[j];" + NL + "" + NL + "  } " + NL + "        " + NL + "        CsvWriter";
  protected final String TEXT_89 = ".writeRecord(finalStr";
  protected final String TEXT_90 = ");" + NL + "        " + NL + "        CsvWriter";
  protected final String TEXT_91 = ".flush();" + NL + "" + NL + "    }" + NL + "    " + NL + "    \tCsvWriter";
  protected final String TEXT_92 = ".close();" + NL;
  protected final String TEXT_93 = NL + NL + "globalMap.put(\"";
  protected final String TEXT_94 = "_NB_LINE\",nb_line_";
  protected final String TEXT_95 = ");" + NL;
  protected final String TEXT_96 = NL + "\tif(nb_line_";
  protected final String TEXT_97 = " == 0){" + NL + "\t\tnew java.io.File(";
  protected final String TEXT_98 = ").delete();" + NL + "\t}\t\t";
  protected final String TEXT_99 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

String filename = ElementParameterParser.getValue(node,"__FILENAME__");
boolean isDeleteEmptyFile = ("true").equals(ElementParameterParser.getValue(node, "__DELETE_EMPTYFILE__"));
	
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null) {
        
        List<Map<String, String>> groupbys = 
            ( List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__GROUPBYS__");

        boolean csvOption = ("true").equals(ElementParameterParser.getValue(node,"__CSV_OPTION__"));
        
        String pivotColumn = ElementParameterParser.getValue(node, "__PIVOT_COLUMN__");

        String aggColumn = ElementParameterParser.getValue(node, "__AGGREGATION_COLUMN__");
        
        String aggFunction = ElementParameterParser.getValue(node, "__AGGREGATION_FUNCTION__");
        
        String rowSeparator = ElementParameterParser.getValue(node,"__ROWSEPARATOR__");
        
        String fieldSeparator = ElementParameterParser.getValue(node, "__FIELDSEPARATOR__");
        

    stringBuffer.append(TEXT_2);
     if(("false").equals(ElementParameterParser.getValue(node,"__CSV_OPTION__"))) { 
    stringBuffer.append(TEXT_3);
      for (int i=0; i<groupbys.size(); i++) {
        
        Map<String, String> groupby = groupbys.get(i);

    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(groupby.get("INPUT_COLUMN"));
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(fieldSeparator);
    stringBuffer.append(TEXT_8);
    
       }  

    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(rowSeparator);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(fieldSeparator);
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
    stringBuffer.append(fieldSeparator);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(fieldSeparator);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(fieldSeparator);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(fieldSeparator);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(rowSeparator);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_45);
    }else{
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(fieldSeparator);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(groupbys.size());
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_52);
      for (int i=0; i<groupbys.size(); i++) {
        
        Map<String, String> groupby = groupbys.get(i);

    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_54);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_55);
    stringBuffer.append(groupby.get("INPUT_COLUMN"));
    stringBuffer.append(TEXT_56);
    
       }  

    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_60);
    stringBuffer.append(groupbys.size());
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_71);
    stringBuffer.append(fieldSeparator);
    stringBuffer.append(TEXT_72);
      for (int i=0; i<groupbys.size(); i++) {
            
        Map<String, String> groupby = groupbys.get(i);

    stringBuffer.append(TEXT_73);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_74);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_76);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_77);
      }  
    stringBuffer.append(TEXT_78);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_82);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_83);
    stringBuffer.append(fieldSeparator);
    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_86);
    stringBuffer.append(groupbys.size());
    stringBuffer.append(TEXT_87);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_92);
    
        }
    }
}

    stringBuffer.append(TEXT_93);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_95);
    if(isDeleteEmptyFile){
    stringBuffer.append(TEXT_96);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_97);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_98);
    }
    stringBuffer.append(TEXT_99);
    return stringBuffer.toString();
  }
}
