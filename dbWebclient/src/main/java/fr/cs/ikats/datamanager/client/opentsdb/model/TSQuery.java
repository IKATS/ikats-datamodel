package fr.cs.ikats.datamanager.client.opentsdb.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class TSQuery {

    String start;
    String end;
    List<AbstractFilter> queries;

    /**
     * create a TSQuery without queries.
     * Must add queries after this.
     * @param start The start time for the query.
     * @param end The end time for the query.
     */
    public TSQuery(String start, String end) {
        super();
        this.start = start;
        this.end = end;
        this.queries = new ArrayList<AbstractFilter>();
    }
    
    /**
     * Generate a mono TSUID request with no filter.
     * 
     * @param start The start time for the query.
     * @param end The end time for the query.
     */
    public TSQuery(String start, String end,String tsuid) {
        super();
        this.start = start;
        this.end = end;
        this.queries = new ArrayList<AbstractFilter>();
        List<String> tsuids = new ArrayList<String>(); 
        tsuids.add(tsuid);
        addTSQuery("sum",tsuids);
        
    }

    /**
     * Getter
     * @return the start
     */
    public String getStart() {
        return start;
    }

    /**
     * Getter
     * @return the end
     */
    public String getEnd() {
        return end;
    }

    /**
     * Getter
     * @return the queries
     */
    public List<AbstractFilter> getQueries() {
        return queries;
    }

    /**
     * Setter
     * @param query the queries to set
     */
    public void addQuery(AbstractFilter query) {
        this.queries.add(query);
    }
    
    /**
     * Setter
     * @param aggregator 
     * @param tsuids 
     * @param query the queries to set
     */
    public void addTSQuery(String aggregator, List<String> tsuids) {
        this.queries.add(new TSUIDFilter(aggregator, tsuids));
    }
    
    /**
     * Setter
     * @param aggregator 
     * @param metric 
     * @param rate 
     * @param tags 
     * @param query the queries to set
     */
    public void addTSQuery(String aggregator, String metric, boolean rate,Map<String,String> tags) {
        this.queries.add(new METRICFilter(aggregator, metric,rate,tags));
    }    
    
}


