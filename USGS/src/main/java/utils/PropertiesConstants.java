package utils;

public class PropertiesConstants {

	// JSON constants
	public static final String TILE_ID_JSON_KEY = "id";
	public static final String TILES_JSON_KEY = "tiles";
	public static final String USERNAME_JSON_KEY = "username";
	public static final String PASSWORD_JSON_KEY = "password";
	public static final String AUTH_TYPE_JSON_KEY = "authType";
	public static final String API_KEY_JSON_KEY = "apiKey";
	public static final String NODE_JSON_KEY = "node";
	public static final String DATASET_NAME_JSON_KEY = "datasetName";
	public static final String DISPLAY_ID_JSON_KEY = "displayId";
	public static final String ENTITY_ID_JSON_KEY = "entityId";
	public static final String LONGITUDE_JSON_KEY = "longitude";
	public static final String LATITUDE_JSON_KEY = "latitude";
	public static final String SORT_ORDER_JSON_KEY = "sortOrder";
	public static final String MAX_RESULTS_JSON_KEY = "maxResults";
	public static final String TEMPORAL_FILTER_JSON_KEY = "temporalFilter";
	public static final String SPATIAL_FILTER_JSON_KEY = "spatialFilter";
	public static final String START_DATE_JSON_KEY = "startDate";
	public static final String END_DATE_JSON_KEY = "endDate";
	public static final String DATE_FIELD_JSON_KEY = "dateField";
	public static final String UPPER_RIGHT_JSON_KEY = "upperRight";
	public static final String LOWER_LEFT_JSON_KEY = "lowerLeft";
	public static final String FILTER_TYPE_JSON_KEY = "filterType";
	public static final String DATA_JSON_KEY = "data";
	public static final String PRODUCTS_JSON_KEY = "products";
	public static final String ENTITY_IDS_JSON_KEY = "entityIds";
	public static final String EROS_JSON_VALUE = "EROS";
	public static final String ASC_JSON_VALUE = "ASC";
	public static final String SEARCH_DATE_JSON_VALUE = "search_date";
	public static final String MBR_JSON_VALUE = "mbr";
	public static final String RESULTS_JSON_KEY = "results";
	public static final String ACQUISITION_DATE_JSON_KEY = "acquisitionDate";
    public static final String _DATASET_NAME_JSON_KEY = "dataset_name";
    public static final String SUMMARY_JSON_KEY = "summary";
    public static final String DATA_ACCESS_URL_JSON_KEY = "dataAccessUrl";
    public static final String URL_JSON_KEY = "url";

	// Metadata constants
	public static final String METADATA_SCENE_MTL_FILE_PATH = "scene_mtl_file_path";
	public static final String METADATA_SCENE_GCP_FILE_PATH = "scene_gcp_file_path";
	public static final String METADATA_SCENE_README_FILE_PATH = "scene_readme_file_path";
	public static final String METADATA_SCENE_STATION_FILE_PATH = "scene_station_file_path";

	// Submission constants
	public static final String DATASET_LT5_TYPE = "landsat_5";
	public static final String DATASET_LE7_TYPE = "landsat_7";
	public static final String DATASET_LC8_TYPE = "landsat_8";

	// Dataset constants
	public static final String LANDSAT_5_PREFIX = "LT5";
	public static final String LANDSAT_7_PREFIX = "LE7";
	public static final String LANDSAT_8_PREFIX = "LC8";
	public static final String LANDSAT_5_DATASET = "LANDSAT_TM_C1";
	public static final String LANDSAT_7_DATASET = "LANDSAT_ETM_C1";
	public static final String LANDSAT_8_DATASET = "LANDSAT_8_C1";

	// USGS constants
	public static final String USGS_LOGIN_URL = "usgs_login_url";
	public static final String USGS_JSON_URL = "usgs_json_url";
	public static final String USGS_USERNAME = "usgs_username";
	public static final String USGS_PASSWORD = "usgs_password";
	public static final String USGS_API_KEY_PERIOD = "usgs_api_key_period";

	// NOAA constants
	public static final String NOAA_FTP_SERVER_URL = "noaa_ftp_server_url";

	// Fix URLs for Elevation and Shapefiles
	public static final String ELEVATION_ACQUIRE_URL = "elevation_acquire_url";
	public static final String SHAPEFILE_ACQUIRE_URL = "shapefile_acquire_url";

	// Properties file constants
	public static final String POSSIBLE_STATIONS_FILE_PATH = "src/main/resources/possible_stations";
	public static final String TILES_COORDINATES_FILE_PATH = "config/WRScornerPoints.csv";
	public static final String GET_STATIONS_SCRIPT_PATH = "/home/saps/get-station-data.sh";

	public static final String SAPS_RESULTS_PATH = "sebal_results_local_path";
	public static final String SAPS_METADATA_PATH = "saps_metadata_path";

	// Timeout conditions in download operation
	public static final String CONNECTION_TIMEOUT = "download_connection_timeout";
	public static final String READ_TIMEOUT = "download_read_timeout";
}
