package org.talend.designer.codegen.translators.file.output;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import org.talend.core.model.utils.NodeUtil;
import java.util.List;
import java.util.Map;

public class TFileOutputPositionalBeginJava
{
  protected static String nl;
  public static synchronized TFileOutputPositionalBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileOutputPositionalBeginJava result = new TFileOutputPositionalBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\tint nb_line_";
  protected final String TEXT_3 = " = 0;" + NL + "\t\t" + NL + "\t\tclass PositionUtil_";
  protected final String TEXT_4 = "{" + NL + "\t\t";
  protected final String TEXT_5 = NL + "              void writeHeader_";
  protected final String TEXT_6 = "(String tempStringB";
  protected final String TEXT_7 = ",int tempLengthB";
  protected final String TEXT_8 = ",final ";
  protected final String TEXT_9 = " out";
  protected final String TEXT_10 = ")throws IOException,java.io.UnsupportedEncodingException{" + NL + "              " + NL + "                 int tempLengthM";
  protected final String TEXT_11 = "= 0;";
  protected final String TEXT_12 = NL + "    \t\t\t//get  and format output String begin" + NL + "    \t\t\ttempStringB";
  protected final String TEXT_13 = "=\"";
  protected final String TEXT_14 = "\";" + NL + "    \t\t\t";
  protected final String TEXT_15 = NL + "    \t\t\ttempLengthB";
  protected final String TEXT_16 = "=tempStringB";
  protected final String TEXT_17 = ".getBytes(";
  protected final String TEXT_18 = ").length;" + NL + "    \t\t\t";
  protected final String TEXT_19 = NL + "    \t\t\ttempLengthB";
  protected final String TEXT_20 = "=tempStringB";
  protected final String TEXT_21 = ".length();" + NL + "    \t\t\t";
  protected final String TEXT_22 = NL + "    \t\t\t" + NL + "            \tif (tempLengthB";
  protected final String TEXT_23 = " > ";
  protected final String TEXT_24 = ") {";
  protected final String TEXT_25 = NL + "                        tempStringB";
  protected final String TEXT_26 = " = tempStringB";
  protected final String TEXT_27 = ".substring(tempLengthB";
  protected final String TEXT_28 = "-";
  protected final String TEXT_29 = ");";
  protected final String TEXT_30 = NL + "                        int begin";
  protected final String TEXT_31 = "=(tempLengthB";
  protected final String TEXT_32 = "-";
  protected final String TEXT_33 = ")/2;" + NL + "                        tempStringB";
  protected final String TEXT_34 = " = tempStringB";
  protected final String TEXT_35 = ".substring(begin";
  protected final String TEXT_36 = ", begin";
  protected final String TEXT_37 = "+";
  protected final String TEXT_38 = ");";
  protected final String TEXT_39 = NL + "                    tempStringB";
  protected final String TEXT_40 = " = tempStringB";
  protected final String TEXT_41 = ".substring(0, ";
  protected final String TEXT_42 = ");";
  protected final String TEXT_43 = NL + "                }else if(tempLengthB";
  protected final String TEXT_44 = "<";
  protected final String TEXT_45 = "){" + NL + "                    StringBuilder result";
  protected final String TEXT_46 = " = new StringBuilder();";
  protected final String TEXT_47 = NL + "                        result";
  protected final String TEXT_48 = ".append(tempStringB";
  protected final String TEXT_49 = ");" + NL + "                        for(int i";
  protected final String TEXT_50 = "=0; i";
  protected final String TEXT_51 = "< ";
  protected final String TEXT_52 = "-tempLengthB";
  protected final String TEXT_53 = "; i";
  protected final String TEXT_54 = "++){" + NL + "                            result";
  protected final String TEXT_55 = ".append(";
  protected final String TEXT_56 = ");" + NL + "                        }" + NL + "                        tempStringB";
  protected final String TEXT_57 = " = result";
  protected final String TEXT_58 = ".toString();";
  protected final String TEXT_59 = NL + "                        for(int i";
  protected final String TEXT_60 = "=0; i";
  protected final String TEXT_61 = "< ";
  protected final String TEXT_62 = "-tempLengthB";
  protected final String TEXT_63 = "; i";
  protected final String TEXT_64 = "++){" + NL + "                            result";
  protected final String TEXT_65 = ".append(";
  protected final String TEXT_66 = ");" + NL + "                        }" + NL + "                        result";
  protected final String TEXT_67 = ".append(tempStringB";
  protected final String TEXT_68 = ");" + NL + "                        tempStringB";
  protected final String TEXT_69 = " = result";
  protected final String TEXT_70 = ".toString();";
  protected final String TEXT_71 = NL + "                        int temp";
  protected final String TEXT_72 = "= (";
  protected final String TEXT_73 = "-tempLengthB";
  protected final String TEXT_74 = ")/2;" + NL + "                        for(int i";
  protected final String TEXT_75 = "=0;i";
  protected final String TEXT_76 = "<temp";
  protected final String TEXT_77 = ";i";
  protected final String TEXT_78 = "++){" + NL + "                            result";
  protected final String TEXT_79 = ".append(";
  protected final String TEXT_80 = ");" + NL + "                        }" + NL + "                        result";
  protected final String TEXT_81 = ".append(tempStringB";
  protected final String TEXT_82 = ");" + NL + "                        for(int i";
  protected final String TEXT_83 = "=0;i";
  protected final String TEXT_84 = "<temp";
  protected final String TEXT_85 = ";i";
  protected final String TEXT_86 = "++){" + NL + "                            result";
  protected final String TEXT_87 = ".append(";
  protected final String TEXT_88 = ");" + NL + "                        }" + NL + "                        if((temp";
  protected final String TEXT_89 = "+temp";
  protected final String TEXT_90 = ")!=(";
  protected final String TEXT_91 = "-tempLengthB";
  protected final String TEXT_92 = ")){" + NL + "                            result";
  protected final String TEXT_93 = ".append(";
  protected final String TEXT_94 = ");" + NL + "                        }" + NL + "                        tempStringB";
  protected final String TEXT_95 = " = result";
  protected final String TEXT_96 = ".toString();";
  protected final String TEXT_97 = "       " + NL + "                }" + NL + "                //get  and format output String end" + NL + "    \t\t\tout";
  protected final String TEXT_98 = ".write(tempStringB";
  protected final String TEXT_99 = ");";
  protected final String TEXT_100 = NL + "              }";
  protected final String TEXT_101 = NL + "              }  ";
  protected final String TEXT_102 = NL + "\t\t\t\t  void setValue_";
  protected final String TEXT_103 = "(final ";
  protected final String TEXT_104 = "Struct ";
  protected final String TEXT_105 = ",StringBuilder sb_";
  protected final String TEXT_106 = ",String tempStringM";
  protected final String TEXT_107 = ",int tempLengthM";
  protected final String TEXT_108 = ")throws IOException,java.io.UnsupportedEncodingException{";
  protected final String TEXT_109 = NL + "\t\t\t\t//get  and format output String begin" + NL + "    \t\t\ttempStringM";
  protected final String TEXT_110 = "=";
  protected final String TEXT_111 = NL + "\t\t\t\t\tString.valueOf(";
  protected final String TEXT_112 = ".";
  protected final String TEXT_113 = ")";
  protected final String TEXT_114 = NL + "\t\t\t\t\t(";
  protected final String TEXT_115 = ".";
  protected final String TEXT_116 = " == null) ? " + NL + "\t\t\t\t\t\"\": ";
  protected final String TEXT_117 = "FormatterUtils.format_Date(";
  protected final String TEXT_118 = ".";
  protected final String TEXT_119 = ", ";
  protected final String TEXT_120 = ")";
  protected final String TEXT_121 = "java.nio.charset.Charset.defaultCharset().decode(java.nio.ByteBuffer.wrap(";
  protected final String TEXT_122 = ".";
  protected final String TEXT_123 = ")).toString()";
  protected final String TEXT_124 = ".";
  protected final String TEXT_125 = NL + "        \t\t\t\t\t\t\t";
  protected final String TEXT_126 = NL + "        \t\t\t\t\t\t\tFormatterUtils.format_Number(";
  protected final String TEXT_127 = ".toPlainString(), ";
  protected final String TEXT_128 = ", ";
  protected final String TEXT_129 = ")\t\t\t\t\t" + NL + "        \t\t\t\t\t\t\t";
  protected final String TEXT_130 = NL + "        \t\t\t\t\t\t\tFormatterUtils.format_Number(String.valueOf(";
  protected final String TEXT_131 = ".";
  protected final String TEXT_132 = "), ";
  protected final String TEXT_133 = ", ";
  protected final String TEXT_134 = ")\t\t\t\t\t\t" + NL + "        \t\t\t\t\t\t\t";
  protected final String TEXT_135 = NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_136 = ".toPlainString()\t" + NL + "\t\t\t\t\t";
  protected final String TEXT_137 = "String.valueOf(";
  protected final String TEXT_138 = ".";
  protected final String TEXT_139 = ")";
  protected final String TEXT_140 = " ;" + NL + "\t\t\t\t";
  protected final String TEXT_141 = NL + "    \t\t\ttempLengthM";
  protected final String TEXT_142 = "=tempStringM";
  protected final String TEXT_143 = ".getBytes(";
  protected final String TEXT_144 = ").length;" + NL + "    \t\t\t";
  protected final String TEXT_145 = NL + "    \t\t\ttempLengthM";
  protected final String TEXT_146 = "=tempStringM";
  protected final String TEXT_147 = ".length();" + NL + "    \t\t\t";
  protected final String TEXT_148 = NL + "    \t\t\t" + NL + "            \tif (tempLengthM";
  protected final String TEXT_149 = " >=";
  protected final String TEXT_150 = ") {";
  protected final String TEXT_151 = NL + "                        sb_";
  protected final String TEXT_152 = ".append(tempStringM";
  protected final String TEXT_153 = ");";
  protected final String TEXT_154 = NL + "                        sb_";
  protected final String TEXT_155 = ".append(tempStringM";
  protected final String TEXT_156 = ".substring(tempLengthM";
  protected final String TEXT_157 = "-";
  protected final String TEXT_158 = "));";
  protected final String TEXT_159 = NL + "                        int begin";
  protected final String TEXT_160 = "=(tempLengthM";
  protected final String TEXT_161 = "-";
  protected final String TEXT_162 = ")/2;" + NL + "                        sb_";
  protected final String TEXT_163 = ".append(tempStringM";
  protected final String TEXT_164 = ".substring(begin";
  protected final String TEXT_165 = ", begin";
  protected final String TEXT_166 = "+";
  protected final String TEXT_167 = "));";
  protected final String TEXT_168 = NL + "                        sb_";
  protected final String TEXT_169 = ".append(tempStringM";
  protected final String TEXT_170 = ".substring(0, ";
  protected final String TEXT_171 = "));";
  protected final String TEXT_172 = NL + "                }else if(tempLengthM";
  protected final String TEXT_173 = "<";
  protected final String TEXT_174 = "){" + NL + "                   ";
  protected final String TEXT_175 = NL + "                        sb_";
  protected final String TEXT_176 = ".append(tempStringM";
  protected final String TEXT_177 = ");" + NL + "                        for(int i_";
  protected final String TEXT_178 = "=0; i_";
  protected final String TEXT_179 = "< ";
  protected final String TEXT_180 = "-tempLengthM";
  protected final String TEXT_181 = "; i_";
  protected final String TEXT_182 = "++){" + NL + "                            sb_";
  protected final String TEXT_183 = ".append(";
  protected final String TEXT_184 = ");" + NL + "                        }" + NL + "                        ";
  protected final String TEXT_185 = NL + "                        for(int i_";
  protected final String TEXT_186 = "=0; i_";
  protected final String TEXT_187 = "< ";
  protected final String TEXT_188 = "-tempLengthM";
  protected final String TEXT_189 = "; i_";
  protected final String TEXT_190 = "++){" + NL + "                            sb_";
  protected final String TEXT_191 = ".append(";
  protected final String TEXT_192 = ");" + NL + "                        }" + NL + "                        sb_";
  protected final String TEXT_193 = ".append(tempStringM";
  protected final String TEXT_194 = ");" + NL + "                        ";
  protected final String TEXT_195 = NL + "                        int temp";
  protected final String TEXT_196 = "= (";
  protected final String TEXT_197 = "-tempLengthM";
  protected final String TEXT_198 = ")/2;" + NL + "                        for(int i_";
  protected final String TEXT_199 = "=0;i_";
  protected final String TEXT_200 = "<temp";
  protected final String TEXT_201 = ";i_";
  protected final String TEXT_202 = "++){" + NL + "                            sb_";
  protected final String TEXT_203 = ".append(";
  protected final String TEXT_204 = ");" + NL + "                        }" + NL + "                        sb_";
  protected final String TEXT_205 = ".append(tempStringM";
  protected final String TEXT_206 = ");" + NL + "                        for(int i=temp";
  protected final String TEXT_207 = "+tempLengthM";
  protected final String TEXT_208 = ";i<";
  protected final String TEXT_209 = ";i++){" + NL + "                            sb_";
  protected final String TEXT_210 = ".append(";
  protected final String TEXT_211 = ");" + NL + "                        }" + NL + "" + NL + "                        ";
  protected final String TEXT_212 = "       " + NL + "                }" + NL + "                //get  and format output String end\t\t\t\t" + NL + "\t\t\t";
  protected final String TEXT_213 = NL + "                 }";
  protected final String TEXT_214 = NL + "                   }";
  protected final String TEXT_215 = NL + "\t\t" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\tPositionUtil_";
  protected final String TEXT_216 = " positionUtil_";
  protected final String TEXT_217 = "=new PositionUtil_";
  protected final String TEXT_218 = "();" + NL + "\t\t";
  protected final String TEXT_219 = NL + "\t\tString fileNewName_";
  protected final String TEXT_220 = " = ";
  protected final String TEXT_221 = ";" + NL + "\t\tjava.io.File createFile";
  protected final String TEXT_222 = " = new java.io.File(fileNewName_";
  protected final String TEXT_223 = ");";
  protected final String TEXT_224 = NL + "        //create directory only if not exists" + NL + "        java.io.File parentFile_";
  protected final String TEXT_225 = " = createFile";
  protected final String TEXT_226 = ".getParentFile();" + NL + "        if(parentFile_";
  protected final String TEXT_227 = " != null && !parentFile_";
  protected final String TEXT_228 = ".exists()) {" + NL + "            parentFile_";
  protected final String TEXT_229 = ".mkdirs();" + NL + "        }";
  protected final String TEXT_230 = NL + "        String fullName_";
  protected final String TEXT_231 = " = null;" + NL + "        String extension_";
  protected final String TEXT_232 = " = null;" + NL + "        String directory_";
  protected final String TEXT_233 = " = null;" + NL + "        if((fileNewName_";
  protected final String TEXT_234 = ".indexOf(\"/\") != -1)) {" + NL + "            if(fileNewName_";
  protected final String TEXT_235 = ".lastIndexOf(\".\") < fileNewName_";
  protected final String TEXT_236 = ".lastIndexOf(\"/\")) {" + NL + "                fullName_";
  protected final String TEXT_237 = " = fileNewName_";
  protected final String TEXT_238 = ";" + NL + "                extension_";
  protected final String TEXT_239 = " = \"\";" + NL + "            } else {" + NL + "                fullName_";
  protected final String TEXT_240 = " = fileNewName_";
  protected final String TEXT_241 = ".substring(0, fileNewName_";
  protected final String TEXT_242 = ".lastIndexOf(\".\"));" + NL + "                extension_";
  protected final String TEXT_243 = " = fileNewName_";
  protected final String TEXT_244 = ".substring(fileNewName_";
  protected final String TEXT_245 = ".lastIndexOf(\".\"));" + NL + "            }           " + NL + "            directory_";
  protected final String TEXT_246 = " = fileNewName_";
  protected final String TEXT_247 = ".substring(0, fileNewName_";
  protected final String TEXT_248 = ".lastIndexOf(\"/\"));            " + NL + "        } else {" + NL + "            if(fileNewName_";
  protected final String TEXT_249 = ".lastIndexOf(\".\") != -1) {" + NL + "                fullName_";
  protected final String TEXT_250 = " = fileNewName_";
  protected final String TEXT_251 = ".substring(0, fileNewName_";
  protected final String TEXT_252 = ".lastIndexOf(\".\"));" + NL + "                extension_";
  protected final String TEXT_253 = " = fileNewName_";
  protected final String TEXT_254 = ".substring(fileNewName_";
  protected final String TEXT_255 = ".lastIndexOf(\".\"));" + NL + "            } else {" + NL + "                fullName_";
  protected final String TEXT_256 = " = fileNewName_";
  protected final String TEXT_257 = ";" + NL + "                extension_";
  protected final String TEXT_258 = " = \"\";" + NL + "            }" + NL + "            directory_";
  protected final String TEXT_259 = " = \"\";" + NL + "        }" + NL + "\t\tString zipName_";
  protected final String TEXT_260 = " = fullName_";
  protected final String TEXT_261 = " + \".zip\";" + NL + "\t    java.util.zip.ZipOutputStream zipOut_";
  protected final String TEXT_262 = "=new java.util.zip.ZipOutputStream(" + NL + "\t    \t\t\tnew java.io.BufferedOutputStream(new java.io.FileOutputStream(zipName_";
  protected final String TEXT_263 = ")));" + NL + "\t    zipOut_";
  protected final String TEXT_264 = ".putNextEntry(new java.util.zip.ZipEntry(createFile";
  protected final String TEXT_265 = ".getName()));" + NL + "\t\tfinal ";
  protected final String TEXT_266 = " out";
  protected final String TEXT_267 = " = new ";
  protected final String TEXT_268 = "(new java.io.OutputStreamWriter(zipOut_";
  protected final String TEXT_269 = ",";
  protected final String TEXT_270 = "));";
  protected final String TEXT_271 = NL + "\t\tfinal ";
  protected final String TEXT_272 = " out";
  protected final String TEXT_273 = " = new ";
  protected final String TEXT_274 = "(new java.io.OutputStreamWriter(" + NL + "        \t\tnew java.io.FileOutputStream(";
  protected final String TEXT_275 = ", ";
  protected final String TEXT_276 = "),";
  protected final String TEXT_277 = "));";
  protected final String TEXT_278 = NL + "\t    java.util.zip.ZipOutputStream zipOut_";
  protected final String TEXT_279 = "=new java.util.zip.ZipOutputStream(" + NL + "\t    \t\t\tnew java.io.BufferedOutputStream(";
  protected final String TEXT_280 = "));" + NL + "\t    zipOut_";
  protected final String TEXT_281 = ".putNextEntry(new java.util.zip.ZipEntry(\"TalendOutputPositional\"));" + NL + "\t\tjava.io.OutputStreamWriter outWriter_";
  protected final String TEXT_282 = " = new java.io.OutputStreamWriter(zipOut_";
  protected final String TEXT_283 = ",";
  protected final String TEXT_284 = ");" + NL + "\t\tfinal ";
  protected final String TEXT_285 = " out";
  protected final String TEXT_286 = " = new ";
  protected final String TEXT_287 = "(outWriter_";
  protected final String TEXT_288 = ");";
  protected final String TEXT_289 = NL + "\t\tjava.io.OutputStreamWriter outWriter_";
  protected final String TEXT_290 = " = new java.io.OutputStreamWriter(";
  protected final String TEXT_291 = ",";
  protected final String TEXT_292 = ");" + NL + "\t\tfinal ";
  protected final String TEXT_293 = " out";
  protected final String TEXT_294 = " = new ";
  protected final String TEXT_295 = "(outWriter_";
  protected final String TEXT_296 = ");  ";
  protected final String TEXT_297 = NL + "\t\tif(createFile";
  protected final String TEXT_298 = ".length()==0){";
  protected final String TEXT_299 = NL + "    \t\t" + NL + "    \t\tString tempStringB";
  protected final String TEXT_300 = "=null;" + NL + "    \t\tint tempLengthB";
  protected final String TEXT_301 = "=0;";
  protected final String TEXT_302 = NL + "            positionUtil_";
  protected final String TEXT_303 = ".writeHeader_";
  protected final String TEXT_304 = "(tempStringB";
  protected final String TEXT_305 = ",tempLengthB";
  protected final String TEXT_306 = ",out";
  protected final String TEXT_307 = ");";
  protected final String TEXT_308 = NL + "    \t\tout";
  protected final String TEXT_309 = ".write(";
  protected final String TEXT_310 = ");";
  protected final String TEXT_311 = NL + "    \t}";
  protected final String TEXT_312 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();

List<IMetadataTable> metadatas = node.getMetadataList();

