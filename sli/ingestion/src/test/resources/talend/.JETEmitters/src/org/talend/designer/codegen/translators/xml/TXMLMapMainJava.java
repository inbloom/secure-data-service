package org.talend.designer.codegen.translators.xml;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.process.AbstractExternalNode;
import org.talend.designer.xmlmap.XmlMapComponent;
import org.talend.designer.xmlmap.model.emf.xmlmap.XmlMapData;
import org.eclipse.emf.common.util.EList;
import org.talend.designer.xmlmap.generation.GenerationManagerFactory;
import org.talend.designer.xmlmap.generation.GenerationManager;
import org.talend.core.model.process.BlockCode;
import org.talend.designer.xmlmap.generation.HashedMetadataTable;
import org.talend.designer.xmlmap.model.emf.xmlmap.VarNode;
import org.talend.designer.xmlmap.model.emf.xmlmap.VarTable;
import org.talend.designer.xmlmap.model.emf.xmlmap.InputXmlTree;
import org.talend.designer.xmlmap.model.emf.xmlmap.OutputXmlTree;
import org.talend.designer.xmlmap.model.emf.xmlmap.TreeNode;
import org.talend.designer.xmlmap.model.emf.xmlmap.OutputTreeNode;
import org.talend.designer.xmlmap.model.emf.xmlmap.Connection;
import org.talend.designer.xmlmap.model.emf.xmlmap.LookupConnection;
import org.talend.designer.xmlmap.model.emf.xmlmap.NodeType;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.utils.NodeUtil;
import org.talend.core.model.metadata.IMetadataColumn;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.Comparator;
import java.util.Collections;
import java.util.ArrayList;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;

/**
 * add by wliu
 */
public class TXMLMapMainJava {

