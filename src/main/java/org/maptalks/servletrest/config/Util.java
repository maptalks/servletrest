package org.maptalks.servletrest.config;

import org.maptalks.servletrest.config.exceptions.InvalidURLPatternException;

public class Util {

	/**
	 * pattern 的合法性检查:不允许连续出现{{或者}}的情况,不允许//之间为空
	 * 
	 * @param pattern
	 * @param varPrefix
	 * @param varPostfix
	 * @throws InvalidURLPatternException
	 * @return 变量个数
	 */
	public static int checkPattern(final String pattern)
			throws InvalidURLPatternException {
		final char varPrefix = '{';
		final char varPostfix = '}';
		if (pattern == null || pattern.length() == 0) {
			return 0;
		}
		int ret = 0;
		final char[] urlArr = pattern.toCharArray();
		boolean inVar = false;
		int lastSlashPos = -2;
		for (int i = 0; i < pattern.length(); i++) {
			if (urlArr[i] == '/') {
				if (i - lastSlashPos == 1) {
					throw new InvalidURLPatternException(pattern);
				}
				lastSlashPos = i;
			} else if (urlArr[i] == ' ') {
				if (i - lastSlashPos == 1) {
					lastSlashPos++;
				}
			} else if (urlArr[i] == varPrefix) {
				ret++;
				if (inVar || (i > 0 && urlArr[i - 1] != '/')
						|| i == urlArr.length - 1
						|| urlArr[i + 1] == varPostfix) {
					throw new InvalidURLPatternException(pattern);
				}
				inVar = true;
			} else if (urlArr[i] == varPostfix) {
				if (!inVar || (i < urlArr.length - 1 && urlArr[i + 1] != '/')) {
					throw new InvalidURLPatternException(pattern);
				}
				inVar = false;
			}
		}
		return ret;
	}

	/**
	 * 比较url是否符合pattern
	 * 
	 * @param pattern
	 * @param url
	 * @param varPrefix
	 * @param varPostfix
	 * @return
	 */
	public static int comparePatternAndUrl(final String pattern,
			final String url) {
		return compareWithSwitch(pattern, url, '{', '}', false);
	}

	/**
	 * 比较两个pattern是否相同
	 * 
	 * @param pattern1
	 * @param pattern2
	 * @param varPrefix
	 * @param varPostfix
	 * @return
	 */
	public static int comparePattern(final String pattern1,
			final String pattern2) {
		return compareWithSwitch(pattern1, pattern2, '{', '}', true);
	}

	/**
	 * 比较两个pattern或一个pattern和url，isComparePattern表示是否是pattern比较
	 * 
	 * @param pattern1
	 * @param pattern2
	 * @param varPrefix
	 * @param varPostfix
	 * @param isComparePattern
	 * @return
	 */
	private static int compareWithSwitch(final String pattern1,
			final String pattern2, final char varPrefix, final char varPostfix,
			final boolean isComparePattern) {
		if (pattern1 == null || pattern1.length() == 0) {
			if (pattern2 == null || pattern2.length() == 0) {
				return 0;
			}
			return -1;
		}
		if (pattern2 == null || pattern2.length() == 0) {
			return 1;
		}

		final String[] firstSegs = pattern1.split("/");
		final String[] secSegs = pattern2.split("/");
		if (firstSegs.length > secSegs.length) {
			return 1;
		}
		if (firstSegs.length < secSegs.length) {
			return -1;
		}
		if (isComparePattern) {
			// checkPattern(pattern1, varPrefix, varPostfix);
			// checkPattern(pattern2, varPrefix, varPostfix);
			validatePattern(pattern1, pattern2, varPrefix, varPostfix);
		}
		for (int i = 0; i < secSegs.length; i++) {
			if ((firstSegs[i] + secSegs[i]).contains(varPrefix + "")) {
				continue;
			}
			final int compareRet = compareString(firstSegs[i], secSegs[i], true);
			if (compareRet != 0) {
				return compareRet;
			}
		}

		return 0;
	}

