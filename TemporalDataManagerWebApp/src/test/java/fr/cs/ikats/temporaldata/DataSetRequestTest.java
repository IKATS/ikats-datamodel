/**
 * LICENSE:
 * --------
 * Copyright 2017 CS SYSTEMES D'INFORMATION
 *
 * Licensed to CS SYSTEMES D'INFORMATION under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. CS licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * @author Fabien TORAL <fabien.toral@c-s.fr>
 * @author Fabien TORTORA <fabien.tortora@c-s.fr>
 * @author Mathieu BERAUD <mathieu.beraud@c-s.fr>
 * @author Pierre BONHOURE <pierre.bonhoure@c-s.fr>
 */

package fr.cs.ikats.temporaldata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import fr.cs.ikats.common.dao.exception.IkatsDaoException;
import fr.cs.ikats.common.dao.exception.IkatsDaoMissingResource;
import fr.cs.ikats.datamanager.client.opentsdb.IkatsWebClientException;
import fr.cs.ikats.metadata.model.FunctionalIdentifier;
import fr.cs.ikats.temporaldata.business.DataSetManager;
import fr.cs.ikats.temporaldata.business.DataSetWithFids;
import fr.cs.ikats.temporaldata.business.MetaDataManager;
import fr.cs.ikats.temporaldata.exception.ResourceNotFoundException;
import fr.cs.ikats.temporaldata.resource.DataSetResource;
import fr.cs.ikats.temporaldata.resource.TimeSerieResource;
import fr.cs.ikats.ts.dataset.DataSetFacade;
import fr.cs.ikats.ts.dataset.model.DataSet;
import fr.cs.ikats.ts.dataset.model.LinkDatasetTimeSeries;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test on webService dataset operations. This class is using standard services from superclass AbstractRequestTest, and
 * super-superclass CommonTest.
 */
@RunWith(MockitoJUnitRunner.class)
public class DataSetRequestTest extends AbstractRequestTest {

    @BeforeClass
    public static void setUpBeforClass() {
        AbstractRequestTest.setUpBeforClass(DataSetRequestTest.class.getSimpleName());
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        AbstractRequestTest.tearDownAfterClass(DataSetRequestTest.class.getSimpleName());
    }

    @Test
    public void testImportDataSet() throws IkatsWebClientException {
        String testCaseName = "testImportDataSet";

        String dataSetId = "ZmonDataSet_" + testCaseName;

        String tsuid1 = "tsuid_Z1" + testCaseName;
        String funcId1 = "funcId1" + testCaseName;
        doFuncIdImport(tsuid1, funcId1, false, 1);
        String tsuid2 = "tsuid_Z2" + testCaseName;
        String funcId2 = "funcId2" + testCaseName;
        doFuncIdImport(tsuid2, funcId2, false, 1);
        String tsuid3 = "MAM1" + testCaseName;
        String funcId3 = "funcIdMAM1" + testCaseName;
        doFuncIdImport(tsuid3, funcId3, false, 1);
        String tsuid4 = "MAM2" + testCaseName;
        String funcId4 = "funcIdMAM2" + testCaseName;
        doFuncIdImport(tsuid4, funcId4, false, 1);

        launchDataSetImport(dataSetId, "une description qui ne doit pas être trop longue",
                tsuid1 + "," + tsuid2 + "," + tsuid3 + "," + tsuid4);

        removeDataSet(dataSetId, false);
    }

