package org.talend.designer.codegen.translators.internet;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import java.util.Map;
import org.talend.commons.utils.StringUtils;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.EConnectionType;

public class TRSSOutputBeginJava
{
  protected static String nl;
  public static synchronized TRSSOutputBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TRSSOutputBeginJava result = new TRSSOutputBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t\tint nb_line_";
  protected final String TEXT_2 = " = 0;" + NL + "\t\tjava.io.File file";
  protected final String TEXT_3 = "=new java.io.File(";
  protected final String TEXT_4 = ");" + NL + "\t\t" + NL + "\t\torg.dom4j.Document document";
  protected final String TEXT_5 = " =null;";
  protected final String TEXT_6 = NL + "\t\t\torg.dom4j.Element channelElement";
  protected final String TEXT_7 = " =null;" + NL + "\t\t\torg.dom4j.Element titleHElement";
  protected final String TEXT_8 = "=null;" + NL + "\t\t\torg.dom4j.Element descriptionHElement";
  protected final String TEXT_9 = "=null;" + NL + "\t\t\torg.dom4j.Element pubdateHElement";
  protected final String TEXT_10 = "=null;" + NL + "\t\t\torg.dom4j.Element linkHElement";
  protected final String TEXT_11 = "=null;";
  protected final String TEXT_12 = NL + "\t\t\torg.dom4j.Element feedElement";
  protected final String TEXT_13 = " =null;" + NL + "\t\t\torg.dom4j.Element titleHElement";
  protected final String TEXT_14 = "=null;" + NL + "\t\t\torg.dom4j.Element idHElement";
  protected final String TEXT_15 = "=null;" + NL + "\t\t\torg.dom4j.Element updatedHElement";
  protected final String TEXT_16 = "=null;" + NL + "\t\t\torg.dom4j.Element linkHElement";
  protected final String TEXT_17 = "=null;" + NL + "\t\t\torg.dom4j.Element authorHElement";
  protected final String TEXT_18 = "=null;";
  protected final String TEXT_19 = NL + "\t\t\t\torg.dom4j.Element ";
  protected final String TEXT_20 = "_Element_";
  protected final String TEXT_21 = " = null;";
  protected final String TEXT_22 = NL + "\t\t\t\torg.dom4j.Element ";
  protected final String TEXT_23 = "_Element_";
  protected final String TEXT_24 = " = null;";
  protected final String TEXT_25 = "\t" + NL + "\t\tif(";
  protected final String TEXT_26 = " && file";
  protected final String TEXT_27 = ".exists()){" + NL + "\t\t\torg.dom4j.io.SAXReader saxReader";
  protected final String TEXT_28 = " = new org.dom4j.io.SAXReader();" + NL + "\t        document";
  protected final String TEXT_29 = " = saxReader";
  protected final String TEXT_30 = " .read(new java.io.File(";
  protected final String TEXT_31 = "));" + NL + "\t\t}else{" + NL + "\t\t\tdocument";
  protected final String TEXT_32 = " = org.dom4j.DocumentHelper.createDocument();";
  protected final String TEXT_33 = NL + "\t\t\t\torg.dom4j.Element rssElement";
  protected final String TEXT_34 = " = document";
  protected final String TEXT_35 = ".addElement(\"rss\");" + NL + "\t\t\t\trssElement";
  protected final String TEXT_36 = ".addAttribute(\"version\",\"2.0\");" + NL + "\t\t\t\tchannelElement";
  protected final String TEXT_37 = " = rssElement";
  protected final String TEXT_38 = ".addElement(\"channel\");" + NL + "\t\t\t\ttitleHElement";
  protected final String TEXT_39 = "=channelElement";
  protected final String TEXT_40 = ".addElement(\"title\");" + NL + "\t\t\t\ttitleHElement";
  protected final String TEXT_41 = ".setText(";
  protected final String TEXT_42 = ");" + NL + "\t\t\t\tdescriptionHElement";
  protected final String TEXT_43 = "=channelElement";
  protected final String TEXT_44 = ".addElement(\"description\");" + NL + "\t\t\t\tdescriptionHElement";
  protected final String TEXT_45 = ".setText(";
  protected final String TEXT_46 = ");" + NL + "\t\t\t\tpubdateHElement";
  protected final String TEXT_47 = "=channelElement";
  protected final String TEXT_48 = ".addElement(\"pubdate\");" + NL + "\t\t\t\tpubdateHElement";
  protected final String TEXT_49 = ".setText(";
  protected final String TEXT_50 = ");" + NL + "\t\t\t\tlinkHElement";
  protected final String TEXT_51 = "=channelElement";
  protected final String TEXT_52 = ".addElement(\"link\");" + NL + "\t\t\t\tlinkHElement";
  protected final String TEXT_53 = ".setText(";
  protected final String TEXT_54 = ");";
  protected final String TEXT_55 = NL + "\t\t\t\tfeedElement";
  protected final String TEXT_56 = " = document";
  protected final String TEXT_57 = ".addElement(\"feed\",\"http://www.w3.org/2005/Atom\");" + NL + "\t\t\t\ttitleHElement";
  protected final String TEXT_58 = "=feedElement";
  protected final String TEXT_59 = ".addElement(\"title\");" + NL + "\t\t\t\ttitleHElement";
  protected final String TEXT_60 = ".setText(";
  protected final String TEXT_61 = ");" + NL + "\t\t\t\tidHElement";
  protected final String TEXT_62 = "=feedElement";
  protected final String TEXT_63 = ".addElement(\"id\");" + NL + "\t\t\t\tidHElement";
  protected final String TEXT_64 = ".setText(";
  protected final String TEXT_65 = ");" + NL + "\t\t\t\tupdatedHElement";
  protected final String TEXT_66 = "=feedElement";
  protected final String TEXT_67 = ".addElement(\"updated\");" + NL + "\t\t\t\tupdatedHElement";
  protected final String TEXT_68 = ".setText(";
  protected final String TEXT_69 = ");" + NL + "\t\t\t\tlinkHElement";
  protected final String TEXT_70 = "=feedElement";
  protected final String TEXT_71 = ".addElement(\"link\");" + NL + "\t\t\t\tlinkHElement";
  protected final String TEXT_72 = ".addAttribute(\"href\",";
  protected final String TEXT_73 = ");" + NL + "\t\t\t\tauthorHElement";
  protected final String TEXT_74 = "=feedElement";
  protected final String TEXT_75 = ".addElement(\"author\").addElement(\"name\");" + NL + "\t\t\t\tauthorHElement";
  protected final String TEXT_76 = ".setText(";
  protected final String TEXT_77 = ");";
  protected final String TEXT_78 = NL + "\t\t\t\t\t";
  protected final String TEXT_79 = "_Element_";
  protected final String TEXT_80 = " = ";
  protected final String TEXT_81 = "_Element_";
  protected final String TEXT_82 = ".addElement(\"";
  protected final String TEXT_83 = "\");";
  protected final String TEXT_84 = NL + "\t\t\t\t\t\t";
  protected final String TEXT_85 = "_Element_";
  protected final String TEXT_86 = ".setText(";
  protected final String TEXT_87 = ");";
  protected final String TEXT_88 = "\t\t\t" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_89 = "_Element_";
  protected final String TEXT_90 = " = channelElement";
  protected final String TEXT_91 = ".addElement(";
  protected final String TEXT_92 = ");";
  protected final String TEXT_93 = NL + "\t\t\t\t\t\t";
  protected final String TEXT_94 = "_Element_";
  protected final String TEXT_95 = " = feedElement";
  protected final String TEXT_96 = ".addElement(";
  protected final String TEXT_97 = ");";
  protected final String TEXT_98 = "\t\t\t\t\t" + NL + "\t\t\t        \t";
  protected final String TEXT_99 = "_Element_";
  protected final String TEXT_100 = ".setText(";
  protected final String TEXT_101 = ");";
  protected final String TEXT_102 = NL + "\t\t}";
  protected final String TEXT_103 = NL;

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
        
