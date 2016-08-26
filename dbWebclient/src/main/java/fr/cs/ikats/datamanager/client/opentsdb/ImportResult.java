package fr.cs.ikats.datamanager.client.opentsdb;

import java.util.HashMap;
import java.util.Map;

/**
 * content result of an opentsdb import operation
 * 
 * @author ikats
 *
 */
public class ImportResult {

    private String summary;
    private String tsuid;
    private String funcId;
    private long numberOfSuccess;
    private long numberOfFailed;
    private long startDate;
    private long endDate;
    private int reponseCode;
    private Map<String, String> errors;
    
    /**
     * default constructor.
     */
    public ImportResult() {
        errors = new HashMap<String, String>();
    }
    
    /**
     * Getter
     * @return the tsuid
     */
    public String getTsuid() {
        return tsuid;
    }

    /**
     * Setter
     * @param tsuid the tsuid to set
     */
    public void setTsuid(String tsuid) {
        this.tsuid = tsuid;
    }

    /**
     * couple of (key,error) to add
     * @param key the error key
     * @param error error
     */
    public void addError(String key, String error) {
        errors.put(key, error);
    }

    /** 
     * add an error to the map
     * @param errors
     *            map of errors to set
     */
    public void addErrors(Map<String, String> errors) {
        errors.putAll(errors);
    }

    /**
     * Getter
     * @return the summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * Getter
     * @return the numberOfSuccess
     */
    public long getNumberOfSuccess() {
        return numberOfSuccess;
    }

    /**
     * Setter
     * @param summary
     *            the summary to set
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * Setter
     * @param numberOfSuccess
     *            numberOfSuccess to set
     */
    public void setNumberOfSuccess(long numberOfSuccess) {
        this.numberOfSuccess = numberOfSuccess;
    }

    /**
     * Getter
     * @return the errors
     */
    public Map<String, String> getErrors() {
        return errors;
    }

    /**
     * Getter
     * @return the timeseries start date
     */
    public long getStartDate() {
        return startDate;
    }

    /**
     * Setter
     * @param startDate
     *            the startDate to set
     */
    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    /**
     * Getter
     * @return the timeseries end date
     */
    public long getEndDate() {
        return endDate;
    }

    /**
     * Setter
     * @param endDate
     *            the end_date to set
     */
    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    /**
     * Getter
     * @return the funcId
     */
    public String getFuncId() {
        return funcId;
    }

    /**
     * Setter
     * @param funcId the funcId to set
     */
    public void setFuncId(String funcId) {
        this.funcId = funcId;
    }

    /**
	 * @return the reponseCode
	 */
	public int getReponseCode() {
		return reponseCode;
	}

	/**
	 * @param reponseCode the reponseCode to set
	 */
	public void setReponseCode(int reponseCode) {
		this.reponseCode = reponseCode;
	}

	/**
     * Return a string representation based on Apache commons ToStringStyle.DEFAULT_STYLE
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append("[tsuid=").append(this.tsuid).append(",");
        sb.append("funcId=").append(this.funcId).append(",");
        sb.append("startDate=").append(this.startDate).append(",");
        sb.append("endDate=").append(this.endDate).append(",");
        sb.append("numberOfSuccess=").append(this.numberOfSuccess).append(",");
        sb.append("summary=").append(this.summary).append(",");
        sb.append("errors=").append(this.errors).append("]");
      
        return sb.toString();
    }

	/**
	 * @return the numberOfFailed
	 */
	public long getNumberOfFailed() {
		return numberOfFailed;
	}

	/**
	 * @param numberOfFailed the numberOfFailed to set
	 */
	public void setNumberOfFailed(long numberOfFailed) {
		this.numberOfFailed = numberOfFailed;
	}
}
