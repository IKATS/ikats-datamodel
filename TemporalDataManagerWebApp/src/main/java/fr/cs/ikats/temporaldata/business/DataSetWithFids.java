/**
 * $Id$
 *
 * HISTORIQUE
 *
 * VERSION : 1.0 : Fournir le Functional Identifier avec le dataset : 140850 : Feb 11, 2016 : Creation 
 *
 * FIN-HISTORIQUE
 */
package fr.cs.ikats.temporaldata.business;

import java.util.ArrayList;
import java.util.List;

import fr.cs.ikats.metadata.model.FunctionalIdentifier;

/**
 * Provides a dataset combining
 */
public class DataSetWithFids {

    /**
     * name of the dataset
     */
    private String name;

    /**
     * a short description of the dataset
     */
    private String description;

    /**
     * list of time series identifiers
     */
    private List<FunctionalIdentifier> fids;

    /**
     * public constructor
     * 
     * @param name
     *            name of the dataset
     * @param description
     *            a short description of the dataset
     * @param functionalId
     *            list of time series identifiers
     */
    public DataSetWithFids(String name, String description, List<FunctionalIdentifier> fids) {
        this.name = name;
        this.description = description;
        this.fids = fids;
    }

    /**
     * Default constructor
     */
    public DataSetWithFids() {
        super();
    }

    /**
     * Getter
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Getter
     * 
     * @return the {@link FunctionalIdentifier} list
     */
    public List<FunctionalIdentifier> getFids() {
        return fids;
    }

    /**
     * return a List of string with tsuids
     * @return a list
     */
    public List<String> getTsuidsAsString() {
        List<String> stringList = new ArrayList<String>();
        if (fids != null) {
            for (FunctionalIdentifier fid : fids) {
                stringList.add(fid.getTsuid());
            }
        }
        return stringList;
    }
    
    /**
     * Return the {@link FunctionalIdentifier} retrieved from a tsuid
     * @return a string for the fid
     */
    public String getFid(String tsuid) {
        for (FunctionalIdentifier functionalIdentifier : fids) {
            if (functionalIdentifier.getTsuid().equals(tsuid)) {
                return functionalIdentifier.getFuncId();
            }
        }
        
        return null;
    }    

	/**
	 * @return String representation: short version without tsuids
	 */
	public String toString() {
		return "DatasetWithFids name=[" + name + "] description=[" + description + "]";
	}

	/**
	 * 
	 * @return String representation: detailed version with tsuids listed
	 */
	public String toDetailedString() {
		StringBuilder lBuff = new StringBuilder(toString());
		lBuff.append(" timeseries=[ ");
	
		if (fids != null) {
			boolean lStart = true;
	            for (FunctionalIdentifier fid : fids) {
	            	if ( ! lStart )
	            	{
	            		lBuff.append( fid.getTsuid() );
	            		lBuff.append( " with funcId=" );
	            		lBuff.append( fid.getFuncId() );
	            	} 
	            }
	        }
		lBuff.append("]");
		return lBuff.toString();
	}
    
}
