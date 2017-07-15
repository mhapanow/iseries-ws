package org.dcm.services.tools;

import java.util.Date;
import java.util.logging.Logger;

import org.dcm.services.exception.DCMException;

public class BasicDataBuilder {

	private static final Logger log = Logger.getLogger(BasicDataBuilder.class.getName());
	
	/**
	 * Auth Helper to use. By now, it is fixed to AuthHelper
	 */

    /**
     * Builds all the basic data needed to run the app
     * @throws DCMException
     */
	public static void buildBasicData() throws DCMException {
		@SuppressWarnings("unused")
		BasicDataBuilder bdb = new BasicDataBuilder();
	}
	
	/**
	 * Does a simple get as a warm up request
	 * @throws DCMException
	 */
	public static void warmUp() throws DCMException {
		long start = new Date().getTime();
		@SuppressWarnings("unused")
		BasicDataBuilder bdb = new BasicDataBuilder();
		long end = new Date().getTime();
		log.info("warmup in " + (end - start) + " ms.");
	}
	
}
