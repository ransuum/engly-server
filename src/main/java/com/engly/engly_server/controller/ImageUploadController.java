package com.engly.engly_server.controller;

import com.engly.engly_server.googledrive.FileUploadResponse;
import com.engly.engly_server.googledrive.GoogleDriveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
@Slf4j
@Tag(name = "13. Image upload", description = "API for uploading images")
@SecurityRequirement(name = "bearerAuth")
public class ImageUploadController {

    private final GoogleDriveService driveService;

    public ImageUploadController(GoogleDriveService driveService) {
        this.driveService = driveService;
    }

    @Operation(
            summary = "Upload an image",
            description = """
                    Retrieves a FileUploadResponse with status, id, message and url to image
                    and then you need to mark the file upload through websocket
                    """
    )
    @ApiResponse(
            responseCode = "202",
            description = "Image uploaded successfully",
            content = @Content
    )
    @ApiResponse(responseCode = "400", description = "Bad Request. The file is not an image", content = @Content)
    @ApiResponse(responseCode = "409", description = "Cannot upload file to storage")
    @ApiResponse(responseCode = "403", description = "Forbidden. User does not have 'SCOPE_READ'.", content = @Content)
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    public ResponseEntity<FileUploadResponse> uploadImage(@RequestPart("image") MultipartFile image) {
        FileUploadResponse body = driveService.uploadImageToDrive(image);
        return ResponseEntity.status(body.status()).body(body);
    }

}
