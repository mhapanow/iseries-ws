package org.dcm.services.impl.test;

import java.util.ArrayList;
import java.util.Map;

import org.dcm.services.exception.DCMException;
import org.dcm.services.impl.RecordHelper;
import org.dcm.services.model.FieldDescriptor;
import org.dcm.services.model.RecordDescriptor;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RecordHelperTest {


	@Test
	public void testDecimalToJson() throws DCMException {

		RecordDescriptor rd = new RecordDescriptor();

		rd.setDescription("test");

		rd.setFields(new ArrayList<FieldDescriptor>());
		FieldDescriptor fieldDescriptor = new FieldDescriptor();


		fieldDescriptor.setType(FieldDescriptor.TYPE_ZONED);
		fieldDescriptor.setLength(18);
		fieldDescriptor.setDecimals(7);
		fieldDescriptor.setJsonName("key");

		rd.getFields().add(fieldDescriptor);

		JsonObject json = DecimalToJson();

		System.out.println(json);	
	}


	private JsonObject DecimalToJson() throws DCMException {

		String value = "000002000001234567";

		RecordDescriptor rd = new RecordDescriptor();

		rd.setDescription("test");

		rd.setFields(new ArrayList<FieldDescriptor>());
		FieldDescriptor fieldDescriptor = new FieldDescriptor();


		fieldDescriptor.setType(FieldDescriptor.TYPE_ZONED);
		fieldDescriptor.setLength(18);
		fieldDescriptor.setDecimals(7);
		fieldDescriptor.setJsonName("key");

		rd.getFields().add(fieldDescriptor);

		JsonObject json = RecordHelper.toJson(rd, value);	
		
		return json;
	}


	@Test
	public void testDecimalFromJson() throws DCMException {


		JsonObject json = DecimalToJson();

		RecordDescriptor rd = new RecordDescriptor();

		rd.setDescription("test");

		rd.setFields(new ArrayList<FieldDescriptor>());
		FieldDescriptor fieldDescriptor = new FieldDescriptor();


		fieldDescriptor.setType(FieldDescriptor.TYPE_ZONED);
		fieldDescriptor.setLength(18);
		fieldDescriptor.setDecimals(7);
		fieldDescriptor.setJsonName("key");

		rd.getFields().add(fieldDescriptor);

		System.out.println(RecordHelper.fromJson(rd, json));
	}

	@Test
	public void fullTestToJson() throws DCMException {

		String value = "PEPE ZARAZAtrue200000000001234567";

		RecordDescriptor rd = new RecordDescriptor();

		rd.setDescription("test");

		rd.setFields(new ArrayList<FieldDescriptor>());
		FieldDescriptor fieldDescriptor = new FieldDescriptor();


		fieldDescriptor.setType(FieldDescriptor.TYPE_CHAR);
		fieldDescriptor.setLength(11);
		fieldDescriptor.setJsonName("key1");

		rd.getFields().add(fieldDescriptor);

		fieldDescriptor = new FieldDescriptor();
		fieldDescriptor.setType(FieldDescriptor.TYPE_CHAR);
		fieldDescriptor.setLength(4);
		fieldDescriptor.setJsonName("key2");

		rd.getFields().add(fieldDescriptor);

		fieldDescriptor = new FieldDescriptor();
		fieldDescriptor.setType(FieldDescriptor.TYPE_ZONED);
		fieldDescriptor.setLength(18);
		fieldDescriptor.setDecimals(7);
		fieldDescriptor.setJsonName("key3");

		rd.getFields().add(fieldDescriptor);

		System.out.println(RecordHelper.toJson(rd, value));
	}

	@Test
	public void fullTestFromJson() throws DCMException {

		String stringJson = "{\r\n" + 
				"	\"keys\": [{\r\n" + 
				"			\"key1\": \"GABRIEL\",\r\n" + 
				"			\"key2\": true,\r\n" + 
				"			\"key3\": 2000000000.1234567\r\n" + 
				"		},\r\n" + 
				"		{\r\n" + 
				"			\"key1\": \"CLAUDIO\",\r\n" + 
				"			\"key2\": false,\r\n" + 
				"			\"key3\": 7777700000.1234567\r\n" + 
				"		},\r\n" + 
				"		{\r\n" + 
				"			\"key1\": \"GONZA\",\r\n" + 
				"			\"key2\": true,\r\n" + 
				"			\"key3\": 5054255210.0004567\r\n" + 
				"		}\r\n" + 
				"	]\r\n" + 
				"}";
		
		JsonParser jsonParser = new JsonParser();

		JsonObject jo = jsonParser.parse(stringJson).getAsJsonObject();

		RecordDescriptor rd = new RecordDescriptor();

		rd.setDescription("test");

		rd.setFields(new ArrayList<FieldDescriptor>());
		FieldDescriptor fieldDescriptor = new FieldDescriptor();


		fieldDescriptor.setType(FieldDescriptor.TYPE_CHAR);
		fieldDescriptor.setLength(11);
		fieldDescriptor.setJsonName("keys[0].key1");

		rd.getFields().add(fieldDescriptor);

		fieldDescriptor = new FieldDescriptor();
		fieldDescriptor.setType(FieldDescriptor.TYPE_CHAR);
		fieldDescriptor.setLength(5);
		fieldDescriptor.setJsonName("keys[0].key2");

		rd.getFields().add(fieldDescriptor);

		fieldDescriptor = new FieldDescriptor();
		fieldDescriptor.setType(FieldDescriptor.TYPE_ZONED);
		fieldDescriptor.setLength(18);
		fieldDescriptor.setDecimals(7);
		fieldDescriptor.setJsonName("keys[0].key3");

		rd.getFields().add(fieldDescriptor);

		fieldDescriptor = new FieldDescriptor();
		fieldDescriptor.setType(FieldDescriptor.TYPE_CHAR);
		fieldDescriptor.setLength(11);
		fieldDescriptor.setJsonName("keys[1].key1");

		rd.getFields().add(fieldDescriptor);

		fieldDescriptor = new FieldDescriptor();
		fieldDescriptor.setType(FieldDescriptor.TYPE_CHAR);
		fieldDescriptor.setLength(5);
		fieldDescriptor.setJsonName("keys[1].key2");

		rd.getFields().add(fieldDescriptor);

		fieldDescriptor = new FieldDescriptor();
		fieldDescriptor.setType(FieldDescriptor.TYPE_ZONED);
		fieldDescriptor.setLength(18);
		fieldDescriptor.setDecimals(7);
		fieldDescriptor.setJsonName("keys[1].key3");

		rd.getFields().add(fieldDescriptor);

		System.out.println(RecordHelper.fromJson(rd, jo));
	}
	
	
	@Test
	public void toMapTest() throws DCMException {
		
		String value = "GABRIEL    true 000000002001234567CLAUDIO    false000000000201234567";

		RecordDescriptor rd = new RecordDescriptor();

		rd.setDescription("test");

		rd.setFields(new ArrayList<FieldDescriptor>());
		FieldDescriptor fieldDescriptor = new FieldDescriptor();


		fieldDescriptor.setType(FieldDescriptor.TYPE_CHAR);
		fieldDescriptor.setLength(11);
		fieldDescriptor.setJsonName("keys[0].key1");

		rd.getFields().add(fieldDescriptor);

		fieldDescriptor = new FieldDescriptor();
		fieldDescriptor.setType(FieldDescriptor.TYPE_CHAR);
		fieldDescriptor.setLength(5);
		fieldDescriptor.setJsonName("keys[0].key2");

		rd.getFields().add(fieldDescriptor);

		fieldDescriptor = new FieldDescriptor();
		fieldDescriptor.setType(FieldDescriptor.TYPE_ZONED);
		fieldDescriptor.setLength(18);
		fieldDescriptor.setDecimals(7);
		fieldDescriptor.setJsonName("keys[0].key3");

		rd.getFields().add(fieldDescriptor);

		fieldDescriptor = new FieldDescriptor();
		fieldDescriptor.setType(FieldDescriptor.TYPE_CHAR);
		fieldDescriptor.setLength(11);
		fieldDescriptor.setJsonName("keys[1].key1");

		rd.getFields().add(fieldDescriptor);

		fieldDescriptor = new FieldDescriptor();
		fieldDescriptor.setType(FieldDescriptor.TYPE_CHAR);
		fieldDescriptor.setLength(5);
		fieldDescriptor.setJsonName("keys[1].key2");

		rd.getFields().add(fieldDescriptor);

		fieldDescriptor = new FieldDescriptor();
		fieldDescriptor.setType(FieldDescriptor.TYPE_ZONED);
		fieldDescriptor.setLength(18);
		fieldDescriptor.setDecimals(7);
		fieldDescriptor.setJsonName("keys[1].key3");

		rd.getFields().add(fieldDescriptor);
		
		Map<String, Object>  map = RecordHelper.toMap(rd, value);
		
		System.out.println(map);
		
	}

}
