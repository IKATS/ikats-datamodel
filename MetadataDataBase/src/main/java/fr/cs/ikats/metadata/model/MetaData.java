package fr.cs.ikats.metadata.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * model class for MetaData.
 * 
 */
@Entity
@Table(name = "TSMetadata", uniqueConstraints = @UniqueConstraint(columnNames = { "tsuid", "name" }) )
public class MetaData {

    /**
     * Enumerate of possible types for a metadata (even if database encoding is fixed)
     */
    public enum MetaType {
        /**
         * Type of a Metadata coding for a string
         */
        string, 
        /**
         * Type of a Metadata value coding for a date
         */
        date, 
        /**
         * Type for Metadata value coding for a number
         */
        number, 
        /**
         * Type for Metadata value coding for a complex structure (json ? ...)
         */
        complex;
    }
    
	/**
	 * HQL request for all tsuids
	 */
	public final static String LIST_ALL_FOR_TSUID = "select md from MetaData md where md.tsuid = :tsuid";

	/**
	 * HQL request for a meta data entry
	 */
	public final static String GET_MD = "select md from MetaData md where md.tsuid = :tsuid and  md.name = :name";

	/**
	 * default constructor
	 */
	public MetaData() {

	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "tsuid")
	private String tsuid;

	@Column(name = "name")
	private String name;

	@Column(name = "value")
	private String value;

	@Column(name = "dtype")
	@Enumerated(EnumType.STRING)
	private MetaType dtype;

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the tsuid
	 */
	public String getTsuid() {
		return tsuid;
	}

	/**
	 * @param tsuid
	 *            the tsuid to set
	 */
	public void setTsuid(String tsuid) {
		this.tsuid = tsuid;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * :tsuid
	 * 
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public String toString() {
		StringBuilder buff = new StringBuilder("MetaData");
		String name = getName();
		String tsuid = getTsuid();
		MetaType dtype = getDType();
		String value = getValue();
		Integer id = getId();
		if (name != null) {
			buff.append("with name=[");
			buff.append(name);
			buff.append("] ");
		}
		if (tsuid != null) {
			buff.append("for tsuid=[");
			buff.append(tsuid);
			buff.append("] ");
		}
		if (id != null) {
			buff.append("with id=[");
			buff.append(id);
			buff.append("] ");
		}
		if (value != null) {
			buff.append("with value=[");
			buff.append(value);
			buff.append("] ");
		}
		if (dtype != null) {
			buff.append("with dtype=[");
			buff.append(dtype);
			buff.append("] ");
		}
		return buff.toString();
	}

	/**
	 * @return the datatype
	 */
	public MetaType getDType() {
		return dtype;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setDType(MetaType value) {
		this.dtype = value;
	}

}
