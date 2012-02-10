package org.talend.designer.codegen.translators.misc;

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
import org.talend.core.model.process.IProcess;
import org.talend.core.model.process.IContextParameter;
import java.util.ArrayList;

public class TContextLoadMainJava
{
  protected static String nl;
  public static synchronized TContextLoadMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TContextLoadMainJava result = new TContextLoadMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t\t//////////////////////////" + NL + "\t\tString tmp_key_";
  protected final String TEXT_2 = " = null;";
  protected final String TEXT_3 = "    \t\t" + NL + "    \t\t    ";
  protected final String TEXT_4 = NL + "\t\t\t\t\tString ";
  protected final String TEXT_5 = "_";
  protected final String TEXT_6 = " = null;\t";
  protected final String TEXT_7 = "                " + NL + "                      if (";
  protected final String TEXT_8 = ".";
  protected final String TEXT_9 = " != null){" + NL + "                          tmp_key_";
  protected final String TEXT_10 = " = ";
  protected final String TEXT_11 = ".";
  protected final String TEXT_12 = ".trim();                        " + NL + "                        if ((tmp_key_";
  protected final String TEXT_13 = ".startsWith(\"#\") || tmp_key_";
  protected final String TEXT_14 = ".startsWith(\"!\") )){" + NL + "                          tmp_key_";
  protected final String TEXT_15 = " = null;" + NL + "                        } else {";
  protected final String TEXT_16 = NL + "                          ";
  protected final String TEXT_17 = ".";
  protected final String TEXT_18 = " = tmp_key_";
  protected final String TEXT_19 = ";" + NL + "                        }" + NL + "                      }";
  protected final String TEXT_20 = "   \t\t\t\t" + NL + "\t\t\t\t\t    if(";
  protected final String TEXT_21 = ".";
  protected final String TEXT_22 = " != null) {" + NL + "\t\t\t\t        ";
  protected final String TEXT_23 = NL + "\t\t\t\t    ";
  protected final String TEXT_24 = "_";
  protected final String TEXT_25 = " =" + NL + "\t\t\t        ";
  protected final String TEXT_26 = NL + "            \t\t    FormatterUtils.format_Date(";
  protected final String TEXT_27 = ".";
  protected final String TEXT_28 = ", ";
  protected final String TEXT_29 = ");\t\t\t\t\t" + NL + "            \t\t    ";
  protected final String TEXT_30 = NL + "\t\t\t\t\t    ";
  protected final String TEXT_31 = ".";
  protected final String TEXT_32 = ";\t\t\t\t\t" + NL + "\t\t\t\t\t    ";
  protected final String TEXT_33 = NL + "\t\t\t\t\t    String.valueOf(";
  protected final String TEXT_34 = ".";
  protected final String TEXT_35 = ");\t\t\t\t\t" + NL + "\t\t\t\t\t    ";
  protected final String TEXT_36 = NL + "            \t\t    }" + NL + "            \t\t    ";
  protected final String TEXT_37 = NL + "    \t\t\t";
  protected final String TEXT_38 = NL + "                    if(";
  protected final String TEXT_39 = ") {" + NL + "                        System.out.println(\"";
  protected final String TEXT_40 = " set key \\\"\" + key_";
  protected final String TEXT_41 = " + \"\\\" with value \\\"\" + value_";
  protected final String TEXT_42 = " + \"\\\"\");" + NL + "                    }";
  protected final String TEXT_43 = NL + "                        System.out.println(\"";
  protected final String TEXT_44 = " set key \\\"\" + key_";
  protected final String TEXT_45 = " + \"\\\" with value \\\"\" + value_";
  protected final String TEXT_46 = " + \"\\\"\");";
  protected final String TEXT_47 = NL + "  if (tmp_key_";
  protected final String TEXT_48 = " != null){" + NL + "  try{";
  protected final String TEXT_49 = "  " + NL + "        if(key_";
  protected final String TEXT_50 = "!=null && \"";
  protected final String TEXT_51 = "\".equals(key_";
  protected final String TEXT_52 = "))" + NL + "        {                ";
  protected final String TEXT_53 = "            " + NL + "                String context_";
  protected final String TEXT_54 = "_value = context.getProperty(\"";
  protected final String TEXT_55 = "\");" + NL + "                if(context_";
  protected final String TEXT_56 = "_value==null)" + NL + "                \tcontext_";
  protected final String TEXT_57 = "_value = \"\";" + NL + "\t\t\t\tint context_";
  protected final String TEXT_58 = "_pos = context_";
  protected final String TEXT_59 = "_value.indexOf(\";\");" + NL + "\t\t\t\tString context_";
  protected final String TEXT_60 = "_pattern =  \"yyyy-MM-dd HH:mm:ss\";" + NL + "\t\t\t\tif(context_";
  protected final String TEXT_61 = "_pos > -1){" + NL + "\t\t\t\t\tcontext_";
  protected final String TEXT_62 = "_pattern = context_";
  protected final String TEXT_63 = "_value.substring(0, context_";
  protected final String TEXT_64 = "_pos);\t\t\t\t\t" + NL + "\t\t\t\t}" + NL + "\t\t\t    context.";
  protected final String TEXT_65 = "=(java.util.Date)(new java.text.SimpleDateFormat(context_";
  protected final String TEXT_66 = "_pattern).parse(value_";
  protected final String TEXT_67 = "));" + NL + "\t\t\t    ";
  protected final String TEXT_68 = NL + NL + "                context.";
  protected final String TEXT_69 = "=Integer.parseInt(value_";
  protected final String TEXT_70 = ");" + NL;
  protected final String TEXT_71 = NL + "           context.";
  protected final String TEXT_72 = "=value_";
  protected final String TEXT_73 = ";";
  protected final String TEXT_74 = NL + "           context.";
  protected final String TEXT_75 = "=new java.text.StringCharacterIterator(value_";
  protected final String TEXT_76 = ").first();";
  protected final String TEXT_77 = NL + "           context.";
  protected final String TEXT_78 = "=new ";
  protected final String TEXT_79 = " (value_";
  protected final String TEXT_80 = ");";
  protected final String TEXT_81 = NL + NL + "               context.";
  protected final String TEXT_82 = "=";
  protected final String TEXT_83 = ".parse";
  protected final String TEXT_84 = "(value_";
  protected final String TEXT_85 = ");" + NL;
  protected final String TEXT_86 = "             " + NL + "        }" + NL + "       ";
  protected final String TEXT_87 = "\t" + NL + "        " + NL + "    \tif (context.getProperty(key_";
  protected final String TEXT_88 = ")!=null)" + NL + "    \t{    \t" + NL + "    \t\tassignList_";
  protected final String TEXT_89 = ".add(key_";
  protected final String TEXT_90 = ");" + NL + "    \t}else  {    \t\t" + NL + "    \t\tnewPropertyList_";
  protected final String TEXT_91 = ".add(key_";
  protected final String TEXT_92 = ");" + NL + "    \t}" + NL + "\t    context.setProperty(key_";
  protected final String TEXT_93 = ",value_";
  protected final String TEXT_94 = ");" + NL + "    }catch(Exception e){    \t" + NL + "    \tSystem.err.println(\"Set value for key: \" + key_";
  protected final String TEXT_95 = " + \" failed, error message: \" + e.getMessage());" + NL + "    }" + NL + "    \tnb_line_";
  protected final String TEXT_96 = "++;" + NL + "    }";
  protected final String TEXT_97 = "    \t" + NL + "    \t//////////////////////////";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
IProcess process=node.getProcess();
List<IContextParameter> params = new ArrayList<IContextParameter>();
params=process.getContextManager().getDefaultContext().getContextParameterList();
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null) {
        String cid = node.getUniqueName();

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    
    	List< ? extends IConnection> conns = node.getIncomingConnections();
    	for (IConnection conn : conns) {
    		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {    		
    		    
    stringBuffer.append(TEXT_3);
    
    			List<IMetadataColumn> columns = metadata.getListColumns();
    			int sizeColumns = columns.size();
    			for (int i = 0; (sizeColumns >= 2)&&(i < 2); i++) {
    				IMetadataColumn column = columns.get(i);
					JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
					boolean isPrimitive = JavaTypesManager.isJavaPrimitiveType( javaType, column.isNullable());					
					
    stringBuffer.append(TEXT_4);
    stringBuffer.append(i==0?"key":"value" );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    
                    // allow to add comment line start with '#' or '!'                                      
					if (i == 0){

    stringBuffer.append(TEXT_7);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    
                    }
					if(!isPrimitive) {
					    
    stringBuffer.append(TEXT_20);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_22);
    
					}
				    
    stringBuffer.append(TEXT_23);
    stringBuffer.append(i==0?"key":"value" );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    
            		String pattern = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
            		if (javaType == JavaTypesManager.DATE && pattern != null && pattern.trim().length() != 0) {
            		    
    stringBuffer.append(TEXT_26);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_28);
    stringBuffer.append( pattern );
    stringBuffer.append(TEXT_29);
    
					} else if(javaType == JavaTypesManager.STRING) {
					    
    stringBuffer.append(TEXT_30);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_32);
    
					} else {
					    
    stringBuffer.append(TEXT_33);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_35);
    
            		}
            		if(!isPrimitive) {
            		    
    stringBuffer.append(TEXT_36);
    
            		}
            	}//here end the last for, the List "columns"
    			
    stringBuffer.append(TEXT_37);
    
                if(node.getElementParameter("PRINT_OPERATIONS").isContextMode()) {

    stringBuffer.append(TEXT_38);
    stringBuffer.append(ElementParameterParser.getValue(node, "__PRINT_OPERATIONS__"));
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_42);
    
                } else {
                    if (ElementParameterParser.getValue(node, "__PRINT_OPERATIONS__").equals("true")) {

    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    
                    }
                }

    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_48);
    
            for (IContextParameter ctxParam :params)
             {
                String typeToGenerate ="String";
                if( !(ctxParam.getType().equals("id_File") || ctxParam.getType().equals("id_Directory") ||ctxParam.getType().equals("id_List Of Value") || ctxParam.getType().equals("id_Password")))
                {
                   typeToGenerate=JavaTypesManager.getTypeToGenerate(ctxParam.getType(),true);
                }
        
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_50);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_52);
     if(typeToGenerate.equals("java.util.Date"))
            {
    stringBuffer.append(TEXT_53);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_54);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_55);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_56);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_57);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_58);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_59);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_60);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_61);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_62);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_63);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_64);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_65);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_67);
    }else if(typeToGenerate.equals("Integer")){
    stringBuffer.append(TEXT_68);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_70);
    }else if(typeToGenerate.equals("Object")||typeToGenerate.equals("String")){
    stringBuffer.append(TEXT_71);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_73);
    }else if(typeToGenerate.equals("Character")){
    stringBuffer.append(TEXT_74);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_76);
    }else if(typeToGenerate.equals("BigDecimal")){
    stringBuffer.append(TEXT_77);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_78);
    stringBuffer.append(typeToGenerate);
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_80);
    }
           else{
    stringBuffer.append(TEXT_81);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_82);
    stringBuffer.append(typeToGenerate);
    stringBuffer.append(TEXT_83);
    stringBuffer.append(typeToGenerate);
    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_85);
    }
    stringBuffer.append(TEXT_86);
                   
             }
        
    stringBuffer.append(TEXT_87);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_92);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_93);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_95);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_96);
    
        	}
        }//here end the first for, the List "conns"

    stringBuffer.append(TEXT_97);
    
	}
}  

    return stringBuffer.toString();
  }
}
