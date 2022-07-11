package com.simplilearn.project.app.sportyshoesecommerceapp.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUploadUtil {

    private static String pathToResources = "src/main/resources/static/uploads";

     
    public static String saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {


        File file = new File(pathToResources);

        String absolutePath = file.getAbsolutePath();
        Path uploadPath = Paths.get(absolutePath);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
         
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            return uploadDir + "" + fileName;
        } catch (IOException ioe) {        
            throw new IOException("Could not save image file: " + fileName, ioe);
        }      
    }
}