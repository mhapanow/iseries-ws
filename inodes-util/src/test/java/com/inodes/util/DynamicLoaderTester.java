/**
 * 
 */
package com.inodes.util;

import org.junit.Test;

import junit.framework.TestCase;

/**
 * @author mhapanowicz
 *
 */
public class DynamicLoaderTester extends TestCase {

	@Test
	public void testDynamicLoader1() {
		DynamicLoader dl = new DynamicLoader();
		dl.adaptClassPath(new String[] {"C:\\Users\\mhapanowicz\\Desktop\\Temp\\clocks\\lib"});
	}
	
	public void testMain() {
		System.setProperty("dynamic.loader", "C:\\Users\\mhapanowicz\\Desktop\\Temp\\clocks\\lib");
		DynamicLoader.main(new String[]{"com.inodes.clockmanager.remote.RemoteClockManager", 							"--subject=MANAGER.REMOTE.0",
				"--manager-queue-subject=MANAGER.LOCAL.0",
				"--manager-queue-type=1",
				"--manager-queue-url=failover://tcp://localhost:61616",
				"--manager-queue-user=null",
				"--manager-queue-password=null"
});
	}
}
