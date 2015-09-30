package com.yuncore.bddown.api.imple;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yuncore.bddown.api.BDSYNCURL;
import com.yuncore.bddown.api.DownloadInputStream;
import com.yuncore.bddown.api.FSApi;
import com.yuncore.bddown.entity.CloudPageFile;
import com.yuncore.bddown.exception.ApiException;
import com.yuncore.bddown.http.Http;
import com.yuncore.bddown.http.Http.Method;
import com.yuncore.bddown.http.HttpInput;
import com.yuncore.bddown.util.Log;

public class FSApiImple implements FSApi {

	static final String TAG = "FSApiImple";

	private static boolean DEBUG = false;

	private static final Properties CONTEXT = new Properties();

	/**
	 * 10分针刷新一次
	 */
	protected static final int INTERVAL = 10 * 60 * 1000;

	protected static volatile long time;

	private final void load() throws ApiException {
		if (time == 0 || System.currentTimeMillis() - time > INTERVAL) {
			Map<String, String> diskHomePage = diskHomePage();
			CONTEXT.clear();
			CONTEXT.putAll(diskHomePage);
			time = System.currentTimeMillis();
		}
	}

	/**
	 * 下载文件 注意响应头有Content-MD5
	 */
	@Override
	public DownloadInputStream download(String file) throws ApiException {
		return download(file, 0);
	}

	@Override
	public DownloadInputStream download(String file, long range) throws ApiException {
		try {
			load();

			final String url = BDSYNCURL.download(file);
			final HttpInput http = new HttpInput(url, Method.GET);
			http.addRequestProperty("Range", String.format("bytes=%s- ", range));
			if (http.http()) {
				final DownloadInputStream in = new DownloadInputStream(http.getInputStream());
				in.setLength(http.getContentLength());
				if (http.getConnet().getHeaderFields().containsKey("Accept-Ranges")) {
					in.setRange(true);
				}

				if (http.getConnet().getHeaderFields().containsKey("Content-MD5")) {
					in.setContentMd5(http.getConnet().getHeaderFields().get("Content-MD5").toString());
				} else if (http.getConnet().getHeaderFields().containsKey("content-md5")) {
					in.setContentMd5(http.getConnet().getHeaderFields().get("content-md5").toString());
				}

				return in;
			} else {
				final int code = http.getResponseCode();
				Log.i(TAG, "download " + code);
				if (code == HttpURLConnection.HTTP_NOT_FOUND) {
					final DownloadInputStream in = new DownloadInputStream(null);
					in.setLength(-1);
					return in;
				}
			}
		} catch (Exception e) {
			throw new ApiException("download error", e);
		}
		return null;
	}

	@Override
	public Map<String, String> diskHomePage() throws ApiException {
		final String url = BDSYNCURL.diskHomePage();

		final Http http = new Http(url, Method.GET);
		try {
			if (http.http() && http.getResponseCode() == HttpURLConnection.HTTP_OK) {
				if (DEBUG)
					Log.d(TAG, String.format("diskHomePage:%s", http.result()));
				final Pattern pattern = Pattern.compile("yunData\\.\\w+\\s*=\\s*['|\"].*['|\"];");
				final Matcher matcher = pattern.matcher(http.result());
				String temp = null;
				String[] strings = null;
				final Map<String, String> maps = new Hashtable<String, String>();
				while (matcher.find()) {
					temp = matcher.group();
					if (null != temp) {
						strings = temp.split("=");
						if (null != strings && strings.length == 2) {
							maps.put(strings[0].trim().replaceAll("yunData.", ""),
									strings[1].trim().replaceAll("'", "").replaceAll(";", "").replaceAll("\"", ""));
						}
					}
				}
				if (maps.isEmpty()) {
					throw new ApiException("diskHomePage error maps empty");
				}
				return maps;
			} else {
				throw new ApiException("diskHomePage error not load " + url);
			}
		} catch (IOException e) {
			throw new ApiException("diskHomePage error", e);
		}
	}

	@Override
	public CloudPageFile list(String dir, int page, int page_num) throws ApiException {
		try {
			load();
			final long c_time = System.currentTimeMillis();
			final String url = BDSYNCURL.list(page, page_num, dir, c_time, CONTEXT.getProperty(BDSTOKEN));

			final Http http = new Http(url, Method.GET);
			if (http.http()) {
				final CloudPageFile pageFile = new CloudPageFile();
				if (pageFile.formJOSN(http.result())) {
					return pageFile;
				}
			}

		} catch (Exception e) {
			throw new ApiException("list error", e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.bdsync.api.FSApi#who()
	 */
	@Override
	public String who() throws ApiException {
		load();
		return (String) CONTEXT.get("MYNAME");
	}

}