 List<? extends IConnection> listInConns = node.getIncomingConnections();
    String sInConnName = null;
    IConnection inConn = null;
    List<IMetadataColumn> listInColumns = null;
    
    if (listInConns != null && listInConns.size() > 0) {
      IConnection inConnTemp = listInConns.get(0);
      sInConnName = inConnTemp.getName();
      if (inConnTemp.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)){
      	inConn = inConnTemp;
        listInColumns = inConnTemp.getMetadataTable().getListColumns();
      }
	}
   
   String inConnName = null;
   
if ((metadatas!=null)&&(metadatas.size()>0)) {
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null) {
    
    	String cid = node.getUniqueName();
    	
    	String encoding = ElementParameterParser.getValue(node, "__ENCODING__");
        if (encoding!=null) {
            if (("").equals(encoding)) {
                encoding = "undef";
            }
        }
        
        String separator = ElementParameterParser.getValue(
            node,
            "__ROWSEPARATOR__"
        );
        
        String filename = ElementParameterParser.getValue(
            node,
            "__FILENAME__"
        );
		
		boolean useByte = ("true").equals(ElementParameterParser.getValue(node, "__USE_BYTE__"));
		
        boolean isIncludeHeader = ("true").equals(ElementParameterParser.getValue(node,"__INCLUDEHEADER__"));

        boolean isAppend = ("true").equals(ElementParameterParser.getValue(node,"__APPEND__"));
        
		boolean useStream = ("true").equals(ElementParameterParser.getValue(node,"__USESTREAM__"));
		String outStream = ElementParameterParser.getValue(node,"__STREAMNAME__");
        
        String advancedSeparatorStr = ElementParameterParser.getValue(node, "__ADVANCED_SEPARATOR__");
		boolean advancedSeparator = (advancedSeparatorStr!=null&&!("").equals(advancedSeparatorStr))?("true").equals(advancedSeparatorStr):false;
		String thousandsSeparator = ElementParameterParser.getValueWithJavaType(node, "__THOUSANDS_SEPARATOR__", JavaTypesManager.CHARACTER);
		String decimalSeparator = ElementParameterParser.getValueWithJavaType(node, "__DECIMAL_SEPARATOR__", JavaTypesManager.CHARACTER);        
 
        List<Map<String, String>> formats =
            (List<Map<String,String>>)ElementParameterParser.getObjectValue(
                node,
                "__FORMATS__"
            );
        
        boolean compress = ("true").equals(ElementParameterParser.getValue(node,"__COMPRESS__"));
        
        boolean isInRowMode = ("true").equals(ElementParameterParser.getValue(node,"__ROW_MODE__"));
        String writerClass = null;
    	if(isInRowMode){
    		writerClass = "routines.system.BufferedOutput";
    	}else{
    		writerClass = "java.io.BufferedWriter";
    	}

    
    class FindConnStartConn{
		IConnection findStartConn(IConnection conn){
			INode node = conn.getSource();
			if(node.isSubProcessStart() || !(NodeUtil.isDataAutoPropagated(node))){
				return conn;
			}
			List<? extends IConnection> listInConns = node.getIncomingConnections();
			IConnection inConnTemp = null;
			if (listInConns != null && listInConns.size() > 0) {
              inConnTemp = listInConns.get(0);
              if (inConnTemp.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)){
                return findStartConn(inConnTemp);
              }
        	}
        	return null;
		}
	}
	if(inConn != null){
		FindConnStartConn finder = new FindConnStartConn();
    	IConnection startConn = finder.findStartConn(inConn);
    	if(startConn!=null){
    		inConnName = startConn.getName();
    	}
	}

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    
		    List<IMetadataColumn> columns = metadata.getListColumns();
    		int sizeColumns = columns.size();
    		if(isIncludeHeader){
    		for (int i = 0; i < sizeColumns; i++) {
    			IMetadataColumn column = columns.get(i);
    			Map<String, String> format=formats.get(i);
    			if(i%100==0){

    stringBuffer.append(TEXT_5);
    stringBuffer.append(i/100);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(writerClass );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    
                }

    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_14);
    if(useByte){
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_18);
    }else{
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    }
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(format.get("SIZE"));
    stringBuffer.append(TEXT_24);
    
                    if (("\'A\'").equals(format.get("KEEP"))) {
                    } else if (("\'R\'").equals(format.get("KEEP"))) {

    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(format.get("SIZE"));
    stringBuffer.append(TEXT_29);
    
                    } else if (("\'M\'").equals(format.get("KEEP"))) {

    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(format.get("SIZE"));
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(format.get("SIZE"));
    stringBuffer.append(TEXT_38);
    
                    } else {

    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(format.get("SIZE"));
    stringBuffer.append(TEXT_42);
    
                    }

    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    stringBuffer.append(format.get("SIZE"));
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    
                    if (("\'L\'").equals(format.get("ALIGN"))) {

    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_51);
    stringBuffer.append(format.get("SIZE"));
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_55);
    stringBuffer.append(format.get("PADDING_CHAR"));
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_58);
    
                    } else if (("\'R\'").equals(format.get("ALIGN"))) {

    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_61);
    stringBuffer.append(format.get("SIZE"));
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_65);
    stringBuffer.append(format.get("PADDING_CHAR"));
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_70);
    
                    } else {

    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_72);
    stringBuffer.append(format.get("SIZE"));
    stringBuffer.append(TEXT_73);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_74);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_76);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_78);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_79);
    stringBuffer.append(format.get("PADDING_CHAR"));
    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_82);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_83);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_86);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_87);
    stringBuffer.append(format.get("PADDING_CHAR"));
    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_90);
    stringBuffer.append(format.get("SIZE"));
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_92);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_93);
    stringBuffer.append(format.get("PADDING_CHAR"));
    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_95);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_96);
    
                    } 

    stringBuffer.append(TEXT_97);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_98);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_99);
    
	          if((i+1)%100==0){

    stringBuffer.append(TEXT_100);
    
              }
    	}
    		  if(sizeColumns>0&&(sizeColumns%100)>0){

    stringBuffer.append(TEXT_101);
    
              }
        }	

    
	  	List< ? extends IConnection> conns = node.getIncomingConnections();
	    for (IConnection conn : conns) {
		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
           for (int i = 0; i < sizeColumns; i++) {
				IMetadataColumn column = columns.get(i);
				Map<String,String> format=formats.get(i);
				JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
				String patternValue = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
				if(i%100==0){

    stringBuffer.append(TEXT_102);
    stringBuffer.append(i/100);
    stringBuffer.append(TEXT_103);
    stringBuffer.append(inConnName!=null?inConnName:conn.getName() );
    stringBuffer.append(TEXT_104);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_105);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_106);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_107);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_108);
    
                }

    stringBuffer.append(TEXT_109);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_110);
    				
				if(JavaTypesManager.isJavaPrimitiveType( column.getTalendType(), column.isNullable()) ) {
    stringBuffer.append(TEXT_111);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_112);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_113);
    					
				} else {
    stringBuffer.append(TEXT_114);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_115);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_116);
    
					if(javaType == JavaTypesManager.DATE && patternValue!=null){
					
    stringBuffer.append(TEXT_117);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_118);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_119);
    stringBuffer.append( patternValue );
    stringBuffer.append(TEXT_120);
    
					}else if(javaType == JavaTypesManager.BYTE_ARRAY){
					
    stringBuffer.append(TEXT_121);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_122);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_123);
    
					}else if(javaType == JavaTypesManager.STRING){
					
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_124);
    stringBuffer.append(column.getLabel() );
    
					} else if(advancedSeparator && JavaTypesManager.isNumberType(javaType, column.isNullable())) { 
							
    stringBuffer.append(TEXT_125);
     if(javaType == JavaTypesManager.BIGDECIMAL) {
    stringBuffer.append(TEXT_126);
    stringBuffer.append(column.getPrecision() == null? conn.getName() + "." + column.getLabel() : conn.getName() + "." + column.getLabel() + ".setScale(" + column.getPrecision() + ", java.math.RoundingMode.HALF_UP)" );
    stringBuffer.append(TEXT_127);
    stringBuffer.append( thousandsSeparator );
    stringBuffer.append(TEXT_128);
    stringBuffer.append( decimalSeparator );
    stringBuffer.append(TEXT_129);
     } else { 
    stringBuffer.append(TEXT_130);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_131);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_132);
    stringBuffer.append( thousandsSeparator );
    stringBuffer.append(TEXT_133);
    stringBuffer.append( decimalSeparator );
    stringBuffer.append(TEXT_134);
     } 
    stringBuffer.append(TEXT_135);
    
					}else if (javaType == JavaTypesManager.BIGDECIMAL) {
					
    stringBuffer.append(column.getPrecision() == null? conn.getName() + "." + column.getLabel() : conn.getName() + "." + column.getLabel() + ".setScale(" + column.getPrecision() + ", java.math.RoundingMode.HALF_UP)" );
    stringBuffer.append(TEXT_136);
     }else{
					
    stringBuffer.append(TEXT_137);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_138);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_139);
    
					}
				}
    stringBuffer.append(TEXT_140);
    if(useByte){
    stringBuffer.append(TEXT_141);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_142);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_143);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_144);
    }else{
    stringBuffer.append(TEXT_145);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_146);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_147);
    }
    stringBuffer.append(TEXT_148);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_149);
    stringBuffer.append(format.get("SIZE"));
    stringBuffer.append(TEXT_150);
    
                    if (("\'A\'").equals(format.get("KEEP"))) {
    stringBuffer.append(TEXT_151);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_152);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_153);
    
                    } else if (("\'R\'").equals(format.get("KEEP"))) {
    stringBuffer.append(TEXT_154);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_155);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_156);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_157);
    stringBuffer.append(format.get("SIZE"));
    stringBuffer.append(TEXT_158);
    
                    } else if (("\'M\'").equals(format.get("KEEP"))) {
    stringBuffer.append(TEXT_159);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_160);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_161);
    stringBuffer.append(format.get("SIZE"));
    stringBuffer.append(TEXT_162);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_163);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_164);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_165);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_166);
    stringBuffer.append(format.get("SIZE"));
    stringBuffer.append(TEXT_167);
    
                    } else {
    stringBuffer.append(TEXT_168);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_169);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_170);
    stringBuffer.append(format.get("SIZE"));
    stringBuffer.append(TEXT_171);
    
                    }
    stringBuffer.append(TEXT_172);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_173);
    stringBuffer.append(format.get("SIZE"));
    stringBuffer.append(TEXT_174);
    
                    if (("\'L\'").equals(format.get("ALIGN"))) {
                    
    stringBuffer.append(TEXT_175);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_176);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_177);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_178);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_179);
    stringBuffer.append(format.get("SIZE"));
    stringBuffer.append(TEXT_180);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_181);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_182);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_183);
    stringBuffer.append(format.get("PADDING_CHAR"));
    stringBuffer.append(TEXT_184);
    
                    } else if (("\'R\'").equals(format.get("ALIGN"))) {
    stringBuffer.append(TEXT_185);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_186);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_187);
    stringBuffer.append(format.get("SIZE"));
    stringBuffer.append(TEXT_188);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_189);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_190);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_191);
    stringBuffer.append(format.get("PADDING_CHAR"));
    stringBuffer.append(TEXT_192);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_193);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_194);
    
                    } else {
    stringBuffer.append(TEXT_195);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_196);
    stringBuffer.append(format.get("SIZE"));
    stringBuffer.append(TEXT_197);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_198);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_199);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_200);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_201);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_202);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_203);
    stringBuffer.append(format.get("PADDING_CHAR"));
    stringBuffer.append(TEXT_204);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_205);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_206);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_207);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_208);
    stringBuffer.append(format.get("SIZE"));
    stringBuffer.append(TEXT_209);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_210);
    stringBuffer.append(format.get("PADDING_CHAR"));
    stringBuffer.append(TEXT_211);
    
                    } 
    stringBuffer.append(TEXT_212);
    
				if((i+1)%100==0){

    stringBuffer.append(TEXT_213);
    
				}
			}
		}
                if(sizeColumns>0&&(sizeColumns%100)>0){

    stringBuffer.append(TEXT_214);
    
                 }
          }

    stringBuffer.append(TEXT_215);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_216);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_217);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_218);
    
		if(!useStream){// the part of file path

    stringBuffer.append(TEXT_219);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_220);
    stringBuffer.append(filename);
    stringBuffer.append(TEXT_221);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_222);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_223);
    
			if(("true").equals(ElementParameterParser.getValue(node,"__CREATE__"))){

    stringBuffer.append(TEXT_224);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_225);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_226);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_227);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_228);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_229);
    
			}
			if(compress && !isAppend){// compress the dest file

    stringBuffer.append(TEXT_230);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_231);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_232);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_233);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_234);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_235);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_236);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_237);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_238);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_239);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_240);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_241);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_242);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_243);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_244);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_245);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_246);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_247);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_248);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_249);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_250);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_251);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_252);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_253);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_254);
    stringBuffer.append(cid);
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
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_262);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_263);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_264);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_265);
    stringBuffer.append(writerClass );
    stringBuffer.append(TEXT_266);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_267);
    stringBuffer.append(writerClass );
    stringBuffer.append(TEXT_268);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_269);
    stringBuffer.append( encoding);
    stringBuffer.append(TEXT_270);
    
			}else{

    stringBuffer.append(TEXT_271);
    stringBuffer.append(writerClass );
    stringBuffer.append(TEXT_272);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_273);
    stringBuffer.append(writerClass );
    stringBuffer.append(TEXT_274);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_275);
    stringBuffer.append( isAppend);
    stringBuffer.append(TEXT_276);
    stringBuffer.append( encoding);
    stringBuffer.append(TEXT_277);
    
			}
		}else{ //the part of the output stream
			if(compress && !isAppend){// compress the dest output stream

    stringBuffer.append(TEXT_278);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_279);
    stringBuffer.append(outStream );
    stringBuffer.append(TEXT_280);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_281);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_282);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_283);
    stringBuffer.append( encoding);
    stringBuffer.append(TEXT_284);
    stringBuffer.append(writerClass );
    stringBuffer.append(TEXT_285);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_286);
    stringBuffer.append(writerClass );
    stringBuffer.append(TEXT_287);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_288);
    
			}else{

    stringBuffer.append(TEXT_289);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_290);
    stringBuffer.append(outStream );
    stringBuffer.append(TEXT_291);
    stringBuffer.append( encoding);
    stringBuffer.append(TEXT_292);
    stringBuffer.append(writerClass );
    stringBuffer.append(TEXT_293);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_294);
    stringBuffer.append(writerClass );
    stringBuffer.append(TEXT_295);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_296);
    
			}
		}
		
		if(isIncludeHeader){      	
			if(!useStream){

    stringBuffer.append(TEXT_297);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_298);
    
			}
			

    stringBuffer.append(TEXT_299);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_300);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_301);
    
    		for (int i = 0; i < sizeColumns; i++) {
    			IMetadataColumn column = columns.get(i);
    			Map<String, String> format=formats.get(i);
    			if(i%100==0){

    stringBuffer.append(TEXT_302);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_303);
    stringBuffer.append(i/100);
    stringBuffer.append(TEXT_304);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_305);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_306);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_307);
    
                }
            }

    stringBuffer.append(TEXT_308);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_309);
    stringBuffer.append(separator);
    stringBuffer.append(TEXT_310);
    
			if(!useStream){

    stringBuffer.append(TEXT_311);
    
			}
		}
    }
}

    stringBuffer.append(TEXT_312);
    return stringBuffer.toString();
  }
}
