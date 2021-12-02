package com.kulaviak.logviewer;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;

public class ListDirectoryService {

    public void listDirectory(HttpServletResponse response, String directoryPath) throws IOException {
        var out = response.getWriter();
        response.setContentType("text/html");
        out.println(getHtmlStart());
        out.println(getHtmlTable(directoryPath));
        out.println(getHtmlEnd());
    }

    private String getHtmlTable(String path) {
        return
                """
<div class="container">
<div class="table-responsive">
<table class="table">
  <thead class="thead-light">
  <tr>
    <th width="33%">File name</th>
    <th width="33%" style=\"text-align: right\">File size</th>
    <th width="33%" style=\"text-align: right\">Modified at</th>
  </tr>
  </thead>
  <tbody>
 """
        + getHtmlRows(path) +
        """
  </tbody>
</table>                      
</div>          
</div>
""";
    }

    private static String getHtmlRows(String dirPath) {
        var files = getDirectoryFiles(dirPath);
        var sb = new StringBuilder();
        for(var file : files) {
            var row = getHtmlRow(file);
            sb.append(row);
        }
        return sb.toString();
    }

    private static File[] getDirectoryFiles(String dirPath) {
        var dir = new File(dirPath);
        var files = dir.listFiles();
        files = Arrays.stream(files).sorted(Comparator.comparing(File::getName)).toArray(File[]::new);
        return files;
    }

    private static String getHtmlRow(File file) {
        if (file.isFile()) {
            var link = "<a href=\"./" + file.getName() + "\">" + file.getName() + "</a>";
            var fileSizeStr = getFileSizeInKB(file) + " kB";
            var modifiedAtStr = formatModificationDate(getModificationDate(file));
            return getHtmlRowInternal(link, fileSizeStr, modifiedAtStr);
        } else {
            var fileLabel = file.getName() + " (directory) ";
            return getHtmlRowInternal(fileLabel, "", "");
        }
    }

    private static String getHtmlRowInternal(String fileLabel, String fileSizeStr, String modifiedAtStr) {
        return "<tr><td>" + fileLabel + "</td><td style=\"text-align: right\">" + fileSizeStr + "</td><td style=\"text-align: right\">" + modifiedAtStr + "</td></tr>";
    }

    private static String formatModificationDate(FileTime fileTime) {
        var localDateTime = fileTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        var formatter = DateTimeFormatter.ofPattern("d. M. yyyy HH:mm:ss");
        var ret = localDateTime.format(formatter);
        return ret;
    }

    private static FileTime getModificationDate(File file) {
        try {
            var attr = Files.readAttributes(Path.of(file.getAbsolutePath()), BasicFileAttributes.class);
            var ret = attr.lastModifiedTime();
            return ret;
        } catch (IOException e) {
            throw new RuntimeException("Getting modification data for file " + file.getName() + " failed.", e);
        }
    }

    private static long getFileSizeInKB(File file) {
        try {
            var bytes = Files.size(Path.of(file.getAbsolutePath()));
            var ret = bytes / 1024;
            return ret;
        } catch (IOException e) {
            throw new RuntimeException("Getting file size for file " + file.getName() + " failed.", e);
        }
    }

    private static String getHtmlEnd() {
        return """
</body>
</html>                
                """;
    }

    private static String getHtmlStart() {
        return """
<!doctype html>                        
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous">
  <style>
    body {
      margin: 10px;
    }
  </style>
  <title>Directory content</title>        
</head>                        
<body>
        """;
    }
}