    @Test
    public void testUpdateDataSet() throws Exception {

        String testCaseName = "testUpdateDataSet";

        String dataSetId = "ZmonDataSet_" + testCaseName;

        String tsuid1 = "tsuid_Z1" + testCaseName;
        String funcId1 = "funcId1" + testCaseName;
        doFuncIdImport(tsuid1, funcId1, false, 1);
        String tsuid2 = "tsuid_Z2" + testCaseName;
        String funcId2 = "funcId2" + testCaseName;
        doFuncIdImport(tsuid2, funcId2, false, 1);
        String tsuid3 = "MAM1" + testCaseName;
        String funcId3 = "funcIdMAM1" + testCaseName;
        doFuncIdImport(tsuid3, funcId3, false, 1);
        String tsuid4 = "MAM2" + testCaseName;
        String funcId4 = "funcIdMAM2" + testCaseName;
        doFuncIdImport(tsuid4, funcId4, false, 1);
        String tsuid5 = "MAM3" + testCaseName;
        String funcId5 = "funcIdMAM3" + testCaseName;
        doFuncIdImport(tsuid5, funcId5, false, 1);

        launchDataSetImport(dataSetId, "une description qui ne doit pas être trop longue", tsuid1 + "," + tsuid2);
        DataSetWithFids dataset = getDataSet(dataSetId, MediaType.APPLICATION_JSON);
        assertEquals(2, dataset.getTsuidsAsString().size());
        launchDataSetUpdate(dataSetId, "autre Description", tsuid3 + "," + tsuid4 + "," + tsuid5, "replace");
        DataSetWithFids dataset2 = getDataSet(dataSetId, MediaType.APPLICATION_JSON);
        assertEquals("autre Description", dataset2.getDescription());
        assertEquals(3, dataset2.getTsuidsAsString().size());

        // ... more detailed tests in DAO Dataset projet
        removeDataSet(dataSetId, false);
    }

    @Test
    public void testUpdateDataSetWithAppendMode() throws IkatsWebClientException {

        String testCaseName = "testUpdateDataSetWithAppendMode";

        String dataSetId = "ZmonDataSet_" + testCaseName;

        String tsuid1 = "tsuid_Z1" + testCaseName;
        String funcId1 = "funcId1" + testCaseName;
        doFuncIdImport(tsuid1, funcId1, false, 1);
        String tsuid2 = "tsuid_Z2" + testCaseName;
        String funcId2 = "funcId2" + testCaseName;
        doFuncIdImport(tsuid2, funcId2, false, 1);
        String tsuid3 = "MAM1" + testCaseName;
        String funcId3 = "funcIdMAM1" + testCaseName;
        doFuncIdImport(tsuid3, funcId3, false, 1);
        String tsuid4 = "MAM2" + testCaseName;
        String funcId4 = "funcIdMAM2" + testCaseName;
        doFuncIdImport(tsuid4, funcId4, false, 1);
        String tsuid5 = "MAM3" + testCaseName;
        String funcId5 = "funcIdMAM3" + testCaseName;
        doFuncIdImport(tsuid5, funcId5, false, 1);

        launchDataSetImport(dataSetId, "une description qui ne doit pas être trop longue", tsuid1 + "," + tsuid2);
        DataSetWithFids dataset = getDataSet(dataSetId, MediaType.APPLICATION_JSON);
        assertEquals(2, dataset.getTsuidsAsString().size());
        launchDataSetUpdate(dataSetId, "autre Description", tsuid3 + "," + tsuid4 + "," + tsuid5, "append");
        DataSetWithFids dataset2 = getDataSet(dataSetId, MediaType.APPLICATION_JSON);
        assertEquals("autre Description", dataset2.getDescription());
        assertEquals(5, dataset2.getTsuidsAsString().size());

        // ... more detailed tests in DAO Dataset projet
        removeDataSet(dataSetId, true);
    }

