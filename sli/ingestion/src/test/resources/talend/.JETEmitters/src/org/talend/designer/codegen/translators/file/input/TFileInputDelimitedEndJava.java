package org.talend.designer.codegen.translators.file.input;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import java.util.List;

public class TFileInputDelimitedEndJava
{
  protected static String nl;
  public static synchronized TFileInputDelimitedEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileInputDelimitedEndJava result = new TFileInputDelimitedEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL;
  protected final String TEXT_3 = NL + "            }" + NL + "            nb_line_";
  protected final String TEXT_4 = "+=fid_";
  protected final String TEXT_5 = ".getRowNumber();" + NL + "\t\t}" + NL + "\t\t}finally{" + NL + "            if(!((Object)(";
  protected final String TEXT_6 = ") instanceof java.io.InputStream)){" + NL + "            \tif(fid_";
  protected final String TEXT_7 = "!=null){" + NL + "            \t\tfid_";
  protected final String TEXT_8 = ".close();" + NL + "            \t}" + NL + "            }" + NL + "            if(fid_";
  protected final String TEXT_9 = "!=null){" + NL + "            \tglobalMap.put(\"";
  protected final String TEXT_10 = "_NB_LINE\", nb_line_";
  protected final String TEXT_11 = ");" + NL + "            }" + NL + "        }";
  protected final String TEXT_12 = NL + "            }" + NL + "            }finally{" + NL + "                if(!((Object)(";
  protected final String TEXT_13 = ") instanceof java.io.InputStream)){" + NL + "                \tif(fid_";
  protected final String TEXT_14 = "!=null){" + NL + "                \t\tfid_";
  protected final String TEXT_15 = ".close();" + NL + "                \t}" + NL + "                }" + NL + "                if(fid_";
  protected final String TEXT_16 = "!=null){" + NL + "                \tglobalMap.put(\"";
  protected final String TEXT_17 = "_NB_LINE\", fid_";
  protected final String TEXT_18 = ".getRowNumber());" + NL + "                }" + NL + "\t\t\t}";
  protected final String TEXT_19 = NL;
  protected final String TEXT_20 = NL;
  protected final String TEXT_21 = NL + "\t\t\t\tnb_line_";
  protected final String TEXT_22 = "++;" + NL + "\t\t\t}" + NL + "\t\t\t";
  protected final String TEXT_23 = "}";
  protected final String TEXT_24 = NL + "\t\t\t}finally{" + NL + "    \t\t\tif(!(filename_";
  protected final String TEXT_25 = " instanceof java.io.InputStream)){" + NL + "    \t\t\t\tif(csvReader";
  protected final String TEXT_26 = "!=null){" + NL + "    \t\t\t\t\tcsvReader";
  protected final String TEXT_27 = ".close();" + NL + "    \t\t\t\t}" + NL + "    \t\t\t}" + NL + "    \t\t\tif(csvReader";
  protected final String TEXT_28 = "!=null){" + NL + "    \t\t\t\tglobalMap.put(\"";
  protected final String TEXT_29 = "_NB_LINE\",nb_line_";
  protected final String TEXT_30 = ");" + NL + "    \t\t\t}" + NL + "\t\t\t}";
  protected final String TEXT_31 = NL + "\t\t\t";
  protected final String TEXT_32 = "\t\t\t  ";
  protected final String TEXT_33 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
     
    CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
    INode node = (INode)codeGenArgument.getArgument();
    String cid = node.getUniqueName();
	
	String vcid = "";
	
	String projectName = codeGenArgument.getCurrentProjectName();
	String jobName = codeGenArgument.getJobName();
	String jobVersion = codeGenArgument.getJobVersion();
	
	String tempDir = ElementParameterParser.getValue(node, "__TEMP_DIR__");

	String destination = ElementParameterParser.getValue(node, "__DESTINATION__");
	if(destination!=null && !"".equals(destination)){
		vcid = destination;
	}
	
	if(("false").equals(ElementParameterParser.getValue(node,"__CSV_OPTION__"))) {	
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    stringBuffer.append(TEXT_2);
    
	List<IMetadataTable> metadatas = node.getMetadataList();
	if ((metadatas!=null)&&(metadatas.size()>0)) {
		IMetadataTable metadata = metadatas.get(0);
		if (metadata!=null) {
			String filename = ElementParameterParser.getValue(node,"__FILENAME__");
			if(!("".equals(vcid))) {
				filename = "\"/"+filename.substring(1, filename.length()-1)+vcid+"_"+projectName+"_"+jobName+"_"+jobVersion+"\"";
				filename = tempDir+"+"+filename;
			}
			
    		boolean uncompress = ("true").equals(ElementParameterParser.getValue(node,"__UNCOMPRESS__"));

			if(uncompress){

    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    
			}else{

    stringBuffer.append(TEXT_12);
    stringBuffer.append(filename );
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
    
			}
		}
	}

    stringBuffer.append(TEXT_19);
    
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}else{//the following is the tFileInputCSV component
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    stringBuffer.append(TEXT_20);
    
	List<IMetadataTable> metadatas = node.getMetadataList();
	if ((metadatas!=null)&&(metadatas.size()>0)) {
		IMetadataTable metadata = metadatas.get(0);
		if (metadata!=null) {
    		boolean uncompress = ("true").equals(ElementParameterParser.getValue(node,"__UNCOMPRESS__"));


    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    if(uncompress){//compress
    stringBuffer.append(TEXT_23);
    }
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
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_30);
    
		}
	}

    stringBuffer.append(TEXT_31);
    
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  }

    stringBuffer.append(TEXT_32);
    stringBuffer.append(TEXT_33);
    return stringBuffer.toString();
  }
}
