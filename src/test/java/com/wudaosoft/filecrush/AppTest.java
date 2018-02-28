package com.wudaosoft.filecrush;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	/**
	 * Rigourous Test :-)
	 * @throws IOException 
	 */
	public void testApp() throws IOException {
		
		App.main(new String[]{"D:\\TDDOWNLOAD", "--eee"});
		
//		System.out.println(new File("D:\\TDDOWNLOAD\\jdk-8u77-macosx-x64.dmg").getParent());
		assertTrue(true);
	}
}
