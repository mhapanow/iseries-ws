package org.dcm.services.model;

import java.io.Serializable;
import java.util.List;

import com.inodes.util.CollectionFactory;

public class RecordDescriptor implements Serializable, IArray {

	private static final long serialVersionUID = 1L;

	private String name;
	private String description;
	private List<FieldDescriptor> fields;
	
	public RecordDescriptor() {
		super();
		fields = CollectionFactory.createList();
	}
	
	public RecordDescriptor(String name, String description) {
		this();
		this.name = name;
		this.description = description;
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
	 * @return the fields
	 */
	public List<FieldDescriptor> getFields() {
		return fields;
	}

	/**
	 * @param fields the fields to set
	 */
	public void setFields(List<FieldDescriptor> fields) {
		this.fields = fields;
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
		RecordDescriptor other = (RecordDescriptor) obj;
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
		return "RecordDescriptor [name=" + name + ", description=" + description + ", fields=" + fields + "]";
	}
	
}