        String filename = ElementParameterParser.getValueWithUIFieldKey(node,"__FILENAME__", "FILENAME");
        
        boolean rssMode = "true".equals(ElementParameterParser.getValue(node,"__RSS__"));
        boolean atomMode = "true".equals(ElementParameterParser.getValue(node,"__ATOM__"));
      	String title= null;
      	String description= null;
      	String pubdate= null;
      	String link= null;
      	String aTitle= null;
      	String aLink= null;
      	String aId= null;
      	String aUpdated= null;
      	String aAuthor= null;
        if(rssMode){
	      	title= ElementParameterParser.getValue(node,"__TITLE__");
	      	description= ElementParameterParser.getValue(node,"__DESCRIPTION__");
	      	pubdate= ElementParameterParser.getValue(node,"__PUBDATE__");
	      	link= ElementParameterParser.getValue(node,"__LINK__");
        }
        if(atomMode){
	      	aTitle= ElementParameterParser.getValue(node,"__aTITLE__");
	      	aLink= ElementParameterParser.getValue(node,"__aLINK__");
	      	aId= ElementParameterParser.getValue(node,"__aID__");
	      	aUpdated= ElementParameterParser.getValue(node,"__aUPDATED__");
	      	aAuthor= ElementParameterParser.getValue(node,"__aAUTHOR__");
        }
      	
