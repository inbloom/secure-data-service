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

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Date;

import routines.TalendDate;

public class FormatterUtils {

	public static String format(Object obj,String pattern) {
		return (obj == null) ? null : obj.toString();
	}
	
	public static String format(Date date,String pattern) {
		if (date != null) {
			return TalendDate.formatDate(pattern == null ? Constant.dateDefaultPattern : pattern, date);
	    } else {
            return null;
        }
	}
	
	public static String format(BigDecimal decimal,String pattern) {
		if(decimal == null) return null;
		return decimal.toPlainString();
	}

	public static String format(byte data[],String pattern) {
		return Charset.defaultCharset().decode(java.nio.ByteBuffer.wrap(data)).toString();
	}
	
	public static String format(char data[],String pattern) {
		return String.valueOf(data);
	}

	public static String format(boolean b,String pattern) {
		return String.valueOf(b);
	}

	public static String format(char c,String pattern) {
		return String.valueOf(c);
	}

	public static String format(int i,String pattern) {
		return String.valueOf(i);
	}

	public static String format(long l,String pattern) {
		return String.valueOf(l);
	}

	public static String format(float f,String pattern) {
		return String.valueOf(f);
	}

	public static String format(double d,String pattern) {
		return String.valueOf(d);
	}
	
	
    public static String format_Date(java.util.Date date, String pattern) {
        if (date != null) {
            return TalendDate.formatDate(pattern == null ? Constant.dateDefaultPattern : pattern, date);
        } else {
            return null;
        }
    }

    public static String format_Date_Locale(java.util.Date date, String pattern, String locale) {
        if (date != null) {
            return TalendDate.formatDateLocale(pattern == null ? Constant.dateDefaultPattern : pattern, date, locale);
        } else {
            return null;
        }
    }

    /**
     * in order to transform the number "1234567.89" to string 123,456.89
     */
    public static String format_Number(String s, Character thousandsSeparator, Character decimalSeparator) {
        if (s == null) {
            return null;
        }
        String result = s;
        int decimalIndex = s.indexOf("."); //$NON-NLS-1$

        if (decimalIndex == -1) {
            if (thousandsSeparator != null) {
                return formatNumber(result, thousandsSeparator);
            } else {
                return result;
            }
        }

        if (thousandsSeparator != null) {
            result = formatNumber(s.substring(0, decimalIndex), thousandsSeparator);
        } else {
            result = s.substring(0, decimalIndex);
        }

        if (decimalSeparator != null) {
            result += (s.substring(decimalIndex)).replace('.', decimalSeparator);
        } else {
            result += s.substring(decimalIndex);
        }
        return result;
    }

    private static String formatNumber(String s, char thousandsSeparator) {

        StringBuilder sb = new StringBuilder(s);
        int index = sb.length();

        index = index - 3;
        while (index > 0 && sb.charAt(index - 1) != '-') {
            sb.insert(index, thousandsSeparator);
            index = index - 3;
        }

        return sb.toString();
    }

    /**
     * Bug 13352 by nsun: always return the format using "." for decimal separator.
     */
    public static String unformat_Number(String s, Character thousandsSeparator, Character decimalSeparator) {
        if (s == null) {
            return null;
        }
        String result = s;
        int decimalIndex = s.indexOf(decimalSeparator);
        if (decimalIndex == -1) {
            if (thousandsSeparator != null) {
                return unformatNumber(result, thousandsSeparator);
            } else {
                return result;
            }
        }
        if (thousandsSeparator != null) {
            result = unformatNumber(s.substring(0, decimalIndex), thousandsSeparator);
        } else {
            result = s.substring(0, decimalIndex);
        }

        if (decimalSeparator != null) {
            if ("\\.".equals(decimalSeparator)) {
                result += (s.substring(decimalIndex)).replace(thousandsSeparator, decimalSeparator);
            } else {
                result += (s.substring(decimalIndex)).replace(decimalSeparator, '.');
            }
        } else {
            result += s.substring(decimalIndex);
        }
        return result;
    }

    private static String unformatNumber(String str, Character thousandsSeparator) {
        StringBuilder returnString = new StringBuilder();
        String separator = thousandsSeparator.toString();
        if (".".equals(separator))
            separator = "\\.";
        String[] s = str.split(separator);
        for (String part : s)
            returnString.append(part);
        return returnString.toString();
    }

    
    private static final DecimalFormat df = new DecimalFormat("#.###########################################################");
    /**
     * DOC Administrator Comment method "formatUnwithE". In java when double more than six decimal that use toString
     * will rentru contains E scientific natation.
     * 
     * @param arg like: double falot String .... e.g:1.0E-8
     * @return 0.00000001 as String
     */
    public static String formatUnwithE(Object arg) {
        String doubleString = String.valueOf(arg);
        int index = doubleString.indexOf("E");
        if (index != -1) {
            if (doubleString.charAt(index + 1) != '-') {
            	return df.format(arg);
            } else {
            	String position = doubleString.substring(index + 2);
                return String.format("%1." + position + "f", arg);
            }
        }
        return doubleString;
    }
    
}
