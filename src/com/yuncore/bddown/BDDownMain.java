/**
 * @(#) BDDownMain.java Created on 2015年9月30日
 *
 * 
 */
package com.yuncore.bddown;

import com.yuncore.bddown.BDDownService.BDDownServiceListener;
import com.yuncore.bddown.down.BDDownload;

/**
 * The class <code>BDDownMain</code>
 * 
 * @author Feng OuYang
 * @version 1.0
 */
public class BDDownMain implements BDDownServiceListener {

	private BDDownload bdDownload;

	public BDDownMain(String[] args) {
		bdDownload = new BDDownload(args[0], args[1], args[2]);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args != null && args.length > 0) {
			if (args[0].equals("stop")) {
				new ShutDownBDDownService().start();
			} else if (args.length == 3) {
				final BDDownMain main = new BDDownMain(args);
				new BDDownService(main).start();
			} else {
				printHelp();
			}

		} else {
			printHelp();
		}
	}

	private static final void printHelp() {
		System.err.println("");
		System.err.println("Usage:bddown [stop] <bddir> <localdir> <tmpdir>");
		System.err.println("");
		System.err.println("VM args:-Dname=value");
		System.err.println("\tbddown.cookie cookie file location");
		System.err.println("\tbddown.log.file log file location");
		System.err.println("\tbddown.log.priority log level [VERBOSE,DEBUG,INFO,WARN,ERROR]");
		System.err.println("");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.bddown.BDDownService.BDDownServiceListener#onStart()
	 */
	@Override
	public void onStart() {
		bdDownload.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.bddown.BDDownService.BDDownServiceListener#onStop()
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void onStop() {
		bdDownload.stop();
	}

}