      	List<Map<String, String>> elements = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node,"__ELEMENTS__");
      	
        boolean isAppend = ("true").equals(ElementParameterParser.getValue(node,"__APPEND__"));
        String encoding = ElementParameterParser.getValue(node,"__ENCODING__");

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(filename);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    
		if(rssMode){

    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    
		}
		if(atomMode){

    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    		
		}
		for(Map<String, String> element : elements){
			String name = element.get("ELEMENT_NAME");
			if(name.split("/").length > 1){

    stringBuffer.append(TEXT_19);
    stringBuffer.append(name.substring(1,name.length()-1).replace("/",""));
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    
			}else{

    stringBuffer.append(TEXT_22);
    stringBuffer.append(name.substring(1,name.length()-1));
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    	
			}
		}

    stringBuffer.append(TEXT_25);
    stringBuffer.append(isAppend);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(filename);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    
			if(rssMode){

    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(title);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(description);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(pubdate);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_53);
    stringBuffer.append(link);
    stringBuffer.append(TEXT_54);
    
			}
			if(atomMode){

    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_60);
    stringBuffer.append(aTitle);
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_64);
    stringBuffer.append(aId);
    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_68);
    stringBuffer.append(aUpdated);
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_72);
    stringBuffer.append(aLink);
    stringBuffer.append(TEXT_73);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_74);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_76);
    stringBuffer.append(aAuthor);
    stringBuffer.append(TEXT_77);
    
			}
			for(Map<String, String> element : elements){
				String name = element.get("ELEMENT_NAME");
				String value = element.get("ELEMENT_VALUE");
				if(name.split("/").length > 1){

    stringBuffer.append(TEXT_78);
    stringBuffer.append(name.substring(1,name.length()-1).replace("/",""));
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_80);
    stringBuffer.append(name.substring(1,name.length()-1).substring(0,name.lastIndexOf("/")-1));
    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_82);
    stringBuffer.append(name.substring(1,name.length()-1).split("/")[1]);
    stringBuffer.append(TEXT_83);
    
					if(value.trim().length() > 2){

    stringBuffer.append(TEXT_84);
    stringBuffer.append(name.substring(1,name.length()-1).replace("/",""));
    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_86);
    stringBuffer.append(value);
    stringBuffer.append(TEXT_87);
    
					}
				}else{
					if(rssMode){

    stringBuffer.append(TEXT_88);
    stringBuffer.append(name.substring(1,name.length()-1));
    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_91);
    stringBuffer.append(name);
    stringBuffer.append(TEXT_92);
    
					}
					if(atomMode){

    stringBuffer.append(TEXT_93);
    stringBuffer.append(name.substring(1,name.length()-1));
    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_95);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_96);
    stringBuffer.append(name);
    stringBuffer.append(TEXT_97);
    
					}
					if(value.trim().length() > 2){

    stringBuffer.append(TEXT_98);
    stringBuffer.append(name.substring(1,name.length()-1));
    stringBuffer.append(TEXT_99);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_100);
    stringBuffer.append(value);
    stringBuffer.append(TEXT_101);
    
					}
				}
			}

    stringBuffer.append(TEXT_102);
    	
	}
}

    stringBuffer.append(TEXT_103);
    return stringBuffer.toString();
  }
}
