/**
 * @(#) DownLoadFiles.java Created on 2015年9月30日
 *
 * 
 */
package com.yuncore.bddown.down;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import com.yuncore.bddown.api.DownloadInputStream;
import com.yuncore.bddown.api.FSApi;
import com.yuncore.bddown.entity.LocalFile;
import com.yuncore.bddown.util.FileMV;
import com.yuncore.bddown.util.Log;

/**
 * The class <code>DownLoadFiles</code>
 * 
 * @author Feng OuYang
 * @version 1.0
 */
public class DownLoadFiles implements BDDownloadStep {

	private static final String TAG = "DownLoadFiles";

	private String bdroot;

	private String localroot;

	private String tmpdir;

	private FSApi api;

	/**
	 * @param bdroot
	 * @param localroot
	 * @param tmpdir
	 */
	public DownLoadFiles(String bdroot, String localroot, String tmpdir, FSApi api) {
		super();
		this.bdroot = bdroot;
		this.localroot = localroot;
		this.tmpdir = tmpdir;
		this.api = api;
		checkDir();
	}

	private final void checkDir() {
		final File localrootFile = new File(localroot);
		if (!localrootFile.exists()) {
			localrootFile.mkdirs();
		}

		final File tmpdirFile = new File(tmpdir);
		if (!tmpdirFile.exists()) {
			tmpdirFile.mkdirs();
		}
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
		
		final List<LocalFile> downs = download.getDownLoadList();
		for (LocalFile file : downs) {
			if (file.isDir()) {
				continue;
			}

			if (!existsLocal(file)) {
				downFile(file);
			}
		}

		return true;
	}

	/**
	 * 检查文件在不在
	 * 
	 * @param cloudFile
	 * @return
	 */
	private final boolean existsLocal(LocalFile cloudFile) {

		final LocalFile localFile = getLocalFile(localroot, cloudFile.getAbsolutePath());
		if (localFile == null) {
			return false;

		}
		final String cloudfid = cloudFile.toFid();
		final String localfid = localFile.toFid();
		if (cloudfid.equals(localfid)) {
			return true;
		}
		return false;
	}

	/**
	 * 下载文件
	 * 
	 * @param cloudFile
	 */
	private final void downFile(LocalFile cloudFile) {

		Log.d(TAG, "downFile " + cloudFile.getAbsolutePath());
		final String tmpFile = tmpdir + "/" + cloudFile.toFid();
		final String finalFile = localroot + cloudFile.getAbsolutePath();

		long sum = 0;
		try {
			long fileStart = checkTempFile(tmpFile);
			// 如果文件下载完成没有移动到目录路径
			if (fileStart == cloudFile.getLength()) {
				if (new FileMV(tmpFile, finalFile).mv()) {
					return;
				} else {
					// 重新下载
					fileStart = 0;
				}
			}

			DownloadInputStream in = null;
			FileOutputStream out = null;

			if (fileStart > 0) {
				Log.d(TAG, "continue download file start:" + fileStart);
				sum = fileStart;
				in = api.download(bdroot + cloudFile.getAbsolutePath(), fileStart);
				if (in.getLength() + sum != cloudFile.getLength()) {
					// 下载返回来的文件大小与要下载的大小不一致,可能文件被改了
					return;
				}
				out = new FileOutputStream(tmpFile, true);
			} else {
				Log.d(TAG, "new download file start:0");
				in = api.download(bdroot + cloudFile.getAbsolutePath());
				if (in.getLength() != cloudFile.getLength()) {
					// 下载返回来的文件大小与要下载的大小不一致,可能文件被改了
					return;
				}
				out = new FileOutputStream(tmpFile);
			}

			if (in != null && (in.getLength() == -1)) {
				// 文件被删除了,可能之前有临时文件 删除,或者md5不对的
				Log.w(TAG, "cloudfile is delete can not down");
				final File file2 = new File(tmpFile);
				file2.delete();
				in.close();
				out.close();
				return;
			}

			if (in != null) {
				final byte[] buffer = new byte[1024 * 1024];
				int len = -1;

				while (-1 != (len = in.read(buffer))) {
					out.write(buffer, 0, len);
					sum += len;
					if (sum == cloudFile.getLength()) {
						break;
					}
				}
				out.flush();
				out.close();
				if (sum == cloudFile.getLength()) {
					new FileMV(tmpFile, finalFile).mv();
				}
				in.close();
				Log.i(TAG, "download " + cloudFile.getParentPath() + " success");
			}
		} catch (Exception e) {
			Log.e(TAG, "cloudFileContext error", e);
		}
	}

	public static final LocalFile getLocalFile(String root, String path) {
		final File file = new File(root, path);
		if (file.exists()) {
			final LocalFile localFile = new LocalFile();
			localFile.setDir(file.isDirectory());
			if (file.isDirectory()) {
				localFile.setLength(0);
			} else {
				localFile.setLength(file.length());
			}
			localFile.setMtime((int) file.lastModified());
			localFile.setPath(path);
			return localFile;
		}
		return null;
	}

	/**
	 * 检查临时文件
	 * 
	 * @param file
	 * @return
	 */
	private static final long checkTempFile(String file) {
		final File f = new File(file);
		if (f.exists()) {
			return f.length();
		} else {
			return -1;
		}
	}

}
