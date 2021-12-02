package com.kulaviak.logviewer;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class ShowFileService {

    private final int BUFFER_SIZE = 1048;

    public void showFile(HttpServletResponse response, String directoryPath, String fileName) throws IOException {
        var out = response.getOutputStream();
        response.setContentType("text/plain");
        var file = new File(directoryPath, fileName);
        try(var in = new FileInputStream(file)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int numBytesRead;
            while ((numBytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, numBytesRead);
            }
        } catch (Exception e) {
            throw new RuntimeException("Opening file " + fileName + " failed.");
        }
    }
}
