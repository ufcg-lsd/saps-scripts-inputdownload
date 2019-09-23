package utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.json.JSONObject;

public class TestStringUtil {

	private String strTest;

	@Before
	public void setUp() {
		JSONObject jjson = new JSONObject(
				"{\"acquisitionDate\":\"1985-01-27\",\"startTime\":\"1985-01-27\",\"endTime\":\"1985-01-27\",\"spatialFootprint\":{\"type\":\"Polygon\",\"coordinates\":[[[-37.76143,-7.90583],[-36.10081,-8.14731],[-35.76158,-6.55574],[-37.41655,-6.31507],[-37.76143,-7.90583]]]},\"sceneBounds\":\"-37.76143,-8.14731,-35.76158,-6.31507\",\"browseUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/browse\\/tm\\/215\\/65\\/1985\\/LT05_L1GS_215065_19850127_20170219_01_T2_REFL.jpg\",\"dataAccessUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/order\\/process?dataset_name=LANDSAT_TM_C1&ordered=LT52150651985027CUB00&node=INVSVC\",\"downloadUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/download\\/external\\/options\\/LANDSAT_TM_C1\\/LT52150651985027CUB00\\/INVSVC\\/\",\"entityId\":\"LT52150651985027CUB00\",\"displayId\":\"LT05_L1GS_215065_19850127_20170219_01_T2\",\"metadataUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/metadata\\/xml\\/12266\\/LT52150651985027CUB00\\/\",\"fgdcMetadataUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/fgdc\\/12266\\/LT52150651985027CUB00\\/save_xml\",\"modifiedDate\":\"2017-02-19\",\"orderUrl\":\"https:\\/\\/earthexplorer.usgs.gov\\/order\\/process?dataset_name=LANDSAT_TM_C1&ordered=LT52150651985027CUB00&node=INVSVC\",\"bulkOrdered\":false,\"ordered\":false,\"summary\":\"Entity ID: LT05_L1GS_215065_19850127_20170219_01_T2, Acquisition Date: 27-JAN-85, Path: 215, Row: 65\"}");
		this.strTest = jjson.getString("summary");
	}

	@Test
	public void testGetStringInsidePatterns() {
		String expected = "215";
		String actual = StringUtil.getStringInsidePatterns(this.strTest, "Path: ", ", ");
		Assert.assertEquals(expected, actual);

		expected = "65";
		actual = StringUtil.getStringInsidePatterns(this.strTest, "Row: ", "");
		Assert.assertEquals(expected, actual);
		
		String text = "startTime";
		Assert.assertNull(StringUtil.getStringInsidePatterns(text, "end", "Time"));
		Assert.assertNull(StringUtil.getStringInsidePatterns(text, "start", "TTime"));
	}
	
}
