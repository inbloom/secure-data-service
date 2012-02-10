package org.talend.designer.codegen.translators.internet;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import java.util.List;

public class TSVNLogInputBeginJava
{
  protected static String nl;
  public static synchronized TSVNLogInputBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSVNLogInputBeginJava result = new TSVNLogInputBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t" + NL + "\t\tint nb_line";
  protected final String TEXT_3 = " = 0;" + NL + "" + NL + "\t\tString url";
  protected final String TEXT_4 = " = ";
  protected final String TEXT_5 = ";" + NL + "" + NL + "        long startRevision";
  protected final String TEXT_6 = " = ";
  protected final String TEXT_7 = ";" + NL + "" + NL + "        org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory.setup();" + NL + "        " + NL + "        org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl.setup();" + NL + "" + NL + "        org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory.setup();" + NL + "        " + NL + "        org.tmatesoft.svn.core.io.SVNRepository repository";
  protected final String TEXT_8 = " = org.tmatesoft.svn.core.io.SVNRepositoryFactory.create(org.tmatesoft.svn.core.SVNURL.parseURIEncoded(url";
  protected final String TEXT_9 = "));";
  protected final String TEXT_10 = NL + "        String username";
  protected final String TEXT_11 = " = ";
  protected final String TEXT_12 = ";" + NL + "\t\tString password";
  protected final String TEXT_13 = " = ";
  protected final String TEXT_14 = ";" + NL + "        org.tmatesoft.svn.core.auth.ISVNAuthenticationManager authManager";
  protected final String TEXT_15 = " = " + NL + "        org.tmatesoft.svn.core.wc.SVNWCUtil.createDefaultAuthenticationManager(username";
  protected final String TEXT_16 = ", password";
  protected final String TEXT_17 = ");" + NL + "        repository";
  protected final String TEXT_18 = ".setAuthenticationManager(authManager";
  protected final String TEXT_19 = ");";
  protected final String TEXT_20 = NL + "      ";
  protected final String TEXT_21 = NL + "    " + NL + "        long endRevision";
  protected final String TEXT_22 = " = startRevision";
  protected final String TEXT_23 = ";" + NL + "             ";
  protected final String TEXT_24 = NL + NL + "        long endRevision";
  protected final String TEXT_25 = " = repository";
  protected final String TEXT_26 = ".getLatestRevision();" + NL + "        ";
  protected final String TEXT_27 = NL + NL + "\t    long endRevision";
  protected final String TEXT_28 = " = ";
  protected final String TEXT_29 = ";" + NL;
  protected final String TEXT_30 = NL + "        java.util.Collection logEntries";
  protected final String TEXT_31 = " = repository";
  protected final String TEXT_32 = ".log(new String[] { \"\" }, null, startRevision";
  protected final String TEXT_33 = ", endRevision";
  protected final String TEXT_34 = ", true, true);" + NL + "" + NL + "        for (java.util.Iterator entries";
  protected final String TEXT_35 = " = logEntries";
  protected final String TEXT_36 = ".iterator(); entries";
  protected final String TEXT_37 = ".hasNext();) {" + NL + "" + NL + "            org.tmatesoft.svn.core.SVNLogEntry logEntry";
  protected final String TEXT_38 = " = (org.tmatesoft.svn.core.SVNLogEntry) entries";
  protected final String TEXT_39 = ".next();           " + NL + "            " + NL + "            java.util.Iterator it";
  protected final String TEXT_40 = " = logEntry";
  protected final String TEXT_41 = ".getChangedPaths().keySet().iterator();" + NL + "            " + NL + "            int nb_file_added";
  protected final String TEXT_42 = " = 0;" + NL + "            " + NL + "            int nb_file_modified";
  protected final String TEXT_43 = " = 0;" + NL + "            " + NL + "            int nb_file_deleted";
  protected final String TEXT_44 = " = 0;" + NL + "            " + NL + "            while (it";
  protected final String TEXT_45 = ".hasNext()) {" + NL + "            " + NL + "                org.tmatesoft.svn.core.SVNLogEntryPath sp";
  protected final String TEXT_46 = " = (org.tmatesoft.svn.core.SVNLogEntryPath) logEntry";
  protected final String TEXT_47 = ".getChangedPaths().get(it";
  protected final String TEXT_48 = ".next());" + NL + "                " + NL + "                if(sp";
  protected final String TEXT_49 = ".getType() == sp";
  protected final String TEXT_50 = ".TYPE_ADDED){" + NL + "                " + NL + "                \tnb_file_added";
  protected final String TEXT_51 = "++;" + NL + "                \t" + NL + "                }else if(sp";
  protected final String TEXT_52 = ".getType() == sp";
  protected final String TEXT_53 = ".TYPE_MODIFIED){" + NL + "                " + NL + "                \tnb_file_modified";
  protected final String TEXT_54 = "++;" + NL + "                " + NL + "                }else if(sp";
  protected final String TEXT_55 = ".getType() == sp";
  protected final String TEXT_56 = ".TYPE_DELETED){" + NL + "                " + NL + "                \tnb_file_deleted";
  protected final String TEXT_57 = "++;" + NL + "                " + NL + "                }else{" + NL + "                " + NL + "                }" + NL + "" + NL + "            }" + NL + "            " + NL + "            " + NL + "            " + NL + "            " + NL + "\t\t   ";
  protected final String TEXT_58 = NL + "                    ";
  protected final String TEXT_59 = NL + "                    ";
  protected final String TEXT_60 = ".revision = ((Long)logEntry";
  protected final String TEXT_61 = ".getRevision()).intValue();" + NL + "                    ";
  protected final String TEXT_62 = NL + "                    ";
  protected final String TEXT_63 = ".author = logEntry";
  protected final String TEXT_64 = ".getAuthor();" + NL + "                    ";
  protected final String TEXT_65 = NL + "                    ";
  protected final String TEXT_66 = ".date = logEntry";
  protected final String TEXT_67 = ".getDate();" + NL + "                    ";
  protected final String TEXT_68 = NL + "                    ";
  protected final String TEXT_69 = ".message = logEntry";
  protected final String TEXT_70 = ".getMessage();" + NL + "                    ";
  protected final String TEXT_71 = NL + "                    ";
  protected final String TEXT_72 = ".nb_file_added = nb_file_added";
  protected final String TEXT_73 = ";" + NL + "                    ";
  protected final String TEXT_74 = NL + "                    ";
  protected final String TEXT_75 = ".nb_file_modified = nb_file_modified";
  protected final String TEXT_76 = ";" + NL + "                    ";
  protected final String TEXT_77 = NL + "                    ";
  protected final String TEXT_78 = ".nb_file_deleted = nb_file_deleted";
  protected final String TEXT_79 = ";" + NL + "                    ";
  protected final String TEXT_80 = "        " + NL + "        " + NL + "\tnb_line";
  protected final String TEXT_81 = "++;" + NL + "        ";
  protected final String TEXT_82 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
List< ? extends IConnection> conns = node.getOutgoingSortedConnections();

List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null) {

        String URL = ElementParameterParser.getValue(node,"__URL__");
        
        boolean needAuth = ("true").equals(ElementParameterParser.getValue(node,"__NEED_AUTH__"));
		
        String fromRev = ElementParameterParser.getValue(node,"__FROM_REV__");

        boolean singleRev = ("true").equals(ElementParameterParser.getValue(node,"__SINGLE_REV__"));

        String toRev = ElementParameterParser.getValue(node,"__TO_REV__");

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(URL);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(fromRev);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    if(needAuth){
        String user = ElementParameterParser.getValue(node,"__USERNAME__");
        String passwd = ElementParameterParser.getValue(node,"__PASSWORD__");
        
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(user);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(passwd);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    }
    stringBuffer.append(TEXT_20);
    if(singleRev){
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    }else if(("'HEAD'").equals(toRev)){
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    }else{
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(toRev);
    stringBuffer.append(TEXT_29);
    }
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid);
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
    stringBuffer.append(cid);
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_57);
    
        if(conns!=null){
            if (conns.size()>0){
                for(IConnection conn:conns){
                    if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {

    stringBuffer.append(TEXT_58);
    stringBuffer.append(TEXT_59);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_61);
    stringBuffer.append(TEXT_62);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_64);
    stringBuffer.append(TEXT_65);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_67);
    stringBuffer.append(TEXT_68);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_70);
    stringBuffer.append(TEXT_71);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_73);
    stringBuffer.append(TEXT_74);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_76);
    stringBuffer.append(TEXT_77);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_78);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_79);
    
                    }
                }
            }
        }

    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_81);
    
    }
}

    stringBuffer.append(TEXT_82);
    return stringBuffer.toString();
  }
}
