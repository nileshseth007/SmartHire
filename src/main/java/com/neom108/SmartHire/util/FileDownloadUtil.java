package com.neom108.SmartHire.util;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileDownloadUtil {
    private Path foundFile;

    public Resource getFileAsResource(String downloadDir, String filename) throws IOException {
        Path path = Paths.get(downloadDir);
        Files.list(path).forEach(file ->{
            if (file.getFileName().toString().startsWith(filename)){
                foundFile = file;
            }
        });

        if(foundFile!=null){
            return new UrlResource(foundFile.toUri());
        }
        return null;
    }
}
