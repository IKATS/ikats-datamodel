package fr.cs.ikats.datamanager.client.opentsdb.importer;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import fr.cs.ikats.datamanager.DataManagerException;
import fr.cs.ikats.datamanager.client.importer.IImportSerializer;
import fr.cs.ikats.datamanager.client.opentsdb.generator.SimpleJsonGenerator;

/**
 *
 */
@Component
@Qualifier("Simple")
public class CSVJsonIzerForOpentsdb implements IImportSerializer {

    BufferedReader reader;
    private String dataset;
    private int period;
    private String tag;
    boolean hasNext = true;

    @Override
    public void init(BufferedReader reader, String fileName, String metric, Map<String, List<String>> tags) {
        this.period = 1;
        this.tag = "numero";
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.cs.ikats.temporaldata.model.importer.IJsonReader#next()
     */
    public synchronized String next(int numberOfPoints) throws IOException, DataManagerException {
        String line = reader.readLine();
        String json = null;
        if (line != null) {
            SimpleJsonGenerator generateur = new SimpleJsonGenerator(dataset, period, tag);
            json = generateur.generate(line);
        }
        else {
            hasNext = false;
        }
        return json;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.cs.ikats.temporaldata.model.importer.IJsonReader#close()
     */
    public void close() {
        if (reader != null) {
            try {
                reader.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean hasNext() {
        return hasNext;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    public CSVJsonIzerForOpentsdb clone() {
        return new CSVJsonIzerForOpentsdb();
    }

    @Override
    public long[] getDates() {
        return null;
    }

    @Override
    public boolean test(String inputline) {
        // TODO Auto-generated method stub
        return true;
    }
}