    @Test
    public void testGetDataSet() throws IkatsWebClientException {

        String testCaseName = "testGetDataSet";

        String prefix = testCaseName;
        doFuncIdImport(prefix + "tsuid1", prefix + "funcId1", false, 1);
        doFuncIdImport(prefix + "tsuid2", prefix + "funcId2", false, 1);
        doFuncIdImport(prefix + "MAM1", prefix + "funcIdMAM1", false, 1);
        doFuncIdImport(prefix + "MAM2", prefix + "funcIdMAM2", false, 1);

        launchDataSetImport(prefix + "QmonDataSet", "une description qui ne doit pas être trop longue",
                prefix + "tsuid1," + prefix + "tsuid2," + prefix + "MAM1," + prefix + "MAM2");

        DataSetWithFids dataset = getDataSet(prefix + "QmonDataSet", MediaType.APPLICATION_JSON);
        assertEquals(prefix + "funcId1", dataset.getFid(prefix + "tsuid1"));
        assertEquals(prefix + "funcId2", dataset.getFid(prefix + "tsuid2"));
        assertEquals(prefix + "funcIdMAM1", dataset.getFid(prefix + "MAM1"));
        assertEquals(prefix + "funcIdMAM2", dataset.getFid(prefix + "MAM2"));

        DataSetWithFids dataset2 = getDataSet("monDataSet___14", MediaType.APPLICATION_JSON);
        assertNull(dataset2);
    }

    @Test
    public void testRemoveTSFromDataSet() throws IkatsWebClientException, IkatsDaoException {

        String testCaseName = "testRemoveTSFromDataSet";

        String prefix = testCaseName;
        doFuncIdImport(prefix + "tsuid1", prefix + "funcId1", false, 1);
        doFuncIdImport(prefix + "tsuid2", prefix + "funcId2", false, 1);
        doFuncIdImport(prefix + "MAM1", prefix + "funcIdMAM1", false, 1);
        doFuncIdImport(prefix + "MAM2", prefix + "funcIdMAM2", false, 1);

        launchDataSetImport(prefix + "QmonDataSet", "une description qui ne doit pas être trop longue",
                prefix + "tsuid1," + prefix + "tsuid2," + prefix + "MAM1," + prefix + "MAM2");

        DataSetWithFids dataset = getDataSet(prefix + "QmonDataSet", MediaType.APPLICATION_JSON);
        assertEquals(dataset.getFids().size(), 4);

        DataSetManager datasetmanager = new DataSetManager();
        datasetmanager.removeTSFromDataSet(prefix + "MAM1", prefix + "QmonDataSet");

        dataset = getDataSet(prefix + "QmonDataSet", MediaType.APPLICATION_JSON);
        assertEquals(dataset.getFids().size(), 3);
    }

    @Test
    public void testRemoveDataSet() throws IkatsDaoMissingResource, IkatsDaoException, IkatsWebClientException {

        String testCaseName = "testRemoveDataSet";

        // Prepare data
        doFuncIdImport(testCaseName + "tsuid11", testCaseName + "funcId11", false, 1);
        doFuncIdImport(testCaseName + "tsuid12", testCaseName + "funcId12", false, 1);
        doFuncIdImport(testCaseName + "MAM30", testCaseName + "funcId_MAM30", false, 1);
        launchMetaDataImport(testCaseName + "tsuid11", "meta1", "value1");
        launchMetaDataImport(testCaseName + "tsuid12", "meta2", "value2");
        launchDataSetImport(testCaseName + "monDataSet___1", "une description qui ne doit pas être trop longue",
                testCaseName + "tsuid11," + testCaseName + "tsuid12," + testCaseName + "MAM30");

        // SOFT remove dataset
        Response reponse = removeDataSet(testCaseName + "monDataSet___1", false);

        // check return code is 204
        assertEquals(reponse.getStatus(), 204);
        // check dataset does not exist any more
        assertNull(getDataSet(testCaseName + "monDataSet___1", MediaType.APPLICATION_JSON));

        // check timeseries still exist
        MetaDataManager metadataManager = new MetaDataManager();

        assertNotNull(metadataManager.getFunctionalIdentifierByTsuid(testCaseName + "tsuid11"));
        assertNotNull(metadataManager.getFunctionalIdentifierByTsuid(testCaseName + "tsuid12"));
        assertNotNull(metadataManager.getFunctionalIdentifierByTsuid(testCaseName + "MAM30"));

        // metadata still exist
        assertNotNull(metadataManager.getList(testCaseName + "tsuid11"));
        assertNotNull(metadataManager.getList(testCaseName + "tsuid12"));
    }

