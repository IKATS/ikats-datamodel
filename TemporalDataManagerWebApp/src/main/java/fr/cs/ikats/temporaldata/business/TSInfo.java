/**
 * 
 */
package fr.cs.ikats.temporaldata.business;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ikats
 * Container of a pair tsuid/metric used for listing the tsuids upon requests
 */
public class TSInfo {

    String tsuid;
    
    String funcId;
    
    String metric;
    
    Map<String,String> tags;
    

    /**
     * Getter
     * @return the tags
     */
    public Map<String, String> getTags() {
        return tags;
    }

    /**
     * @return the metric
     */
    public String getMetric() {
        return metric;
    }

    /**
     * @param metric the metric to set
     */
    public void setMetric(String metric) {
        this.metric = metric;
    }

    /**
     * @return the tsuid
     */
    public String getTsuid() {
        return tsuid;
    }

    /**
     * @param funcId the funcId to set
     */
    public void setFuncId(String funcId) {
        this.funcId = funcId;
    }
    
    /**
     * @return the funcId
     */
    public String getfuncId() {
        return funcId;
    }

    /**
     * @param tsuid the tsuid to set
     */
    public void setTsuid(String tsuid) {
        this.tsuid = tsuid;
    }

    /**
     * constructor
     * @param metric the metric for the TS
     * @param tsuid the TSUID
     * @param funcId the functional identifier
     * @param tags the tags
     */
    public TSInfo(String metric,String tsuid, String funcId,Map<String,String> tags) {
        super();
        this.metric = metric;
        this.tsuid = tsuid;
        this.tags = tags;
        this.funcId=funcId;
    }
    
    /**
     * default constructor
     */
    public TSInfo() {
        super();
        this.tags = new HashMap<String,String>();
    }

    /**
     * add a tag of name name and value value;
     * @param name name of the tag
     * @param value value of the tag
     */
    public void addTag(String name, String value) {
        this.tags.put(name, value);
        
    }
    
    
}
