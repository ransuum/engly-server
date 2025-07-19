package com.engly.engly_server.googleDrive;


public record FileUploadResponse(int status, String imageId, String message, String url) {

}