    /**
     * Beside testRemoveDataset(): the test testDeepRemoveDataSet() is checking if facade methods are correctly called
     * in order to complete deletion with timeseries tsuidX1 and tsuidX2 attached to the deleted dataset.
     *
     * @throws IkatsDaoMissingResource
     * @throws IkatsDaoException
     * @throws IkatsWebClientException
     * @throws ResourceNotFoundException
     */
    @Test
    public void testDeepRemoveDataSet() throws IkatsDaoMissingResource, IkatsDaoException, ResourceNotFoundException, IkatsWebClientException {

        String testCaseName = "testDeepRemoveDataSet";
        // This black-box test -high level test- simulates that a dataset exists,
        // and verify that all deleting services are called.
        // => it does not check the DAO layer !

        // Mocked DT => required in order to stub a Dataset
        String dataSetId = "monDataSet_" + testCaseName;
        FunctionalIdentifier[] fids = {new FunctionalIdentifier("tsuidX1", "fidX1"),
                new FunctionalIdentifier("tsuidX2", "fidX2")};
        DataSet mockedDataset = Mockito.spy(DataSet.class);
        mockedDataset.setName(dataSetId);
        mockedDataset.setDescription("desc");
        LinkDatasetTimeSeries[] linksDT = {new LinkDatasetTimeSeries(fids[0], mockedDataset),
                new LinkDatasetTimeSeries(fids[1], mockedDataset)};

        when(mockedDataset.getLinksToTimeSeries()).thenReturn(Arrays.asList(linksDT));

        // Mock the TimeSerieResource 
        // required: simulate the DELETE on TS (content+meta+fid)
        Response respDeletedTS = Response.status(Status.NO_CONTENT).entity("mocked delete TS").build();
        TimeSerieResource mockedTS = Mockito.spy(TimeSerieResource.class);
        doReturn(respDeletedTS).when(mockedTS).removeTimeSeries(fids[0].getTsuid());
        doReturn(respDeletedTS).when(mockedTS).removeTimeSeries(fids[1].getTsuid());

        // Mock DataSetFacade in order to simulate
        //  - DELETE on Dataset
        //  - DELETE on the links between dataset and fids.
        // These services are tested by testRemoveDataSet
        DataSetFacade mockedFacade = Mockito.spy(DataSetFacade.class);
        Mockito.doNothing().when(mockedFacade).removeTSFromDataSet(fids[0].getTsuid(), dataSetId);
        Mockito.doNothing().when(mockedFacade).removeTSFromDataSet(fids[1].getTsuid(), dataSetId);
        Mockito.doNothing().when(mockedFacade).removeDataSet(dataSetId);

        // Spy the DataSetManager in order to inject mockedFacade + mockedTS + dataset
        DataSetManager mockedDataSetManager = Mockito.spy(DataSetManager.class);
        when(mockedDataSetManager.getDataSetFacade()).thenReturn(mockedFacade);
        // 
        doReturn(mockedDataset).when(mockedDataSetManager).getDataSet(dataSetId);
        doReturn(mockedTS).when(mockedDataSetManager).getTimeSerieResource();
        doReturn(Arrays.asList(dataSetId)).when(mockedDataSetManager).getContainers(fids[0].getTsuid());
        doReturn(Arrays.asList(dataSetId)).when(mockedDataSetManager).getContainers(fids[1].getTsuid());

        // DataSet stub = mockedDataSetManager.getDataSet(dataSetId);
        // works: partial solution before injecting mocked manager(s) into the webapp tested by grizzly client 
        // mockedDataSetManager.removeDataSet(dataSetId, true);

        // TODO : Finally: inject mockedDataSetManager into DataSetResource,
        // in the webapp tested by grizzly client (requires extra works, new dependancies ...)
        //
        // => done presently: test directly on DataSetResource

        DataSetResource mockedDataSetResource = new DataSetResource();
        mockedDataSetResource.setDataSetManager(mockedDataSetManager);

        Response httpResponse = mockedDataSetResource.removeDataSet(dataSetId, true);
        assertTrue(httpResponse.getStatus() == Status.NO_CONTENT.getStatusCode());

        long delay = 500;
        // Test that delete on links is called ...
        verify(mockedFacade, timeout(delay)).removeTSFromDataSet(fids[0].getTsuid(), dataSetId);
        verify(mockedFacade, timeout(delay)).removeTSFromDataSet(fids[1].getTsuid(), dataSetId);

        // Test that delete on dataset is called ...
        verify(mockedFacade, timeout(delay)).removeDataSet(dataSetId);

        // Test if service deleting the TS, its meta and its funcId is called
        verify(mockedTS, timeout(delay)).removeTimeSeries(fids[0].getTsuid());
        verify(mockedTS, timeout(delay)).removeTimeSeries(fids[1].getTsuid());
    }