	/**
	 * 判断两个pattern是否有冲突即同一层级即定义变量又定义常量，如下：<br/>
	 * /u/111111 与 /u/{id}
	 * 
	 * @param pattern1
	 * @param pattern2
	 * @param varPrefix
	 * @param varPostfix
	 */
	public static void validatePattern(final String pattern1,
			final String pattern2, final char varPrefix, final char varPostfix) {
		final String[] firstSegs = pattern1.split("/");
		final String[] secSegs = pattern2.split("/");
		/*
		 * if (secSegs.length != firstSegs.length) { return; }
		 */
		final int count = firstSegs.length < secSegs.length ? firstSegs.length
				: secSegs.length;
		for (int i = 0; i < count; i++) {
			if (!(firstSegs[i] + secSegs[i]).contains(varPrefix + "")
					&& !firstSegs[i].equalsIgnoreCase(secSegs[i])) {
				break;
			}
			if ((firstSegs[i] + secSegs[i]).contains(varPrefix + "")) {
				if (!firstSegs[i].contains(varPrefix + "")
						|| !secSegs[i].contains(varPrefix + "")) {
					throw new InvalidURLPatternException(pattern1 + " and "
							+ pattern2 + "  conflict.");
				}
			}

		}
	}

	/**
	 * 比较两个字符串大小 <br/>
	 * 规则:<br/>
	 * a 小于 b <br/>
	 * ab大于b <br/>
	 * ab 小于 ac <br/>
	 * a 等于 a<br/>
	 * 
	 * @param str1
	 * @param str2
	 * @param ignoreCase
	 * @return
	 */
	private static int compareString(final String str1, final String str2,
			final boolean ignoreCase) {
		final char[] firstChrs = str1.toCharArray();
		final char[] secChrs = str2.toCharArray();
		if (firstChrs.length > secChrs.length) {
			return 1;
		}
		if (firstChrs.length < secChrs.length) {
			return -1;
		}
		for (int i = 0; i < secChrs.length; i++) {
			if (firstChrs[i] > secChrs[i]) {
				return 1;
			}
			if (firstChrs[i] < secChrs[i]) {
				return -1;
			}
		}
		return 0;
	}

	/**
	 * 比较两个urlpattern，标准是<br/>
	 * /a/ == /a/<br/>
	 * /a/ == /a<br/>
	 * /a/b > /a/<br/>
	 * /a/ < /b/ <br/>
	 * 
	 * @param url1
	 * @param url2
	 * @param ignorePrefix
	 * @param ignorePostfix
	 * @param compareLen
	 *            是否比较长度，如果是，长度长的较大
	 * @return
	 */
	private static int _comparePattern(final String url1, final String url2,
			final char ignorePrefix, final char ignorePostfix,
			final boolean compareLen) {
		if (url1 == null || url1.length() == 0) {
			if (url2 == null || url2.length() == 0) {
				return 0;
			}
			return -1;
		}
		if (url2 == null || url2.length() == 0) {
			return 1;
		}

		final char[] firstArr = url1.toCharArray();
		final char[] secArr = url2.toCharArray();

		final int firstLen = firstArr.length;
		final int secLen = secArr.length;

		int firstCounter = 0;
		int secCounter = 0;

		boolean ignoringFirst = false;
		boolean ignoringSec = false;

		while (firstCounter < firstLen && secCounter < secLen) {
			if (firstArr[firstCounter] == ignorePrefix) {

				ignoringFirst = true;
			} else if (firstArr[firstCounter] == ignorePostfix) {
				ignoringFirst = false;
			}
			if (secArr[secCounter] == ignorePrefix) {

				ignoringSec = true;
			} else if (secArr[secCounter] == ignorePostfix) {

				ignoringSec = false;
			}
			// 如果有处于ignore状态的，则等两方都不是ignore后再进行比较
			if (ignoringFirst || ignoringSec) {
				if (ignoringFirst) {
					firstCounter++;
				}
				if (ignoringSec) {
					secCounter++;
				}
				continue;
			}
			// 逐位比较
			if (!ignoringFirst && !ignoringSec) {
				if (firstArr[firstCounter] > secArr[secCounter]) {
					return 1;
				}
				if (firstArr[firstCounter] < secArr[secCounter]) {
					return -1;
				}
			}
			firstCounter++;
			secCounter++;
		}
		if (compareLen) {
			if (firstCounter == firstLen) {
				if (secCounter == secLen) {
					return 0;
				}
				return -1;
			}
			if (secCounter == secLen) {
				return 1;
			}
		}
		return -1;
	}

}
