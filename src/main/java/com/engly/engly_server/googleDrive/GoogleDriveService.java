package com.engly.engly_server.googledrive;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleDriveService {
    private final Drive drive;
    @Value("${google.drive.folderId}")
    public String folderId;


    public FileUploadResponse uploadImageToDrive(MultipartFile multipartFile) {
        try {
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
            log.error("GoogleDriveService: error during file upload description", e);
            return new FileUploadResponse(500, "Error during multipartFile upload", "");
        }
    }
}
