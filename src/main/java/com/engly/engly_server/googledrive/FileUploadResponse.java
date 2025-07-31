package com.engly.engly_server.googledrive;


public record FileUploadResponse(int status, String imageId, String message, String url) {

}
