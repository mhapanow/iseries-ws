package org.dcm.services.model;

public class Record {

	private RecordDescriptor format;
	private String stringValue;
	private String jsonValue;
	
	public Record(RecordDescriptor format) {
		super();
		this.format = format;
	}

	/**
	 * @return the format
	 */
	public RecordDescriptor getFormat() {
		return format;
	}

	/**
	 * @param format the format to set
	 */
	public void setFormat(RecordDescriptor format) {
		this.format = format;
	}

	/**
	 * @return the stringValue
	 */
	public String getStringValue() {
		return stringValue;
	}

	/**
	 * @param stringValue the stringValue to set
	 */
	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	/**
	 * @return the jsonValue
	 */
	public String getJsonValue() {
		return jsonValue;
	}

	/**
	 * @param jsonValue the jsonValue to set
	 */
	public void setJsonValue(String jsonValue) {
		this.jsonValue = jsonValue;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Record [format=" + format + ", stringValue=" + stringValue + ", jsonValue=" + jsonValue + "]";
	}
	
}
