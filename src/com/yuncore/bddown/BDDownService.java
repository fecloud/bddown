/**
 * @(#) BDDownService.java Created on Sep 9, 2015
 *
 * 
 */
package com.yuncore.bddown;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.yuncore.bddown.util.Log;

/**
 * The class <code>BDDownService</code>
 * 
 * @author Feng OuYang
 * @version 1.0
 */
public class BDDownService extends ShutDownBDDownService {

	private static final String TAG = "BDDownService";

	public BDDownServiceListener listener;

	public BDDownService(BDDownServiceListener listener) {
		this.listener = listener;
	}

	public synchronized void start() {
		final int port = Integer.parseInt(System.getProperty(SERVICE_PORT, "10002"));
		try {

			final ServerSocket serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress("127.0.0.1", port));
			if (listener != null) {
				listener.onStart();
			}

			final Socket accept = serverSocket.accept();
			Log.w(TAG, "request shutdown service");
			if (null != listener) {
				listener.onStop();
			}

			accept.getOutputStream().write("1".getBytes("UTF-8"));
			accept.getOutputStream().flush();

			accept.close();
			Thread.sleep(1000);
			serverSocket.close();
			Log.w(TAG, "service exit");
			System.exit(0);
		} catch (Exception e) {
			System.exit(1);
		}
	}

	/**
	 * @return the listener
	 */
	public BDDownServiceListener getBDSyncServiceListener() {
		return listener;
	}

	/**
	 * @param listener
	 *            the listener to set
	 */
	public void setBDSyncServiceListener(BDDownServiceListener listener) {
		this.listener = listener;
	}

	/**
	 * 当收到请求关闭程序时 The class <code>BDDownServiceListener</code>
	 * 
	 * @author Feng OuYang
	 * @version 1.0
	 */
	public interface BDDownServiceListener {

		void onStart();

		void onStop();

	}

}
