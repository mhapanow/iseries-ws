package org.dcm.services.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class WSDescriptor {

	public static final String METHOD_CMD = "CMD";
	
	private String name;
	private String description;
	private String type;
	
	private String url;
	private String method;
	private Map<String, String> headers;
	private String sendTemplate;
	private List<String> recvTemplate;
	private RecordDescriptor inputRecordDescriptor;
	private RecordDescriptor outputRecordDescriptor;
	private Date creationDateTime;
	private Date updateDateTime;

	public WSDescriptor() {
		super();
	}
	
	public WSDescriptor(String name, String description, String type) {
		super();
		this.name = name;
		this.description = description;
		this.type = type;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
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
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}
	
	/**
	 * @param method the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}
	
	/**
	 * @return the headers
	 */
	public Map<String, String> getHeaders() {
		return headers;
	}
	
	/**
	 * @param headers the headers to set
	 */
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	
	/**
	 * @return the sendTemplate
	 */
	public String getSendTemplate() {
		return sendTemplate;
	}
	
	/**
	 * @param sendTemplate the sendTemplate to set
	 */
	public void setSendTemplate(String sendTemplate) {
		this.sendTemplate = sendTemplate;
	}
	
	/**
	 * @return the recvTemplate
	 */
	public List<String> getRecvTemplate() {
		return recvTemplate;
	}

	/**
	 * @param recvTemplate the recvTemplate to set
	 */
	public void setRecvTemplate(List<String> recvTemplate) {
		this.recvTemplate = recvTemplate;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/**
	 * @return the creationDateTime
	 */
	public Date getCreationDateTime() {
		return creationDateTime;
	}

	/**
	 * @param creationDateTime the creationDateTime to set
	 */
	public void setCreationDateTime(Date creationDateTime) {
		this.creationDateTime = creationDateTime;
	}

	/**
	 * @return the updateDateTime
	 */
	public Date getUpdateDateTime() {
		return updateDateTime;
	}

	/**
	 * @param updateDateTime the updateDateTime to set
	 */
	public void setUpdateDateTime(Date updateDateTime) {
		this.updateDateTime = updateDateTime;
	}

	/**
	 * @return the inputRecordDescriptor
	 */
	public RecordDescriptor getInputRecordDescriptor() {
		return inputRecordDescriptor;
	}

	/**
	 * @param inputRecordDescriptor the inputRecordDescriptor to set
	 */
	public void setInputRecordDescriptor(RecordDescriptor inputRecordDescriptor) {
		this.inputRecordDescriptor = inputRecordDescriptor;
	}

	/**
	 * @return the outputRecordDescriptor
	 */
	public RecordDescriptor getOutputRecordDescriptor() {
		return outputRecordDescriptor;
	}

	/**
	 * @param outputRecordDescriptor the outputRecordDescriptor to set
	 */
	public void setOutputRecordDescriptor(RecordDescriptor outputRecordDescriptor) {
		this.outputRecordDescriptor = outputRecordDescriptor;
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
		WSDescriptor other = (WSDescriptor) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "WSDescription [name=" + name + ", description=" + description + ", type=" + type + ", url=" + url
				+ ", method=" + method + "]";
	}

}
