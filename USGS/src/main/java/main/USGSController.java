package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;

import org.apache.log4j.Logger;

import core.USGSNasaRepository;
import model.ImageTask;
import utils.MetadataUtilImpl;
import utils.PropertiesConstants;

public class USGSController {

	static final Logger LOGGER = Logger.getLogger(USGSController.class);

	private USGSNasaRepository usgsRepository;
	private ImageTask imageTask;
	private Properties properties;

	public USGSController(Properties properties, ImageTask imageTask) {
		this(new USGSNasaRepository(properties), imageTask, properties);
	}

	public USGSController(USGSNasaRepository usgsNasaRepository, ImageTask imageTask,
			Properties properties) {
		this.usgsRepository = usgsNasaRepository;
		this.imageTask = imageTask;
		this.properties = properties;
	}

	public USGSController(String dataSet, String region, String date, String pathStorage,
			String pathMetadata) throws Exception {
		properties = loadProperties();
		setUsgsRepository(new USGSNasaRepository(pathStorage, pathMetadata, properties));
		usgsRepository.handleAPIKeyUpdate();
		String parsedDataset = formatDataSet(dataSet);
		String imageName = getImageName(parsedDataset, region, date);
		setImageTask(new ImageTask(imageName, parsedDataset, region, date));
		imageTask.setDownloadLink(usgsRepository.getImageDownloadLink(imageTask.getName()));
	}

	/**
	 * Other errors this program can handle: Status 28: Means an error while
	 * downloading, operation timeout. Check conditions in PropertiesConstants
	 * (SPEED_LIMIT and SPEED_TIME)
	 *
	 */
	public void startDownload() {
		try {
			usgsRepository.downloadImage(imageTask);
		} catch (MalformedURLException e) {
			/**
			 * Tried to make download but a Malformed URL was given
			 */
			LOGGER.error("Error while downloading image", e);
			System.exit(3);
		} catch (IOException e) {
			/**
			 * Tried to make download but URL is not Reachable, or Tried to create a
			 * file/directory but got an error. Check logs
			 */
			LOGGER.error("Error while downloading image", e);
			System.exit(4);
		} catch (Exception e) {
			/**
			 * Tried to make download but had an error with Process Builder command
			 */
			LOGGER.error("Error while downloading image", e);
			System.exit(5);
		}
	}

	public void saveMetadata() {
		LOGGER.info("Starting to generate metadata file");

		String resultsDirPath = properties.getProperty(PropertiesConstants.SAPS_RESULTS_PATH);
		String metadataFilePath = properties.getProperty(PropertiesConstants.SAPS_METADATA_PATH)
				+ File.separator + "inputDescription.txt";

		MetadataUtilImpl metadataUtilImpl = new MetadataUtilImpl();
		try {
			metadataUtilImpl.writeMetadata(resultsDirPath, new File(metadataFilePath));
		} catch (Exception e) {
			/**
			 * Tried to generate metadata file but had an error while doing it
			 */
			LOGGER.error("Error while writing metadata file", e);
			System.exit(7);
		}
	}

	public String getImageName(String dataSet, String region, String date) {
		String imageName = null;
		try {
			imageName = this.usgsRepository.getImageName(dataSet, date, region);
		} catch (Exception e) {
			/**
			 * Tried to make download but a Malformed URL was given
			 */
			LOGGER.error("Not found the Image in the USGS Repository", e);
			System.exit(3);
		}
		return imageName;
	}
	
	private String formatDataSet(String dataset) {
		if (dataset.equals(PropertiesConstants.DATASET_LT5_TYPE)) {
			return PropertiesConstants.LANDSAT_5_DATASET;
		} else if (dataset.equals(PropertiesConstants.DATASET_LE7_TYPE)) {
			return PropertiesConstants.LANDSAT_7_DATASET;
		} else {
			return PropertiesConstants.LANDSAT_8_DATASET;
		}
	}

	public USGSNasaRepository getUsgsRepository() {
		return usgsRepository;
	}

	public void setUsgsRepository(USGSNasaRepository usgsRepository) {
		this.usgsRepository = usgsRepository;
	}

	public ImageTask getImageTask() {
		return imageTask;
	}

	public void setImageTask(ImageTask imageTask) {
		this.imageTask = imageTask;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public Properties getProperties() {
		return this.properties;
	}

	public static Properties loadProperties() {
		Properties props = new Properties();
		FileInputStream input;
		try {
			input = new FileInputStream(
					System.getProperty("user.dir") + File.separator + "config/sebal.conf");
			props.load(input);
		} catch (FileNotFoundException e) {
			LOGGER.error("Error while reading conf file", e);
		} catch (IOException e) {
			LOGGER.error("Error while loading properties", e);
		}
		return props;
	}
}
