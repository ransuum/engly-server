package com.engly.engly_server.googledrive;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class GoogleDriveService {


    private final Drive drive;


    public FileUploadResponse uploadImageToDrive(MultipartFile multipartFile) {
        try {
            String folderId = "19bFkshK4Ts-cUxHlWJhoNrPYDrbf9a6R";
            File fileMetaData = new File();
            fileMetaData.setName(multipartFile.getName());
            fileMetaData.setMimeType(multipartFile.getContentType());
            fileMetaData.setParents(Collections.singletonList(folderId));

            InputStreamContent mediaContent = new InputStreamContent(multipartFile.getContentType(), multipartFile.getInputStream());

            File uploadedFile = drive.files().create(fileMetaData, mediaContent)
                    .setFields("id,name,webViewLink") // Specify fields to retrieve in the response
                    .execute();
            Object webViewLink = uploadedFile.get("webViewLink");
            return new FileUploadResponse(200, "Image Successfully Uploaded To Drive", webViewLink.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new FileUploadResponse(500, "Error during multipartFile upload", "");
        }
    }

}
