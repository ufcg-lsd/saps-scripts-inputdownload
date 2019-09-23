package core;

import model.ImageTask;
import utils.NotFoundException;
import utils.PropertiesConstants;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicStatusLine;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.*;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Properties;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class USGSNasaRepositoryTest {

	private static final String UTF_8 = "UTF-8";

	private String usgsUserName;
	private String usgsPassword;
	private String usgsJsonUrl;
	private String sapsExportPath;
	private String sapsResultsPath;
	private String sapsMetadataPath;

	@Rule
	public ExpectedSystemExit expectedSystemExit = ExpectedSystemExit.none();

	@Before
	public void setUp() {
		usgsUserName = "fake-user-name";
		usgsPassword = "fake-password";
		usgsJsonUrl = "https://earthexplorer.usgs.gov/inventory/json";
		sapsExportPath = "/tmp";
		sapsResultsPath = "/tmp/results";
		sapsMetadataPath = "temp/metadata";
	}

	@Test
	public void testGenerateAPIKeyResponse() throws ClientProtocolException, IOException {
		// set up
		HttpResponse httpResponse = mock(HttpResponse.class);
		HttpEntity httpEntity = mock(HttpEntity.class);

		String content = "{ \"errorCode\":null, \"error\":\"\", \"data\":\"9ccf44a1c7e74d7f94769956b54cd889\", \"api_version\":\"1.0\" }";

		InputStream contentInputStream = new ByteArrayInputStream(content.getBytes(UTF_8));
		doReturn(contentInputStream).when(httpEntity).getContent();
		doReturn(httpEntity).when(httpResponse).getEntity();

		BasicStatusLine basicStatus = new BasicStatusLine(new ProtocolVersion("", 0, 0),
				HttpStatus.SC_OK, "");
		doReturn(basicStatus).when(httpResponse).getStatusLine();
		doReturn(new Header[0]).when(httpResponse).getAllHeaders();

		USGSNasaRepository usgsNasaRepository = spy(new USGSNasaRepository(sapsExportPath,
				sapsMetadataPath, usgsJsonUrl, usgsUserName, usgsPassword, new Properties()));

		doReturn(content).when(usgsNasaRepository).getLoginResponse();

		// exercise
		String apiKey = usgsNasaRepository.generateAPIKey();

		// expect
		Assert.assertNotNull(apiKey);
		Assert.assertEquals("9ccf44a1c7e74d7f94769956b54cd889", apiKey);
	}

	@Test
	public void testMalFormedURLDownload() throws Exception {
		USGSNasaRepository usgsNasaRepository = new USGSNasaRepository(sapsExportPath,
				sapsMetadataPath, usgsJsonUrl, usgsUserName, usgsPassword, new Properties());
		usgsNasaRepository.handleAPIKeyUpdate();

		ImageTask imageTask = new ImageTask("fake-name", "fake-dataSet", "fake-region",
				"1984-01-01");
		imageTask.setDownloadLink(usgsNasaRepository.getImageDownloadLink(imageTask.getName()));
		try {
			usgsNasaRepository.downloadImage(imageTask);
		} catch (MalformedURLException e) {
			Assert.assertEquals(MalformedURLException.class, e.getClass());
			Assert.assertEquals("The given URL: " + imageTask.getDownloadLink() + " is not valid.",
					e.getMessage());
		} finally {
			File f = new File(sapsExportPath + "/data/" + imageTask.getName());
			f.delete();
		}
	}

	@Test
	public void testUnknownImageTaskName() throws Exception {
		expectedSystemExit.expectSystemExitWithStatus(5);
		String urlToBeTested = "http://www.google.com/invalidURL";
		String content = "{ \"errorCode\":null, \"error\":\"\", \"data\":\"9ccf44a1c7e74d7f94769956b54cd889\", \"api_version\":\"1.0\" }";

		USGSNasaRepository usgsNasaRepository = spy(new USGSNasaRepository(sapsExportPath,
				sapsMetadataPath, usgsJsonUrl, usgsUserName, usgsPassword, new Properties()));
		doReturn(content).when(usgsNasaRepository).getLoginResponse();
		usgsNasaRepository.handleAPIKeyUpdate();
		ImageTask imageTask = spy(
				new ImageTask("fake-name", "fake-dataSet", "fake-region", "1984-01-01"));
		doReturn(urlToBeTested).when(imageTask).getDownloadLink();

		usgsNasaRepository.downloadImage(imageTask);

		File f = new File(sapsExportPath + "/data/" + imageTask.getName());
		f.delete();
	}

	@Test
	public void testURLAvailability() throws IOException {
		String urlToBeTested = "abc";
		try {
			USGSNasaRepository.isReachable(urlToBeTested);
		} catch (MalformedURLException e) {
			Assert.assertEquals("The given URL: " + urlToBeTested + " is not valid.",
					e.getMessage());
		}
	}

	@Test
	public void testCorrectDownloadLinkFromResponse() throws Exception {
		String fakeDownloadResponse = "{\"errorCode\":null,\"error\":\"\",\"data\":[{\"entityId\":\"LE72150652000013EDC00\",\"product\":\"STANDARD\",\"url\":\"https:\\/\\/dds.cr.usgs.gov\\/ltaauth\\/hsm\\/lsat1\\/collection01\\/etm\\/T1\\/2000\\/215\\/65\\/LE07_L1TP_215065_20000113_20170215_01_T1.tar.gz?id=6506kfds27fj6f2dj2ffh60u43&iid=LE72150652000013EDC00&did=362814459&ver=production\"}],\"api_version\":\"1.4.0\",\"access_level\":\"approved\",\"catalog_id\":\"EE\",\"executionTime\":1.1173131465912}";
		String expectedDownloadLink = "https://dds.cr.usgs.gov/ltaauth/hsm/lsat1/collection01/etm/T1/2000/215/65/LE07_L1TP_215065_20000113_20170215_01_T1.tar.gz?id=6506kfds27fj6f2dj2ffh60u43&iid=LE72150652000013EDC00&did=362814459&ver=production";

		USGSNasaRepository usgsNasaRepository = spy(new USGSNasaRepository(sapsResultsPath,
				sapsMetadataPath, usgsJsonUrl, usgsUserName, usgsPassword, new Properties()));

		doReturn(fakeDownloadResponse).when(usgsNasaRepository).getDownloadHttpResponse(anyString(),
				anyString(), anyString(), anyString());

		Assert.assertEquals(expectedDownloadLink, usgsNasaRepository.doGetDownloadLink("LT5"));
	}

	@Test
	public void testGetRegionGeolocation() {
		USGSNasaRepository usgsNasaRepository = spy(new USGSNasaRepository(sapsResultsPath,
				sapsMetadataPath, usgsJsonUrl, usgsUserName, usgsPassword, new Properties()));
		
		JSONObject json = usgsNasaRepository.getRegionGeolocation("215065");
		Assert.assertEquals("-7.231189", json.get(PropertiesConstants.LATITUDE_JSON_KEY));
		Assert.assertEquals("-36.784093", json.get(PropertiesConstants.LONGITUDE_JSON_KEY));
		
		Assert.assertNull(usgsNasaRepository.getRegionGeolocation("999999"));
	}
	
	@Test
	public void testGetImageName() throws Exception {
		USGSNasaRepository usgsNasaRepository = spy(new USGSNasaRepository(sapsResultsPath,
				sapsMetadataPath, usgsJsonUrl, usgsUserName, usgsPassword, new Properties()));

		JSONArray response = new JSONArray(
				"[{\"acquisitionDate\":\"1985-01-27\",\"startTime\":\"1985-01-27\",\"endTime\":\"1985-01-27\",\"spatialFootprint\":{\"type\":\"Polygon\",\"coordinates\":[[[-37.76143,-7.90583],[-36.10081,-8.14731],[-35.76158,-6.55574],[-37.41655,-6.31507],[-37.76143,-7.90583]]]},\"sceneBounds\":\"-37.76143,-8.14731,-35.76158,-6.31507\",\"browseUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/browse\\/tm\\/215\\/65\\/1985\\/LT05_L1GS_215065_19850127_20170219_01_T2_REFL.jpg\",\"dataAccessUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/order\\/process?dataset_name=LANDSAT_TM_C1&ordered=LT52150651985027CUB00&node=INVSVC\",\"downloadUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/download\\/external\\/options\\/LANDSAT_TM_C1\\/LT52150651985027CUB00\\/INVSVC\\/\",\"entityId\":\"LT52150651985027CUB00\",\"displayId\":\"LT05_L1GS_215065_19850127_20170219_01_T2\",\"metadataUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/metadata\\/xml\\/12266\\/LT52150651985027CUB00\\/\",\"fgdcMetadataUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/fgdc\\/12266\\/LT52150651985027CUB00\\/save_xml\",\"modifiedDate\":\"2017-02-19\",\"orderUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/order\\/process?dataset_name=LANDSAT_TM_C1&ordered=LT52150651985027CUB00&node=INVSVC\",\"bulkOrdered\":false,\"ordered\":false,\"summary\":\"Entity ID: LT05_L1GS_215065_19850127_20170219_01_T2, Acquisition Date: 27-JAN-85, Path: 215, Row: 65\"},{\"acquisitionDate\":\"1985-02-12\",\"startTime\":\"1985-02-12\",\"endTime\":\"1985-02-12\",\"spatialFootprint\":{\"type\":\"Polygon\",\"coordinates\":[[[-37.76295,-7.9065],[-36.10233,-8.14798],[-35.7631,-6.55641],[-37.41807,-6.31574],[-37.76295,-7.9065]]]},\"sceneBounds\":\"-37.76295,-8.14798,-35.7631,-6.31574\",\"browseUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/browse\\/tm\\/215\\/65\\/1985\\/LT05_L1GS_215065_19850212_20170219_01_T2_REFL.jpg\",\"dataAccessUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/order\\/process?dataset_name=LANDSAT_TM_C1&ordered=LT52150651985043CUB00&node=INVSVC\",\"downloadUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/download\\/external\\/options\\/LANDSAT_TM_C1\\/LT52150651985043CUB00\\/INVSVC\\/\",\"entityId\":\"LT52150651985043CUB00\",\"displayId\":\"LT05_L1GS_215065_19850212_20170219_01_T2\",\"metadataUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/metadata\\/xml\\/12266\\/LT52150651985043CUB00\\/\",\"fgdcMetadataUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/fgdc\\/12266\\/LT52150651985043CUB00\\/save_xml\",\"modifiedDate\":\"2017-02-19\",\"orderUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/order\\/process?dataset_name=LANDSAT_TM_C1&ordered=LT52150651985043CUB00&node=INVSVC\",\"bulkOrdered\":false,\"ordered\":false,\"summary\":\"Entity ID: LT05_L1GS_215065_19850212_20170219_01_T2, Acquisition Date: 12-FEB-85, Path: 215, Row: 65\"},{\"acquisitionDate\":\"1985-04-17\",\"startTime\":\"1985-04-17\",\"endTime\":\"1985-04-17\",\"spatialFootprint\":{\"type\":\"Polygon\",\"coordinates\":[[[-37.74835,-7.90875],[-36.08772,-8.15023],[-35.74848,-6.55866],[-37.40347,-6.31799],[-37.74835,-7.90875]]]},\"sceneBounds\":\"-37.74835,-8.15023,-35.74848,-6.31799\",\"browseUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/browse\\/tm\\/215\\/65\\/1985\\/LT05_L1GS_215065_19850417_20170219_01_T2_REFL.jpg\",\"dataAccessUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/order\\/process?dataset_name=LANDSAT_TM_C1&ordered=LT52150651985107CUB00&node=INVSVC\",\"downloadUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/download\\/external\\/options\\/LANDSAT_TM_C1\\/LT52150651985107CUB00\\/INVSVC\\/\",\"entityId\":\"LT52150651985107CUB00\",\"displayId\":\"LT05_L1GS_215065_19850417_20170219_01_T2\",\"metadataUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/metadata\\/xml\\/12266\\/LT52150651985107CUB00\\/\",\"fgdcMetadataUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/fgdc\\/12266\\/LT52150651985107CUB00\\/save_xml\",\"modifiedDate\":\"2017-02-19\",\"orderUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/order\\/process?dataset_name=LANDSAT_TM_C1&ordered=LT52150651985107CUB00&node=INVSVC\",\"bulkOrdered\":false,\"ordered\":false,\"summary\":\"Entity ID: LT05_L1GS_215065_19850417_20170219_01_T2, Acquisition Date: 17-APR-85, Path: 215, Row: 65\"},{\"acquisitionDate\":\"1985-05-19\",\"startTime\":\"1985-05-19\",\"endTime\":\"1985-05-19\",\"spatialFootprint\":{\"type\":\"Polygon\",\"coordinates\":[[[-37.74616,-7.91149],[-36.08552,-8.15298],[-35.74629,-6.5614],[-37.40128,-6.32073],[-37.74616,-7.91149]]]},\"sceneBounds\":\"-37.74616,-8.15298,-35.74629,-6.32073\",\"browseUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/browse\\/tm\\/215\\/65\\/1985\\/LT05_L1GS_215065_19850519_20170219_01_T2_REFL.jpg\",\"dataAccessUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/order\\/process?dataset_name=LANDSAT_TM_C1&ordered=LT52150651985139CUB00&node=INVSVC\",\"downloadUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/download\\/external\\/options\\/LANDSAT_TM_C1\\/LT52150651985139CUB00\\/INVSVC\\/\",\"entityId\":\"LT52150651985139CUB00\",\"displayId\":\"LT05_L1GS_215065_19850519_20170219_01_T2\",\"metadataUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/metadata\\/xml\\/12266\\/LT52150651985139CUB00\\/\",\"fgdcMetadataUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/fgdc\\/12266\\/LT52150651985139CUB00\\/save_xml\",\"modifiedDate\":\"2017-02-19\",\"orderUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/order\\/process?dataset_name=LANDSAT_TM_C1&ordered=LT52150651985139CUB00&node=INVSVC\",\"bulkOrdered\":false,\"ordered\":false,\"summary\":\"Entity ID: LT05_L1GS_215065_19850519_20170219_01_T2, Acquisition Date: 19-MAY-85, Path: 215, Row: 65\"},{\"acquisitionDate\":\"1985-06-04\",\"startTime\":\"1985-06-04\",\"endTime\":\"1985-06-04\",\"spatialFootprint\":{\"type\":\"Polygon\",\"coordinates\":[[[-37.7697,-7.90792],[-36.10908,-8.1494],[-35.76984,-6.55783],[-37.42483,-6.31716],[-37.7697,-7.90792]]]},\"sceneBounds\":\"-37.7697,-8.1494,-35.76984,-6.31716\",\"browseUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/browse\\/tm\\/215\\/65\\/1985\\/LT05_L1GS_215065_19850604_20170219_01_T2_REFL.jpg\",\"dataAccessUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/order\\/process?dataset_name=LANDSAT_TM_C1&ordered=LT52150651985155CUB00&node=INVSVC\",\"downloadUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/download\\/external\\/options\\/LANDSAT_TM_C1\\/LT52150651985155CUB00\\/INVSVC\\/\",\"entityId\":\"LT52150651985155CUB00\",\"displayId\":\"LT05_L1GS_215065_19850604_20170219_01_T2\",\"metadataUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/metadata\\/xml\\/12266\\/LT52150651985155CUB00\\/\",\"fgdcMetadataUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/fgdc\\/12266\\/LT52150651985155CUB00\\/save_xml\",\"modifiedDate\":\"2017-02-19\",\"orderUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/order\\/process?dataset_name=LANDSAT_TM_C1&ordered=LT52150651985155CUB00&node=INVSVC\",\"bulkOrdered\":false,\"ordered\":false,\"summary\":\"Entity ID: LT05_L1GS_215065_19850604_20170219_01_T2, Acquisition Date: 04-JUN-85, Path: 215, Row: 65\"},{\"acquisitionDate\":\"1985-10-26\",\"startTime\":\"1985-10-26\",\"endTime\":\"1985-10-26\",\"spatialFootprint\":{\"type\":\"Polygon\",\"coordinates\":[[[-37.7865,-7.9013],[-36.1259,-8.14278],[-35.78667,-6.55121],[-37.44163,-6.31054],[-37.7865,-7.9013]]]},\"sceneBounds\":\"-37.7865,-8.14278,-35.78667,-6.31054\",\"browseUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/browse\\/tm\\/215\\/65\\/1985\\/LT05_L1TP_215065_19851026_20170218_01_T1_REFL.jpg\",\"dataAccessUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/order\\/process?dataset_name=LANDSAT_TM_C1&ordered=LT52150651985299CUB00&node=INVSVC\",\"downloadUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/download\\/external\\/options\\/LANDSAT_TM_C1\\/LT52150651985299CUB00\\/INVSVC\\/\",\"entityId\":\"LT52150651985299CUB00\",\"displayId\":\"LT05_L1TP_215065_19851026_20170218_01_T1\",\"metadataUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/metadata\\/xml\\/12266\\/LT52150651985299CUB00\\/\",\"fgdcMetadataUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/fgdc\\/12266\\/LT52150651985299CUB00\\/save_xml\",\"modifiedDate\":\"2017-02-18\",\"orderUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/order\\/process?dataset_name=LANDSAT_TM_C1&ordered=LT52150651985299CUB00&node=INVSVC\",\"bulkOrdered\":false,\"ordered\":false,\"summary\":\"Entity ID: LT05_L1TP_215065_19851026_20170218_01_T1, Acquisition Date: 26-OCT-85, Path: 215, Row: 65\"},{\"acquisitionDate\":\"1985-11-27\",\"startTime\":\"1985-11-27\",\"endTime\":\"1985-11-27\",\"spatialFootprint\":{\"type\":\"Polygon\",\"coordinates\":[[[-37.78294,-7.90037],[-36.12234,-8.14185],[-35.78311,-6.55028],[-37.43807,-6.30961],[-37.78294,-7.90037]]]},\"sceneBounds\":\"-37.78294,-8.14185,-35.78311,-6.30961\",\"browseUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/browse\\/tm\\/215\\/65\\/1985\\/LT05_L1TP_215065_19851127_20170218_01_T1_REFL.jpg\",\"dataAccessUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/order\\/process?dataset_name=LANDSAT_TM_C1&ordered=LT52150651985331CUB00&node=INVSVC\",\"downloadUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/download\\/external\\/options\\/LANDSAT_TM_C1\\/LT52150651985331CUB00\\/INVSVC\\/\",\"entityId\":\"LT52150651985331CUB00\",\"displayId\":\"LT05_L1TP_215065_19851127_20170218_01_T1\",\"metadataUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/metadata\\/xml\\/12266\\/LT52150651985331CUB00\\/\",\"fgdcMetadataUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/fgdc\\/12266\\/LT52150651985331CUB00\\/save_xml\",\"modifiedDate\":\"2017-02-18\",\"orderUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/order\\/process?dataset_name=LANDSAT_TM_C1&ordered=LT52150651985331CUB00&node=INVSVC\",\"bulkOrdered\":false,\"ordered\":false,\"summary\":\"Entity ID: LT05_L1TP_215065_19851127_20170218_01_T1, Acquisition Date: 27-NOV-85, Path: 215, Row: 65\"}]");

		doReturn(response).when(usgsNasaRepository).getAvailableImagesInRange("LANDSAT_TM_C1", 1985,
				1985, "215065");

		String expected = "LT52150651985299CUB00";
		String actual = usgsNasaRepository.getImageName("LANDSAT_TM_C1", "1985-10-26", "215065");

		Assert.assertEquals(expected, actual);

	}
	
	@Test(expected = NotFoundException.class)
	public void testGetImageNameNotFound() throws Exception {
		USGSNasaRepository usgsNasaRepository = spy(new USGSNasaRepository(sapsResultsPath,
				sapsMetadataPath, usgsJsonUrl, usgsUserName, usgsPassword, new Properties()));

		JSONArray response = new JSONArray(
				"[{\"acquisitionDate\":\"1985-01-27\",\"startTime\":\"1985-01-27\",\"endTime\":\"1985-01-27\",\"spatialFootprint\":{\"type\":\"Polygon\",\"coordinates\":[[[-37.76143,-7.90583],[-36.10081,-8.14731],[-35.76158,-6.55574],[-37.41655,-6.31507],[-37.76143,-7.90583]]]},\"sceneBounds\":\"-37.76143,-8.14731,-35.76158,-6.31507\",\"browseUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/browse\\/tm\\/215\\/65\\/1985\\/LT05_L1GS_215065_19850127_20170219_01_T2_REFL.jpg\",\"dataAccessUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/order\\/process?dataset_name=LANDSAT_TM_C1&ordered=LT52150651985027CUB00&node=INVSVC\",\"downloadUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/download\\/external\\/options\\/LANDSAT_TM_C1\\/LT52150651985027CUB00\\/INVSVC\\/\",\"entityId\":\"LT52150651985027CUB00\",\"displayId\":\"LT05_L1GS_215065_19850127_20170219_01_T2\",\"metadataUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/metadata\\/xml\\/12266\\/LT52150651985027CUB00\\/\",\"fgdcMetadataUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/fgdc\\/12266\\/LT52150651985027CUB00\\/save_xml\",\"modifiedDate\":\"2017-02-19\",\"orderUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/order\\/process?dataset_name=LANDSAT_TM_C1&ordered=LT52150651985027CUB00&node=INVSVC\",\"bulkOrdered\":false,\"ordered\":false,\"summary\":\"Entity ID: LT05_L1GS_215065_19850127_20170219_01_T2, Acquisition Date: 27-JAN-85, Path: 215, Row: 65\"},{\"acquisitionDate\":\"1985-02-12\",\"startTime\":\"1985-02-12\",\"endTime\":\"1985-02-12\",\"spatialFootprint\":{\"type\":\"Polygon\",\"coordinates\":[[[-37.76295,-7.9065],[-36.10233,-8.14798],[-35.7631,-6.55641],[-37.41807,-6.31574],[-37.76295,-7.9065]]]},\"sceneBounds\":\"-37.76295,-8.14798,-35.7631,-6.31574\",\"browseUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/browse\\/tm\\/215\\/65\\/1985\\/LT05_L1GS_215065_19850212_20170219_01_T2_REFL.jpg\",\"dataAccessUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/order\\/process?dataset_name=LANDSAT_TM_C1&ordered=LT52150651985043CUB00&node=INVSVC\",\"downloadUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/download\\/external\\/options\\/LANDSAT_TM_C1\\/LT52150651985043CUB00\\/INVSVC\\/\",\"entityId\":\"LT52150651985043CUB00\",\"displayId\":\"LT05_L1GS_215065_19850212_20170219_01_T2\",\"metadataUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/metadata\\/xml\\/12266\\/LT52150651985043CUB00\\/\",\"fgdcMetadataUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/fgdc\\/12266\\/LT52150651985043CUB00\\/save_xml\",\"modifiedDate\":\"2017-02-19\",\"orderUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/order\\/process?dataset_name=LANDSAT_TM_C1&ordered=LT52150651985043CUB00&node=INVSVC\",\"bulkOrdered\":false,\"ordered\":false,\"summary\":\"Entity ID: LT05_L1GS_215065_19850212_20170219_01_T2, Acquisition Date: 12-FEB-85, Path: 215, Row: 65\"},{\"acquisitionDate\":\"1985-04-17\",\"startTime\":\"1985-04-17\",\"endTime\":\"1985-04-17\",\"spatialFootprint\":{\"type\":\"Polygon\",\"coordinates\":[[[-37.74835,-7.90875],[-36.08772,-8.15023],[-35.74848,-6.55866],[-37.40347,-6.31799],[-37.74835,-7.90875]]]},\"sceneBounds\":\"-37.74835,-8.15023,-35.74848,-6.31799\",\"browseUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/browse\\/tm\\/215\\/65\\/1985\\/LT05_L1GS_215065_19850417_20170219_01_T2_REFL.jpg\",\"dataAccessUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/order\\/process?dataset_name=LANDSAT_TM_C1&ordered=LT52150651985107CUB00&node=INVSVC\",\"downloadUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/download\\/external\\/options\\/LANDSAT_TM_C1\\/LT52150651985107CUB00\\/INVSVC\\/\",\"entityId\":\"LT52150651985107CUB00\",\"displayId\":\"LT05_L1GS_215065_19850417_20170219_01_T2\",\"metadataUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/metadata\\/xml\\/12266\\/LT52150651985107CUB00\\/\",\"fgdcMetadataUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/fgdc\\/12266\\/LT52150651985107CUB00\\/save_xml\",\"modifiedDate\":\"2017-02-19\",\"orderUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/order\\/process?dataset_name=LANDSAT_TM_C1&ordered=LT52150651985107CUB00&node=INVSVC\",\"bulkOrdered\":false,\"ordered\":false,\"summary\":\"Entity ID: LT05_L1GS_215065_19850417_20170219_01_T2, Acquisition Date: 17-APR-85, Path: 215, Row: 65\"},{\"acquisitionDate\":\"1985-05-19\",\"startTime\":\"1985-05-19\",\"endTime\":\"1985-05-19\",\"spatialFootprint\":{\"type\":\"Polygon\",\"coordinates\":[[[-37.74616,-7.91149],[-36.08552,-8.15298],[-35.74629,-6.5614],[-37.40128,-6.32073],[-37.74616,-7.91149]]]},\"sceneBounds\":\"-37.74616,-8.15298,-35.74629,-6.32073\",\"browseUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/browse\\/tm\\/215\\/65\\/1985\\/LT05_L1GS_215065_19850519_20170219_01_T2_REFL.jpg\",\"dataAccessUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/order\\/process?dataset_name=LANDSAT_TM_C1&ordered=LT52150651985139CUB00&node=INVSVC\",\"downloadUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/download\\/external\\/options\\/LANDSAT_TM_C1\\/LT52150651985139CUB00\\/INVSVC\\/\",\"entityId\":\"LT52150651985139CUB00\",\"displayId\":\"LT05_L1GS_215065_19850519_20170219_01_T2\",\"metadataUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/metadata\\/xml\\/12266\\/LT52150651985139CUB00\\/\",\"fgdcMetadataUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/fgdc\\/12266\\/LT52150651985139CUB00\\/save_xml\",\"modifiedDate\":\"2017-02-19\",\"orderUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/order\\/process?dataset_name=LANDSAT_TM_C1&ordered=LT52150651985139CUB00&node=INVSVC\",\"bulkOrdered\":false,\"ordered\":false,\"summary\":\"Entity ID: LT05_L1GS_215065_19850519_20170219_01_T2, Acquisition Date: 19-MAY-85, Path: 215, Row: 65\"},{\"acquisitionDate\":\"1985-06-04\",\"startTime\":\"1985-06-04\",\"endTime\":\"1985-06-04\",\"spatialFootprint\":{\"type\":\"Polygon\",\"coordinates\":[[[-37.7697,-7.90792],[-36.10908,-8.1494],[-35.76984,-6.55783],[-37.42483,-6.31716],[-37.7697,-7.90792]]]},\"sceneBounds\":\"-37.7697,-8.1494,-35.76984,-6.31716\",\"browseUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/browse\\/tm\\/215\\/65\\/1985\\/LT05_L1GS_215065_19850604_20170219_01_T2_REFL.jpg\",\"dataAccessUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/order\\/process?dataset_name=LANDSAT_TM_C1&ordered=LT52150651985155CUB00&node=INVSVC\",\"downloadUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/download\\/external\\/options\\/LANDSAT_TM_C1\\/LT52150651985155CUB00\\/INVSVC\\/\",\"entityId\":\"LT52150651985155CUB00\",\"displayId\":\"LT05_L1GS_215065_19850604_20170219_01_T2\",\"metadataUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/metadata\\/xml\\/12266\\/LT52150651985155CUB00\\/\",\"fgdcMetadataUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/fgdc\\/12266\\/LT52150651985155CUB00\\/save_xml\",\"modifiedDate\":\"2017-02-19\",\"orderUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/order\\/process?dataset_name=LANDSAT_TM_C1&ordered=LT52150651985155CUB00&node=INVSVC\",\"bulkOrdered\":false,\"ordered\":false,\"summary\":\"Entity ID: LT05_L1GS_215065_19850604_20170219_01_T2, Acquisition Date: 04-JUN-85, Path: 215, Row: 65\"},{\"acquisitionDate\":\"1985-10-26\",\"startTime\":\"1985-10-26\",\"endTime\":\"1985-10-26\",\"spatialFootprint\":{\"type\":\"Polygon\",\"coordinates\":[[[-37.7865,-7.9013],[-36.1259,-8.14278],[-35.78667,-6.55121],[-37.44163,-6.31054],[-37.7865,-7.9013]]]},\"sceneBounds\":\"-37.7865,-8.14278,-35.78667,-6.31054\",\"browseUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/browse\\/tm\\/215\\/65\\/1985\\/LT05_L1TP_215065_19851026_20170218_01_T1_REFL.jpg\",\"dataAccessUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/order\\/process?dataset_name=LANDSAT_TM_C1&ordered=LT52150651985299CUB00&node=INVSVC\",\"downloadUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/download\\/external\\/options\\/LANDSAT_TM_C1\\/LT52150651985299CUB00\\/INVSVC\\/\",\"entityId\":\"LT52150651985299CUB00\",\"displayId\":\"LT05_L1TP_215065_19851026_20170218_01_T1\",\"metadataUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/metadata\\/xml\\/12266\\/LT52150651985299CUB00\\/\",\"fgdcMetadataUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/fgdc\\/12266\\/LT52150651985299CUB00\\/save_xml\",\"modifiedDate\":\"2017-02-18\",\"orderUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/order\\/process?dataset_name=LANDSAT_TM_C1&ordered=LT52150651985299CUB00&node=INVSVC\",\"bulkOrdered\":false,\"ordered\":false,\"summary\":\"Entity ID: LT05_L1TP_215065_19851026_20170218_01_T1, Acquisition Date: 26-OCT-85, Path: 215, Row: 65\"},{\"acquisitionDate\":\"1985-11-27\",\"startTime\":\"1985-11-27\",\"endTime\":\"1985-11-27\",\"spatialFootprint\":{\"type\":\"Polygon\",\"coordinates\":[[[-37.78294,-7.90037],[-36.12234,-8.14185],[-35.78311,-6.55028],[-37.43807,-6.30961],[-37.78294,-7.90037]]]},\"sceneBounds\":\"-37.78294,-8.14185,-35.78311,-6.30961\",\"browseUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/browse\\/tm\\/215\\/65\\/1985\\/LT05_L1TP_215065_19851127_20170218_01_T1_REFL.jpg\",\"dataAccessUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/order\\/process?dataset_name=LANDSAT_TM_C1&ordered=LT52150651985331CUB00&node=INVSVC\",\"downloadUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/download\\/external\\/options\\/LANDSAT_TM_C1\\/LT52150651985331CUB00\\/INVSVC\\/\",\"entityId\":\"LT52150651985331CUB00\",\"displayId\":\"LT05_L1TP_215065_19851127_20170218_01_T1\",\"metadataUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/metadata\\/xml\\/12266\\/LT52150651985331CUB00\\/\",\"fgdcMetadataUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/fgdc\\/12266\\/LT52150651985331CUB00\\/save_xml\",\"modifiedDate\":\"2017-02-18\",\"orderUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/order\\/process?dataset_name=LANDSAT_TM_C1&ordered=LT52150651985331CUB00&node=INVSVC\",\"bulkOrdered\":false,\"ordered\":false,\"summary\":\"Entity ID: LT05_L1TP_215065_19851127_20170218_01_T1, Acquisition Date: 27-NOV-85, Path: 215, Row: 65\"}]");

		doReturn(response).when(usgsNasaRepository).getAvailableImagesInRange("LANDSAT_TM_C1", 1985,
				1985, "215065");

		usgsNasaRepository.getImageName("LANDSAT_TM_C1", "1985-12-15", "215065");
	}
	
	@Test(expected = NotFoundException.class)
	public void testGetImageNameEmptyFile() throws Exception {
		USGSNasaRepository usgsNasaRepository = spy(new USGSNasaRepository(sapsResultsPath,
				sapsMetadataPath, usgsJsonUrl, usgsUserName, usgsPassword, new Properties()));

		doReturn(null).when(usgsNasaRepository).getAvailableImagesInRange("LANDSAT_TM_C1", 1985,
				1985, "215065");

		usgsNasaRepository.getImageName("LANDSAT_TM_C1", "1985-10-26", "215065");
	}

}
