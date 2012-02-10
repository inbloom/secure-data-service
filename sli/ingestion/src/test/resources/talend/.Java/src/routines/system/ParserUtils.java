// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//   
// ============================================================================
package routines.system;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ParserUtils {

    public static List parseTo_List(String s) {
        if (s != null) {
            List list = new ArrayList();
            list.add(s);
            return list;
        }
        return null;
    }

    public static Character parseTo_Character(String s) {
        if (s == null) {
            return null;
        }
        return s.charAt(0);
    }

    public static char parseTo_char(String s) {
        return parseTo_Character(s);
    }

    public static Byte parseTo_Byte(String s) {
        if (s == null) {
            return null;
        }
        return Byte.decode(s).byteValue();
    }

    public static byte parseTo_byte(String s) {
        return parseTo_Byte(s);
    }

    public static Double parseTo_Double(String s) {
        if (s == null) {
            return null;
        }
        return Double.parseDouble(s);
    }

    public static double parseTo_double(String s) {
        return parseTo_Double(s);
    }

    public static float parseTo_float(String s) {
        return Float.parseFloat(s);
    }

    public static Float parseTo_Float(String s) {
        if (s == null) {
            return null;
        }
        return parseTo_float(s);
    }

    public static int parseTo_int(String s) {
        return Integer.parseInt(s);
    }

    public static Integer parseTo_Integer(String s) {
        if (s == null) {
            return null;
        }
        return parseTo_int(s);
    }

    public static short parseTo_short(String s) {
        return Short.parseShort(s);
    }

    public static Short parseTo_Short(String s) {
        if (s == null) {
            return null;
        }
        return parseTo_short(s);
    }

    public static long parseTo_long(String s) {
        return Long.parseLong(s);
    }

    public static Long parseTo_Long(String s) {
        if (s == null) {
            return null;
        }
        return parseTo_long(s);
    }

    public static Boolean parseTo_Boolean(String s) {
        if (s == null) {
            return null;
        }
        if (s.equals("1")) { //$NON-NLS-1$
            return Boolean.parseBoolean("true"); //$NON-NLS-1$
        }
        return Boolean.parseBoolean(s);
    }

    public static boolean parseTo_boolean(String s) {
        return parseTo_Boolean(s);
    }

    public static String parseTo_String(String s) {
        return s;
    }

    public static BigDecimal parseTo_BigDecimal(String s) {
        if (s == null) {
            return null;
        }
        return new BigDecimal(s);
    }

    public static routines.system.Document parseTo_Document(String s) throws org.dom4j.DocumentException {
    	return parseTo_Document(s,false);
    }
    
    public static routines.system.Document parseTo_Document(String s, boolean ignoreDTD) throws org.dom4j.DocumentException {
        if (s == null) {
            return null;
        }
        routines.system.Document theDoc = new routines.system.Document();
        org.dom4j.io.SAXReader reader = new org.dom4j.io.SAXReader();
        
        if(ignoreDTD) {
        	reader.setEntityResolver(new EntityResolver() {
				
				public InputSource resolveEntity(String publicId, String systemId)
						throws SAXException, IOException {
					return new org.xml.sax.InputSource(new java.io.ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes())); 
				}
			});
        }
        
		org.dom4j.Document document = reader.read(new java.io.StringReader(s));

        theDoc.setDocument(document);
        return theDoc;
    }

    public synchronized static java.util.Date parseTo_Date(String s, String pattern) {
        // check the parameter for supporting " ","2007-09-13"," 2007-09-13 "
        if (s != null) {
            s = s.trim();
        }
        if (s == null || s.length() == 0) {
            return null;
        }
        if(pattern == null) {
        	pattern = Constant.dateDefaultPattern;
        }
        java.util.Date date = null;
        // try {
        // date = FastDateParser.getInstance(pattern).parse(s);
        // } catch (java.text.ParseException e) {
        // e.printStackTrace();
        // System.err.println("Current string to parse '" + s + "'");
        // }
        // add by wliu for special pattern:yyyy-MM-dd'T'HH:mm:ss'000Z'---------start
        if (pattern.equals("yyyy-MM-dd'T'HH:mm:ss'000Z'")) {
            if (!s.endsWith("000Z")) {
                throw new RuntimeException("Unparseable date: \"" + s + "\""); //$NON-NLS-1$ //$NON-NLS-2$
            }
            pattern = "yyyy-MM-dd'T'HH:mm:ss";
            s = s.substring(0, s.lastIndexOf("000Z"));
        }
        // add by wliu -------------------------------------------------------end
        DateFormat format = FastDateParser.getInstance(pattern);
        ParsePosition pp = new ParsePosition(0);
        pp.setIndex(0);

        date = format.parse(s, pp);
        if (pp.getIndex() != s.length() || date == null) {
            throw new RuntimeException("Unparseable date: \"" + s + "\""); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return date;
    }

    public synchronized static java.util.Date parseTo_Date(String s, String pattern, boolean lenient) {
        // check the parameter for supporting " ","2007-09-13"," 2007-09-13 "
        if (s != null) {
            s = s.trim();
        }
        if (s == null || s.length() == 0) {
            return null;
        }
        if(pattern == null) {
        	pattern = Constant.dateDefaultPattern;
        }
        java.util.Date date = null;
        // try {
        // date = FastDateParser.getInstance(pattern).parse(s);
        // } catch (java.text.ParseException e) {
        // e.printStackTrace();
        // System.err.println("Current string to parse '" + s + "'");
        // }
        // add by wliu for special pattern:yyyy-MM-dd'T'HH:mm:ss'000Z'---------start
        if (pattern.equals("yyyy-MM-dd'T'HH:mm:ss'000Z'")) {
            if (!s.endsWith("000Z")) {
                throw new RuntimeException("Unparseable date: \"" + s + "\""); //$NON-NLS-1$ //$NON-NLS-2$
            }
            pattern = "yyyy-MM-dd'T'HH:mm:ss";
            s = s.substring(0, s.lastIndexOf("000Z"));
        }
        // add by wliu -------------------------------------------------------end
        DateFormat format = FastDateParser.getInstance(pattern, lenient);
        ParsePosition pp = new ParsePosition(0);
        pp.setIndex(0);

        date = format.parse(s, pp);
        if (pp.getIndex() != s.length() || date == null) {
            throw new RuntimeException("Unparseable date: \"" + s + "\""); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return date;
    }

    public static java.util.Date parseTo_Date(java.util.Date date, String pattern) {
        // java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat(pattern);
        // java.util.Date date = null;
        // try {
        // date = simpleDateFormat.parse(date);
        // } catch (java.text.ParseException e) {
        // e.printStackTrace();
        // System.err.println("Current string to parse '" + s + "'");
        // }
        return date;
    }

    /**
     * in order to transform the string "1.234.567,89" to number 1234567.89
     */
    public static String parseTo_Number(String s, Character thousandsSeparator, Character decimalSeparator) {
        if (s == null) {
            return null;
        }
        String result = s;
        if (thousandsSeparator != null) {
            result = StringUtils.deleteChar(s, thousandsSeparator);
        }
        if (decimalSeparator != null) {
            result = result.replace(decimalSeparator, '.');
        }
        return result;
    }
    
    private static final Set<String> primitiveType = new HashSet<String>();
    
    private static final Map<String,String> primitiveTypeToDefaultValueMap = new HashMap<String,String>();
    
    static {
    	primitiveType.add("boolean");
    	primitiveType.add("int");
    	primitiveType.add("byte");
    	primitiveType.add("char");
    	primitiveType.add("double");
    	primitiveType.add("float");
    	primitiveType.add("long");
    	primitiveType.add("short");
    	
    	primitiveTypeToDefaultValueMap.put("boolean", "false");
    	primitiveTypeToDefaultValueMap.put("int", "0");
    	primitiveTypeToDefaultValueMap.put("byte", "0");
    	primitiveTypeToDefaultValueMap.put("char", " ");
    	primitiveTypeToDefaultValueMap.put("double", "0");
    	primitiveTypeToDefaultValueMap.put("float", "0");
    	primitiveTypeToDefaultValueMap.put("long", "0");
    	primitiveTypeToDefaultValueMap.put("short", "0");
    }
    
    public static Object parse(String text,String javaType,String pattern) {
    	if("String".equals(javaType) || "Object".equals(javaType)) {
    		return text;
    	}
    	
    	if(text==null || text.length()==0){
    		boolean isPrimitiveType =  primitiveType.contains(javaType);
    		if(!isPrimitiveType) {
    			return null;
    		} else {
    			text = primitiveTypeToDefaultValueMap.get(javaType);
    		}
    	} else {
	    	if("java.util.Date".equals(javaType)) {
	    		return ParserUtils.parseTo_Date(text, pattern); 
	    	}
	    	
	    	if("byte[]".equals(javaType)) {
	    		return text.getBytes();
	    	}
    	}
    	
    	try {
			Method method = ParserUtils.class.getMethod("parseTo_" + javaType, String.class);
			return method.invoke(null, text);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
    }
    
}
