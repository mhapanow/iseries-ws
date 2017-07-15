package org.dcm.services.impl;

import java.text.DecimalFormat;
import java.util.Map;

import org.dcm.services.exception.DCMException;
import org.dcm.services.exception.DCMExceptionHelper;
import org.dcm.services.model.FieldDescriptor;
import org.dcm.services.model.RecordDescriptor;
import org.json.JSONObject;

import com.inodes.util.CollectionFactory;

public class RecordHelper {

	private final static DecimalFormat df = new DecimalFormat("0");
	
	/**
	 * Converts String formatted record to a JSON Object
	 * 
	 * @param rd
	 *            The Record Descriptor used
	 * @param stringValue
	 *            The String valued object
	 * @return A fully formatted JSON Object
	 * @throws DCMException
	 */
	public static JSONObject toJson(RecordDescriptor rd, String stringValue) throws DCMException {
		
		try {
			JSONObject obj = new JSONObject();
			int offset = 0;
			for( FieldDescriptor fd : rd.getFields()) {
				int to = offset + fd.getLength();
				if( to > stringValue.length()) to = stringValue.length();
				String val = stringValue.substring(offset, to);
				if( fd.getType().equals(FieldDescriptor.TYPE_CHAR)) {
					obj.put(fd.getJsonName(), val.trim());
				} else if( fd.getType().equals(FieldDescriptor.TYPE_ZONED)) {
					if( fd.getDecimals() == 0 ) {
						obj.put(fd.getJsonName(), Long.parseLong(val));
					} else {
						Double d = Double.parseDouble(val);
						for( int i = 0; i < fd.getDecimals(); i++ ) {
							d = d / 10;
						}
						obj.put(fd.getJsonName(), d);
					}
				}
				offset += fd.getLength();
			}
			
			return obj;
			
		} catch( Exception e ) {
			throw DCMExceptionHelper.defaultException(e.getMessage(), e);
		}
		
	}

	/**
	 * Converts String formatted record to a Map Object
	 * 
	 * @param rd
	 *            The Record Descriptor used
	 * @param stringValue
	 *            The String valued object
	 * @return A fully formatted Map Object
	 * @throws DCMException
	 */
	public static Map<String, Object> toMap(RecordDescriptor rd, String stringValue) throws DCMException {
		
		Map<String, Object> map = CollectionFactory.createMap();
		JSONObject json = toJson(rd, stringValue);
		
		String[] keys = JSONObject.getNames(json);
		for( String key : keys ) {
			map.put(key, json.get(key));
		}
		
		return map;
	}
	
	/**
	 * Returns a record string from a JSON Object
	 * 
	 * @param rd
	 *            The Record Destriptor to use
	 * @param json
	 *            The input JSON
	 * @return a String representing the record
	 * @throws DCMException
	 */
	public static String fromJson(RecordDescriptor rd, JSONObject json) throws DCMException {
		
		try {
			StringBuffer sb = new StringBuffer();
			StringBuffer val = new StringBuffer();

			for( FieldDescriptor fd : rd.getFields()) {
				
				val = new StringBuffer();
				
				if( json.has(fd.getJsonName())) {
					if( fd.getType().equals("CHAR")) {
						String sv = json.getString(fd.getJsonName());
						if( sv.length() > fd.getLength()) {
							val.append(sv.substring(0, fd.getLength()));
						} else {
							val.append(sv);
							for( int i = sv.length(); i < fd.getLength(); i++ ) {
								val.append(" ");
							}
						}
					} else {
						Double sv = json.getDouble(fd.getJsonName());
						for( int i = 0; i < fd.getDecimals(); i++ ) {
							sv = sv * 10;
						}
						String sv1 = df.format(sv);
						if( sv1.length() < fd.getLength()) {
							for( int i = sv1.length(); i < fd.getLength(); i++ )
								val.append("0");
							val.append(sv1);
						} else {
							val.append(sv1.substring(sv1.length() - fd.getLength() -1));
						}
					}
					
				} else {
					for( int i = 0; i < fd.getLength(); i++ ) {
						if( fd.getType().equals("CHAR")) 
							val.append(" ");
						else
							val.append("0");
					}
				}
				
				sb.append(val.toString());
				
			}
			
			return sb.toString();
			
		} catch( Exception e ) {
			throw DCMExceptionHelper.defaultException(e.getMessage(), e);
		}
		
	}
	
	
	/**
	 * Converts a JSON Formatted object into a String Record Format
	 * 
	 * @param rd
	 *            The Record Descriptor used
	 * @param jsonValue
	 *            The JSON Object
	 * @return a Fully formed Record Format String
	 * @throws DCMException
	 */
	public static String toString(RecordDescriptor rd, JSONObject jsonValue) throws DCMException {

		try {
			
			StringBuffer sb = new StringBuffer();
			for( FieldDescriptor fd : rd.getFields()) {
				if( fd.getType().equals(FieldDescriptor.TYPE_CHAR)) {
					String v = jsonValue.get(fd.getJsonName()).toString();
					if( v.length() > fd.getLength()) v = v.substring(0, fd.getLength());
					else {
						while( v.length() < fd.getLength())
							v = v + " ";
					}
					sb.append(v);
				} else if( fd.getType().equals(FieldDescriptor.TYPE_ZONED)) {
					String v = null;
					if( fd.getDecimals() == 0 ) {
						v = jsonValue.get(fd.getJsonName()).toString();
						if( v.length() > fd.getLength()) v = v.substring(0, fd.getLength());
						else {
							while( v.length() < fd.getLength())
								v = "0" +v;
						}
					} else {
						Double d = Double.parseDouble(jsonValue.get(fd.getJsonName()).toString());
						for( int i = 0; i < fd.getDecimals(); i++) {
							d = d * 10;
						}
						long v1 = d.longValue();
						v = String.valueOf(v1);
						if( v.length() > fd.getLength()) v = v.substring(0, fd.getLength());
						else {
							while( v.length() < fd.getLength())
								v = "0" +v;
						}
					}
					sb.append(v);
				}
			}
			
			return sb.toString();
			
		} catch( Exception e ) {
			throw DCMExceptionHelper.defaultException(e.getMessage(), e);
		}
	}
	
}