    @Test
    public void testReturnCode404RemoveDataSet() throws IkatsWebClientException {

        // remove dataset which does not exist
        Response reponse = removeDataSet("dataset_qui_n_existe_pas", true);
        assertTrue(reponse.getStatus() == 404);

        // deep remove dataset which does not exist
        reponse = removeDataSet("dataset_qui_n_existe_pas", false);
        assertTrue(reponse.getStatus() == 404);
    }

    @Test
    public void testGetSummary() throws IkatsWebClientException {

        String prefix = "DatasetRequestTestGetSummary";

        String[][] nameAndDescInputs = {{prefix + "DataSet7", "desc DataSet7"},
                {prefix + "DataSet2", "desc DataSet2"}, {prefix + "DataSet3", "desc DataSet3"},
                {prefix + "DataSet4", "desc DataSet4"}, {prefix + "DataSet5", "desc DataSet5"},
                {prefix + "DataSet6", "desc DataSet6"}};

        String prefixFid = "FuncId";

        String[] importedTsuids = {prefix + "tsuid1", prefix + "tsuid2", prefix + "MAM1", prefix + "MAM2"};

        for (int ii = 0; ii < importedTsuids.length; ii++) {
            doFuncIdImport(importedTsuids[ii], prefixFid + importedTsuids[ii], false, 1);
        }

        for (int i = 0; i < nameAndDescInputs.length; i++) {

            String[] currentInput = nameAndDescInputs[i];

            String contentDesc = getPrefixedListAsString("", importedTsuids, ",");

            launchDataSetImport(currentInput[0], currentInput[1], contentDesc);

            DataSetWithFids dataset = getDataSet(currentInput[0], MediaType.APPLICATION_JSON);
            getLogger().info(dataset.toDetailedString());

            assertEquals("Test on first imported fid for currentContent[0]", prefixFid + importedTsuids[0],
                    dataset.getFid(importedTsuids[0]));

        }

        List<DataSet> datasets = getAllDataSetSummary(MediaType.APPLICATION_JSON);

        // It is needed to watch only the dataset from this test:
        //
        List<DataSet> goodDatasets = new ArrayList<DataSet>();
        assertNotNull(datasets);
        int countDatasets = 0;
        for (DataSet dataSet : datasets) {

            if (dataSet.getName().startsWith(prefix)) {
                assertNotNull(dataSet.getName());
                assertNotNull(dataSet.getDescription());

                getLogger().info("[testGetSummary] " + dataSet.toDetailedString(false));
                countDatasets++;
                goodDatasets.add(dataSet);
                int matched = 0;
                for (int i = 0; i < nameAndDescInputs.length; i++) {
                    String[] currentInput = nameAndDescInputs[i];
                    if (dataSet.getName().equals(currentInput[0])
                            && dataSet.getDescription().equals(currentInput[1])) {
                        matched++;
                    }
                }
                assertEquals("Dataset summary matches exactly each name + desc", 1, matched);
            }
        }
        assertEquals("Dataset summary matches expected count for TU", nameAndDescInputs.length, countDatasets);
    }

