package fr.cs.ikats.temporaldata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.cs.ikats.process.data.model.ProcessData;

/**
 *
 */
public class ProcessDataRequestTest extends AbstractRequestTest {

	@BeforeClass
	public static void setUpBeforClass() {
		AbstractRequestTest.setUpBeforClass(ProcessDataRequestTest.class.getSimpleName());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		AbstractRequestTest.tearDownAfterClass(ProcessDataRequestTest.class.getSimpleName());
	}

	@Test
	public void testImportCSVFile() {

		String testCaseName = "testImportCSVFile";
		boolean isNominal = true;
		try {
			start(testCaseName, isNominal);

			File file = getFileMatchingResource(testCaseName, "/data/matrice_distance.csv");
			
			getLogger().info("CSV process Data file : " + file.getAbsolutePath());
			String processId = "execId2";
			String url = getAPIURL() + "/processdata/" + processId;
			doImport(url, file, "CSV", 200);

			endNominal(testCaseName);
		} catch (Throwable e) {
			endWithFailure(testCaseName, e);
		}
	}
 
	@Test
	public void testGetData() {

		String testCaseName = "testGetData";
		boolean isNominal = true;
		try {
			start(testCaseName, isNominal);

			
			File file = getFileMatchingResource(testCaseName, "/data/test_import.csv" );
			 
			getLogger().info("CSV process Data file : " + file.getAbsolutePath());
			String processId = "execId2";
			String url = getAPIURL() + "/processdata/" + processId;
			String id = doImport(url, file, "CSV", 200);

			try {
				File resultFile = doGetData(id);
				assertEquals(file.length(), resultFile.length());

			} catch (IOException e) {
				getLogger().error("Error reading Input Stream", e);
				throw e;
			}

			endNominal(testCaseName);
		} catch (Throwable e) {
			endWithFailure(testCaseName, e);
		}

	}

	@Test
	public void testImportJSON() {
		
		String testCaseName = "testImportJSON";
		boolean isNominal = true;
		try {
			start(testCaseName, isNominal);
			
			String json = "{ \"input1\":\"valeur1\";\"input2\":\"valeur2\"}";
			String processId = "execIdJSON";
			String url = getAPIURL() + "/processdata/" + processId + "/JSON";
			String id = doImport(url, "jsondata", json, 200);
			
			String result = doGetDataJson(id);
			getLogger().info("Result : " + result);
			
			endNominal(testCaseName);
		} catch (Throwable e) {
			endWithFailure(testCaseName, e);
		} 
	}

	/**
	 * @param file
	 * @param url
	 */
	protected String doImport(String url, File file, String dataType, int statusExpected) {
		Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).register(JacksonFeature.class)
				.build();
		WebTarget target = client.target(url);

		// build form param
		final FormDataMultiPart multipart = new FormDataMultiPart();

		FileDataBodyPart fileBodyPart = new FileDataBodyPart("file", file);
		multipart.bodyPart(fileBodyPart);
		multipart.field("fileType", dataType);

		getLogger().info("sending url : " + url);
		Response response = target.request().post(Entity.entity(multipart, multipart.getMediaType()));
		getLogger().info("parsing response of " + url);
		getLogger().info(response);
		int status = response.getStatus();
		String result = response.readEntity(String.class);
		getLogger().info(result);
		// check status 204 - all data points stored successfully
		assertEquals(statusExpected, status);
		return result;
	}

	/**
	 * @param url
	 * @param name
	 * @param json
	 * @param statusExpected
	 * @return
	 */
	protected String doImport(String url, String name, String json, int statusExpected) {
		Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).register(JacksonFeature.class)
				.build();
		WebTarget target = client.target(url);
		// build form param
		final FormDataMultiPart multipart = new FormDataMultiPart();

		multipart.field("json", json);
		multipart.field("name", name);
		multipart.field("size", Integer.toString(json.length()));

		MultivaluedMap<String, String> formData = new MultivaluedStringMap();
		formData.add("json", json);
		formData.add("name", name);
		formData.add("size", Integer.toString(json.length()));

		getLogger().info("sending url : " + url);
		// Response response = target.request().post(Entity.entity(multipart,
		// multipart.getMediaType()));
		Response response = target.request().post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED));
		getLogger().info("parsing response of " + url);
		getLogger().info(response);
		int status = response.getStatus();
		String result = response.readEntity(String.class);
		getLogger().info(result);
		// check status 204 - all data points stored successfully
		assertEquals(statusExpected, status);
		return result;
	}

	protected File doGetDataDownload(String id) throws IOException {
		Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).register(JacksonFeature.class)
				.build();
		String url = getAPIURL() + "/processdata/id/download/" + id;
		WebTarget target = client.target(url);
		Response response = target.request().get();
		response.bufferEntity();
		ByteArrayInputStream output = (ByteArrayInputStream) response.getEntity();
		File outputFile = File.createTempFile("ikats", "dogetTestResult.csv");
		outputFile.deleteOnExit();
		FileWriter fos = new FileWriter(outputFile);
		try {
			byte[] buff = new byte[512];
			while ((output.read(buff)) != -1) {
				fos.write(new String(buff, Charset.defaultCharset()));
			}
		} finally {
			fos.close();
		}

		getLogger().info("Result written in file " + outputFile.getAbsolutePath());
		return outputFile;
	}

	protected String doGetDataJson(String id) throws IOException {
		Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).register(JacksonFeature.class)
				.build();
		String url = getAPIURL() + "/processdata/id/download/" + id;
		WebTarget target = client.target(url);
		Response response = target.request().get();
		response.bufferEntity();
		String output = (String) response.readEntity(String.class);
		return output;
	}

	protected File doGetData(String id) throws IOException {
		Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).register(JacksonFeature.class)
				.build();
		String url = getAPIURL() + "/processdata/id/" + id;
		WebTarget target = client.target(url);
		final FormDataMultiPart response = target.request().get(FormDataMultiPart.class);

		// This will iterate the individual parts of the multipart response
		File outputFile = File.createTempFile("ikats", "dogetTestResult.csv");
		outputFile.deleteOnExit();
		for (BodyPart part : response.getBodyParts()) {
			System.out.printf("Embedded Body Part [Mime Type: %s]\n", part.getMediaType());
			if (part.getMediaType().isCompatible(MediaType.APPLICATION_OCTET_STREAM_TYPE)) {
				BodyPartEntity entity = (BodyPartEntity) part.getEntity();
				InputStream is = entity.getInputStream();
				FileWriter fos = new FileWriter(outputFile);

				try {
					byte[] buff = new byte[1];
					int read = 0;
					while ((read = is.read(buff)) != -1) {
						fos.write(new String(buff, Charset.defaultCharset()));
					}
				} finally {
					fos.close();
				}
				getLogger().info("Result written in file " + outputFile.getAbsolutePath());
			} else {
				ProcessData data = part.getEntityAs(ProcessData.class);
				assertEquals(id, data.getId().toString());
				System.out.println(data);
			}
		}
		return outputFile;
	}

	protected String getInputStreamResult(InputStream inS) throws IOException {
		byte[] buff = new byte[512];
		int read = 0;
		StringBuffer strBuff = new StringBuffer();

		while ((read = inS.read(buff)) != -1) {
			strBuff.append(new String(buff, Charset.defaultCharset()));
		}
		return strBuff.toString().trim();
	}
}
