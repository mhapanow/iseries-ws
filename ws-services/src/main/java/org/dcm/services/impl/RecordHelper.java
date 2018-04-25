package org.dcm.services.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Map;

import org.dcm.services.exception.DCMException;
import org.dcm.services.exception.DCMExceptionHelper;
import org.dcm.services.model.FieldDescriptor;
import org.dcm.services.model.RecordDescriptor;
import org.json.JSONObject;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
	public static JsonObject toJson(RecordDescriptor rd, String stringValue) throws DCMException {

		try {
			JsonObject obj = new JsonObject();
			int offset = 0;
			for( FieldDescriptor fd : rd.getFields()) {
				int to = offset + fd.getLength();
				if( to > stringValue.length()) to = stringValue.length();
				String val = stringValue.substring(offset, to);
				if( fd.getType().equals(FieldDescriptor.TYPE_CHAR)) {
					obj.addProperty(fd.getJsonName(), val.trim());
				} else if( fd.getType().equals(FieldDescriptor.TYPE_ZONED)) {
					if( fd.getDecimals() == 0 ) {
						obj.addProperty(fd.getJsonName(), Long.parseLong(val));
					}else {
						BigDecimal d = new BigDecimal(val);
						for( int i = 0; i < fd.getDecimals(); i++ ) {
							d = d.divide(new BigDecimal(10));
						}
						obj.addProperty(fd.getJsonName(), d);
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
		JsonObject json = toJson(rd, stringValue);


		String[] keys = new String[json.entrySet().size()];

		Integer i = 0;
		for(Map.Entry<String, JsonElement> jsonEntry : json.entrySet()) {
			keys[i++] = jsonEntry.getKey();
		}	

		for( String key : keys ) {
			map.put(key, json.get(key).getAsString());
		}

		return map;
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
	public static Map<String, Object> toMapOld(RecordDescriptor rd, String stringValue) throws DCMException {

		Map<String, Object> map = CollectionFactory.createMap();
		JSONObject json = toJsonOld(rd, stringValue);

		String[] keys = JSONObject.getNames(json);
		for( String key : keys ) {
			map.put(key, json.get(key));
		}

		return map;
	}

	public static JSONObject toJsonOld(RecordDescriptor rd, String stringValue) throws DCMException {

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
	 * Returns a record string from a JSON Object
	 * 
	 * @param rd
	 *            The Record Destriptor to use
	 * @param json
	 *            The input JSON
	 * @return a String representing the record
	 * @throws DCMException
	 */
	public static String fromJson(RecordDescriptor rd, JsonObject json) throws DCMException {

		try {
			StringBuffer sb = new StringBuffer();
			StringBuffer val = new StringBuffer();

			for( FieldDescriptor fd : rd.getFields()) {

				val = new StringBuffer();

				JsonObject myJson = json;
				String fieldName = fd.getJsonName();

				if( fieldName.contains(".")) {
					String parts[] = fieldName.split("\\.");

					if( parts[0].contains("[")) {
						String sParts[] = parts[0].split("\\[");
						sParts[1] = sParts[1].substring(0, sParts[1].length() -1);
						int index = Integer.valueOf(sParts[1]);
						if( myJson.has(sParts[0])) {
							try {
								myJson = myJson.getAsJsonArray(sParts[0]).get(index).getAsJsonObject();
								fieldName = parts[1];
							} catch( Exception e) {}
						}
					} else {

						if( myJson.has(parts[0])) {

							myJson = myJson.getAsJsonObject(parts[0]);
							fieldName = parts[1];
						}
					}
				}

				if( myJson.has(fieldName)) {
					if( fd.getType().equals("CHAR")) {
						String sv = null;
						// boolean patch for cases when the value is not enclosed in ""
						try {
							sv = myJson.get(fieldName).getAsString();
						} catch( Exception e ) {
							Boolean bsv = myJson.get(fieldName).getAsBoolean();
							sv = bsv.toString(); 
						}
						if( sv.length() > fd.getLength()) {
							val.append(sv.substring(0, fd.getLength()));
						} else {
							val.append(sv);
							for( int i = sv.length(); i < fd.getLength(); i++ ) {
								val.append(" ");
							}
						}
					} else {
						BigDecimal sv = myJson.get(fieldName).getAsBigDecimal();
						for( int i = 0; i < fd.getDecimals(); i++ ) {
							sv = sv.multiply(new BigDecimal(10));
						}
						String sv1 = df.format(sv);
						if( sv1.length() < fd.getLength()) {
							for( int i = sv1.length(); i < fd.getLength(); i++ )
								val.append("0");
							val.append(sv1);
						} else {
							if( sv1.length() == fd.getLength()) {
								val.append(sv1.substring(sv1.length() - fd.getLength()));
							} else {
								val.append(sv1.substring(sv1.length() - fd.getLength() -1));
							}
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
	 * Returns a record string from a JSON Object
	 * 
	 * @param rd
	 *            The Record Destriptor to use
	 * @param json
	 *            The input JSON
	 * @return a String representing the record
	 * @throws DCMException
	 */
	public static String fromJsonOld(RecordDescriptor rd, JSONObject json) throws DCMException {
		
		try {
			StringBuffer sb = new StringBuffer();
			StringBuffer val = new StringBuffer();

			for( FieldDescriptor fd : rd.getFields()) {
				
				val = new StringBuffer();
				
				JSONObject myJson = new JSONObject(json.toString());
				String fieldName = fd.getJsonName();
				
				if( fieldName.contains(".")) {
					String parts[] = fieldName.split("\\.");

					if( parts[0].contains("[")) {
						String sParts[] = parts[0].split("\\[");
						sParts[1] = sParts[1].substring(0, sParts[1].length() -1);
						int index = Integer.valueOf(sParts[1]);
						if( myJson.has(sParts[0])) {
							try {
								myJson = myJson.getJSONArray(sParts[0]).getJSONObject(index);
								fieldName = parts[1];
							} catch( Exception e) {}
						}
					} else {

						if( myJson.has(parts[0])) {

							myJson = myJson.getJSONObject(parts[0]);
							fieldName = parts[1];
						}
					}
				}
				
				if( myJson.has(fieldName)) {
					if( fd.getType().equals("CHAR")) {
						String sv = null;
						// boolean patch for cases when the value is not enclosed in ""
						try {
							sv = myJson.getString(fieldName);
						} catch( Exception e ) {
							Boolean bsv = myJson.getBoolean(fieldName);
							sv = bsv.toString(); 
						}
						if( sv.length() > fd.getLength()) {
							val.append(sv.substring(0, fd.getLength()));
						} else {
							val.append(sv);
							for( int i = sv.length(); i < fd.getLength(); i++ ) {
								val.append(" ");
							}
						}
					} else {
						Double sv = myJson.getDouble(fieldName);
						for( int i = 0; i < fd.getDecimals(); i++ ) {
							sv = sv * 10;
						}
						String sv1 = df.format(sv);
						if( sv1.length() < fd.getLength()) {
							for( int i = sv1.length(); i < fd.getLength(); i++ )
								val.append("0");
							val.append(sv1);
						} else {
							if( sv1.length() == fd.getLength()) {
								val.append(sv1.substring(sv1.length() - fd.getLength()));
							} else {
								val.append(sv1.substring(sv1.length() - fd.getLength() -1));
							}
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
