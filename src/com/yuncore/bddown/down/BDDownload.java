/**
 * @(#) BDDownload.java Created on 2015年9月30日
 *
 * 
 */
package com.yuncore.bddown.down;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.yuncore.bddown.Environment;
import com.yuncore.bddown.api.FSApi;
import com.yuncore.bddown.api.imple.FSApiImple;
import com.yuncore.bddown.entity.LocalFile;
import com.yuncore.bddown.exception.ApiException;
import com.yuncore.bddown.http.cookie.FileCookieContainer;
import com.yuncore.bddown.util.Log;

/**
 * The class <code>BDDownload</code>
 * 
 * @author Feng OuYang
 * @version 1.0
 */
public class BDDownload extends Thread {

	private static final String TAG = "BDDownload";

	private String from_dir;

	private String to_dir;

	private String tmpdir;

	private volatile boolean flag;

	private List<LocalFile> list = new ArrayList<LocalFile>();

	private List<BDDownloadStep> steps = new ArrayList<BDDownloadStep>();

	private FSApi api;

	/**
	 * @param from_cookie
	 * @param from_dir
	 * @param to_cookie
	 * @param to_dir
	 */
	public BDDownload(String from_dir, String to_dir, String tmpdir) {
		super();
		this.from_dir = from_dir;
		this.to_dir = to_dir;
		this.tmpdir = tmpdir;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#start()
	 */
	@Override
	public synchronized void start() {
		this.flag = true;
		api = new FSApiImple();
		steps.add(new GetDownloadList(from_dir, api));
		steps.add(new DownLoadFiles(from_dir, to_dir, tmpdir, api));
		steps.add(new DownLoadSleep());
		super.start();
	}

	public List<LocalFile> getDownLoadList() {
		return this.list;
	}

	/**
	 * 检查cookie有效性
	 * 
	 * @return
	 */
	private boolean checkCookie() {

		if (Environment.getCookieFile() == null || Environment.getCookieFile().trim().length() == 0) {
			Log.w(TAG, "please set cookie");
			System.exit(1);
		}

		if (!new File(Environment.getCookieFile()).exists()) {
			Log.w(TAG, "cookie file not exists");
			System.exit(1);
		}

		final FSApi fsApi = new FSApiImple();
		try {
			final String who = fsApi.who();
			if (null != who && who.trim().length() > 0) {
				Log.w(TAG, "current cookie user:" + who);
				return true;
			}
		} catch (ApiException e) {
		}
		Log.w(TAG, "cookie error exit");
		System.exit(1);
		return false;
	}

	private void setEnv() {
		Thread.currentThread().setName(TAG);
		Environment.setBDDir(from_dir);
		Environment.setBDdownTmpDir(tmpdir);
		Environment.setLocalDir(to_dir);
		Environment.setCookiecontainerClassName(FileCookieContainer.class.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		setEnv();
		checkCookie();
		while (flag) {
			for (int i = 0; i < steps.size() && flag; i++) {
				if (!steps.get(i).execute(this)) {
					break;
				}
			}
		}
	}
}