  protected static String nl;
  public static synchronized TXMLMapMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TXMLMapMainJava result = new TXMLMapMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL;
  protected final String TEXT_3 = NL + "\tboolean ";
  protected final String TEXT_4 = " = false;" + NL + "\tboolean ";
  protected final String TEXT_5 = " = false;" + NL + "\tboolean ";
  protected final String TEXT_6 = " = false;" + NL + "\tboolean isMatchDocRow";
  protected final String TEXT_7 = " = false;" + NL + "\t  " + NL + "\t";
  protected final String TEXT_8 = NL + "\t\t\t\t    GenerateDocument_";
  protected final String TEXT_9 = " gen_Doc_";
  protected final String TEXT_10 = "_";
  protected final String TEXT_11 = " = new GenerateDocument_";
  protected final String TEXT_12 = "();" + NL + "\t\t\t\t\t";
  protected final String TEXT_13 = "_tmp.";
  protected final String TEXT_14 = " = null;" + NL + "\t\t\t\t\t";
  protected final String TEXT_15 = NL + "\t\t";
  protected final String TEXT_16 = NL + "\t\t\t\t\tString xPathLoop_";
  protected final String TEXT_17 = " = \"";
  protected final String TEXT_18 = "\";" + NL + "\t\t\t\t\torg.dom4j.Document doc_";
  protected final String TEXT_19 = " = ";
  protected final String TEXT_20 = ".";
  protected final String TEXT_21 = ".getDocument(); " + NL + "\t\t\t\t\t";
  protected final String TEXT_22 = NL + "\t\t\t\t\t\t//old version, find NS from doc" + NL + "\t\t\t\t\t\tnsTool_";
  protected final String TEXT_23 = ".countNSMap(doc_";
  protected final String TEXT_24 = ".getRootElement());" + NL + "\t\t\t\t\t\tjava.util.HashMap<String,String> xmlNameSpaceMap_";
  protected final String TEXT_25 = " = nsTool_";
  protected final String TEXT_26 = ".xmlNameSpaceMap;" + NL + "\t\t\t\t\t\torg.dom4j.XPath x_";
  protected final String TEXT_27 = " = doc_";
  protected final String TEXT_28 = ".createXPath(nsTool_";
  protected final String TEXT_29 = ".addDefaultNSPrefix(xPathLoop_";
  protected final String TEXT_30 = ",xPathLoop_";
  protected final String TEXT_31 = "));  " + NL + "\t\t\t\t\t\tx_";
  protected final String TEXT_32 = ".setNamespaceURIs(xmlNameSpaceMap_";
  protected final String TEXT_33 = ");" + NL + "\t\t\t\t\t";
  protected final String TEXT_34 = NL + "\t\t\t\t\t\tjava.util.HashMap<String,String> xmlNameSpaceMap_";
  protected final String TEXT_35 = " = new java.util.HashMap<String,String>();" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_36 = NL + "\t\t\t\t\t\torg.dom4j.XPath x_";
  protected final String TEXT_37 = " = doc_";
  protected final String TEXT_38 = ".createXPath(xPathLoop_";
  protected final String TEXT_39 = ");" + NL + "\t\t\t\t\t\tx_";
  protected final String TEXT_40 = ".setNamespaceURIs(xmlNameSpaceMap_";
  protected final String TEXT_41 = ");" + NL + "\t\t\t\t\t";
  protected final String TEXT_42 = NL + "\t" + NL + "\t\t\t\t    java.util.List<org.dom4j.tree.AbstractNode> nodeList_";
  protected final String TEXT_43 = " = (java.util.List<org.dom4j.tree.AbstractNode>)x_";
  protected final String TEXT_44 = ".selectNodes(doc_";
  protected final String TEXT_45 = ");\t" + NL + "\t\t\t\t    String str_";
  protected final String TEXT_46 = " = null;" + NL + "\t\t\t\t    org.dom4j.Node node_";
  protected final String TEXT_47 = " = null;" + NL + "\t" + NL + "\t\t\t\t\tfor (org.dom4j.tree.AbstractNode temp_";
  protected final String TEXT_48 = ": nodeList_";
  protected final String TEXT_49 = ") { // G_TXM_M_001" + NL + "\t\t\t\t    \tnb_line_";
  protected final String TEXT_50 = "++;" + NL + "\t\t\t\t    \t";
  protected final String TEXT_51 = " = false;" + NL + "\t\t\t\t    \t";
  protected final String TEXT_52 = " = false;" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_53 = "=false;" + NL + "\t\t\t\t\t    isMatchDocRow";
  protected final String TEXT_54 = " = false;" + NL + "\t\t\t\t\t\t" + NL + "\t\t\t\t    \ttreeNodeAPI_";
  protected final String TEXT_55 = ".clear();" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_56 = NL + "\t\t\t\t\t\t\torg.dom4j.XPath xTmp";
  protected final String TEXT_57 = "_";
  protected final String TEXT_58 = " = temp_";
  protected final String TEXT_59 = ".createXPath(nsTool_";
  protected final String TEXT_60 = ".addDefaultNSPrefix(\"";
  protected final String TEXT_61 = "\",xPathLoop_";
  protected final String TEXT_62 = "));" + NL + "\t\t\t\t\t\t    ";
  protected final String TEXT_63 = NL + "\t\t\t\t\t\t   \torg.dom4j.XPath xTmp";
  protected final String TEXT_64 = "_";
  protected final String TEXT_65 = " = temp_";
  protected final String TEXT_66 = ".createXPath(\"";
  protected final String TEXT_67 = "\");" + NL + "\t\t\t\t\t\t    ";
  protected final String TEXT_68 = NL + "\t\t\t\t\t\t    xTmp";
  protected final String TEXT_69 = "_";
  protected final String TEXT_70 = ".setNamespaceURIs(xmlNameSpaceMap_";
  protected final String TEXT_71 = ");" + NL + "\t\t\t\t\t\t    Object obj";
  protected final String TEXT_72 = "_";
  protected final String TEXT_73 = " = xTmp";
  protected final String TEXT_74 = "_";
  protected final String TEXT_75 = ".evaluate(temp_";
  protected final String TEXT_76 = ");" + NL + "\t\t\t\t\t\t    if(obj";
  protected final String TEXT_77 = "_";
  protected final String TEXT_78 = " instanceof String || obj";
  protected final String TEXT_79 = "_";
  protected final String TEXT_80 = " instanceof Number){" + NL + "\t\t\t\t\t\t    \tstr_";
  protected final String TEXT_81 = " = String.valueOf(obj";
  protected final String TEXT_82 = "_";
  protected final String TEXT_83 = ");" + NL + "\t\t\t\t\t\t    \ttreeNodeAPI_";
  protected final String TEXT_84 = ".put(\"";
  protected final String TEXT_85 = "\", str_";
  protected final String TEXT_86 = ");" + NL + "\t\t\t\t\t\t    }else{" + NL + "\t\t\t\t\t\t    \tnode_";
  protected final String TEXT_87 = " = xTmp";
  protected final String TEXT_88 = "_";
  protected final String TEXT_89 = ".selectSingleNode(temp_";
  protected final String TEXT_90 = ");" + NL + "\t\t\t\t\t\t    \ttreeNodeAPI_";
  protected final String TEXT_91 = ".put(\"";
  protected final String TEXT_92 = "\", node_";
  protected final String TEXT_93 = "!=null ? xTmp";
  protected final String TEXT_94 = "_";
  protected final String TEXT_95 = ".valueOf(temp_";
  protected final String TEXT_96 = ") : null);" + NL + "\t\t\t\t\t\t    }" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_97 = NL;
  protected final String TEXT_98 = NL + NL + "\t\t";
  protected final String TEXT_99 = "\t";
  protected final String TEXT_100 = NL + "\t\t\t\t///////////////////////////////////////////////" + NL + "\t\t\t\t//Starting Lookup Table \"";
  protected final String TEXT_101 = "\" " + NL + "\t\t\t\t///////////////////////////////////////////////" + NL + "\t\t\t\t//tHash_Lookup_";
  protected final String TEXT_102 = ".initGet();" + NL + "\t\t\t\t";
  protected final String TEXT_103 = NL + "\t\t\t\t\t\tjava.util.HashMap<String,String> xmlNameSpaceMap_";
  protected final String TEXT_104 = " = new java.util.HashMap<String,String>();" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_105 = NL + "\t\t\t\t\t\t" + NL + "\t\t\t\t\t\tjava.util.Map<String,String> xpathToPatternMap_";
  protected final String TEXT_106 = " = new java.util.HashMap<String,String>();" + NL + "\t\t\t\t\t\tjava.util.Map<String,String> xpathToTypeMap_";
  protected final String TEXT_107 = " = new java.util.HashMap<String,String>();" + NL + "\t\t\t\t    ";
  protected final String TEXT_108 = NL + "\t\t\t\t" + NL + "\t\t\t\t";
  protected final String TEXT_109 = NL + "\t\t\t\t\tboolean forceLoop";
  protected final String TEXT_110 = " = false;" + NL + "\t\t\t\t\t";
  protected final String TEXT_111 = " ";
  protected final String TEXT_112 = "ObjectFromLookup = null;" + NL + "\t\t\t\t\tboolean hasResultFromLookupCache";
  protected final String TEXT_113 = " = false;" + NL + "\t\t\t\t\tjava.util.Map<String,Object> cacheResult";
  protected final String TEXT_114 = " = new java.util.HashMap<String,Object>();" + NL + "\t\t\t\t";
  protected final String TEXT_115 = NL + "\t\t\t\t" + NL + "\t\t\t\t";
  protected final String TEXT_116 = NL + "\t\t\t   \t\t\t\tList<Object> lookupCacheKey";
  protected final String TEXT_117 = " = new java.util.ArrayList<Object>();" + NL + "\t\t\t   \t\t\t";
  protected final String TEXT_118 = NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_119 = "HashKey.documentLookupResult.put(\"";
  protected final String TEXT_120 = "\",";
  protected final String TEXT_121 = ");" + NL + "\t\t\t\t\t\t\t\txpathToPatternMap_";
  protected final String TEXT_122 = ".put(\"";
  protected final String TEXT_123 = "\",";
  protected final String TEXT_124 = ");" + NL + "\t\t\t\t\t\t\t\txpathToTypeMap_";
  protected final String TEXT_125 = ".put(\"";
  protected final String TEXT_126 = "\",\"";
  protected final String TEXT_127 = "\");" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_128 = NL + "\t\t\t\t\t\t\t\t\tlookupCacheKey";
  protected final String TEXT_129 = ".add(";
  protected final String TEXT_130 = ");" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_131 = NL + "\t\t\t\t\t\t   \t";
  protected final String TEXT_132 = "HashKey.";
  protected final String TEXT_133 = " = ";
  protected final String TEXT_134 = ";" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_135 = "HashKey.hashCodeDirty = true;" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_136 = NL + "\t\t\t\t\t\tlookupCacheKey";
  protected final String TEXT_137 = ".add(";
  protected final String TEXT_138 = "HashKey);" + NL + "\t\t\t\t\t";
  protected final String TEXT_139 = NL + "\t\t\t\t\t" + NL + "\t\t\t\t\t";
  protected final String TEXT_140 = NL + "\t\t\t\t\t\tif(!";
  protected final String TEXT_141 = "){//TD120" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_142 = NL + "\t\t\t\t\t\t\t\t\ttHash_Lookup_Cache_";
  protected final String TEXT_143 = ".lookup(lookupCacheKey";
  protected final String TEXT_144 = ");" + NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_145 = NL + "\t\t\t\t\t\t\t\t\ttHash_Lookup_Cache_";
  protected final String TEXT_146 = ".lookup(lookupCacheKey";
  protected final String TEXT_147 = ");" + NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_148 = NL + "\t\t  \t\t\t\t\t\t\tif(tHash_Lookup_Cache_";
  protected final String TEXT_149 = ".hasNext()) {" + NL + "\t  \t\t\t\t\t\t\t\t\thasResultFromLookupCache";
  protected final String TEXT_150 = " = true;" + NL + "\t\t  \t\t\t\t\t\t\t} else {" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_151 = NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_152 = "Process(globalMap);" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_153 = NL + "\t\t\t\t\t\t\t\t\ttHash_Lookup_";
  protected final String TEXT_154 = " = " + NL + "\t\t\t\t\t\t\t\t\t\t(org.talend.designer.components.lookup.persistent.Persistent";
  protected final String TEXT_155 = "LookupManager<";
  protected final String TEXT_156 = "Struct>) " + NL + "\t\t\t\t\t\t\t\t\t\t\t((org.talend.designer.components.lookup.persistent.Persistent";
  protected final String TEXT_157 = "LookupManager<";
  protected final String TEXT_158 = "Struct>) " + NL + "\t\t\t\t\t\t\t\t\t\t\t\t globalMap.get( \"tHash_Lookup_";
  protected final String TEXT_159 = "\" ));" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_160 = NL + "\t\t\t\t\t\t\t\t\ttHash_Lookup_";
  protected final String TEXT_161 = " = " + NL + "\t\t\t\t\t\t\t\t\t(org.talend.designer.components.lookup.memory.AdvancedMemoryLookup<";
  protected final String TEXT_162 = "Struct>) " + NL + "\t\t\t\t\t\t\t\t\t\t((org.talend.designer.components.lookup.memory.AdvancedMemoryLookup<";
  protected final String TEXT_163 = "Struct>) " + NL + "\t\t\t\t\t\t\t\t\t\t\tglobalMap.get( \"tHash_Lookup_";
  protected final String TEXT_164 = "\" ));" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_165 = NL + "\t\t\t\t\t    \t\t\ttHash_Lookup_";
  protected final String TEXT_166 = ".initGet();" + NL + "\t\t\t\t\t    \t\t";
  protected final String TEXT_167 = NL + "\t\t\t\t\t    \t\t\ttHash_Lookup_";
  protected final String TEXT_168 = ".lookup( ";
  protected final String TEXT_169 = "HashKey );" + NL + "\t\t\t\t\t    \t\t";
  protected final String TEXT_170 = NL + "\t\t\t\t\t    \t\t\ttHash_Lookup_";
  protected final String TEXT_171 = ".lookup();" + NL + "\t\t\t\t\t    \t\t";
  protected final String TEXT_172 = NL + "\t\t\t\t\t\t    \t\t}" + NL + "\t\t\t\t\t\t\t    ";
  protected final String TEXT_173 = NL + "\t\t\t\t\t" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_174 = NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_175 = NL + "\t\t\t\t\t\t\t\t\ttHash_Lookup_";
  protected final String TEXT_176 = ".lookup( ";
  protected final String TEXT_177 = "HashKey );" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_178 = NL + "\t\t\t\t\t\t\t\t\ttHash_Lookup_";
  protected final String TEXT_179 = ".lookup();" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_180 = NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_181 = NL + "\t\t\t\t\t\t\tif(hasResultFromLookupCache";
  protected final String TEXT_182 = "  || tHash_Lookup_";
  protected final String TEXT_183 = ".hasNext()){" + NL + "\t\t\t\t\t\t\t" + NL + "\t\t\t\t\t\t\t} else {" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_184 = NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_185 = " = true;" + NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_186 = " = true;" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_187 = NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_188 = " = ";
  protected final String TEXT_189 = "Default;" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_190 = NL + "\t\t\t\t\t\t\t\t" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_191 = NL + "\t\t\t\t\t\t\t\tforceLoop";
  protected final String TEXT_192 = " = true;" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_193 = NL + "\t\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\t}//TD120" + NL + "\t\t\t\t   " + NL + "\t\t\t\t   \t\t";
  protected final String TEXT_194 = NL + "\t\t\t\t   \t\t\tif(";
  protected final String TEXT_195 = ") {" + NL + "\t\t\t\t   \t\t\t\tforceLoop";
  protected final String TEXT_196 = " = true;" + NL + "\t\t\t\t   \t\t\t}" + NL + "\t\t\t\t   \t\t";
  protected final String TEXT_197 = NL + "\t\t\t\t   " + NL + "\t\t\t\t   \t\t";
  protected final String TEXT_198 = "Struct fromLookup_";
  protected final String TEXT_199 = " = null;" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_200 = " = ";
  protected final String TEXT_201 = "Default;" + NL + "\t\t\t\t   " + NL + "\t\t\t\t   \t\t";
  protected final String TEXT_202 = NL + "\t\t\t\t\t    if(hasResultFromLookupCache";
  protected final String TEXT_203 = ") {//TD10086" + NL + "\t\t\t\t\t    \t";
  protected final String TEXT_204 = NL + "\t\t\t\t\t    \tjava.util.Map<String,Object> cacheResult_";
  protected final String TEXT_205 = " = tHash_Lookup_Cache_";
  protected final String TEXT_206 = ".next();" + NL + "\t\t\t\t\t    \t\t";
  protected final String TEXT_207 = NL + "\t\t\t\t\t    \t\t\ttreeNodeAPI_";
  protected final String TEXT_208 = ".put(\"";
  protected final String TEXT_209 = "\",StringUtils.valueOf(cacheResult_";
  protected final String TEXT_210 = ".get(\"";
  protected final String TEXT_211 = "\")));" + NL + "\t\t\t\t\t    \t\t";
  protected final String TEXT_212 = NL + "\t\t\t\t    \t\t\t";
  protected final String TEXT_213 = " = (";
  protected final String TEXT_214 = ")cacheResult_";
  protected final String TEXT_215 = ".get(\"";
  protected final String TEXT_216 = "\");" + NL + "\t\t\t\t\t    \t";
  protected final String TEXT_217 = NL + "\t\t\t\t\t    } else if(!";
  protected final String TEXT_218 = ") {" + NL + "\t\t\t\t\t\t  \t";
  protected final String TEXT_219 = NL + "\t\t\t\t\t\t   \tisMatchDocRow";
  protected final String TEXT_220 = " = false;" + NL + "\t\t\t\t\t\t  \t";
  protected final String TEXT_221 = NL + "\t\t\t\t\t\t\twhile (tHash_Lookup_";
  protected final String TEXT_222 = ".hasNext()) {//TD119" + NL + "\t\t\t\t\t\t\t\tfromLookup_";
  protected final String TEXT_223 = " = null;" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_224 = " = ";
  protected final String TEXT_225 = "Default;" + NL + "\t\t\t\t\t\t\t\tfromLookup_";
  protected final String TEXT_226 = " = tHash_Lookup_";
  protected final String TEXT_227 = ".next();" + NL + "\t\t\t\t\t\t\t\tif (fromLookup_";
  protected final String TEXT_228 = " != null) {" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_229 = NL + "\t\t\t\t\t\t\t\t\t//begin to lookup Document Object (case 1:lookup doc key exists),(case 2:lookup doc key not exists but lookup line doc output exist)" + NL + "\t\t\t\t\t\t\t\t\tDocument lookupDoc_";
  protected final String TEXT_230 = " = fromLookup_";
  protected final String TEXT_231 = ".";
  protected final String TEXT_232 = ";" + NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_233 = NL + "\t\t\t\t\t\t\t\t\tboolean quit";
  protected final String TEXT_234 = " = false;" + NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_235 = NL + "\t\t\t\t\t\t\t\t\tjava.util.Map<String,String> xPathAsOutput_";
  protected final String TEXT_236 = " = new java.util.HashMap<String,String>();" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_237 = NL + "\t\t\t\t\t\t\t\t\txPathAsOutput_";
  protected final String TEXT_238 = ".put(\"";
  protected final String TEXT_239 = "\",\"";
  protected final String TEXT_240 = "\");" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_241 = NL + "\t\t\t\t\t\t\t\t\tList<java.util.Map<String,Object>> result_";
  protected final String TEXT_242 = " = lookupDoc_";
  protected final String TEXT_243 = ".LookupDocument(\"";
  protected final String TEXT_244 = "\",";
  protected final String TEXT_245 = "HashKey.documentLookupResult," + NL + "\t\t\t\t\t\t\t\t\t\txPathAsOutput_";
  protected final String TEXT_246 = ",xmlNameSpaceMap_";
  protected final String TEXT_247 = ",xpathToTypeMap_";
  protected final String TEXT_248 = ",xpathToPatternMap_";
  protected final String TEXT_249 = ",\"";
  protected final String TEXT_250 = "\");" + NL + "\t\t\t\t\t\t\t\t\tif(result_";
  protected final String TEXT_251 = " == null || result_";
  protected final String TEXT_252 = ".size() == 0) {" + NL + "\t\t\t\t\t\t\t\t\t\t// not find" + NL + "\t\t\t\t\t\t\t\t\t} else {" + NL + "\t\t\t\t\t\t\t\t\t\t//find and cache it,now only memory,inner join and unique match mode" + NL + "\t\t\t\t\t\t\t\t\t\t//once find,not reject." + NL + "\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_253 = NL + "\t\t\t\t\t\t\t\t\t\tisMatchDocRow";
  protected final String TEXT_254 = " = true;" + NL + "\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_255 = NL + "\t\t\t\t\t\t\t\t\t\tfor(java.util.Map<String,Object> docRow_";
  protected final String TEXT_256 = " : result_";
  protected final String TEXT_257 = ") {" + NL + "\t\t\t\t\t\t\t\t\t\t\tfor(String path_";
  protected final String TEXT_258 = " : docRow_";
  protected final String TEXT_259 = ".keySet()) {" + NL + "\t\t\t\t\t\t\t\t\t\t\t\ttreeNodeAPI_";
  protected final String TEXT_260 = ".put(path_";
  protected final String TEXT_261 = ", StringUtils.valueOf(docRow_";
  protected final String TEXT_262 = ".get(path_";
  protected final String TEXT_263 = ")));" + NL + "\t\t\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_264 = NL + "\t\t\t\t\t\t\t\t\t\t\t\tcacheResult";
  protected final String TEXT_265 = ".put(path_";
  protected final String TEXT_266 = ",StringUtils.valueOf(docRow_";
  protected final String TEXT_267 = ".get(path_";
  protected final String TEXT_268 = ")));" + NL + "\t\t\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_269 = NL + "\t\t\t\t\t\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_270 = NL + "\t\t\t\t\t\t\t\t\t\tquit";
  protected final String TEXT_271 = " = true;" + NL + "\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_272 = NL + "\t\t\t\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_273 = NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_274 = " = fromLookup_";
  protected final String TEXT_275 = ";" + NL + "\t\t\t\t\t\t\t\t\t" + NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_276 = NL + "\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_277 = NL + "\t\t\t\t\t\t\t\t\t\tif(isMatchDocRow";
  protected final String TEXT_278 = ") {" + NL + "\t\t\t\t\t\t\t\t\t\t\tcacheResult";
  protected final String TEXT_279 = ".put(\"";
  protected final String TEXT_280 = "\",";
  protected final String TEXT_281 = ");" + NL + "\t\t\t\t\t\t\t\t\t\t\ttHash_Lookup_Cache_";
  protected final String TEXT_282 = ".put(lookupCacheKey";
  protected final String TEXT_283 = ",cacheResult";
  protected final String TEXT_284 = "); " + NL + "\t\t\t\t\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_285 = NL + "\t\t\t\t\t\t\t\t\t\tcacheResult";
  protected final String TEXT_286 = ".put(\"";
  protected final String TEXT_287 = "\",";
  protected final String TEXT_288 = "); " + NL + "\t\t\t\t\t\t\t\t\t\ttHash_Lookup_Cache_";
  protected final String TEXT_289 = ".put(lookupCacheKey";
  protected final String TEXT_290 = ",cacheResult";
  protected final String TEXT_291 = "); " + NL + "\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_292 = NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_293 = NL + "\t\t\t\t\t\t\t\t\t" + NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_294 = NL + "\t\t\t\t\t\t\t\t\t\tif(quit";
  protected final String TEXT_295 = ") {//for first match,once find in document,no need to continue to lookup." + NL + "\t\t\t\t\t\t\t\t\t\t\tbreak;" + NL + "\t\t\t\t\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_296 = NL + "\t\t\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\t\t}//TD119" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_297 = NL + "\t\t\t\t\t\t   \t";
  protected final String TEXT_298 = " = !isMatchDocRow";
  protected final String TEXT_299 = " && !hasResultFromLookupCache";
  protected final String TEXT_300 = ";" + NL + "\t\t\t\t\t\t    ";
  protected final String TEXT_301 = " = ";
  protected final String TEXT_302 = ";" + NL + "\t\t\t\t\t\t  \t";
  protected final String TEXT_303 = NL + "\t\t\t\t\t\t  \t";
  protected final String TEXT_304 = NL + "\t\t\t\t\t\t  \tif(!isMatchDocRow";
  protected final String TEXT_305 = " && !hasResultFromLookupCache";
  protected final String TEXT_306 = ") {" + NL + "\t\t\t\t\t\t  \t\t";
  protected final String TEXT_307 = " = ";
  protected final String TEXT_308 = "Default;" + NL + "\t\t\t\t\t\t  \t}" + NL + "\t\t\t\t\t\t  \t";
  protected final String TEXT_309 = NL + "\t\t\t\t\t\t  \t";
  protected final String TEXT_310 = NL + "\t\t\t\t\t\t  \t\t//do nothing" + NL + "\t\t\t\t\t\t  \t";
  protected final String TEXT_311 = NL + "\t\t\t\t  \t\t}//TD10086" + NL + "\t\t\t\t  \t\t";
  protected final String TEXT_312 = NL + "\t\t\t\t  \t\t" + NL + "\t\t\t\t  \t\t//////////////////////////////////////////////////////////////////////////////////////////////" + NL + "\t\t\t\t  \t\t";
  protected final String TEXT_313 = NL + "\t\t\t\t  \t\tboolean fromCache";
  protected final String TEXT_314 = " = hasResultFromLookupCache";
  protected final String TEXT_315 = ";" + NL + "\t\t\t\t  \t\tList<java.util.Map<String,Object>> multipleResultSet";
  protected final String TEXT_316 = " = new java.util.ArrayList<java.util.Map<String,Object>>();" + NL + "\t\t\t\t  \t\t//the var for cache the ";
  protected final String TEXT_317 = "Struct" + NL + "\t\t\t\t  \t\tjava.util.Map<String,Object> oneRow_";
  protected final String TEXT_318 = " = null;" + NL + "\t\t\t\t\t\tif(hasResultFromLookupCache";
  protected final String TEXT_319 = ") {" + NL + "\t\t\t\t\t    \t";
  protected final String TEXT_320 = NL + "\t\t\t\t\t    \tjava.util.Map<String,Object> cacheResult_";
  protected final String TEXT_321 = " = tHash_Lookup_Cache_";
  protected final String TEXT_322 = ".next();" + NL + "\t\t\t\t\t    \tmultipleResultSet";
  protected final String TEXT_323 = ".add(cacheResult_";
  protected final String TEXT_324 = ");" + NL + "\t\t\t\t\t    \t";
  protected final String TEXT_325 = NL + "\t\t\t\t\t    } else if(!";
  protected final String TEXT_326 = ") {" + NL + "\t\t\t\t\t    \t";
  protected final String TEXT_327 = NL + "\t\t\t\t\t\t   \tisMatchDocRow";
  protected final String TEXT_328 = " = false;" + NL + "\t\t\t\t\t\t  \t";
  protected final String TEXT_329 = NL + "\t\t\t\t\t    \twhile (tHash_Lookup_";
  protected final String TEXT_330 = ".hasNext()) {" + NL + "\t\t\t\t\t    \t\tfromLookup_";
  protected final String TEXT_331 = " = null;" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_332 = " = ";
  protected final String TEXT_333 = "Default;" + NL + "\t\t\t\t\t\t\t\tfromLookup_";
  protected final String TEXT_334 = " = tHash_Lookup_";
  protected final String TEXT_335 = ".next();" + NL + "\t\t\t\t\t\t\t\tif(fromLookup_";
  protected final String TEXT_336 = " != null) {" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_337 = NL + "\t\t\t\t\t\t\t\t\tDocument lookupDoc_";
  protected final String TEXT_338 = " = fromLookup_";
  protected final String TEXT_339 = ".";
  protected final String TEXT_340 = ";" + NL + "\t\t\t\t\t\t\t\t\tjava.util.Map<String,String> xPathAsOutput_";
  protected final String TEXT_341 = " = new java.util.HashMap<String,String>();" + NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_342 = NL + "\t\t\t\t\t\t\t\t\t\txPathAsOutput_";
  protected final String TEXT_343 = ".put(\"";
  protected final String TEXT_344 = "\",\"";
  protected final String TEXT_345 = "\");" + NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_346 = NL + "\t\t\t\t\t\t\t\t\tList<java.util.Map<String,Object>> result_";
  protected final String TEXT_347 = " = lookupDoc_";
  protected final String TEXT_348 = ".LookupDocument(\"";
  protected final String TEXT_349 = "\",";
  protected final String TEXT_350 = "HashKey.documentLookupResult," + NL + "\t\t\t\t\t\t\t\t\t\txPathAsOutput_";
  protected final String TEXT_351 = ",xmlNameSpaceMap_";
  protected final String TEXT_352 = ",xpathToTypeMap_";
  protected final String TEXT_353 = ",xpathToPatternMap_";
  protected final String TEXT_354 = ",\"";
  protected final String TEXT_355 = "\");" + NL + "\t\t\t\t\t\t\t\t\tif(result_";
  protected final String TEXT_356 = " == null || result_";
  protected final String TEXT_357 = ".size() == 0) {" + NL + "\t\t\t\t\t\t\t\t\t\t//do nothing" + NL + "\t\t\t\t\t\t\t\t\t} else {" + NL + "\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_358 = NL + "\t\t\t\t\t\t\t\t\t\tisMatchDocRow";
  protected final String TEXT_359 = " = true;" + NL + "\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_360 = NL + "\t\t\t\t\t\t\t\t\t\tfor(java.util.Map<String,Object> docRow_";
  protected final String TEXT_361 = " : result_";
  protected final String TEXT_362 = ") {" + NL + "\t\t\t\t\t\t\t\t\t\t\tdocRow_";
  protected final String TEXT_363 = ".put(\"";
  protected final String TEXT_364 = "\",fromLookup_";
  protected final String TEXT_365 = ");" + NL + "\t\t\t\t\t\t\t\t\t\t\tmultipleResultSet";
  protected final String TEXT_366 = ".add(docRow_";
  protected final String TEXT_367 = ");" + NL + "\t\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_368 = NL + "\t\t\t\t\t\t\t\t\t\t\tfor(String path_";
  protected final String TEXT_369 = " : docRow_";
  protected final String TEXT_370 = ".keySet()) {" + NL + "\t\t\t\t\t\t\t\t\t\t\t\tcacheResult";
  protected final String TEXT_371 = ".put(path_";
  protected final String TEXT_372 = ",StringUtils.valueOf(docRow_";
  protected final String TEXT_373 = ".get(path_";
  protected final String TEXT_374 = ")));" + NL + "\t\t\t\t\t\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\t\t\t\t\t\tcacheResult";
  protected final String TEXT_375 = ".put(\"";
  protected final String TEXT_376 = "\",fromLookup_";
  protected final String TEXT_377 = ");" + NL + "\t\t\t\t\t\t\t\t\t\t\ttHash_Lookup_Cache_";
  protected final String TEXT_378 = ".put(lookupCacheKey";
  protected final String TEXT_379 = ",cacheResult";
  protected final String TEXT_380 = ");" + NL + "\t\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_381 = NL + "\t\t\t\t\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_382 = NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_383 = "\t" + NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_384 = " = fromLookup_";
  protected final String TEXT_385 = ";" + NL + "\t\t\t\t\t\t\t\t\t//construct the resultset for mutiple lookup when no document lookup." + NL + "\t\t\t\t\t\t\t\t\toneRow_";
  protected final String TEXT_386 = " = new java.util.HashMap<String, Object>();" + NL + "\t\t\t\t\t\t\t\t\toneRow_";
  protected final String TEXT_387 = ".put(\"";
  protected final String TEXT_388 = "\",";
  protected final String TEXT_389 = ");" + NL + "\t\t\t\t\t\t\t\t\tmultipleResultSet";
  protected final String TEXT_390 = ".add(oneRow_";
  protected final String TEXT_391 = ");" + NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_392 = NL + "\t\t\t\t\t\t\t\t}" + NL + "\t\t\t\t\t    \t}//end while" + NL + "\t\t\t\t\t    \t" + NL + "\t\t\t\t\t    }//end else if" + NL + "\t\t\t\t\t    //now not support cache all_matches lookup" + NL + "\t\t\t\t\t    ";
  protected final String TEXT_393 = NL + "\t\t\t\t\t    \tforceLoop";
  protected final String TEXT_394 = " = !isMatchDocRow";
  protected final String TEXT_395 = " && !hasResultFromLookupCache";
  protected final String TEXT_396 = ";" + NL + "\t\t\t\t\t    ";
  protected final String TEXT_397 = NL + "\t\t\t\t\t    " + NL + "\t\t\t\t\t    ";
  protected final String TEXT_398 = NL + "\t\t\t\t\t   \t";
  protected final String TEXT_399 = " = !isMatchDocRow";
  protected final String TEXT_400 = " && !hasResultFromLookupCache";
  protected final String TEXT_401 = ";" + NL + "\t\t\t\t\t    ";
  protected final String TEXT_402 = " = ";
  protected final String TEXT_403 = ";" + NL + "\t\t\t\t\t    forceLoop";
  protected final String TEXT_404 = " = ";
  protected final String TEXT_405 = ";" + NL + "\t\t\t\t\t  \t";
  protected final String TEXT_406 = NL + "\t\t\t\t\t    " + NL + "\t\t\t\t\t    java.util.Iterator<java.util.Map<String,Object>> iter";
  protected final String TEXT_407 = " = multipleResultSet";
  protected final String TEXT_408 = ".iterator();" + NL + "\t\t\t\t\t    while(iter";
  protected final String TEXT_409 = ".hasNext() || forceLoop";
  protected final String TEXT_410 = ") { // G_TM_M_002" + NL + "\t\t\t\t\t    \t";
  protected final String TEXT_411 = " = ";
  protected final String TEXT_412 = "Default;" + NL + "\t\t\t\t\t    \t";
  protected final String TEXT_413 = "Struct tempLookup";
  protected final String TEXT_414 = " = null; " + NL + "\t\t\t\t\t    \tif(!forceLoop";
  protected final String TEXT_415 = ") {" + NL + "\t\t\t\t\t\t    \tjava.util.Map<String,Object> oneRow";
  protected final String TEXT_416 = " = iter";
  protected final String TEXT_417 = ".next();" + NL + "\t\t\t\t\t\t   \t\t";
  protected final String TEXT_418 = NL + "\t\t\t\t\t\t    \t\t\ttreeNodeAPI_";
  protected final String TEXT_419 = ".put(\"";
  protected final String TEXT_420 = "\",StringUtils.valueOf(oneRow";
  protected final String TEXT_421 = ".get(\"";
  protected final String TEXT_422 = "\")));" + NL + "\t\t\t\t\t\t    \t";
  protected final String TEXT_423 = NL + "\t\t\t\t\t    \t\ttempLookup";
  protected final String TEXT_424 = " = (";
  protected final String TEXT_425 = ")oneRow";
  protected final String TEXT_426 = ".get(\"";
  protected final String TEXT_427 = "\");" + NL + "\t\t\t\t\t    \t\tif(tempLookup";
  protected final String TEXT_428 = "!=null) {" + NL + "\t\t\t\t\t    \t\t\t";
  protected final String TEXT_429 = " = tempLookup";
  protected final String TEXT_430 = ";" + NL + "\t\t\t\t\t    \t\t}" + NL + "\t\t\t\t    \t\t}" + NL + "\t\t\t\t    \t\tforceLoop";
  protected final String TEXT_431 = " = false;" + NL + "\t\t\t\t  \t\t";
  protected final String TEXT_432 = NL + "\t\t\t\t  \t\t/////////////////////////////////////////////////////////////////////////////////////////////////" + NL + "\t\t\t\t  \t\t" + NL + "\t\t\t\t\t";
  protected final String TEXT_433 = NL + "\t\t\t\t";
  protected final String TEXT_434 = NL + "\t\t\t\t\t\tif(!";
  protected final String TEXT_435 = "){" + NL + "\t\t\t\t\t\t\ttHash_Lookup_";
  protected final String TEXT_436 = ".lookup( ";
  protected final String TEXT_437 = "HashKey );" + NL + "\t\t\t\t\t\t\tif(tHash_Lookup_";
  protected final String TEXT_438 = ".hasNext()){" + NL + "\t\t\t\t\t\t\t" + NL + "\t\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\t}else {" + NL + "\t\t\t\t\t\t" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\twhile (tHash_Lookup_";
  protected final String TEXT_439 = ".hasNext()) {//TD128 " + NL + "\t\t\t\t\t\t\t\t" + NL + "\t\t\t\t\t\t\tfromLookup_";
  protected final String TEXT_440 = " = null;" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_441 = " = ";
  protected final String TEXT_442 = "Default;" + NL + "\t\t\t\t\t" + NL + "\t\t\t\t\t\t\tfromLookup_";
  protected final String TEXT_443 = " = tHash_Lookup_";
  protected final String TEXT_444 = ".next();" + NL + "\t\t\t\t\t\t\tif (fromLookup_";
  protected final String TEXT_445 = " != null) {" + NL + "" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_446 = " = fromLookup_";
  protected final String TEXT_447 = ";" + NL + "\t\t\t\t\t\t\t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_448 = NL + "        { // start of Var scope" + NL + "        " + NL + "\t        // ###############################" + NL + "        \t// # Vars tables" + NL + "        ";
  protected final String TEXT_449 = NL + "\t\t\t\t\t";
  protected final String TEXT_450 = NL + "\t\t// ###############################" + NL + "\t\t// # Output tables";
  protected final String TEXT_451 = NL;
  protected final String TEXT_452 = NL;
  protected final String TEXT_453 = NL + "\t\tif(nb_line_";
  protected final String TEXT_454 = " == nodeList_";
  protected final String TEXT_455 = ".size()) {";
  protected final String TEXT_456 = NL + "\t\t\t\t\t";
  protected final String TEXT_457 = " = ";
  protected final String TEXT_458 = "_save;";
  protected final String TEXT_459 = NL + "\t\t}";
  protected final String TEXT_460 = NL;

	class XMLMapUtil {
	
		private XmlMapData getXmlMapData(INode node) {
		
			XmlMapData xmlMapData = (XmlMapData)ElementParameterParser.getObjectValueXMLTree(node);
			
			return xmlMapData;
		}
		
		private List<IConnection> filterAndSort(INode node) {
			
			XmlMapData xmlMapData= getXmlMapData(node);
			EList<InputXmlTree> inputTables = xmlMapData.getInputTrees();
			final List<String> tableNames = new ArrayList<String>();
			HashMap<String, InputXmlTree> hNameToInputXmlTree = new HashMap<String, InputXmlTree>();
			
			for(InputXmlTree inputTable : inputTables){
				hNameToInputXmlTree.put(inputTable.getName(), inputTable);
				tableNames.add(inputTable.getName());
			}
			
			List<IConnection> inputConnections = new ArrayList<IConnection>();
			
			for(IConnection conn : node.getIncomingConnections()){
				if(hNameToInputXmlTree.get(conn.getName()) != null){
					inputConnections.add(conn);
				}
			}
			
			java.util.Collections.sort(inputConnections,new java.util.Comparator<IConnection>() {
				public int compare(IConnection conn1, IConnection conn2) {
					return tableNames.indexOf(conn1.getName()) - tableNames.indexOf(conn2.getName());
				}	
			});
			
			return inputConnections;
		}
	
		public String createSignature(INode node, boolean force) {
			String toReturn = "";
			
			List<IConnection> inputConnections = filterAndSort(node);
			
			for(IConnection conn : inputConnections) {
				if (conn.getLineStyle().equals(EConnectionType.FLOW_MAIN) || conn.getLineStyle().equals(EConnectionType.FLOW_MERGE) || conn.getLineStyle().equals(EConnectionType.FLOW_REF)) {
					if ((force)|| conn.getLineStyle().equals(EConnectionType.FLOW_REF) ||(conn.getSource().isSubProcessStart() || !(NodeUtil.isDataAutoPropagated(conn.getSource())))) {
						toReturn+=", "+conn.getName() + "Struct " + conn.getName();
					} else {
						toReturn+=", "+getConnectionType(conn.getSource())+"Struct " + conn.getName();
					}
				}
			}
    		return toReturn;
    	}
    	
    	public String getConnectionType(INode node) {
    		for(IConnection conn : node.getIncomingConnections()) {
				if (conn.getLineStyle().equals(EConnectionType.FLOW_MAIN) || conn.getLineStyle().equals(EConnectionType.FLOW_MERGE) || conn.getLineStyle().equals(EConnectionType.FLOW_REF)) {
					if ((conn.getLineStyle().equals(EConnectionType.FLOW_REF) || conn.getSource().isSubProcessStart() || !(NodeUtil.isDataAutoPropagated(conn.getSource())))) {
						return conn.getName();
					} else {
						return getConnectionType(conn.getSource());
					}
				} else if (conn.getLineStyle().equals(EConnectionType.ITERATE)||conn.getLineStyle().equals(EConnectionType.ON_ROWS_END)) {
					if(node.getOutgoingConnections() != null && node.getOutgoingConnections().size() > 0) {
						return node.getOutgoingConnections().get(0).getName();
					}
				}
			}
			return "";
    	}
    	
    	public boolean tableHasADocument(EList<OutputTreeNode> outputTableEntries) {
    		for (OutputTreeNode outputTableEntry : outputTableEntries) {
    			if(("id_Document").equals(outputTableEntry.getType())){
    				return true;
    			}
    		}
    		return false;
    	}
	}

	class MatchXmlTreeExpr {
		String cid = null;
		java.util.Map<String, String> pathTypeMap = new java.util.HashMap<String, String>(); // Map<input table xpath, typeToGenerate>
		java.util.Map<String, String> pathPatternMap = new java.util.HashMap<String, String>(); // Map<input table xpath, Pattern>
		java.util.Map<String, String> pathTypeToGenMap = new java.util.HashMap<String, String>(); // Map<"/root/name","String">
		
		//only for main input table
		public MatchXmlTreeExpr(TreeNode node, String cid) {
			this.cid = cid;
			init(node);
		}
		
		//for main and all lookup tables
		public MatchXmlTreeExpr(List<TreeNode> nodes, String cid) {
			this.cid = cid;
			for(TreeNode node : nodes) {
				init(node);
			}
		}
		
		//NO TYPE CHECK 
		public MatchXmlTreeExpr(String cid) {
			this.cid = cid;
		}
		
		private void init(TreeNode node) {
			if(node.getOutgoingConnections().size()>0 || node.getLookupOutgoingConnections().size()>0){
				String talendType = node.getType();
				JavaType javaType = JavaTypesManager.getJavaTypeFromId(talendType);
				String typeToGenerate = JavaTypesManager.getTypeToGenerate(talendType, node.isNullable());
				String patternValue = node.getPattern() == null || node.getPattern().trim().length() == 0 ? null : node.getPattern();
				
				pathTypeMap.put(node.getXpath(), talendType);
				pathPatternMap.put(node.getXpath(), patternValue);
				pathTypeToGenMap.put(node.getXpath(), typeToGenerate);
			}
    		
    		for(TreeNode tmpNode : node.getChildren()) {
    			init(tmpNode);
    		}
		}
		
		String generateExprCode(String expression){
			StringBuilder strBuilder = new StringBuilder();
			if(expression==null || ("").equals(expression)) {
				return "";
			}
	
			String currentExpression = expression;
			String tmpXpath = "";
			java.util.regex.Pattern expressionFromDoc = java.util.regex.Pattern.compile("\\[.*\\..*:.*\\]");
			java.util.regex.Matcher matcherFromDoc;
			
			boolean end = false;
			
			if(expression.indexOf("[")>-1) {
				strBuilder.append(expression.substring(0, expression.indexOf("[")));
				currentExpression = currentExpression.substring(currentExpression.indexOf("["), currentExpression.length());
				
				while(currentExpression.length()>0 && !end) {
					expression = currentExpression.substring(0, currentExpression.indexOf("]")+1);
					currentExpression = currentExpression.substring(expression.length(), currentExpression.length());
					matcherFromDoc = expressionFromDoc.matcher(expression);
					if(matcherFromDoc.matches()) {
						tmpXpath = expression.substring(1, expression.length()-1);
						if("id_String".equals(pathTypeMap.get(tmpXpath)) || "id_Object".equals(pathTypeMap.get(tmpXpath))){
							strBuilder.append("treeNodeAPI_"+cid+".get_String(\""+ tmpXpath + "\")");
						} else if("id_Date".equals(pathTypeMap.get(tmpXpath))) {
							strBuilder.append("treeNodeAPI_"+cid+".get_Date(\""+ tmpXpath + "\" , " + pathPatternMap.get(tmpXpath) + ")");
						} else if("id_byte[]".equals(pathTypeMap.get(tmpXpath))) {
							strBuilder.append("treeNodeAPI_"+cid+".get_Bytes(\""+ tmpXpath + "\")");
						} else {
							if(pathTypeToGenMap.get(tmpXpath)!=null) {
								strBuilder.append("treeNodeAPI_"+cid+".get_" + pathTypeToGenMap.get(tmpXpath) + "(\""+ tmpXpath + "\")");
							} else {
								strBuilder.append("treeNodeAPI_"+cid+".get_String(\""+ tmpXpath + "\")");
							}
						}				
					} else {
						strBuilder.append(expression);
					}
					if(currentExpression.indexOf("[")>-1) {
						strBuilder.append(currentExpression.substring(0, currentExpression.indexOf("[")));
						currentExpression = currentExpression.substring(currentExpression.indexOf("["), currentExpression.length());
					} else {
						strBuilder.append(currentExpression);
						end=true;
					}
				
				}
			} else {
				strBuilder.append(expression);
			}
			return strBuilder.toString();
		}
	}
	
	class XPathHelper{
		int dPrefixCount = 0;
		Map<String,String> nsMapping;
		TreeNode loopNode;
		String finalLoopNodeXPath;
		Map<String,String> outNodesXPath;
		Map<String,String> lookupInputNodesXPath;
		
		Map<String,String> xpathToPattern = new HashMap<String,String>();
		Map<String,String> xpathToType = new HashMap<String,String>();
		
		public Map<String,String> getXpathToPatternMap(){
			return xpathToPattern;
		}
		
		public Map<String,String> getXpathToTypeMap(){
			return xpathToType;
		}
		
		public XPathHelper(TreeNode rootNode){
			//1. find loop node
			loopNode = findLoopNode(rootNode);
			if(loopNode!=null){
				nsMapping = new HashMap<String,String>(); 
				Map<String,String> dPrefixMapping = new HashMap<String,String>();
				//2. collection all NS
				collectionNS(rootNode, nsMapping, dPrefixMapping, null);
				
				List<TreeNode> outNodes = new ArrayList<TreeNode>(); 
				//3. find all output node(out & lookup out)
				findOutputNodes(rootNode, outNodes);
				
				//4. build xpath with prefix
				finalLoopNodeXPath = buildXPathWithPrefix(loopNode, dPrefixMapping);
				
				outNodesXPath = new HashMap<String,String>();
				buildXPathWithPrefix(outNodes, dPrefixMapping, outNodesXPath);
				
				//5. build xpath for the node has lookup input connection
				List<TreeNode> lookupInputNodes = new ArrayList<TreeNode>();
				findLookupInputNodes(rootNode, lookupInputNodes);
				lookupInputNodesXPath = new HashMap<String,String>();
				buildXPathWithPrefix(lookupInputNodes, dPrefixMapping, lookupInputNodesXPath);
			}
			
		}
		public boolean hasLoopNode(){
			if(loopNode == null){
				return false;
			}
			return true;
		}
		public boolean hasDefinedNS(){
			if(nsMapping.size()==0){
				return false;
			}
			return true;
		}
		
		public Map<String,String> getOutNodesXPath(){
			return outNodesXPath;
		}
		
		public Map<String,String> getLookupInputNodesXPath(){
			return lookupInputNodesXPath;
		}
		
		public String buildNSMapping(String name){
			StringBuilder sb = new StringBuilder();
			for (Object key : nsMapping.keySet()) { 
			    Object val = nsMapping.get(key);
			    sb.append(name+".put(\""+key+"\",\""+val+"\");"); 
			} 
			return sb.toString();
		}
		
		public String getLoopNodeXPath(){
			return finalLoopNodeXPath;
		}
		
		private void buildXPathWithPrefix(List<TreeNode> nodes, Map<String,String> dPrefixMapping, Map<String,String> nodesXPath){
			String loopNodeXPath = loopNode.getXpath();
			for(TreeNode node: nodes){
				String currentNodeXPath = node.getXpath();
				String typeToGenerate = JavaTypesManager.getTypeToGenerate(node.getType(), node.isNullable()); 
				xpathToType.put(currentNodeXPath,typeToGenerate);
				xpathToPattern.put(currentNodeXPath,node.getPattern());
				
				if(currentNodeXPath==null || ("").equals(currentNodeXPath)){
				}else if(loopNodeXPath.equals(currentNodeXPath)){
					nodesXPath.put(currentNodeXPath, ".");
				}else{
					String nodeXPathWithPrefix = buildXPathWithPrefix(node, dPrefixMapping);
					if(nodeXPathWithPrefix.startsWith(finalLoopNodeXPath)){
						String relativeXPath = nodeXPathWithPrefix.substring(finalLoopNodeXPath.length() + 1);
						nodesXPath.put(currentNodeXPath, relativeXPath);
					}else{
						StringBuilder relativeXPath = new StringBuilder();
						String tmp = finalLoopNodeXPath;
						
						while(!nodeXPathWithPrefix.startsWith(tmp)){
							int index = tmp.lastIndexOf("/");
							if(index<0){ break; }
							tmp = tmp.substring(0,index);
							relativeXPath.append("../");
						}
						if(tmp.lastIndexOf("/") < 0 ){
							System.out.println("Loop Path is not set or loop Path is invalid");
						}else{
							relativeXPath.append(nodeXPathWithPrefix.substring(tmp.length() + 1));
						}
						relativeXPath.toString();
						nodesXPath.put(currentNodeXPath, relativeXPath.toString());
					}
				}
			}
		} 
		
		private String buildXPathWithPrefix(TreeNode node, Map<String,String> dPrefixMapping){
			String xpath = getXPath(node);
			StringBuilder finalXPath = new StringBuilder();
			return buildXPathWithPrefix(finalXPath, xpath, dPrefixMapping);
			
		}
		private String buildXPathWithPrefix(StringBuilder finalXPath, String xpath, Map<String,String> dPrefixMapping){
			List<String> nodePaths = new ArrayList<String>();
			
			while(xpath.lastIndexOf("/") != -1){
				nodePaths.add(xpath);
				xpath = xpath.substring(0,xpath.lastIndexOf("/"));
			}
			
			for(int i=nodePaths.size()-1; i>=0; i--){
				String nodePath = nodePaths.get(i);
				String prefix = dPrefixMapping.get(nodePath);
				String nodeName = nodePath.substring(nodePath.lastIndexOf("/")+1);
				
				finalXPath.append("/");
				if(prefix != null && !"".equals(prefix)){
					finalXPath.append(prefix);
					finalXPath.append(":");
					finalXPath.append(nodeName);
				}else{
					finalXPath.append(nodeName);
				}
			}
			
			return finalXPath.toString();
		}
		
		private void findOutputNodes(TreeNode currentNode, List<TreeNode> outNodes){
			if(currentNode.getOutgoingConnections().size()>0 || currentNode.getLookupOutgoingConnections().size()>0){
				outNodes.add(currentNode);
			}
			for(TreeNode childNode : currentNode.getChildren()) {
				if(childNode.getNodeType() == NodeType.ELEMENT || childNode.getNodeType() == NodeType.ATTRIBUT){
					findOutputNodes(childNode, outNodes);
				}
			}
		}
		
		private void findLookupInputNodes(TreeNode currentNode, List<TreeNode> lookupInputNodes){
			if(currentNode.getLookupIncomingConnections().size()>0){
				lookupInputNodes.add(currentNode);
			}
			for(TreeNode childNode : currentNode.getChildren()) {
				if(childNode.getNodeType() == NodeType.ELEMENT || childNode.getNodeType() == NodeType.ATTRIBUT){
					findLookupInputNodes(childNode, lookupInputNodes);
				}
			}
		}
		
		private void collectionNS(TreeNode currentNode, Map<String,String> nsMapping, Map<String,String> dPrefixMapping, String parentDPrefix){
			EList<TreeNode> childNodes = currentNode.getChildren();
			for(TreeNode childNode : childNodes){
				if(childNode.getNodeType() == NodeType.NAME_SPACE){
					if(childNode.getName() != null && !"(default)".equals(childNode.getName())){
						nsMapping.put(childNode.getName(),childNode.getDefaultValue());
					}else{
						String defaultPrefix = findVaildDefaultPrefix();
						nsMapping.put(defaultPrefix,childNode.getDefaultValue());
						//dPrefixMapping.put(getXPath(currentNode),defaultPrefix);
						parentDPrefix = defaultPrefix;
					}
				}
			}
			if(!currentNode.getName().contains(":")){
				if(parentDPrefix != null && !"".equals(parentDPrefix)){
					dPrefixMapping.put(getXPath(currentNode),parentDPrefix);
				}
			}else{
				//parentDPrefix = null;
			}
			for(TreeNode childNode : childNodes){
				if(childNode.getNodeType() == NodeType.ELEMENT){
					collectionNS(childNode, nsMapping, dPrefixMapping, parentDPrefix);
				}
			}
		}
		
		private String findVaildDefaultPrefix(){
			String prefix = "TPrefix"+dPrefixCount;
			dPrefixCount++;
			if(nsMapping.get(prefix)==null || "".equals(nsMapping.get(prefix))){
				return prefix;
			}else{
				return findVaildDefaultPrefix();
			}
		}
		
		private TreeNode findLoopNode(TreeNode node){
			if(node == null){
				return null;
			}
			if(node.isLoop()) {
				return node;
			}
			for(TreeNode childNode : node.getChildren()) {
				if(childNode.getNodeType() == NodeType.ELEMENT){
					TreeNode resultNode = findLoopNode(childNode);
					if(resultNode!=null){
						return resultNode;
					}
				}
			}
			
			return null;
		}
		
		private String getXPath(TreeNode node){
			String uiXPath = node.getXpath();
			return uiXPath.substring(uiXPath.indexOf(":")+1);
		}
	}
	
	/*class FindNodeUtil{
		
		TreeNode findLoopPathNode(TreeNode node){
			if(node == null){
				return null;
			}
			if(node.isLoop()) {
				return node;
			}
			for(TreeNode tmpNode : node.getChildren()) {
				TreeNode resultNode = findLoopPathNode(tmpNode);
				if(resultNode!=null){
					return resultNode;
				}
			}
			
			return null;
		}
		
		void findXpathArray(TreeNode node, String loopPath, List<String> resultList, java.util.Map<String, String> map) {
			if(node.getOutgoingConnections().size()>0 || node.getLookupOutgoingConnections().size()>0){
				String xpath = node.getXpath();
				String result = null;
				if(xpath==null || ("").equals(xpath)){
				} else if(xpath.equals(loopPath)){ // get the value in the loop elment
					result = ".";
				} else if(xpath.contains(loopPath)){ // find the loop path
					result = xpath.substring(loopPath.length() + 1);
				} else {
					StringBuilder resultBuff = new StringBuilder();
					String tmp = loopPath;
					
					while(!xpath.contains(tmp)){
						int index = tmp.lastIndexOf("/");
						if(index<0){ break; }
						tmp = tmp.substring(0,index-1);
						resultBuff.append("../");
					}
					if(tmp.lastIndexOf("/") < 0 ){
						System.out.println("Loop Path is not set or loop Path is invalid");
					}else{
						resultBuff.append(xpath.substring(tmp.length() + 1));
					}
					result = resultBuff.toString();
				}
				resultList.add(result);
				map.put(result, node.getXpath());
			} else {
    			for(TreeNode tmpNode : node.getChildren()) {
    				findXpathArray(tmpNode, loopPath, resultList, map);
    			}
			}
		}
	}*/

	class TreeUtil{
		//get all nodes whose expression is editable;
		void getAllLeaf(TreeNode node,List<TreeNode> result) {
			EList<TreeNode> children = node.getChildren();
			if(children==null || children.size() == 0) {
				result.add(node);//leaf is editable
			} else {
				boolean editableAtExpression = true;
				for(TreeNode child : children) {
					if(child!=null) {
						//attribute and namespace are not treat as subnode , so the expression of treeNode should be editable.
						if(NodeType.ATTRIBUT != child.getNodeType() && NodeType.NAME_SPACE != child.getNodeType()) {
							editableAtExpression = false;
						}
						getAllLeaf(child,result);
					}
				}
				
				if(editableAtExpression) {
					result.add(node);
				}
			}
		}
		
		boolean isRootLoop(TreeNode root) {
			if(root == null) return false;
			List<TreeNode> children = root.getChildren();
			if(children == null || children.size() == 0) {
				return false;
			}
			TreeNode realRoot = children.get(0);
			return realRoot.isLoop();
		}
	}
	
	static class XMLOrderUtil {
		
		int groupCount = 0;
		
		int getGroupCount(OutputTreeNode rootNode) {
			groupCount = 0;
			countGroupNode(rootNode);
			return groupCount;
		}
		
		static int getCurrOrder(OutputTreeNode currNode) {
			int currOrder = 0;
			if(isGroupOrLoopInMain(currNode)) {
				OutputTreeNode parent = (OutputTreeNode)currNode.eContainer();
				if(parent!=null) {
					EList<TreeNode> children = parent.getChildren();
					//the order not containing namespace and attribute
					for(TreeNode child : children) {
						NodeType nodeType = child.getNodeType();
						if(nodeType != NodeType.ELEMENT) {
							continue;
						}
						if(currNode.equals(child)) {
							break;
						} 
						currOrder++;
					}
				}
			}
			return currOrder;
		}
		
		static int getCurrPos(OutputTreeNode currNode) {
			int currPos = 0;
			if(isGroupOrLoopInMain(currNode)) {
				OutputTreeNode parent = (OutputTreeNode)currNode.eContainer();
				OutputTreeNode tmpNode = parent;
				while(tmpNode!=null && isGroupOrLoopInMain(tmpNode)){
    				currPos++;
    				tmpNode = (OutputTreeNode)tmpNode.eContainer();
    			}
			}
			return currPos;
		}
		
		private static boolean isGroupOrLoopInMain(OutputTreeNode currNode) {
			return currNode.isMain() && (currNode.isGroup() || currNode.isLoop());
		}
		
		private void countGroupNode(OutputTreeNode node) {
			EList<TreeNode> children = node.getChildren();
			
			if(children==null || children.size() == 0) {
				//it is impossible that leaf is Group.
				
			} else {
				//branch maybe Group
				if(node.isGroup()) {
					groupCount++;
				}
				
				for(TreeNode child : children) {
					if(child!=null) {
						countGroupNode((OutputTreeNode)child);
					}
				}
			}
		}
		
		
		
	}
	
	public INode searchSubProcessStartNode(IConnection connection) {
       	INode source = connection.getSource();
    	INode subprocessStartNode = null;
    	if(source != null) {
			String searchedComponentName = source.getUniqueName();
			List<? extends INode> generatedNodes = source.getProcess().getGeneratingNodes();
			for(INode loopNode : generatedNodes) {
				if(loopNode.getUniqueName().equals(searchedComponentName)) {
					subprocessStartNode = loopNode.getSubProcessStartNode(false);
				}
			}
		}
		return subprocessStartNode;
	}
	
    public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    stringBuffer.append(TEXT_2);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	XmlMapComponent node = (XmlMapComponent) codeGenArgument.getArgument();
	GenerationManager gm =  (GenerationManager)node.initGenerationManager();
	String cid = node.getUniqueName();
	
	String uniqueNameComponent = cid.replaceAll("_TXMLMAP_IN", "");
	uniqueNameComponent = uniqueNameComponent.replaceAll("_TXMLMAP_OUT", "");
	
	boolean checkingSyntax = codeGenArgument.isCheckingSyntax();
	String cr = "\n";
	
	XmlMapData xmlMapData=(XmlMapData)ElementParameterParser.getObjectValueXMLTree(node);

	
	EList<InputXmlTree> inputTablesWithInvalid = xmlMapData.getInputTrees();
	EList<OutputXmlTree> outputTables = xmlMapData.getOutputTrees();
	EList<VarTable> varsTables = xmlMapData.getVarTables();

	String rejectedInnerJoin = "rejectedInnerJoin_" + cid;
	String rejectedDocInnerJoin = "rejectedDocInnerJoin_" + cid;
	String rejected = "rejected_" + cid;
	String mainRowRejected = "mainRowRejected_" + cid;
	
//	boolean isLookup = false;
	boolean isXpathFromLookup = true;
	boolean isExpressionEmpty = true;
	boolean hasOutgoingConnection = false;
	boolean hasDocumentInMainInputTable = false;
	boolean atLeastOneInputTableWithInnerJoin = false;
	boolean isPlainNode = true;
//	boolean isPlainNodeInLookup = false;
	boolean hasValidLookupTables = false;
	int closeWhileBlockCount = 0;
	boolean hasDocumentGlobal = false;
	
//	boolean hasMainFilter = false;
//	boolean mainFilterAlreadyDone = false;
	
	List<IConnection> outputConnections = (List<IConnection>) node.getOutgoingConnections();
	Map<String, IConnection> nameToOutputConnection = new HashMap<String, IConnection>();
    for (IConnection connection : outputConnections) {
	  		nameToOutputConnection.put(connection.getName(), connection);
	}
    
	List<IConnection> inputConnections = (List<IConnection>) node.getIncomingConnections();
	HashMap<String, IConnection> hNameToConnection = new HashMap<String, IConnection>();
	for(IConnection connection : inputConnections){
		 hNameToConnection.put(connection.getName(), connection);
	}
	
	List<InputXmlTree> inputTables = new ArrayList<InputXmlTree>();
	for(int i=0; i<inputTablesWithInvalid.size(); i++){
		InputXmlTree  currentTree = inputTablesWithInvalid.get(i);
		if(hNameToConnection.get(currentTree.getName()) != null){
			inputTables.add(currentTree);
		}
	}
	
	boolean hasDocumentInAnyLookupTable = false;
	
	int lstSizeInputs = inputTables.size();
	for(int i = 1; i<lstSizeInputs; i++){
		InputXmlTree inputTable = (InputXmlTree)inputTables.get(i);
		EList<TreeNode> treeNodes = inputTable.getNodes();
		if(treeNodes!=null) {
			for(TreeNode treeNode : treeNodes) {
				if("id_Document".equals(treeNode.getType())) {
					hasDocumentInAnyLookupTable = true;
					break;
				}
			}
		}
	}
	
//	HashMap<String, TreeNode> hInputTableNodes = new HashMap<String, TreeNode>();
	int sizeInputTables = inputTables.size();
	InputXmlTree mainInputTable = null;  // the main input table
	
//	String mainInputTableName = null;
//	IConnection realMainConnection = null;
//	java.util.Map<String, String> xpathTypeMap = null;
//	String str  = "";
//	TreeNode currentMainNode = null;
//	boolean findNext = false;

//	String nextLookupName = null;
//	List<IMetadataColumn> nextLookupColumnsKeys = new ArrayList<IMetadataColumn>();
 
//	boolean hasPersistentLookup = false;
//	int indexLastPersistentSortedTable = -1;
	 
	ArrayList<InputXmlTree> inputTablesWithInnerJoin = new ArrayList<InputXmlTree>();
	if(inputConnections==null || inputConnections.size() < 1) {
		return "";
	}
	
    stringBuffer.append(TEXT_3);
    stringBuffer.append( rejectedInnerJoin );
    stringBuffer.append(TEXT_4);
    stringBuffer.append( rejectedDocInnerJoin );
    stringBuffer.append(TEXT_5);
    stringBuffer.append( mainRowRejected );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    
	if(inputTables==null || inputTables.size()==0) return "";
	for(IConnection conn : inputConnections) { // find the input main table
		InputXmlTree inputTable = inputTables.get(0);
		EConnectionType connectionType = conn.getLineStyle();
		if (connectionType == EConnectionType.FLOW_MAIN) {
			if(!inputTable.isLookup()) { // lookup is used to sign it is lookup table
    			mainInputTable = inputTable;
    			break;
			}
		}
	}
	if(mainInputTable != null) {
		
		List<TreeNode> allNodes = new ArrayList<TreeNode>();
		for(InputXmlTree inputTable : inputTables) {
			allNodes.addAll(inputTable.getNodes());
		}
		
		MatchXmlTreeExpr matchXmlTreeExpr = new MatchXmlTreeExpr(allNodes,cid);
		for(OutputXmlTree outputTable : outputTables) {
			String outputTableName = outputTable.getName();
			if(nameToOutputConnection.get(outputTableName) == null) {
				continue;
			}
			for(OutputTreeNode outputNode: outputTable.getNodes()) {
				if(("id_Document").equals(outputNode.getType())){
				
    stringBuffer.append(TEXT_8);
    stringBuffer.append(outputTableName );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(outputTableName );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(outputTableName );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(outputTableName );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(outputNode.getName());
    stringBuffer.append(TEXT_14);
    
					break;
				} // if(docuemnt)
			}// for(outputNode)
		} // for (outputXMLTree)
		
    stringBuffer.append(TEXT_15);
    
		String mainInputName = mainInputTable.getName();
		for(TreeNode tmpNode : mainInputTable.getNodes()){
			boolean isInnerJoin = true;
//			currentMainNode = tmpNode;

			if(tmpNode.getType().equals("id_Document")){
				isPlainNode = false;
			}
			
			if(("id_Document").equals(tmpNode.getType())) { // find the type:document node.
				hasDocumentInMainInputTable = true;
				//find the loop path
				XPathHelper xpathHelper = new XPathHelper(tmpNode);
				
				if(!xpathHelper.hasLoopNode()){
					stringBuffer.delete(0,stringBuffer.length());
					stringBuffer.append("if(true){throw new Exception(\"Loop element is missing in the input Table:" + mainInputTable.getName() +"!\");}");
					return stringBuffer.toString();
				} else {
					String loopPath = xpathHelper.getLoopNodeXPath();
					
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(loopPath );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(mainInputName );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(tmpNode.getName() );
    stringBuffer.append(TEXT_21);
    if(!xpathHelper.hasDefinedNS()){
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    }else{
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(xpathHelper.buildNSMapping("xmlNameSpaceMap"+"_"+cid));
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_41);
    }
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_50);
    stringBuffer.append( rejectedInnerJoin );
    stringBuffer.append(TEXT_51);
    stringBuffer.append( rejectedDocInnerJoin );
    stringBuffer.append(TEXT_52);
    stringBuffer.append(mainRowRejected);
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_55);
    
						Map<String,String> outNodesXPath = xpathHelper.getOutNodesXPath();
						int i = 0;
						for (Object key : outNodesXPath.keySet()) { 
						    Object val = outNodesXPath.get(key);
							if(!xpathHelper.hasDefinedNS()){
							
    stringBuffer.append(TEXT_56);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_60);
    stringBuffer.append(val );
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_62);
    }else{
    stringBuffer.append(TEXT_63);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_66);
    stringBuffer.append(val);
    stringBuffer.append(TEXT_67);
    }
    stringBuffer.append(TEXT_68);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_71);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_73);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_74);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_76);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_78);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_81);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_82);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_83);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_84);
    stringBuffer.append(key );
    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_86);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_87);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_91);
    stringBuffer.append(key );
    stringBuffer.append(TEXT_92);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_93);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_95);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_96);
    
							i++;
						}
				}
	   		}// if(document)
		}// for()
		
    stringBuffer.append(TEXT_97);
    
	//	System.out.println("size of vartable begin:" + varTables.size());
	TreeUtil treeUtil = new TreeUtil();
	for (int iInputTable = 0; iInputTable < sizeInputTables; iInputTable++) {
		hasOutgoingConnection = false;//reset the var for every lookup table 
		InputXmlTree nextInputTable = inputTables.get(iInputTable);
		if(!atLeastOneInputTableWithInnerJoin) {
			if(nextInputTable.isInnerJoin()) {
				atLeastOneInputTableWithInnerJoin = true;
			}
		}

    stringBuffer.append(TEXT_98);
    stringBuffer.append(TEXT_99);
    
	EConnectionType connectionType = null;
	if (nextInputTable != null && nextInputTable.isLookup()) { 
		IConnection connection =  hNameToConnection.get(nextInputTable.getName());

		if(connection == null){
			continue;
		}else{
			connectionType = connection.getLineStyle();
			
			if(connectionType == EConnectionType.FLOW_REF) {
				IMetadataTable metadataTable = connection.getMetadataTable();
				String tableName = nextInputTable.getName();
				String lookupMode = nextInputTable.getLookupMode();
				boolean isCacheOrReloadLookupMode = org.talend.designer.xmlmap.model.tree.LOOKUP_MODE.CACHE_OR_RELOAD.name().equals(lookupMode);
				boolean isOneOfReloadLookupMode = isCacheOrReloadLookupMode ||
					   org.talend.designer.xmlmap.model.tree.LOOKUP_MODE.RELOAD.name().equals(lookupMode);
				INode lookupSubProcessStartNode = searchSubProcessStartNode(connection);
			  	String lookupSubProcessStartNodeName = lookupSubProcessStartNode.getUniqueName();
				boolean isInnerJoin = false;
				if (nextInputTable.isInnerJoin()) {
					isInnerJoin = true;
			    	inputTablesWithInnerJoin.add(nextInputTable);
			 	}
			    //  nextJoinedTableNames =tableName+  "__" + tableName; 
			    // System.out.println("nextJoinedTableNames : " + nextJoinedTableNames);
				
    stringBuffer.append(TEXT_100);
    stringBuffer.append( nextInputTable );
    stringBuffer.append(TEXT_101);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_102);
    
				String expressionKey = "";
				List<IMetadataColumn> listColumns = metadataTable.getListColumns();
				ArrayList<String> keysNames = new ArrayList<String>();
				ArrayList<String> keysValues = new ArrayList<String>();
				ArrayList<String> keysTypes = new ArrayList<String>();
				EList<TreeNode> externalTreeNodes = nextInputTable.getNodes();
				
				boolean findFromBasicType = false;
				boolean findFromDocumentType = false;
				String documentColumn = "";
				boolean hasOutputFromLookupDoc = false;//to output or another lookup
				Map<String,String> outNodesXPath = new HashMap<String,String>();//output contain(1.lookup table to output table 2.lookup table to lookup table)
				Map<String,String> lookupInputNodesXPath = new HashMap<String,String>();
				String loopXPath = "";
				boolean isMultipleResult = false;
				boolean existXpathKeyFromCurrentLookup = false;
				boolean hasExpressionKey = false;
				//cache the type and pattern information for sub columns in Document column
				Map<String,String> xpathToPatternMap = null;
				Map<String,String> xpathToTypeMap = null;
				
				for(int j = 0; j < listColumns.size(); j++){
					IMetadataColumn column = listColumns.get(j);
					String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
				    String columnName = column.getLabel();
				    TreeNode  externalInputTableEntry = externalTreeNodes.get(j);
				    XPathHelper xpathHelper;
				    if("Document".equals(typeToGenerate)) {
				    	xpathHelper = new XPathHelper(externalInputTableEntry);
						loopXPath = xpathHelper.getLoopNodeXPath();
						outNodesXPath = xpathHelper.getOutNodesXPath();
						lookupInputNodesXPath = xpathHelper.getLookupInputNodesXPath();
						xpathToPatternMap = xpathHelper.getXpathToPatternMap();
						xpathToTypeMap = xpathHelper.getXpathToTypeMap();
						
    stringBuffer.append(TEXT_103);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_104);
    stringBuffer.append(xpathHelper.buildNSMapping("xmlNameSpaceMap"+"_"+tableName));
    stringBuffer.append(TEXT_105);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_106);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_107);
    
				    }
				    List<TreeNode> allLeaf = new ArrayList<TreeNode>();
				    treeUtil.getAllLeaf(externalInputTableEntry,allLeaf); 
				    
				    for(TreeNode leaf : allLeaf) { 
					    if (leaf != null) {
				    		if("Document".equals(typeToGenerate)) {
				    			documentColumn = columnName;
				    			EList<Connection> outputs = leaf.getOutgoingConnections();
				    			EList<LookupConnection> lookupOutputs = leaf.getLookupOutgoingConnections();
					    		if(outputs!=null && outputs.size()!=0) {
					    			hasOutputFromLookupDoc = true;
					    			hasOutgoingConnection = true;
					    		} else if(lookupOutputs!=null && lookupOutputs.size()!=0) {
					    			hasOutputFromLookupDoc = true;
					    			hasOutgoingConnection = true;
					    		}
				    		}
				    	  	if(leaf.getOutgoingConnections().size() > 0){
						    	hasOutgoingConnection = true;
						    }
						    if(leaf.getLookupOutgoingConnections().size() > 0) {
						    	hasOutgoingConnection = true;
						    }
						    
				    	  	expressionKey = leaf.getExpression();
							if (expressionKey != null && !"".equals(expressionKey.trim())) {
								hasExpressionKey = true;
							    String resultExpression = null;
							    if("Document".equals(typeToGenerate)) {
							    	documentColumn = columnName;
							    	findFromDocumentType = true;
							    	keysNames.add(leaf.getXpath());
							    } else {
							    	keysNames.add(columnName);
							    	findFromBasicType = true;
							    }
							    if(matchXmlTreeExpr != null){
							    	resultExpression = matchXmlTreeExpr.generateExprCode(expressionKey);
							    }
							    if(resultExpression != null && !("").equals(resultExpression)){
							    	keysValues.add(resultExpression);
							    	if(resultExpression.indexOf("/") != -1) {
							    		existXpathKeyFromCurrentLookup = true;
							    	}
							    } else {
							    	keysValues.add(expressionKey);
							    }
							    keysTypes.add(typeToGenerate);
							    boolean javaPrimitiveKeyColumn = JavaTypesManager.isJavaPrimitiveType(typeToGenerate);
							}
					    }
					}
				}
				
				isXpathFromLookup = existXpathKeyFromCurrentLookup;
				String[] aKeysNames = keysNames.toArray(new String[0]);
				String[] aKeysValues = keysValues.toArray(new String[0]);
				String className = tableName + "Struct";
				
				hasValidLookupTables = true;
				
				String matchingMode = nextInputTable.getMatchingMode();
				if(matchingMode == null) {
					if(aKeysValues.length > 0) {
						matchingMode = "UNIQUE_MATCH";
					} else {
						matchingMode = "ALL_ROWS";
					}
				}
				if("ALL_ROWS".equals(matchingMode) || "ALL_MATCHES".equals(matchingMode)) {
					isMultipleResult = true;
				}
				
    stringBuffer.append(TEXT_108);
    if(hasOutgoingConnection || hasExpressionKey){ 
    stringBuffer.append(TEXT_109);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_110);
    stringBuffer.append( className );
    stringBuffer.append(TEXT_111);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_112);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_113);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_114);
    } 
    stringBuffer.append(TEXT_115);
    
				if( null != aKeysValues && aKeysValues.length != 0 ){
					isExpressionEmpty = false;
					
					if(aKeysValues.length > 0) {//TD114
					 	if(isCacheOrReloadLookupMode) {
			   			
    stringBuffer.append(TEXT_116);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_117);
    	
			  			} 
						for (int iKeyName = 0; iKeyName < aKeysNames.length; iKeyName++) {
							String typeToGenerate = keysTypes.get(iKeyName);
							boolean javaPrimitiveKeyColumn = JavaTypesManager.isJavaPrimitiveType(typeToGenerate);
				   			if("Document".equals(typeToGenerate)) {
				   				String xpath = aKeysNames[iKeyName];
				   				String relativeXPath = lookupInputNodesXPath.get(xpath);
							
    stringBuffer.append(TEXT_118);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_119);
    stringBuffer.append(relativeXPath);
    stringBuffer.append(TEXT_120);
    stringBuffer.append(aKeysValues[iKeyName] );
    stringBuffer.append(TEXT_121);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_122);
    stringBuffer.append(relativeXPath);
    stringBuffer.append(TEXT_123);
    stringBuffer.append(xpathToPatternMap.get(xpath));
    stringBuffer.append(TEXT_124);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_125);
    stringBuffer.append(relativeXPath);
    stringBuffer.append(TEXT_126);
    stringBuffer.append(xpathToTypeMap.get(xpath));
    stringBuffer.append(TEXT_127);
    					
								if(isCacheOrReloadLookupMode) {
								
    stringBuffer.append(TEXT_128);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_129);
    stringBuffer.append(aKeysValues[iKeyName] );
    stringBuffer.append(TEXT_130);
    
								}
				   			} else {
							
    stringBuffer.append(TEXT_131);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_132);
    stringBuffer.append( aKeysNames[iKeyName] );
    stringBuffer.append(TEXT_133);
    stringBuffer.append(aKeysValues[iKeyName] );
    stringBuffer.append(TEXT_134);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_135);
    		
					   		}
						}// end for aKeysNames
					}//TD114
					
					if(findFromBasicType && isCacheOrReloadLookupMode) {
					
    stringBuffer.append(TEXT_136);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_137);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_138);
    	
					} 
					
    stringBuffer.append(TEXT_139);
    
					if(hasOutgoingConnection || hasExpressionKey){
					
    stringBuffer.append(TEXT_140);
    stringBuffer.append( rejectedInnerJoin );
    stringBuffer.append(TEXT_141);
    if(isOneOfReloadLookupMode) {//TD110
								if(isCacheOrReloadLookupMode) {
									if(findFromBasicType) { 
									
    stringBuffer.append(TEXT_142);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_143);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_144);
     } else if(findFromDocumentType) { 
    stringBuffer.append(TEXT_145);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_146);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_147);
     } 
    stringBuffer.append(TEXT_148);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_149);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_150);
    
								}
								
    stringBuffer.append(TEXT_151);
    stringBuffer.append( lookupSubProcessStartNodeName );
    stringBuffer.append(TEXT_152);
    
								boolean isAllRows = "ALL_ROWS".equals(nextInputTable.getMatchingMode());
					            if(nextInputTable.isPersistent()) {
    stringBuffer.append(TEXT_153);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_154);
    stringBuffer.append( isAllRows ? "" : "Sorted" );
    stringBuffer.append(TEXT_155);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_156);
    stringBuffer.append( isAllRows ? "" : "Sorted" );
    stringBuffer.append(TEXT_157);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_158);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_159);
    
							    } else {
							    
    stringBuffer.append(TEXT_160);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_161);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_162);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_163);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_164);
    
							    }
							    
    stringBuffer.append(TEXT_165);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_166);
     if(findFromBasicType) { 
    stringBuffer.append(TEXT_167);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_168);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_169);
     } else if(findFromDocumentType) { 
    stringBuffer.append(TEXT_170);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_171);
     } 
						    	if(isCacheOrReloadLookupMode) {
						    	
    stringBuffer.append(TEXT_172);
    	
							    }
							}//TD110
							
    stringBuffer.append(TEXT_173);
     if(!isOneOfReloadLookupMode) {
    stringBuffer.append(TEXT_174);
    if(findFromBasicType) {
    stringBuffer.append(TEXT_175);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_176);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_177);
     } else if(findFromDocumentType) { 
    stringBuffer.append(TEXT_178);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_179);
     } 
    stringBuffer.append(TEXT_180);
     } 
    stringBuffer.append(TEXT_181);
    stringBuffer.append(tableName );
    stringBuffer.append(TEXT_182);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_183);
     if(isInnerJoin) {//inner join,not find and should reject data(not Document)
    stringBuffer.append(TEXT_184);
    stringBuffer.append( rejectedInnerJoin );
    stringBuffer.append(TEXT_185);
    stringBuffer.append( rejectedDocInnerJoin );
    stringBuffer.append(TEXT_186);
     } else {//left outter join should keep main table data,not reject anytime.
    stringBuffer.append(TEXT_187);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_188);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_189);
     } 
    stringBuffer.append(TEXT_190);
    if(isMultipleResult) {
    stringBuffer.append(TEXT_191);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_192);
    }
    stringBuffer.append(TEXT_193);
    if(isMultipleResult) {
    stringBuffer.append(TEXT_194);
    stringBuffer.append( rejectedInnerJoin );
    stringBuffer.append(TEXT_195);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_196);
     } 
    stringBuffer.append(TEXT_197);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_198);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_199);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_200);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_201);
     if(!isMultipleResult) {
    stringBuffer.append(TEXT_202);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_203);
    if(isCacheOrReloadLookupMode) {
    stringBuffer.append(TEXT_204);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_205);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_206);
     for (Object key : outNodesXPath.keySet()) { 
    stringBuffer.append(TEXT_207);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_208);
    stringBuffer.append(key);
    stringBuffer.append(TEXT_209);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_210);
    stringBuffer.append(key);
    stringBuffer.append(TEXT_211);
     } 
    stringBuffer.append(TEXT_212);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_213);
    stringBuffer.append(className);
    stringBuffer.append(TEXT_214);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_215);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_216);
     } 
    stringBuffer.append(TEXT_217);
    stringBuffer.append( rejectedInnerJoin );
    stringBuffer.append(TEXT_218);
     if(findFromDocumentType) {
    stringBuffer.append(TEXT_219);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_220);
     } 
    stringBuffer.append(TEXT_221);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_222);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_223);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_224);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_225);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_226);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_227);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_228);
     if(findFromDocumentType || hasOutputFromLookupDoc) {
    stringBuffer.append(TEXT_229);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_230);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_231);
    stringBuffer.append(documentColumn);
    stringBuffer.append(TEXT_232);
     if("FIRST_MATCH".equals(matchingMode)) {
    stringBuffer.append(TEXT_233);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_234);
     } 
    stringBuffer.append(TEXT_235);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_236);
    
								for (Object key : outNodesXPath.keySet()) { 
									Object val = outNodesXPath.get(key);
								
    stringBuffer.append(TEXT_237);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_238);
    stringBuffer.append(key);
    stringBuffer.append(TEXT_239);
    stringBuffer.append(val);
    stringBuffer.append(TEXT_240);
    
								}
								
    stringBuffer.append(TEXT_241);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_242);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_243);
    stringBuffer.append(loopXPath);
    stringBuffer.append(TEXT_244);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_245);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_246);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_247);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_248);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_249);
    stringBuffer.append(matchingMode);
    stringBuffer.append(TEXT_250);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_251);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_252);
     if(findFromDocumentType) { 
    stringBuffer.append(TEXT_253);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_254);
     } 
    stringBuffer.append(TEXT_255);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_256);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_257);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_258);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_259);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_260);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_261);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_262);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_263);
    if(isCacheOrReloadLookupMode) {
    stringBuffer.append(TEXT_264);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_265);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_266);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_267);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_268);
     } 
    stringBuffer.append(TEXT_269);
     if("FIRST_MATCH".equals(matchingMode)) {
    stringBuffer.append(TEXT_270);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_271);
     } 
    stringBuffer.append(TEXT_272);
     } 
    stringBuffer.append(TEXT_273);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_274);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_275);
    if(isCacheOrReloadLookupMode) {
    stringBuffer.append(TEXT_276);
    if(findFromDocumentType) {
    stringBuffer.append(TEXT_277);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_278);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_279);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_280);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_281);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_282);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_283);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_284);
     } else { 
    stringBuffer.append(TEXT_285);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_286);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_287);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_288);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_289);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_290);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_291);
     } 
    stringBuffer.append(TEXT_292);
     } 
    stringBuffer.append(TEXT_293);
     if("FIRST_MATCH".equals(matchingMode) && (findFromDocumentType || hasOutputFromLookupDoc)) {
    stringBuffer.append(TEXT_294);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_295);
     } 
    stringBuffer.append(TEXT_296);
     if(isInnerJoin && findFromDocumentType) {
    stringBuffer.append(TEXT_297);
    stringBuffer.append( rejectedDocInnerJoin );
    stringBuffer.append(TEXT_298);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_299);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_300);
    stringBuffer.append( rejectedInnerJoin );
    stringBuffer.append(TEXT_301);
    stringBuffer.append( rejectedDocInnerJoin );
    stringBuffer.append(TEXT_302);
     } 
    stringBuffer.append(TEXT_303);
     if(!isInnerJoin && findFromDocumentType) { 
    stringBuffer.append(TEXT_304);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_305);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_306);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_307);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_308);
     } 
    stringBuffer.append(TEXT_309);
     if(hasOutputFromLookupDoc && !findFromDocumentType) {//for the case : not lookup doc,but doc output exists
    stringBuffer.append(TEXT_310);
     } 
    stringBuffer.append(TEXT_311);
     } 
    stringBuffer.append(TEXT_312);
    if(isMultipleResult) {//TD114
    stringBuffer.append(TEXT_313);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_314);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_315);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_316);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_317);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_318);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_319);
    if(isCacheOrReloadLookupMode) {
    stringBuffer.append(TEXT_320);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_321);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_322);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_323);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_324);
     } 
    stringBuffer.append(TEXT_325);
    stringBuffer.append( rejectedInnerJoin );
    stringBuffer.append(TEXT_326);
     if(findFromDocumentType) {
    stringBuffer.append(TEXT_327);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_328);
     } 
    stringBuffer.append(TEXT_329);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_330);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_331);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_332);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_333);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_334);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_335);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_336);
     if(findFromDocumentType || hasOutputFromLookupDoc) {//TA110
    stringBuffer.append(TEXT_337);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_338);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_339);
    stringBuffer.append(documentColumn);
    stringBuffer.append(TEXT_340);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_341);
    
									for (Object key : outNodesXPath.keySet()) { 
										Object val = outNodesXPath.get(key);
									
    stringBuffer.append(TEXT_342);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_343);
    stringBuffer.append(key);
    stringBuffer.append(TEXT_344);
    stringBuffer.append(val);
    stringBuffer.append(TEXT_345);
    
									}
									
    stringBuffer.append(TEXT_346);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_347);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_348);
    stringBuffer.append(loopXPath);
    stringBuffer.append(TEXT_349);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_350);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_351);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_352);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_353);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_354);
    stringBuffer.append(matchingMode);
    stringBuffer.append(TEXT_355);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_356);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_357);
     if(findFromDocumentType) { 
    stringBuffer.append(TEXT_358);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_359);
     } 
    stringBuffer.append(TEXT_360);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_361);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_362);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_363);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_364);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_365);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_366);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_367);
    if(isCacheOrReloadLookupMode) {
    stringBuffer.append(TEXT_368);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_369);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_370);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_371);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_372);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_373);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_374);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_375);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_376);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_377);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_378);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_379);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_380);
     } 
    stringBuffer.append(TEXT_381);
     }//TA110 
    stringBuffer.append(TEXT_382);
    if(!findFromDocumentType){
    stringBuffer.append(TEXT_383);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_384);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_385);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_386);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_387);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_388);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_389);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_390);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_391);
    }
    stringBuffer.append(TEXT_392);
     if(findFromDocumentType && !isInnerJoin) {
    stringBuffer.append(TEXT_393);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_394);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_395);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_396);
     } 
    stringBuffer.append(TEXT_397);
     if(findFromDocumentType && isInnerJoin) {
    stringBuffer.append(TEXT_398);
    stringBuffer.append( rejectedDocInnerJoin );
    stringBuffer.append(TEXT_399);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_400);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_401);
    stringBuffer.append( rejectedInnerJoin );
    stringBuffer.append(TEXT_402);
    stringBuffer.append( rejectedDocInnerJoin );
    stringBuffer.append(TEXT_403);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_404);
    stringBuffer.append( rejectedInnerJoin );
    stringBuffer.append(TEXT_405);
     } 
    stringBuffer.append(TEXT_406);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_407);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_408);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_409);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_410);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_411);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_412);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_413);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_414);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_415);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_416);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_417);
     for (Object key : outNodesXPath.keySet()) { 
    stringBuffer.append(TEXT_418);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_419);
    stringBuffer.append(key);
    stringBuffer.append(TEXT_420);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_421);
    stringBuffer.append(key);
    stringBuffer.append(TEXT_422);
     } 
    stringBuffer.append(TEXT_423);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_424);
    stringBuffer.append(className);
    stringBuffer.append(TEXT_425);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_426);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_427);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_428);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_429);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_430);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_431);
     
				  			gm.addBlocksCodeToClose(new BlockCode("close loop of lookup '" + tableName +"' // G_TM_M_002"));
				  		}//TD114 
				  		
    stringBuffer.append(TEXT_432);
    				
					}
					
    stringBuffer.append(TEXT_433);
    
				} // end of expression key checking
				else {
	
					isExpressionEmpty = true;
					if(hasOutgoingConnection){
					
    stringBuffer.append(TEXT_434);
    stringBuffer.append( rejectedInnerJoin );
    stringBuffer.append(TEXT_435);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_436);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_437);
    stringBuffer.append( tableName );
    stringBuffer.append(TEXT_438);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_439);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_440);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_441);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_442);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_443);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_444);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_445);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_446);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_447);
    
					}
				}
			}
		}
	} else {
		isXpathFromLookup = false;
	}
	
    
	
	if(hasOutgoingConnection && isExpressionEmpty) {
		closeWhileBlockCount++;
	}
	
	} // end for lookup tables
	
	// /////////////////////////////////////////////////////////////////////////////////////////////////////
        // /////////////////////////////////////////////////////////////////////////////////////////////////////
        // VARIABLES
        // 
        
        
    stringBuffer.append(TEXT_448);
    
        
        for (VarTable varsTable : varsTables) {
            EList<VarNode> varsTableEntries = varsTable.getNodes();
            if (varsTableEntries == null) {
                continue;
            }
            String varsTableName = varsTable.getName();
            String instanceVarName = varsTableName + "__" + uniqueNameComponent;
            String className = instanceVarName + "__Struct";

            
    stringBuffer.append( cr + className + " " + varsTableName + " = " + instanceVarName + ";" );
    
            
            for (VarNode varsTableEntry : varsTableEntries) {
                String varsColumnName = varsTableEntry.getName();
                String varExpression = varsTableEntry.getExpression();
                if (varExpression == null || varExpression.trim().length() == 0) {
                    varExpression = JavaTypesManager.getDefaultValueFromJavaIdType(varsTableEntry.getType(),
                            varsTableEntry.isNullable());
                }
                
				String resultExpression = "";
				if(matchXmlTreeExpr != null)
					resultExpression = matchXmlTreeExpr.generateExprCode(varExpression);
				if(resultExpression!=null && !("").equals(resultExpression)){

    stringBuffer.append(TEXT_449);
    stringBuffer.append( cr + gm.getGeneratedCodeTableColumnVariable(uniqueNameComponent, varsTableName, varsColumnName, false) + " = " + resultExpression
                        + ";" );
    
				}
            }
        }
	
	// /////////////////////////////////////////////////////////////////////////////////////////////////////
        // /////////////////////////////////////////////////////////////////////////////////////////////////////

        // /////////////////////////////////////////////////////////////////////////////////////////////////////
        // /////////////////////////////////////////////////////////////////////////////////////////////////////
        // OUTPUTS
        // 
        

    stringBuffer.append(TEXT_450);
    

		StringBuilder sb = new StringBuilder();
		
		ArrayList<OutputXmlTree> outputTablesSortedByReject = new ArrayList<OutputXmlTree>(outputTables);
		// sorting outputs : rejects tables after not rejects table
		Collections.sort(outputTablesSortedByReject, new Comparator<OutputXmlTree>() {

			public int compare(OutputXmlTree o1, OutputXmlTree o2) {
				if (o1.isReject() != o2.isReject()) {
					if (o1.isReject()) {
						return 1;
					} else {
						return -1;
					}
				}
				if (o1.isRejectInnerJoin() != o2.isRejectInnerJoin()) {
					if (o1.isRejectInnerJoin()) {
						return 1;
					} else {
						return -1;
					}
				}
				return 0;
			}

		});
		
		boolean lastValueReject = false;
        boolean oneFilterForNotRejectTable = false;
        boolean allNotRejectTablesHaveFilter = true;
        boolean atLeastOneReject = false;
