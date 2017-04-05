package fr.cs.ikats.ts.dataset.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.cs.ikats.metadata.model.FunctionalIdentifier;

/**
 * Association class mapping DB table timeseries_dataset linking tables tsdataset and tsfunctionalidentifier.
 * <BR/>
 * Versionning follow-up:
 * <ul><li> 
 *     V4: class renamed LinkDatasetTimeSeries (old name: TimeSerie)
 * </li>
 * <li>V3
 * <ul>
 * <li>Hibernate update: this.funcIdentifier + this.datasetName</li>
 * <li>Hibernate update: removed cascade ALL defined on dataset</li>
 * <li>Add new constraint in DB: see details in Forge FT [#144551]: point 3</li>
 * </ul>
 *    
 * </li>
 * <li>V2
 *    Add the bidirectional relation with DataSet :
 *      
 *    Script to migrate from V1 : 
 *    insert into timeserie select id,tsuid,ds_name as name from timeserieindataset,timeserie where ts_id=id
 *    delete * from timeserieindataset
 *    drop table timeserieindataset
 * </li>
 * </ul>
 */
@Entity
@Table(name="TimeSeries_Dataset")
public class LinkDatasetTimeSeries {

    /**
     * Request to select datasetname containing a tsuid
     */
    public transient final static String LIST_DATASET_NAMES_FOR_TSUID = "select distinct T.dataset.name from LinkDatasetTimeSeries T where T.funcIdentifier.tsuid = :tsuid";
    
    /**
     * Request to delete a TS from dataset
     */
    public transient final static String DELETE_TS_FROM_DATASET = "delete LinkDatasetTimeSeries T where T.funcIdentifier = :tsuid and T.dataset.name = :dataset";
    
    /**
     * HQL request: remove all the TS links under a dataset. TS are not deleted, only the links dataset->TS.
     */
    public transient final static String DELETE_ALL_TS_LINKS_FROM_DATASET = "delete LinkDatasetTimeSeries T where T.dataset.name = :dataset";
    
    
    /**
     * default constructor
     */
    public LinkDatasetTimeSeries() {
    }
    
    /**
     * constructor with simple fields
     * @param tsuid : the tsuid
     */
    public LinkDatasetTimeSeries(String tsuid, String datasetname) {
        setFunctionalIdentifier(new FunctionalIdentifier( tsuid, null ) );// setTsuid(tsuid);
        setDataset( new DataSet( datasetname, null, null ) );// setDatasetName( datasetname );
    }
    
    /**
     * constructor with linked entities FunctionalIdentifier and DataSet
     * @param funcIdInfo
     * @param dataSet
     */
    public LinkDatasetTimeSeries(FunctionalIdentifier funcIdInfo, DataSet dataSet) {
        setFunctionalIdentifier( funcIdInfo ); // updates also this.tsuid !
        setDataset( dataSet ); // updates also this.datasetName !
    }
    
    @Id
    @SequenceGenerator(name="timeserie_id_seq", sequenceName="timeserie_id_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="timeserie_id_seq")
    @Column(name = "id", updatable = false)
    private Long id;
         
    @JsonIgnore
    @ManyToOne 
    @JoinColumn(name="tsuid")
    private FunctionalIdentifier funcIdentifier;
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="dataset_name")
    private DataSet dataset;
    
    /**
     * Getter
     * @return the id
     */
    @Transient
    public Long getId() { 
        return id; 
    }
    
    /**
     * Setter
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id; 
    }

    /**
     * Getter
     * @return the tsuid
     */
    public String getTsuid() {
        if ( funcIdentifier != null )
        {
            return funcIdentifier.getTsuid();
        }
        else return "";
//        else
//        {
//            return tsuid;
//        } 
    }

    /**
     * Setter
     * @param tsuid the tsuid to set
     */
    public void setTsuid(String tsuid) {
        
        if ( getFuncIdentifier() == null )
        {
            setFunctionalIdentifier(new FunctionalIdentifier() );
        }
        getFuncIdentifier().setTsuid(tsuid);
        // this.tsuid = tsuid;
    }

    /**
     * Getter
     * @return the dataset
     */
    public DataSet getDataset() {
        return dataset;
    }

    /**
     * Setter
     * 
     * @param dataset
     *            the dataset to set
     */
    public void setDataset(DataSet dataset) {
        this.dataset = dataset;
//        if (this.dataset != null) {
//            this.datasetName = this.dataset.getName();
//        }
    }
    
    /**
     * Getter
     * @return this.datasetName
     */
    public String getDatasetName()
    {
        if ( dataset != null )
        {
            return dataset.getName();
        } else return "";
//        else
//        {
//            return this.datasetName;
//        } 
    }
    
    /**
     * @param datasetname
     */
    public void setDatasetName(String datasetname) {
        
        if ( getDataset() == null )
        {
            setDataset( new DataSet( datasetname, null, null));
        }
        getDataset().setName( datasetname );
        
        
        // this.datasetName = datasetname;
    }

    /**
     * Getter 
     * @return this.funcIdentifier
     */
    public FunctionalIdentifier getFuncIdentifier()
    {
        return this.funcIdentifier;
    }
    
    /**
     * Setter
     * @param funcIdentifier the funcIdentifier to set
     */
    public void setFunctionalIdentifier(FunctionalIdentifier funcIdentifier) {
        this.funcIdentifier= funcIdentifier;
//        if ( this.funcIdentifier != null)
//        {
//            this.tsuid = this.funcIdentifier.getTsuid();
//        }
    }
    
    /**
     * TimeSeries equality: needed by computings with Collection API
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if ( obj instanceof LinkDatasetTimeSeries )
        {
            LinkDatasetTimeSeries tsObj = (LinkDatasetTimeSeries) obj;
            String tsuid = this.getTsuid(); 
            String datasetName = this.getDatasetName();
            return tsuid.equals( tsObj.getTsuid()) && datasetName.equals( tsObj.getDatasetName() );
        }
        else
        {
            return false;
        }
    }
}