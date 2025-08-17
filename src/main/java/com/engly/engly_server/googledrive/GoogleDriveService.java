package com.engly.engly_server.googledrive;

import com.google.api.client.http.HttpStatusCodes;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleDriveService {
    private final Drive drive;

    @Value("${google.drive.folderId}")
    private String folderId;

    private static final String TYPE_LINK = "thumbnailLink";

    public FileUploadResponse uploadImageToDrive(MultipartFile multipartFile) {
        if (!ifFileIsImage(multipartFile)) {
            return new FileUploadResponse(HttpStatusCodes.STATUS_CODE_BAD_REQUEST, "", "We don't support file upload", "");
        }

        try {
            File fileMetaData = new File();
            fileMetaData.setName(multipartFile.getName());
            fileMetaData.setMimeType(multipartFile.getContentType());
            fileMetaData.setParents(Collections.singletonList(folderId));


            InputStreamContent mediaContent = new InputStreamContent(multipartFile.getContentType(), multipartFile.getInputStream());

            File uploadedFile = drive.files().create(fileMetaData, mediaContent)
                    .setFields("id,name,thumbnailLink") // Specify fields to retrieve in the response
                    .execute();
            Object thumbnailLink = uploadedFile.get(TYPE_LINK);
            String fileId = uploadedFile.getId();
            setPermissions(fileId);
            return new FileUploadResponse(HttpStatusCodes.STATUS_CODE_ACCEPTED, fileId, "Image Successfully Uploaded To Drive", thumbnailLink.toString());
        } catch (Exception e) {
            log.error("GoogleDriveService: error during file upload description", e);
            return new FileUploadResponse(HttpStatusCodes.STATUS_CODE_CONFLICT, "", "Error during multipartFile upload", "");
        }
    }

    private void setPermissions(String fileId) throws IOException {
        // Set file permission to anyone with the link can view
        Permission permission = new Permission()
                .setType("anyone")
                .setRole("reader");
        drive.permissions().create(fileId, permission).execute();
    }

    private boolean ifFileIsImage(MultipartFile multipartFile) {
        String contentType = multipartFile.getContentType();

        if (contentType != null && contentType.startsWith("image/")) {
            return true;
        }
        log.info("GoogleDriveService: file is not image");
        return false;
    }

    public String getImageThumbnailLink(String imageId) {
        if (imageId == null) {
            return null;
        }
        try {
            File execute = drive.files().get(imageId).setFields(TYPE_LINK).execute();
            return execute.get(TYPE_LINK).toString();
        } catch (Exception e) {
            log.error("GoogleDriveService: error during file check if uploaded maybe file is not uploaded yet", e);
            return null;
        }
    }
}
