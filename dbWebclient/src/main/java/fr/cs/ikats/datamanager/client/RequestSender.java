package fr.cs.ikats.datamanager.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import fr.cs.ikats.datamanager.client.opentsdb.IkatsWebClientException;

/**
 * Utility class to send request
 */
public class RequestSender {
    private static final Logger LOGGER = Logger.getLogger(RequestSender.class);

    /**
     * send GET request and return JSON format response
     * 
     * @param url
     *            the url to send
     * @param host
     *            the host
     * @return the response
     * @throws IkatsWebClientException
     *             if request has error
     */
    public static Response sendGETRequest(String url, String host) throws IkatsWebClientException {
        LOGGER.debug("Sending GET request to url : " + url);
        ClientConfig clientConfig = new ClientConfig();
        Client client = ClientBuilder.newClient(clientConfig);
        Response response = null;
        try {
            WebTarget target = client.target(url);
            response = target.request().get();
        }
        catch (Exception e) {
            LOGGER.error("", e);
        }
        return response;
    }

    /**
     * send POST request and return JSON format response
     * 
     * @param url
     *            the url to send
     * @return the response
     * @throws IkatsWebClientException
     *             if request has error
     */
    public static Response sendPOSTRequest(String url, Entity<?> entity) throws IkatsWebClientException {
        LOGGER.debug("Sending POST request to url : " + url);
        ClientConfig clientConfig = new ClientConfig();
        Client client = ClientBuilder.newClient(clientConfig);
        Response response = null;
        try {
            WebTarget target = client.target(url);
            response = target.request().post(entity);
        }
        catch (Exception e) {
            LOGGER.error("", e);
        }
        return response;
    }

    /**
     * send PUT request and return JSON format response
     * 
     * @param url
     *            the url to send
     * @return the response
     * @throws IkatsWebClientException
     *             if request has error
     */
    public static Response sendPUTRequest(String url, Entity<?> entity) throws IkatsWebClientException {
        LOGGER.debug("Sending PUT request to url : " + url);
        ClientConfig clientConfig = new ClientConfig();
        Client client = ClientBuilder.newClient(clientConfig);
        Response response = null;
        try {
            WebTarget target = client.target(url);
            response = target.request().put(entity);
        }
        catch (Exception e) {
            LOGGER.error("", e);
        }
        return response;
    }

    /**
     * send DELETE request and return JSON format response
     * 
     * @param url
     *            the url to send
     * @return the response
     * @throws IkatsWebClientException
     *             if request has error
     */
    public static Response sendDELETERequest(String url) throws IkatsWebClientException {
        LOGGER.debug("Sending DELETE request to url : " + url);
        ClientConfig clientConfig = new ClientConfig();
        Client client = ClientBuilder.newClient(clientConfig);
        Response response = null;
        try {
            WebTarget target = client.target(url);
            response = target.request().delete();
        }
        catch (Exception e) {
            LOGGER.error("", e);
        }
        return response;
    }

    /**
     * 
     * send PUT request and return response
     * 
     * @param url
     *            the url to send
     * @param body
     *            the body
     * @return the response
     * @throws IkatsWebClientException
     *             if request has error
     */
    public static Response sendPUTJsonRequest(String url, String body) {

        LOGGER.debug("Sending PUT request to url : " + url);
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(MultiPartFeature.class);
        Client client = ClientBuilder.newClient(clientConfig);

        Response response = null;
        try {
            WebTarget target = client.target(url);
            response = target.request().post(Entity.entity(body, MediaType.APPLICATION_JSON));
        }
        finally {
        	// FIXME : commented since SVN rev1271 after Jersey update. why ?
            // client.close();
        }
        return response;
    }
}
