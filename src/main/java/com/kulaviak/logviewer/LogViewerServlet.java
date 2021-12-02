package com.kulaviak.logviewer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Servlet show content of directory or content of file. Intended for listing of log directory and showing log files.
 */
public class LogViewerServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            setNoCacheHeader(response);
            // configured in web.xml
            var directoryPath = getServletConfig().getInitParameter("directoryPath");
            if (directoryPath == null) {
                throw new RuntimeException("directoryPath is not set");
            }
            if (!Files.exists(Path.of(directoryPath))) {
                throw new RuntimeException("Directory " + directoryPath + " doesn't exist.");
            }

            var pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                new ListDirectoryService().listDirectory(response, directoryPath);
            } else {
                new ShowFileService().showFile(response, directoryPath, pathInfo);
            }
        } catch (Exception ex) {
            response.getWriter().println(exceptionToHtml(ex));
        }
    }

    private static String exceptionToHtml(Exception ex) {
        var stringWriter = new StringWriter();
        var printWriter = new PrintWriter(stringWriter);
        ex.printStackTrace(printWriter);
        var stackTraceStr = stringWriter.toString();
        var ret = "<pre>" + stackTraceStr + "</pre>";
        return ret;
    }

    // set Cache-Control as no-store so browser will always ask server for new data, so it will always show current logs or content of log
    private static void setNoCacheHeader(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store, must-revalidate");
        response.setHeader("Expires", "0");
    }
}
