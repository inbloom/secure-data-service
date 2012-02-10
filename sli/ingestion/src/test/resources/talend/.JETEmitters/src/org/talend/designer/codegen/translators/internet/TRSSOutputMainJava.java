package org.talend.designer.codegen.translators.internet;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import java.io.File;
import java.util.ArrayList;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;

public class TRSSOutputMainJava
{
  protected static String nl;
  public static synchronized TRSSOutputMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TRSSOutputMainJava result = new TRSSOutputMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t" + NL + "\t\t\t\t\t\tif(";
  protected final String TEXT_2 = " && file";
  protected final String TEXT_3 = ".exists()){" + NL + "\t\t\t\t\t\t\tjava.util.List list";
  protected final String TEXT_4 = "= document";
  protected final String TEXT_5 = ".selectNodes(\"/rss/channel\" );" + NL + "\t\t\t        \t\tchannelElement";
  protected final String TEXT_6 = "=(org.dom4j.Element)list";
  protected final String TEXT_7 = ".get(0);" + NL + "\t\t\t        \t\tlist";
  protected final String TEXT_8 = "= document";
  protected final String TEXT_9 = ".selectNodes(\"/rss/channel/title\" );" + NL + "\t\t\t        \t\ttitleHElement";
  protected final String TEXT_10 = "=(org.dom4j.Element)list";
  protected final String TEXT_11 = ".get(0);" + NL + "\t\t\t        \t\ttitleHElement";
  protected final String TEXT_12 = ".setText(";
  protected final String TEXT_13 = ");" + NL + "\t\t\t        \t\tlist";
  protected final String TEXT_14 = "= document";
  protected final String TEXT_15 = ".selectNodes(\"/rss/channel/description\" );" + NL + "\t\t\t        \t\tdescriptionHElement";
  protected final String TEXT_16 = "=(org.dom4j.Element)list";
  protected final String TEXT_17 = ".get(0);" + NL + "\t\t\t        \t\tdescriptionHElement";
  protected final String TEXT_18 = ".setText(";
  protected final String TEXT_19 = ");" + NL + "\t\t\t        \t\tlist";
  protected final String TEXT_20 = "= document";
  protected final String TEXT_21 = ".selectNodes(\"/rss/channel/pubdate\" );" + NL + "\t\t\t        \t\tpubdateHElement";
  protected final String TEXT_22 = "=(org.dom4j.Element)list";
  protected final String TEXT_23 = ".get(0);" + NL + "\t\t\t        \t\tpubdateHElement";
  protected final String TEXT_24 = ".setText(";
  protected final String TEXT_25 = ");" + NL + "\t\t\t        \t\tlist";
  protected final String TEXT_26 = "= document";
  protected final String TEXT_27 = ".selectNodes(\"/rss/channel/link\" );" + NL + "\t\t\t        \t\tlinkHElement";
  protected final String TEXT_28 = "=(org.dom4j.Element)list";
  protected final String TEXT_29 = ".get(0);" + NL + "\t\t\t        \t\tlinkHElement";
  protected final String TEXT_30 = ".setText(";
  protected final String TEXT_31 = ");" + NL + "\t\t\t        \t}" + NL + "\t\t\t  \t\t \torg.dom4j.Element rootElement = channelElement";
  protected final String TEXT_32 = ".addElement(\"item\");";
  protected final String TEXT_33 = NL + "\t\t\t\t\t\tif(";
  protected final String TEXT_34 = " && file";
  protected final String TEXT_35 = ".exists()){" + NL + "\t\t\t\t\t\t\tjava.util.List list";
  protected final String TEXT_36 = "= document";
  protected final String TEXT_37 = ".selectNodes(\"/feed\" );" + NL + "\t\t\t        \t\tfeedElement";
  protected final String TEXT_38 = "=(org.dom4j.Element)list";
  protected final String TEXT_39 = ".get(0);" + NL + "\t\t\t        \t\tlist";
  protected final String TEXT_40 = "= document";
  protected final String TEXT_41 = ".selectNodes(\"/feed/title\" );" + NL + "\t\t\t        \t\ttitleHElement";
  protected final String TEXT_42 = "=(org.dom4j.Element)list";
  protected final String TEXT_43 = ".get(0);" + NL + "\t\t\t        \t\ttitleHElement";
  protected final String TEXT_44 = ".setText(";
  protected final String TEXT_45 = ");" + NL + "\t\t\t        \t\tlist";
  protected final String TEXT_46 = "= document";
  protected final String TEXT_47 = ".selectNodes(\"/feed/id\" );" + NL + "\t\t\t        \t\tidHElement";
  protected final String TEXT_48 = "=(org.dom4j.Element)list";
  protected final String TEXT_49 = ".get(0);" + NL + "\t\t\t        \t\tidHElement";
  protected final String TEXT_50 = ".setText(";
  protected final String TEXT_51 = ");" + NL + "\t\t\t        \t\tlist";
  protected final String TEXT_52 = "= document";
  protected final String TEXT_53 = ".selectNodes(\"/feed/updated\" );" + NL + "\t\t\t        \t\tupdatedHElement";
  protected final String TEXT_54 = "=(org.dom4j.Element)list";
  protected final String TEXT_55 = ".get(0);" + NL + "\t\t\t        \t\tupdatedHElement";
  protected final String TEXT_56 = ".setText(";
  protected final String TEXT_57 = ");" + NL + "\t\t\t        \t\tlist";
  protected final String TEXT_58 = "= document";
  protected final String TEXT_59 = ".selectNodes(\"/feed/link\" );" + NL + "\t\t\t        \t\tlinkHElement";
  protected final String TEXT_60 = "=(org.dom4j.Element)list";
  protected final String TEXT_61 = ".get(0);" + NL + "\t\t\t        \t\tlinkHElement";
  protected final String TEXT_62 = ".setText(";
  protected final String TEXT_63 = ");" + NL + "\t\t\t        \t}" + NL + "\t\t\t  \t\t \torg.dom4j.Element rootElement = feedElement";
  protected final String TEXT_64 = ".addElement(\"entry\");" + NL;
  protected final String TEXT_65 = " \t" + NL + "   \t\t\t\t\t\torg.dom4j.Element ";
  protected final String TEXT_66 = "Element=rootElement.addElement(\"";
  protected final String TEXT_67 = "\");";
  protected final String TEXT_68 = NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_69 = "Element.addAttribute(\"href\", ";
  protected final String TEXT_70 = ".";
  protected final String TEXT_71 = " ==null ? \"\" : ";
  protected final String TEXT_72 = ".";
  protected final String TEXT_73 = ");" + NL + "\t\t\t\t\t\t    " + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_74 = NL + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_75 = "Element.setText(";
  protected final String TEXT_76 = ".";
  protected final String TEXT_77 = " ==null ? \"\" : ";
  protected final String TEXT_78 = ".";
  protected final String TEXT_79 = ");";
  protected final String TEXT_80 = "\t" + NL + " \t\t\t\t\t\t\t";
  protected final String TEXT_81 = "Element.setText(";
  protected final String TEXT_82 = ".";
  protected final String TEXT_83 = " ==null ? \"\" : ";
  protected final String TEXT_84 = ".";
  protected final String TEXT_85 = ");";
  protected final String TEXT_86 = NL + "\t\t\t\t\t\t\t\tjava.text.SimpleDateFormat sdfTemp";
  protected final String TEXT_87 = " = new java.text.SimpleDateFormat(\"EEE, d MMM yyyy HH:mm:ss z\",java.util.Locale.US);";
  protected final String TEXT_88 = NL + "\t\t\t\t\t\t\t\tjava.text.SimpleDateFormat sdfTemp";
  protected final String TEXT_89 = " = new java.text.SimpleDateFormat(\"yyyy-MM-dd'T'hh:mm:ssZ\",java.util.Locale.US);";
  protected final String TEXT_90 = NL + "\t\t\t\t\t\t\tjava.util.SimpleTimeZone aZone";
  protected final String TEXT_91 = " = new java.util.SimpleTimeZone(8,\"GMT\");" + NL + "\t\t\t\t\t\t\tsdfTemp";
  protected final String TEXT_92 = ".setTimeZone(aZone";
  protected final String TEXT_93 = ");" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_94 = "Element.setText(";
  protected final String TEXT_95 = ".";
  protected final String TEXT_96 = " ==null ? \"\" : sdfTemp";
  protected final String TEXT_97 = ".format( ";
  protected final String TEXT_98 = ".";
  protected final String TEXT_99 = "));";
  protected final String TEXT_100 = NL + " \t\t\t\t\t\t\t";
  protected final String TEXT_101 = "Element.setText(String.valueOf(";
  protected final String TEXT_102 = ".";
  protected final String TEXT_103 = "));";
  protected final String TEXT_104 = "\t\t " + NL + "\t\tnb_line_";
  protected final String TEXT_105 = "++;";
  protected final String TEXT_106 = NL;

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
        }
	    File file=new File(filename);
	    boolean isAppend = ("true").equals(ElementParameterParser.getValue(node,"__APPEND__"));
	    List< ? extends IConnection> conns = node.getIncomingConnections();
		if(conns!=null){
			if (conns.size()>0){
				IConnection conn =conns.get(0);
	    		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
					List<IMetadataColumn> columns = metadata.getListColumns();
	    			int sizeColumns = columns.size();
	    			if(rssMode){

    stringBuffer.append(TEXT_1);
    stringBuffer.append(isAppend);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(title);
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
    stringBuffer.append(description);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(pubdate);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(link);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    
					}
					if(atomMode){

    stringBuffer.append(TEXT_33);
    stringBuffer.append(isAppend);
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(aTitle);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(aId);
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_56);
    stringBuffer.append(aUpdated);
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_62);
    stringBuffer.append(aLink);
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_64);
    
					}
	    			for (int i = 0; i < sizeColumns; i++) {
		    			IMetadataColumn column = columns.get(i);
		    			String coluLabel=column.getLabel();
		    			coluLabel=coluLabel.toLowerCase();
		    			JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());

    stringBuffer.append(TEXT_65);
    stringBuffer.append(coluLabel);
    stringBuffer.append(TEXT_66);
    stringBuffer.append(coluLabel);
    stringBuffer.append(TEXT_67);
    		
						if(coluLabel.equals("link")){
							if(atomMode){

    stringBuffer.append(TEXT_68);
    stringBuffer.append(coluLabel);
    stringBuffer.append(TEXT_69);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_70);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_72);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_73);
     } else if(rssMode){
    stringBuffer.append(TEXT_74);
    stringBuffer.append(coluLabel);
    stringBuffer.append(TEXT_75);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_76);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_77);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_78);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_79);
    
				            }
    					}else if(javaType == JavaTypesManager.STRING ){

    stringBuffer.append(TEXT_80);
    stringBuffer.append(coluLabel);
    stringBuffer.append(TEXT_81);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_82);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_83);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_84);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_85);
    		
						}else if(javaType == JavaTypesManager.DATE ){
							if(rssMode){

    stringBuffer.append(TEXT_86);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_87);
    
							}
							if(atomMode){

    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_89);
    
							}

    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_92);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_93);
    stringBuffer.append(coluLabel);
    stringBuffer.append(TEXT_94);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_95);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_96);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_97);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_98);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_99);
    			
						}else{

    stringBuffer.append(TEXT_100);
    stringBuffer.append(coluLabel);
    stringBuffer.append(TEXT_101);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_102);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_103);
    						
						}
					}
				}
			}
		}

    stringBuffer.append(TEXT_104);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_105);
    		 	
	}
}

    stringBuffer.append(TEXT_106);
    return stringBuffer.toString();
  }
}
