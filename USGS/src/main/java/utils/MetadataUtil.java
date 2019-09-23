package utils;

import java.io.File;
import java.io.IOException;

public interface MetadataUtil {

	public void writeMetadata(String inputDirPath, File metadataFile) throws IOException;

}
