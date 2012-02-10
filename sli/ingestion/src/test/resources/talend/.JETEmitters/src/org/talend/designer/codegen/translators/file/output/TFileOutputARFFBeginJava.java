package org.talend.designer.codegen.translators.file.output;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.EConnectionType;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.Map;

public class TFileOutputARFFBeginJava
{
  protected static String nl;
  public static synchronized TFileOutputARFFBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileOutputARFFBeginJava result = new TFileOutputARFFBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "        " + NL + " int nb_line_";
  protected final String TEXT_2 = " = 0;" + NL + " int splitedFileNo_";
  protected final String TEXT_3 = " =0;" + NL + " int currentRow_";
  protected final String TEXT_4 = " = 0;" + NL + " double vals";
  protected final String TEXT_5 = "[];" + NL + " String fileNewName_";
  protected final String TEXT_6 = " = ";
  protected final String TEXT_7 = ";" + NL + " java.io.File createFile";
  protected final String TEXT_8 = " = new java.io.File(fileNewName_";
  protected final String TEXT_9 = ");";
  protected final String TEXT_10 = NL + "\t \t//create directory only if not exists" + NL + "" + NL + "\t\tjava.io.File parentFile_";
  protected final String TEXT_11 = " = createFile";
  protected final String TEXT_12 = ".getParentFile();" + NL + "\t\tif(parentFile_";
  protected final String TEXT_13 = " != null && !parentFile_";
  protected final String TEXT_14 = ".exists()) {" + NL + "\t\t\tparentFile_";
  protected final String TEXT_15 = ".mkdirs();" + NL + "\t\t}";
  protected final String TEXT_16 = NL + "       //initialize nominal array" + NL + "      weka.core.FastVector classValues";
  protected final String TEXT_17 = ";" + NL + "      String nom";
  protected final String TEXT_18 = "[] = ";
  protected final String TEXT_19 = ".split(\",\");" + NL + "      classValues";
  protected final String TEXT_20 = " = new weka.core.FastVector(nom";
  protected final String TEXT_21 = ".length);" + NL + "      for(int j=0;j<nom";
  protected final String TEXT_22 = ".length;j++){" + NL + "          classValues";
  protected final String TEXT_23 = ".addElement(nom";
  protected final String TEXT_24 = "[j]);" + NL + "      }";
  protected final String TEXT_25 = NL + "   \t\tString[] headColu";
  protected final String TEXT_26 = "=new String[";
  protected final String TEXT_27 = "];\t" + NL + "   \t\tcreateFile";
  protected final String TEXT_28 = ".createNewFile();" + NL + "   \t\tweka.core.Instances data";
  protected final String TEXT_29 = ";" + NL + "   \t\tweka.core.Instances m_Data";
  protected final String TEXT_30 = ";" + NL + "   \t\t";
  protected final String TEXT_31 = NL + "   \t\t try{" + NL + "             //read existing Instances" + NL + "   \t\t    m_Data";
  protected final String TEXT_32 = " = new weka.core.Instances(new java.io.BufferedReader(new java.io.FileReader(fileNewName_";
  protected final String TEXT_33 = ")));" + NL + "         }catch(Exception e){" + NL + "               // Create vector of attributes." + NL + "               int numAtts";
  protected final String TEXT_34 = "=";
  protected final String TEXT_35 = ";" + NL + "               weka.core.FastVector attributes";
  protected final String TEXT_36 = " = new weka.core.FastVector(numAtts";
  protected final String TEXT_37 = ");";
  protected final String TEXT_38 = NL + "                      //A String attribute" + NL + "                      attributes";
  protected final String TEXT_39 = ".addElement(new weka.core.Attribute(\"";
  protected final String TEXT_40 = "\", (weka.core.FastVector) null));";
  protected final String TEXT_41 = NL + "                      //A Numeric attribute" + NL + "                      attributes";
  protected final String TEXT_42 = ".addElement(new weka.core.Attribute(\"";
  protected final String TEXT_43 = "\"));";
  protected final String TEXT_44 = NL + "                      //A Date attribute" + NL + "                      attributes";
  protected final String TEXT_45 = ".addElement(new weka.core.Attribute(\"";
  protected final String TEXT_46 = "\", ";
  protected final String TEXT_47 = "));";
  protected final String TEXT_48 = NL + "                      //A Nominal attribute" + NL + "                      attributes";
  protected final String TEXT_49 = ".addElement(new weka.core.Attribute(\"";
  protected final String TEXT_50 = "\", classValues";
  protected final String TEXT_51 = "));";
  protected final String TEXT_52 = NL + "            //Create a new Instances" + NL + "             m_Data";
  protected final String TEXT_53 = " = new weka.core.Instances(";
  protected final String TEXT_54 = ", attributes";
  protected final String TEXT_55 = ", 100);" + NL + "             m_Data";
  protected final String TEXT_56 = ".setClassIndex(m_Data";
  protected final String TEXT_57 = ".numAttributes() - 1);" + NL + "        }";
  protected final String TEXT_58 = NL + "        int numAtts";
  protected final String TEXT_59 = "=";
  protected final String TEXT_60 = ";" + NL + "        weka.core.FastVector attributes";
  protected final String TEXT_61 = " = new weka.core.FastVector(numAtts";
  protected final String TEXT_62 = ");";
  protected final String TEXT_63 = NL + "               //A String attribute" + NL + "               attributes";
  protected final String TEXT_64 = ".addElement(new weka.core.Attribute(\"";
  protected final String TEXT_65 = "\", (weka.core.FastVector) null));";
  protected final String TEXT_66 = NL + "               //A Numeric attribute" + NL + "               attributes";
  protected final String TEXT_67 = ".addElement(new weka.core.Attribute(\"";
  protected final String TEXT_68 = "\"));";
  protected final String TEXT_69 = NL + "               //A Date attribute" + NL + "               attributes";
  protected final String TEXT_70 = ".addElement(new weka.core.Attribute(\"";
  protected final String TEXT_71 = "\", ";
  protected final String TEXT_72 = "));";
  protected final String TEXT_73 = NL + "               //A Nominal attribute" + NL + "               attributes";
  protected final String TEXT_74 = ".addElement(new weka.core.Attribute(\"";
  protected final String TEXT_75 = "\", classValues";
  protected final String TEXT_76 = "));";
  protected final String TEXT_77 = NL + "            //Create a new Instances" + NL + "            m_Data";
  protected final String TEXT_78 = " = new weka.core.Instances(";
  protected final String TEXT_79 = ", attributes";
  protected final String TEXT_80 = ", 100);" + NL + "            m_Data";
  protected final String TEXT_81 = ".setClassIndex(m_Data";
  protected final String TEXT_82 = ".numAttributes() - 1);";
  protected final String TEXT_83 = NL + NL + "int oldInsNum";
  protected final String TEXT_84 = "=m_Data";
  protected final String TEXT_85 = ".numInstances();" + NL + "    " + NL;
  protected final String TEXT_86 = NL + NL + NL + NL + "   " + NL + NL + NL;
  protected final String TEXT_87 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null) {
     	String cid = node.getUniqueName();
        String filename = ElementParameterParser.getValue(node,"__FILENAME__");
	    boolean isAppend = ("true").equals(ElementParameterParser.getValue(node,"__APPEND__"));
	    List<Map<String, String>> colDef = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__COLDEFINE__");
	    String relation = ElementParameterParser.getValue(node,"__RELATION__");
 
    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(filename);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    if(("true").equals(ElementParameterParser.getValue(node,"__CREATE__"))){
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
    
	}

    List<IMetadataColumn> columns = metadata.getListColumns();
    	int sizeColumns = columns.size();
    	
    
    for(int i=0;i<colDef.size();i++){
        if(("Nominal").equals(colDef.get(i).get("TYPE"))){

    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(colDef.get(i).get("PATTERN"));
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_24);
      
        }
    }
   
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(sizeColumns);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    if(isAppend){
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(colDef.size());
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
                int attIndex=0;
               for(Map<String, String> colD:colDef){
                  if(("String").equals(colD.get("TYPE"))){
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(columns.get(attIndex).getLabel());
    stringBuffer.append(TEXT_40);
                    }else if(("Numeric").equals(colD.get("TYPE"))){
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(columns.get(attIndex).getLabel());
    stringBuffer.append(TEXT_43);
                    }else if(("Date").equals(colD.get("TYPE"))){
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(columns.get(attIndex).getLabel());
    stringBuffer.append(TEXT_46);
    stringBuffer.append(colD.get("PATTERN"));
    stringBuffer.append(TEXT_47);
                    }else if(("Nominal").equals(colD.get("TYPE"))){
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(columns.get(attIndex).getLabel());
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid);
    stringBuffer.append(attIndex);
    stringBuffer.append(TEXT_51);
                    }
                  attIndex++;
               }
 
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_53);
    stringBuffer.append(relation);
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_57);
           }else{
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_59);
    stringBuffer.append(colDef.size());
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_62);
          int attIndex=0;
        for(Map<String, String> colD:colDef){//for 1
           if(("String").equals(colD.get("TYPE"))){
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_64);
    stringBuffer.append(columns.get(attIndex).getLabel());
    stringBuffer.append(TEXT_65);
             }else if(("Numeric").equals(colD.get("TYPE"))){
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_67);
    stringBuffer.append(columns.get(attIndex).getLabel());
    stringBuffer.append(TEXT_68);
             }else if(("Date").equals(colD.get("TYPE"))){
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_70);
    stringBuffer.append(columns.get(attIndex).getLabel());
    stringBuffer.append(TEXT_71);
    stringBuffer.append(colD.get("PATTERN"));
    stringBuffer.append(TEXT_72);
             }else if(("Nominal").equals(colD.get("TYPE"))){
    stringBuffer.append(TEXT_73);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_74);
    stringBuffer.append(columns.get(attIndex).getLabel());
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid);
    stringBuffer.append(attIndex);
    stringBuffer.append(TEXT_76);
             }
           attIndex++;
        }//for 1

    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_78);
    stringBuffer.append(relation);
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_82);
      	}
    stringBuffer.append(TEXT_83);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_85);
        	}
    }

    stringBuffer.append(TEXT_86);
    stringBuffer.append(TEXT_87);
    return stringBuffer.toString();
  }
}
