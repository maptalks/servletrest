package org.maptalks.servletrest.output.util;

public class Native2Ascii {
	public static String toAscii(String str) {
		StringBuffer unicode = new StringBuffer(str.length());

		char[] charAry = new char[str.length()];

		for (int i = 0; i < charAry.length; i++) {
			charAry[i] = str.charAt(i);
			unicode.append("\\u");
			unicode.append(Integer.toHexString(charAry[i]));
		}
		return unicode.toString();
	}
}