    private String getPrefixedListAsString(String testPrefix, String[] values, String sep) {
        StringBuilder buff = new StringBuilder("");
        boolean start = true;
        for (int i = 0; i < values.length; i++) {
            if (!start) {
                buff.append(sep);
            } else {
                start = false;
            }

            buff.append(testPrefix);
            buff.append(values[i]);
        }
        return buff.toString();
    }

    /**
     * launch a request for meta data import.
     *
     * @param dataSetId
     * @param description
     * @param tsuids
     */
    private void launchDataSetImport(String dataSetId, String description, String tsuids) {
        String url = getAPIURL() + "/dataset/import/" + dataSetId;

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(MultiPartFeature.class);
        Client client = ClientBuilder.newClient(clientConfig);

        Response response = null;
        WebTarget target = client.target(url);
        Form form = new Form();
        form.param("description", description);
        form.param("tsuidList", tsuids);
        response = target.request().post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        getLogger().info(response.getStatusInfo());
        getLogger().info(response);
        assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
    }

    /**
     * @param dataSetId
     * @param description
     * @param tsuids
     * @param mode        "replace" or "append"
     * @throws IkatsWebClientException
     */
    private void launchDataSetUpdate(String dataSetId, String description, String tsuids, String mode) throws IkatsWebClientException {
        String url = getAPIURL() + "/dataset/" + dataSetId;

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(MultiPartFeature.class);
        Client client = ClientBuilder.newClient(clientConfig);

        Response response = null;
        Form form = new Form();
        form.param("description", description);
        form.param("tsuidList", tsuids);
        url = url + "?updateMode=" + mode;
        response = utils.sendPUTRequest(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE),
                MediaType.APPLICATION_FORM_URLENCODED, client, url, "");
        getLogger().info(response.getStatusInfo());
        getLogger().info(response);
    }

    private List<DataSet> getAllDataSetSummary(String mediaType) throws IkatsWebClientException {
        List<DataSet> result = null;

        String url = getAPIURL() + "/dataset";
        Client client = utils.getClientWithJSONFeature();

        Response response = utils.sendGETRequest(mediaType, client, url);
        getLogger().info(url + " : response status" + response.getStatus());
        if (response.getStatus() <= 200) {
            result = response.readEntity(new GenericType<List<DataSet>>() {
            });
        }

        return result;

    }

    private DataSetWithFids getDataSet(String datasetId, String mediaType) throws IkatsWebClientException {
        DataSetWithFids result = null;

        String url = getAPIURL() + "/dataset/" + datasetId;
        Client client = utils.getClientWithJSONFeature();


        // TODO stub the dataset content ... hard-coded host removed
        Response response = utils.sendGETRequest(mediaType, client, url);
        getLogger().info(url + " : response status" + response.getStatus());
        if (response.getStatus() <= 200) {
            result = response.readEntity(DataSetWithFids.class);
        }

        return result;

    }

    private Response removeDataSet(String datasetId, boolean deep) throws IkatsWebClientException {
        String result = null;
        String url = getAPIURL() + "/dataset/" + datasetId;
        if (deep) {
            url = url + "?deep=true";
        }
        Response response = null;
        Client client = utils.getClientWithJSONFeature();

        response = utils.sendDeleteRequest(client, url);
        getLogger().info(url + " : response status" + response.getStatus());
        if (response.getStatus() <= 200) {
            result = response.readEntity(String.class);
            getLogger().info(url + " : result" + result);
        }

        return response;
    }


}

