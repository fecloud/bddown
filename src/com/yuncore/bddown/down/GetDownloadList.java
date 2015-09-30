/**
 * @(#) GetDownloadList.java Created on 2015年9月30日
 *
 * 
 */
package com.yuncore.bddown.down;

import com.yuncore.bddown.api.FSApi;
import com.yuncore.bddown.entity.CloudPageFile;
import com.yuncore.bddown.entity.LocalFile;
import com.yuncore.bddown.exception.ApiException;
import com.yuncore.bddown.util.Log;

/**
 * The class <code>GetDownloadList</code>
 * 
 * @author Feng OuYang
 * @version 1.0
 */
public class GetDownloadList implements BDDownloadStep {

	private static final String TAG = "GetDownloadList";

	private String root;

	private FSApi fsApi;

	/**
	 * @param root
	 * @param fsApi
	 */
	public GetDownloadList(String root, FSApi fsApi) {
		super();
		this.root = root;
		this.fsApi = fsApi;
	}

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
		download.getDownLoadList().clear();

		try {
			final CloudPageFile list = fsApi.list(root, 1, 10000);
			if (null != list) {
				if (list.getErrno() == -9) {
					Log.w(TAG, root + " not exists");
				} else if (list.getErrno() == 0) {
					if (null != list.getList()) {
						long sum = 0l;
						for (LocalFile f : list.getList()) {
							f.setPath(f.getPath().substring(root.length()));
							sum += f.getLength();
						}

						download.getDownLoadList().addAll(list.getList());
						Log.w(TAG, "all files size:" + byteSizeToHuman(sum));
					}
					return true;
				} else {
					Log.e(TAG, "GetDownloadList error:" + list.getErrno());
				}
			}

		} catch (ApiException e) {
			Log.e(TAG, "execute error", e);
		}
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}
		return false;
	}

	public static final String byteSizeToHuman(long size) {
		final String BYTE_SIZE_UNIT[] = { "byte", "KB", "MB", "GB", "TB" };
		final StringBuilder builder = new StringBuilder();
		int i = 0;
		double unit = size;
		double temp = 0;
		while ((temp = (unit / 1024)) >= 1) {
			i++;
			unit = temp;
		}
		builder.append(String.format("%.1f", unit));
		// builder.append(".").append(size % 1024);
		builder.append(BYTE_SIZE_UNIT[i]);
		return builder.toString();
	}

}
