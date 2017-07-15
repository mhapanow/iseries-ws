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
public class FileLoaderTester extends TestCase {

	@Test
	public void testFileLoader1() {
		System.out.println(FileLoader.getResource("log4j.properties"));
	}
	
}
