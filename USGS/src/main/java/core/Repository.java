package core;

import model.ImageTask;

import java.io.IOException;

public interface Repository {

    /**
     *
     * @param imageData
     * @throws IOException
     */
    void downloadImage(final ImageTask imageData) throws IOException, Exception;

}