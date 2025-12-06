package com.engly.engly_server.utils;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.services.drive.Drive;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.UserCredentials;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static com.engly.engly_server.googledrive.GoogleDriveConfig.JSON_FACTORY;

public class GoogleDriveUtility {

    private GoogleDriveUtility() {
        // Private constructor to prevent instantiation
    }

    public static UserCredentials getInstance() {
        return UserCredentials.newBuilder()
                .setClientId("clientId")
                .setClientSecret("clientSecret")
                .setRefreshToken("refreshToken")
                .build();
    }

    public static Drive createDriveServiceDefault() throws GeneralSecurityException, IOException {
        return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY, new HttpCredentialsAdapter(getInstance())).setApplicationName("eng-ly-chat").build();
    }
}
