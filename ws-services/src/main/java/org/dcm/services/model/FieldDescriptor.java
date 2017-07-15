package org.dcm.services.model;

import java.io.Serializable;

public class FieldDescriptor implements Serializable, IArray {

	private static final long serialVersionUID = 1L;

	public static final String TYPE_CHAR = "CHAR";
	public static final String TYPE_ARRAY = "ARRAY";
	public static final String TYPE_ZONED = "ZONED";
	
	private String iSeriesName;
	private String iSeriesDescription;
	private String jsonName;
	private String type;
	private IArray arrayOf;
	private int length;
	private int decimals;

	public FieldDescriptor() {
		super();
	}
	
	public FieldDescriptor(String iSeriesName, String iSeriesDescription, String jsonName, String type, int length, int decimals) {
		super();
		this.iSeriesName = iSeriesName;
		this.iSeriesDescription = iSeriesDescription;
		this.jsonName = jsonName;
		this.type = type;
		this.length = length;
		this.decimals = decimals;
	}

	/**
	 * @return the iSeriesName
	 */
	public String getiSeriesName() {
		return iSeriesName;
	}

	/**
	 * @param iSeriesName the iSeriesName to set
	 */
	public void setiSeriesName(String iSeriesName) {
		this.iSeriesName = iSeriesName;
	}

	/**
	 * @return the iSeriesDescription
	 */
	public String getiSeriesDescription() {
		return iSeriesDescription;
	}

	/**
	 * @param iSeriesDescription the iSeriesDescription to set
	 */
	public void setiSeriesDescription(String iSeriesDescription) {
		this.iSeriesDescription = iSeriesDescription;
	}

	/**
	 * @return the jsonName
	 */
	public String getJsonName() {
		return jsonName;
	}

	/**
	 * @param jsonName the jsonName to set
	 */
	public void setJsonName(String jsonName) {
		this.jsonName = jsonName;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * @return the decimals
	 */
	public int getDecimals() {
		return decimals;
	}

	/**
	 * @param decimals the decimals to set
	 */
	public void setDecimals(int decimals) {
		this.decimals = decimals;
	}

	/**
	 * @return the arrayOf
	 */
	public IArray getArrayOf() {
		return arrayOf;
	}

	/**
	 * @param arrayOf the arrayOf to set
	 */
	public void setArrayOf(IArray arrayOf) {
		this.arrayOf = arrayOf;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((iSeriesName == null) ? 0 : iSeriesName.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FieldDescriptor other = (FieldDescriptor) obj;
		if (iSeriesName == null) {
			if (other.iSeriesName != null)
				return false;
		} else if (!iSeriesName.equals(other.iSeriesName))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FieldDescriptor [iSeriesName=" + iSeriesName + ", iSeriesDescription=" + iSeriesDescription
				+ ", jsonName=" + jsonName + ", type=" + type + ", arrayOf=" + arrayOf + ", length=" + length
				+ ", decimals=" + decimals + "]";
	}

}
