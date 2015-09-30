/**
 * @(#) Environment.java Created on 2015-9-7
 *
 * 
 */
package com.yuncore.bddown;

/**
 * The class <code>Environment</code>
 * 
 * @author Feng OuYang
 * @version 1.0
 */
public final class Environment {

	/**
	 * cookie文件路径
	 */
	public static final String COOKIE_FILE = "bddown.cookie";

	/**
	 * 日志文件路径
	 */
	public static final String LOG_FILE = "bddown.log.file";

	/**
	 * 日志等级
	 */
	public static final String LOG_PRIORITY = "bddown.log.priority";

	/**
	 * 云端目录
	 */
	public static final String BDDIR = "bddown.bddir";

	/**
	 * 下载到本地目录
	 */
	public static final String LOCALDIR = "bddown.localdir";

	/**
	 * 下载文件临时目录
	 */
	public static final String BDDOWNTMPDIR = "bddown.tmp";

	/**
	 * cookie容器
	 */
	public static final String COOKIECONTAINER = "bdsync.cookiecontainer";

	/**
	 * 程序启动时间
	 */
	public static final String BOOT_TIME = "start.runtime";

	static {
		System.getProperty(BOOT_TIME, "" + System.currentTimeMillis());
	}

	public static String getProperty(String key) {
		return System.getProperty(key);
	}

	public static String getProperty(String key, String def) {
		return System.getProperty(key, def);
	}

	public static void setProperty(String key, String value) {
		System.setProperty(key, value);
	}

	public static final void setCookieFile(String file) {
		System.setProperty(COOKIE_FILE, file);
	}

	public static final String getCookieFile() {
		return System.getProperty(COOKIE_FILE, "resource/cookie.json");
	}

	public static final String getLogFile() {
		return System.getProperty(LOG_FILE, "bddown.log");
	}

	public static final String getLogPriority() {
		return System.getProperty(LOG_PRIORITY, "VERBOSE");
	}

	public static final String getJavaTmpDir() {
		return System.getProperty("java.io.tmpdir", "");
	}

	public static final void setCookiecontainerClassName(String className) {
		System.setProperty(COOKIECONTAINER, className);
	}

	public static final String getCookiecontainerClassName() {
		return System.getProperty(COOKIECONTAINER, null);
	}

	public static final String getBDDir() {
		return System.getProperty(BDDIR, null);
	}

	public static final void setBDDir(String dir) {
		System.setProperty(BDDIR, dir);
	}

	public static final String getLocalDir() {
		return System.getProperty(LOCALDIR, "");
	}

	public static final void setLocalDir(String dir) {
		System.setProperty(LOCALDIR, dir);
	}

	public static final String getBDdownTmpDir() {
		return System.getProperty(BDDOWNTMPDIR, "");
	}

	public static final void setBDdownTmpDir(String dir) {
		System.setProperty(BDDOWNTMPDIR, dir);
	}

}
