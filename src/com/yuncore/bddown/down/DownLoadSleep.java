/**
 * @(#) DownLoadSleep.java Created on 2015年9月30日
 *
 * 
 */
package com.yuncore.bddown.down;

import com.yuncore.bddown.util.Log;

/**
 * The class <code>DownLoadSleep</code>
 * 
 * @author Feng OuYang
 * @version 1.0
 */
public class DownLoadSleep implements BDDownloadStep {

	private static final String TAG = "DownLoadSleep";

	private static final long INTERVAL = 60 * 60 * 1000;

	private long time;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yuncore.bddown.down.BDDownloadStep#execute(com.yuncore.bddown.down.
	 * BDDownload)
	 */
	@Override
	public boolean execute(BDDownload download) {
		Log.d(TAG, "execute");
		if (time == 0 || (System.currentTimeMillis() - time >= INTERVAL)) {
			time = System.currentTimeMillis();
			try {
				Thread.sleep(INTERVAL);
			} catch (InterruptedException e) {
			}
		}
		return true;
	}

}
