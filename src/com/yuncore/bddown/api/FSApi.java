/**
 * @(#) FSApi.java Created on 2015年9月30日
 *
 * 
 */
package com.yuncore.bddown.api;

import java.util.Map;

import com.yuncore.bddown.entity.CloudPageFile;
import com.yuncore.bddown.exception.ApiException;

/**
 * The class <code>FSApi</code>
 * 
 * @author Feng OuYang
 * @version 1.0
 */
public interface FSApi {
	
	/**
	 * bdstoken
	 */
	String BDSTOKEN = "MYBDSTOKEN";

	/**
	 * 到page 取参数
	 * 
	 * @return
	 */
	public Map<String, String> diskHomePage() throws ApiException;

	/**
	 * 当前用户
	 * 
	 * @return
	 */
	public String who() throws ApiException;

	/**
	 * 列表当前目录的文件(包含文件夹)
	 * 
	 * @param dir
	 * @return
	 */
	public CloudPageFile list(String dir, int page, int page_num) throws ApiException;

	/**
	 * 下载文件
	 * 
	 * @param file
	 * @return
	 */
	public DownloadInputStream download(String file) throws ApiException;

	/**
	 * 下载文件(断点)
	 * 
	 * @param file
	 * @return
	 */
	public DownloadInputStream download(String file, long range) throws ApiException;

}
