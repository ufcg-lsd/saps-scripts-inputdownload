package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MetadataUtilImplTest {

	private static File inputDir;
	private static File metadataDir;
	private static File bandOneFile;
	private static File bandTwoFile;
	private static File mtlFile;
	private static File gcpFile;
	private static File readmeFile;
	private static File stationFile;
	private static File metadataFile;

	@Before
	public void setUp() throws IOException {
		// Create temporary inputDir and metadataDir folders
		inputDir = new File("results");
		if (!inputDir.exists()) {
			inputDir.mkdir();
		}

		metadataDir = new File("metadata");
		if (!metadataDir.exists()) {
			metadataDir.mkdir();
		}
		
		metadataFile = new File(metadataDir.getAbsolutePath() + File.separator + "metadataFile");
		if (!metadataFile.exists()) {
			metadataFile.createNewFile();
		}

		// Create temporary input files
		bandOneFile = new File(inputDir.getAbsolutePath() + File.separator + "test_B1.TIF");
		bandTwoFile = new File(inputDir.getAbsolutePath() + File.separator + "test_B2.TIF");
		mtlFile = new File(inputDir.getAbsolutePath() + File.separator + "test_MTL.txt");
		gcpFile = new File(inputDir.getAbsolutePath() + File.separator + "test_GCP.txt");
		readmeFile = new File(inputDir.getAbsolutePath() + File.separator + "README.txt");
		stationFile = new File(inputDir.getAbsolutePath() + File.separator + "test_station.csv");

		bandOneFile.createNewFile();
		bandTwoFile.createNewFile();
		mtlFile.createNewFile();
		gcpFile.createNewFile();
		readmeFile.createNewFile();
		stationFile.createNewFile();
	}

	@Test
	public void testWriteMetadata() throws Exception {
		// Set Up
		MetadataUtilImpl metadataUtilImpl = new MetadataUtilImpl();

		// Exercise
		metadataUtilImpl.writeMetadata(inputDir.getAbsolutePath(), metadataFile);

		BufferedReader br = new BufferedReader(new FileReader(metadataFile.getAbsolutePath()));

		// Expect
		Assert.assertNotNull(br.readLine());
		br.close();
	}

	@After
	public void teardown() throws IOException {
		if (inputDir.exists()) {
			FileUtils.deleteDirectory(inputDir);
		}

		if (metadataDir.exists()) {
			FileUtils.deleteDirectory(metadataDir);
		}

		if (metadataFile.exists()) {
			FileUtils.deleteQuietly(metadataFile);
		}
	}
}