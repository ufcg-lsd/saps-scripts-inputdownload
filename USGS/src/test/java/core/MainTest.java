package core;

import main.USGSController;
import main.Main;
import model.ImageTask;
import org.apache.commons.io.FileUtils;
import org.junit.*;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static org.mockito.Mockito.*;

public class MainTest {

    private String sapsResultsPath;
    private String sapsMetadataPath;

    @Before
    public void setUp(){
        sapsResultsPath = "/tmp/results";
        sapsMetadataPath = "/tmp/metadata";
    }

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @After
    public void cleansDataDir() throws IOException {
        File f = new File(sapsResultsPath + "/data");
        FileUtils.deleteDirectory(f);
    }

    @Test
    public void singleArgTest() throws Exception {
        exit.expectSystemExitWithStatus(6);
        String[] args = {"a"};
        Main.main(args);
    }

    @Test
    public void testMalFormedURLDownload() throws Exception {
        exit.expectSystemExitWithStatus(3);
		USGSController USGSController = new USGSController("landsat_5", "fake-region", "1984-01-01",
				sapsResultsPath, sapsMetadataPath);
        USGSController.startDownload();
    }

    @Test
    public void testUnknownImageTaskName() throws Exception {
        exit.expectSystemExitWithStatus(3);

		USGSController USGSController = new USGSController("landsat_5", "215065", "1984-01-27",
				sapsResultsPath, sapsMetadataPath);
        Properties properties = main.USGSController.loadProperties();

        USGSNasaRepository usgsNasaRepository = spy(new USGSNasaRepository(properties));
        USGSController.setUsgsRepository(usgsNasaRepository);

        doThrow(new IOException()).when(usgsNasaRepository).downloadImage(any(ImageTask.class));

        USGSController.startDownload();
    }
}
