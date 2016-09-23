package fr.cs.ikats.temporaldata.resource;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import fr.cs.ikats.datamanager.client.opentsdb.IkatsWebClientException;
import org.apache.log4j.Logger;

import fr.cs.ikats.common.dao.exception.IkatsDaoException;
import fr.cs.ikats.workflow.Workflow;
import fr.cs.ikats.workflow.WorkflowFacade;

/**
 * This class hosts all the operations on Workflow
 */
@Path("wf")
public class WorkflowResource extends AbstractResource {

    private static Logger logger = Logger.getLogger(MetaDataResource.class);
    private WorkflowFacade Facade = new WorkflowFacade();

    /**
     * Default constructor
     */
    public WorkflowResource() {
        super();
    }

    /**
     * create a new Workflow
     *
     * @param wf      Workflow to provide
     * @param uriInfo the uri info
     * @return the id of the created workflow
     * @throws IkatsDaoException         if any DAO exception occurs
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
            Workflow wf,
            @Context UriInfo uriInfo
    ) throws IkatsDaoException {

        Integer wfId = Facade.persist(wf);

        // Return the location header
        UriBuilder uri = uriInfo.getAbsolutePathBuilder();
        uri.path(wfId.toString());

        return Response.created(uri.build()).build();
    }

    /**
     * Get the list of all workflow summary (raw content is not provided unless full is set to true)
     *
     * @param full to indicate if the raw content shall be included in response
     * @return the workflow
     * @throws IkatsDaoException if any DAO exception occurs
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAll(
            @QueryParam("full") @DefaultValue("false") Boolean full
    ) throws IkatsDaoException {

        List<Workflow> result = Facade.listAll();
        if (!full) {
            // Remove Raw content from data to reduce the payload
            result.forEach(wf -> wf.setRaw(null));
        }

        Response.StatusType resultStatus = Response.Status.OK;
        if (result.size() == 0){
            resultStatus = Response.Status.NO_CONTENT;
        }

        return Response.status(resultStatus).entity(result).build();

    }

    /**
     * Get the content of a workflow by providing its id
     *
     * @param id id of the workflow to read
     * @return the workflow
     * @throws IkatsDaoException if any DAO exception occurs
     */
    @GET
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWorkflow(
            @PathParam("id") Integer id
    ) throws IkatsDaoException {

        Workflow wf = Facade.getById(id);

        return Response.status(Response.Status.OK).entity(wf).build();
    }

    /**
     * Update all workflow at once
     *
     * @return HTTP response
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateWorkflow() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    /**
     * Update a workflow identified by its Id
     *
     * @param wf      New content for the workflow
     * @param uriInfo the uri info
     * @param id      id of the workflow to update
     * @return HTTP response
     * @throws IkatsDaoException if any DAO exception occurs
     */
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateWorkflow(
            Workflow wf,
            @Context UriInfo uriInfo,
            @PathParam("id") int id
    ) throws IkatsDaoException, IkatsWebClientException {

        if (wf.getId() != null && id != wf.getId()){
            throw new IkatsWebClientException("Mismatch in request with Id between URI and body part");
        }
        wf.setId(id);

        boolean result = Facade.update(wf);

        if (result) {
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete a workflow identified by its id
     *
     * @param id id of the workflow to delete
     * @return HTTP response
     * @throws IkatsDaoException if any DAO exception occurs
     */
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeWorkflow(
            @PathParam("id") Integer id
    ) throws IkatsDaoException {

        Facade.removeById(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    /**
     * Delete all workflow
     *
     * @return HTTP response
     * @throws IkatsDaoException if any DAO exception occurs
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeAll() throws IkatsDaoException {

        Facade.removeAll();

        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
