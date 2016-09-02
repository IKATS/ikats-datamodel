package fr.cs.ikats.datamanager.client.opentsdb.importer;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

import org.apache.log4j.Logger;

import fr.cs.ikats.datamanager.DataManagerException;
import fr.cs.ikats.datamanager.client.importer.IImportSerializer;
import fr.cs.ikats.datamanager.client.opentsdb.generator.AdvancedJsonGenerator;
import fr.cs.ikats.datamanager.client.opentsdb.generator.SplittedLineReader;

/**
 * Serializer in json format. Abstract class
 * 
 * @author ikats
 *
 */
public abstract class AbstractDataJsonIzer implements IImportSerializer {

    /**
     * static logger reference
     */
    private static final Logger LOGGER = Logger.getLogger(AbstractDataJsonIzer.class);

    /**
     * Input stream reader.
     */
    BufferedReader reader;
    /**
     * indicated if input data is still in the pipe.
     */
    boolean hasNext = true;
    /**
     * the start date of the timeseries
     */
    long startDate = 0L;
    /**
     * the end date of the timeseries
     */
    long endDate = 0L;

    /**
     * the generateur for this file
     * 
     */
    private AdvancedJsonGenerator generateur;

    @Override
    public long[] getDates() {
        return new long[]{startDate,endDate};
    }
    /**
     * init the serializer. Consume the first line of the input.
     * 
     * {@inheritDoc}
     */
    @Override
    public void init(BufferedReader reader, String fileName, String metric, Map<String, String> tags) {
        this.reader = reader;
        this.generateur = new AdvancedJsonGenerator(getReader(), metric, tags);
        try {
            String line = reader.readLine();
            LOGGER.debug("first line read "+line);
        }
        catch (IOException e) {
            LOGGER.error("unable to read input ", e);
        }
    }

    @Override
    public boolean test(String inputline) {
        try {
            // get the json representation for the line
            AdvancedJsonGenerator testGenerateur = new AdvancedJsonGenerator(getReader(),null,null);
            String line = testGenerateur.generate(inputline);
            if(line!=null) {
                return true;
            } else {
                return false;
            }
        }
        catch (Exception e) {
            LOGGER.info(e.getMessage());
            //LOGGER.error("",e);
            return false;
        }
    }
    
    /**
     * consume the next data to create
     * @throws IOException 
     * @throws DataManagerException 
     * 
     * @see fr.cs.ikats.datamanager.client.importer.IImportSerializer#next(int)
     */
    public synchronized String next(int numberOfPointsByImport) throws IOException, DataManagerException {
        String json = null;
        StringBuilder builder = new StringBuilder();
        
        String readLine = reader.readLine();
        int pointRead = 1;
        for (; pointRead < numberOfPointsByImport && readLine != null; pointRead++) {
            builder.append(readLine).append(";");
            readLine = reader.readLine();
		}
        
        if(readLine!=null) {
            builder.append(readLine);
        }
        
        String line = builder.toString();
		if (line != null && !line.isEmpty()) {
		    // get the json representation for the line
			try {
				json = generateur.generate(line);
			} catch (ParseException pe) {
				hasNext = false;
				throw new DataManagerException(pe.getMessage(), pe);
			}
		    // set startDate and endDate from the generator.
		    if (this.startDate == 0L){
		        this.startDate = generateur.getLowestTimeStampValue();
		    }
		    else if (generateur.getLowestTimeStampValue() < this.startDate) {
		        this.startDate = generateur.getLowestTimeStampValue();
		    }
		    
		    if (generateur.getHighestTimeStampValue() > this.endDate) {
		        this.endDate = generateur.getHighestTimeStampValue();
		    }
		}
		else {
		    hasNext = false;
		}
		
		if (pointRead < numberOfPointsByImport) {
			// the last read, reads less points than expected = no more points 
			hasNext = false;
		}
		
		if (json == null || json.isEmpty()) {
			hasNext = false;
		}
    
        return json;
    }

    /**
     * close the input stream.
     * 
     * 
     * @see fr.cs.ikats.datamanager.client.importer.IImportSerializer#close()
     */
    public void close() {
        if (reader != null) {
            try {
                reader.close();
            }
            catch (IOException e) {
                // NOTING CAN BE DONE
            }
        }
    }

    /**
     * getter {@inheritDoc}
     */
    public synchronized boolean hasNext() {
        return hasNext;
    }

    /**
     * get a cloned instance of this Serializer {@inheritDoc}
     */
    @Override
    public abstract IImportSerializer clone();

    /**
     * get the LineReader
     * 
     * @return the reader
     */
    public abstract SplittedLineReader getReader();

}