//        boolean atLeastOneRejectInnerJoin = false;
        boolean closeTestInnerJoinConditionsBracket = false;

        for (IConnection outputConnection : outputConnections) {
            nameToOutputConnection.put(outputConnection.getName(), outputConnection);
        }

        int lstSizeOutputs = outputTablesSortedByReject.size();
        // ///////////////////////////////////////////////////////////////////
        // init of allNotRejectTablesHaveFilter and atLeastOneReject
        String lastValidOutputTableName = null;
		
		for (int i = 0; i < lstSizeOutputs; i++) {
            OutputXmlTree outputTable = (OutputXmlTree) outputTablesSortedByReject.get(i);

            String outputTableName = outputTable.getName();

//            if (outputTable.isRejectInnerJoin()) {
//               atLeastOneRejectInnerJoin = true;
//            }
            EList<OutputTreeNode> columnEntries = outputTable.getNodes();
            boolean hasFilter = outputTable.isActivateExpressionFilter() && outputTable.getExpressionFilter() != null && !("").equals(outputTable.getExpressionFilter().trim());
            if (columnEntries != null && columnEntries.size() > 0) {
                if (!hasFilter && !(outputTable.isReject() || outputTable.isRejectInnerJoin())) {
                    if(!outputTable.isErrorReject()){
                    	allNotRejectTablesHaveFilter = false;
                    }
                }
                if (outputTable.isReject()) {
                    atLeastOneReject = true;
                }
            }
			IConnection outputConnection = nameToOutputConnection.get(outputTableName);            
			if (outputConnection != null) {
				sb.append(cr + outputTableName + " = null;");
			}
			if(checkingSyntax 
			|| !checkingSyntax && outputConnection != null) {
				lastValidOutputTableName = outputTableName;
			}
			

        }
        // ///////////////////////////////////////////////////////////////////
		sb.append(cr);

        if (allNotRejectTablesHaveFilter && atLeastOneReject) {
            // write rejected = false;
            sb.append(cr + "boolean " + rejected + " = true;");
        }

        // write conditions for inner join reject
        if (lastValidOutputTableName != null && hasValidLookupTables && lstSizeOutputs > 0 && atLeastOneInputTableWithInnerJoin) {
            sb.append(cr + "if(");
            sb.append("!" + rejectedInnerJoin);
            sb.append(" ) {");
            closeTestInnerJoinConditionsBracket = true;
        }

        // ///////////////////////////////////////////////////////////////////
        // run through output tables list for generating intilization of outputs arrays
        int dummyVarCounter = 0;
        for (int indexCurrentTable = 0; indexCurrentTable < lstSizeOutputs; indexCurrentTable++) {
            OutputXmlTree outputTable = (OutputXmlTree) outputTablesSortedByReject.get(indexCurrentTable);
            EList<OutputTreeNode> outputTableEntries = outputTable.getNodes();
            String outputTableName = outputTable.getName();
            
            boolean connectionExists = true;
            IConnection outputConnection =null;
            outputConnection = nameToOutputConnection.get(outputTableName);
            
            HashedMetadataTable hashedMetadataTable = null;
            if (outputTableEntries == null || outputConnection == null) {
                connectionExists = false;
            } else {
            	hashedMetadataTable = new HashedMetadataTable(outputConnection.getMetadataTable());
            }

            boolean currentIsReject = outputTable.isReject();
            boolean currentIsRejectInnerJoin = outputTable.isRejectInnerJoin();

            boolean hasExpressionFilter = outputTable.isActivateExpressionFilter() && outputTable.getExpressionFilter() != null && !("").equals(outputTable.getExpressionFilter().trim());
            boolean rejectValueHasJustChanged = lastValueReject != (currentIsReject || currentIsRejectInnerJoin);
            oneFilterForNotRejectTable = !(currentIsReject || currentIsRejectInnerJoin) && hasExpressionFilter;

            if (rejectValueHasJustChanged) {
                if (closeTestInnerJoinConditionsBracket) {
                    sb.append(cr + "} // closing inner join bracket (1)");
                    if (atLeastOneReject && allNotRejectTablesHaveFilter) {
                        sb.append(cr + " else {");
                        sb.append(cr + rejected + " = false;");
                        sb.append(cr + "} // closing else inner join bracket (1)");
                    }
                    closeTestInnerJoinConditionsBracket = false;
                }
            }

            // No connection matching and no checking errors
            if (connectionExists || checkingSyntax) {
	            if (rejectValueHasJustChanged) {
	                sb.append(cr + "// ###### START REJECTS ##### ");
	            }

	            // write filters conditions and code to execute
	            if (!currentIsReject && !currentIsRejectInnerJoin || rejectValueHasJustChanged
	                    && oneFilterForNotRejectTable || currentIsReject && allNotRejectTablesHaveFilter
	                    || currentIsRejectInnerJoin && atLeastOneInputTableWithInnerJoin || checkingSyntax) {

	                boolean closeFilterOrRejectBracket = false;
	                if (currentIsReject || currentIsRejectInnerJoin) {
	                    sb.append(cr + cr + "// # Output reject table : '" + outputTableName + "'");
	                } else {
	                    sb.append(cr + cr + "// # Output table : '" + outputTableName + "'");
	                }
	                if (hasExpressionFilter || currentIsReject || currentIsRejectInnerJoin && atLeastOneInputTableWithInnerJoin) {
	                    sb.append(cr + "// # Filter conditions ");
						String ifConditions = "if( ";
	                    String rejectedTests = null;
	                    if (allNotRejectTablesHaveFilter && atLeastOneReject && currentIsReject && currentIsRejectInnerJoin
	                            && atLeastOneInputTableWithInnerJoin) {
	                        rejectedTests = rejected + " || " + rejectedInnerJoin;
	                        if (hasExpressionFilter) {
	                            rejectedTests = "(" + rejectedTests + ")";
	                        }
	                    } else if (allNotRejectTablesHaveFilter && atLeastOneReject && currentIsReject) {
	                        rejectedTests = rejected;
	                    } else if (currentIsRejectInnerJoin && atLeastOneInputTableWithInnerJoin) {
	                        rejectedTests = rejectedInnerJoin;
	                    }
	                    String filtersConditions = null;
	                    filtersConditions = outputTable.getExpressionFilter();
						if(filtersConditions != null && !("".equals(filtersConditions.trim())) && (filtersConditions.trim().length()>0)) {
							String filtersConditionsFormatted = "";
							if(matchXmlTreeExpr != null)
								filtersConditionsFormatted = matchXmlTreeExpr.generateExprCode(filtersConditions);
							if (rejectedTests == null) {
								ifConditions += cr + cr + filtersConditionsFormatted  + cr + cr;
							} else {
								ifConditions += rejectedTests + " && (" + cr + cr + filtersConditionsFormatted + cr + cr +")";
							}
						} else {
							if (rejectedTests != null) {
								ifConditions += rejectedTests;
							}
						}
	                    
	                    ifConditions += " ) {";
	
	                    sb.append(cr).append(ifConditions);
	
	                    closeFilterOrRejectBracket = true;
	                    if (allNotRejectTablesHaveFilter && !(currentIsReject || currentIsRejectInnerJoin)
	                            && atLeastOneReject) {
	                        sb.append(cr + rejected + " = false;");
	                    }
	                }

	                if (outputTableEntries != null && (!currentIsReject && !currentIsRejectInnerJoin || currentIsReject || currentIsRejectInnerJoin
	                        && atLeastOneInputTableWithInnerJoin || checkingSyntax)) {
						boolean hasDocument = false;
	                    for (OutputTreeNode outputTableEntry : outputTableEntries) {
	                        String outputColumnName = outputTableEntry.getName();
	                        IMetadataColumn metadataColumnFromConn = null;
	                        if(connectionExists) {
		                        metadataColumnFromConn = hashedMetadataTable.getColumn(outputColumnName);
		                    }
	                        
	                        String outputExpression = outputTableEntry.getExpression();
							String resultExpression = "";
							if(matchXmlTreeExpr != null)
								resultExpression = matchXmlTreeExpr.generateExprCode(outputExpression);
	                        if (resultExpression == null || resultExpression.trim().length() == 0) {
	                        	if(metadataColumnFromConn == null) {
		                            resultExpression = JavaTypesManager.getDefaultValueFromJavaIdType(outputTableEntry
	    	                                .getType(), outputTableEntry.isNullable());
	    	                    } else {
		                            resultExpression = JavaTypesManager.getDefaultValueFromJavaIdType(metadataColumnFromConn
	    	                                .getTalendType(), metadataColumnFromConn.isNullable());
	    	                    }
	                        } else {
	                        
	                        	if(metadataColumnFromConn == null) {
									continue;
								}	                        
	                        
	                        }
		
	                        String assignationVar = null;
	                        if (connectionExists) {                       
	                        	assignationVar = gm.getGeneratedCodeTableColumnVariable(uniqueNameComponent, outputTableName + "_tmp",outputColumnName, false);
	                        } else {
	                        	if(metadataColumnFromConn == null) {
		                            assignationVar = JavaTypesManager.getTypeToGenerate(outputTableEntry.getType(),
	                                    outputTableEntry.isNullable())
	                                    + " dummyVar" + (dummyVarCounter++);
	                            } else {
		                            assignationVar = JavaTypesManager.getTypeToGenerate(metadataColumnFromConn
	    	                                .getTalendType(), metadataColumnFromConn.isNullable())
	                                    + " dummyVar" + (dummyVarCounter++);
	                            }
	                        }
    	                    String expression = assignationVar + " = " + resultExpression + ";";
	
							if(("id_Document").equals(outputTableEntry.getType())){
								hasDocument = true;
								hasDocumentGlobal = true;
								String typeToGenerate = JavaTypesManager.getTypeToGenerate(outputTableEntry.getType(), outputTableEntry.isNullable());
								sb.append(cr + cr +"gen_Doc_"+outputTableName+"_"+uniqueNameComponent+".generateElements(");
								if(hasDocumentInMainInputTable || hasDocumentInAnyLookupTable) {
									sb.append("treeNodeAPI_"+uniqueNameComponent+",");
								}
								sb.append(rejectedDocInnerJoin);
								for (InputXmlTree inputTable: inputTables) {
									sb.append(","+inputTable.getName());
								}
								for (VarTable var : varsTables) {
									sb.append(","+var.getName());
								}

								sb.append(");" + cr + cr);
								sb.append("if("+assignationVar + " == null){" + cr);
								sb.append(assignationVar + " = new " + typeToGenerate + "();" + cr);
								sb.append(assignationVar + ".setDocument(gen_Doc_"+outputTableName+"_"+uniqueNameComponent+".getDocument());" + cr);
								
								sb.append("}" + cr);
							} else {
								sb.append(cr).append(expression);
							}
	                    } // for entries
	
	                    if (connectionExists) {
							if(hasDocument && !isPlainNode) {
								sb.append("if(nb_line_"+uniqueNameComponent+">=nodeList_"+uniqueNameComponent+".size())");
							}
							sb.append(cr + outputTableName + " = " + outputTableName + "_tmp;");
							if(hasDocument)
								sb.append(cr + outputTableName + "_save = " + outputTableName + "_tmp;");
							
							
	                    }
	                        
	                                                
	                }
					if (closeFilterOrRejectBracket) {
						sb.append(cr + "} // closing filter/reject");
					}
				}
				lastValueReject = currentIsReject || currentIsRejectInnerJoin;
			}

			// System.out.println("Output table: (2)" + outputTableName);

			boolean isLastTable = indexCurrentTable == lstSizeOutputs - 1;
			if (closeTestInnerJoinConditionsBracket 
				&& (isLastTable || !checkingSyntax && (outputTableName.equals(lastValidOutputTableName) || lastValidOutputTableName == null))) {
				sb.append(cr + "}  // closing inner join bracket (2)");
				closeTestInnerJoinConditionsBracket = false;
			}

		} // for output tables
			
		sb.append(cr + "// ###############################");

		
		sb.append(cr);
		sb.append(cr + "} // end of Var scope");



		sb.append(cr + cr + rejectedInnerJoin + " = false;");
		
		sb.append(cr);
		while(closeWhileBlockCount-- > 0) {
			sb.append("} // end for while loop(TD128) " + cr + cr);
		}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    stringBuffer.append(TEXT_451);
    stringBuffer.append( sb.toString());
    stringBuffer.append(TEXT_452);
    
	}
	
	if((!isPlainNode && !hasDocumentGlobal) || (hasDocumentGlobal && !isPlainNode)) {
		gm.addBlocksCodeToClose(new BlockCode("G_TXM_M_001 close"));

    stringBuffer.append(TEXT_453);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_454);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_455);
    
			boolean flat = true;
			for (OutputXmlTree table : outputTables) {        
				EList<OutputTreeNode> tableEntries = table.getNodes();
				if (tableEntries == null || nameToOutputConnection.get(table.getName()) == null) {
					continue;
				}
				for(OutputTreeNode tableEntry : tableEntries) {
					if(("id_Document").equals(tableEntry.getType())){
						flat = false;
						break;
					}
				}
				
				String tableName1 = table.getName();
            
				String instanceVarName = tableName1 + "__" + node.getUniqueName();
				String className = tableName1 + "Struct";
				if(!flat) {
            

    stringBuffer.append(TEXT_456);
    stringBuffer.append( tableName1 );
    stringBuffer.append(TEXT_457);
    stringBuffer.append( tableName1 );
    stringBuffer.append(TEXT_458);
    
				}
			}

    stringBuffer.append(TEXT_459);
    
	}

    stringBuffer.append(TEXT_460);
    return stringBuffer.toString();
  }
}
