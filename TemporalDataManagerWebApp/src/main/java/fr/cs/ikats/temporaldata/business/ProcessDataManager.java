package fr.cs.ikats.temporaldata.business;

import java.io.InputStream;
import java.util.List;

import fr.cs.ikats.process.data.ProcessDataFacade;
import fr.cs.ikats.process.data.model.ProcessData;
import fr.cs.ikats.temporaldata.application.TemporalDataApplication;

/**
 * Manager for ProcessData data
 */
public class ProcessDataManager {
    
    /**
     * private method to get the MetaDataFacade from Spring context.
     * 
     * @return
     */
    private ProcessDataFacade getProcessDataFacade() {
        return TemporalDataApplication.getApplicationConfiguration().getSpringContext().getBean(ProcessDataFacade.class);
    }
    
    
    /**
     * import an inputStream into database 
     * @param fileis the inputStream
     * @param fileLength size of data
     * @param name name of data
     * @param processId the data producer identifier
     * @param dataType the dataType
     * @return the internal identifier of the result.
     */
    public String importProcessData(InputStream fileis, Long fileLength,String processId, String dataType,String name) {
        ProcessData data = new ProcessData(processId,dataType,name);
        return getProcessDataFacade().importProcessData(data,fileis,fileLength.intValue());
    }

    /**
     * ResultType ENUM
     */
    public enum ProcessResultTypeEnum {
        /**
         * Value for JSON type
         */
        JSON,
        /**
         * Value for matrix type
         */
        MATRIX,
        /**
         * Value for CSV file type
         */
        CSV,
        /**
         * Value for Other type
         */
        OTHER;
    }
    
    
    /**
     * get a single processResult for internal identifier id.
     * @param id the internal process identifier
     * @return null if nothing is found.
     */
    public ProcessData getProcessPieceOfData(int id) {
        return getProcessDataFacade().getProcessPieceOfData(id);
    }
    
    /**
     * get All processResults for a processId.
     * @param processId the process execution identifier.
     * @return null if not processResult is found.
     */
    public List<ProcessData> getProcessData(String processId) {
        return getProcessDataFacade().getProcessData(processId);
    }

    /**
     * remove all processResults for a processId.
     * @param processId the process exec identifier.
     */
    public void removeProcessData(String processId) {
        getProcessDataFacade().removeProcessData(processId);       
    }
    
}
