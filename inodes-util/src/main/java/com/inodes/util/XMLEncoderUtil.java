/**
 * 
 */
package com.inodes.util;

import com.thoughtworks.xstream.XStream;


/**
 * @author mhapanowicz
 *
 */
public class XMLEncoderUtil {

	public static String XMLencode(Object obj) {
		XStream xstream = new XStream();
		String ret = xstream.toXML(obj);
		return ret;
	}
	
	public static Object XMLDecode(String stream) {
		XStream xstream = new XStream();
		Object o = xstream.fromXML(stream);
		return o;
	}
}
