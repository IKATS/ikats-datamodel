package fr.cs.ikats.temporaldata.resource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import fr.cs.ikats.common.dao.exception.IkatsDaoException;
import fr.cs.ikats.common.dao.exception.IkatsDaoInvalidValueException;
import fr.cs.ikats.common.dao.exception.IkatsDaoMissingRessource;
import fr.cs.ikats.metadata.model.FunctionalIdentifier;
import fr.cs.ikats.temporaldata.business.DataSetInfo;
import fr.cs.ikats.temporaldata.business.DataSetManager;
import fr.cs.ikats.temporaldata.business.DataSetWithFids;
import fr.cs.ikats.temporaldata.exception.ResourceNotFoundException;
import fr.cs.ikats.ts.dataset.model.DataSet;

/**
 * data set resource class.
 */
@Path("dataset")
public class DataSetResource extends AbstractResource {

    private static Logger logger = Logger.getLogger(DataSetResource.class);
    /**
     * DatSet manager class containing business logic
     */
    protected DataSetManager dataSetManager;

    /**
     * default constructor, init the DataSetmanager
     */
    public DataSetResource() {
        dataSetManager = new DataSetManager();
    }

    /**
     * get a summary of all dataset in database. including number of ts per
     * dataset (optional)
     * 
     * @return a json of DataSets (name, description, nb_ts)
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public List<DataSet> getAllDataSetNamesWithNb(@QueryParam("size") @DefaultValue("false") boolean getSize)
            throws IkatsDaoMissingRessource, IkatsDaoException {
        return dataSetManager.getAllDataSetSummary();
    }

    /**
     * import a dataset
     * 
     * @param datasetId
     *            identifier of the dataset
     * @param tsuids
     *            comma separated list of tsuids.
     * @param description
     *            description of the dataset
     * @return "OK" if import is successfull. throw an exception if import
     *         failed.
     * @throws URISyntaxException
     *             en cas d'erreur (mais ne doit jamais arriver...)
     */
    @POST
    @Path("/import/{datasetId}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response importDataSet(@PathParam("datasetId") String datasetId, @FormParam("tsuidList") String tsuids,
            @FormParam("description") String description) throws IkatsDaoException, URISyntaxException {
        logger.info("importing dataset " + datasetId + " with tsuids :" + tsuids);
        List<String> tsuidList = Arrays.asList(tsuids.split(","));
        String result = dataSetManager.persistDataSet(datasetId, description, tsuidList);
        String message = "Import sucessfull : dataset stored with id " + result;
        logger.info(message);

        // FIXME : URI devrait être à new URI("/dataset/" + datasetId) car c'est
        // la loc de la resource crée qu'il faut renvoyer càd l'URI d'accès de
        // la ressource en mode GET.
        return Response.created(new URI("/dataset/import/" + datasetId)).entity(message).build();
    }

    /**
     * 
     * @param datasetId
     *            identifier of the dataset.
     * @param tsuids
     *            comma separated list of tsuids.
     * @param description
     *            description of the dataset.
     * @param updateMode
     *            "replace" for classical update, "append" to add new tsuids to
     *            the dataset in database.
     * 
     * @throws IkatsDaoInvalidValueException
     * @throws IkatsDaoMissingRessource
     * @throws IkatsDaoException
     */
    @PUT
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("{datasetId}")
    public void updateDataSet(@PathParam("datasetId") String datasetId, @FormParam("tsuidList") String tsuids,
            @FormParam("description") String description, @QueryParam("updateMode") @DefaultValue("replace") String updateMode)
            throws IkatsDaoInvalidValueException, IkatsDaoMissingRessource, IkatsDaoException {

        logger.info("importing dataset " + datasetId + " with tsuids :" + tsuids);
        List<String> tsuidList = Arrays.asList(tsuids.split(","));

        dataSetManager.updateDataSet(datasetId, description, tsuidList, updateMode);
        String message = "Update sucessfull";
        logger.info(message);
    }

    /**
     * get the data set with the given datasetId.
     * 
     * @param datasetId
     *            the dataset idenfifier, which is the name of dataset
     * @return the list of {@link DataSetWithFids}.
     * @throws ResourceNotFoundException
     *             if no dataset is found
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("{datasetId}")
    public DataSetWithFids getDataSet(@PathParam("datasetId") String datasetId) throws IkatsDaoMissingRessource, IkatsDaoException {
        logger.info("get infos on dataset " + datasetId);

        DataSetWithFids result = getDataSetWithFids(datasetId);

        return result;
    }

    /**
     * Remove the dataset reference and links with timeseries. 
     * 
     * Option when deep is True: each linked timeseries is deleted -including its metadata-,
     * unless it belongs to another dataset.
     * 
     * @param datasetId
     *            the dataset idenfifier
     * @param deep
     *            boolean flag, optional (default false): true activates the
     *            deletion of timeseries and their associated metadata.
     * @return a summary of the execution.
     * @throws IkatsDaoMissingRessource
     * @throws IkatsDaoException
     */
    @DELETE
    @Path("{datasetId}")
    public Response removeDataSet(@PathParam("datasetId") String datasetId, @DefaultValue("false") @QueryParam("deep") Boolean deep)
            throws IkatsDaoMissingRessource, IkatsDaoException {
        String context = "Removing dataset=" + datasetId + " : ";

        logger.info(context + ": remove the dataset and its links");
        dataSetManager.removeDataSet(datasetId, deep);

        return Response.status(Status.NO_CONTENT).build();
    }

    /**
     * @param datasetId
     * @return
     */
    private DataSetWithFids getDataSetWithFids(String datasetId) throws IkatsDaoMissingRessource, IkatsDaoException {
        DataSet ds = dataSetManager.getDataSet(datasetId);
        List<FunctionalIdentifier> fids = metadataManager.getFunctionalIdentifierByTsuidList(ds.getTsuidsAsString());
        DataSetWithFids result = new DataSetWithFids(ds.getName(), ds.getDescription(), fids);
        return result;
    }

}
