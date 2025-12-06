package com.engly.engly_server.googledrive;

import com.engly.engly_server.utils.GoogleDriveUtility;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.UserCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Slf4j
@Configuration
public class GoogleDriveConfig {
    public static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    @Value("${google.drive.refreshToken}")
    String refreshToken;
    @Value("${google.drive.clientSecret}")
    String clientSecret;
    @Value("${google.drive.clientId}")
    String clientId;

    /**
     * Authorizes the installed application to access user's protected data.
     */
    @Bean
    public UserCredentials userCredentials() {
        try {
            // Build credentials directly with refresh token
            UserCredentials credentials = UserCredentials.newBuilder()
                    .setClientId(clientId)
                    .setClientSecret(clientSecret)
                    .setRefreshToken(refreshToken)
                    .build();

            // Refresh the access token
            credentials.refresh();
            return credentials;
        } catch (Exception e) {
            log.error("Cannot create UserCredentials: {}", e.getMessage());
            return GoogleDriveUtility.getInstance();
        }
    }

    @Bean
    public Drive createDriveService(UserCredentials userCredentials) throws GeneralSecurityException, IOException {
        try {
            return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY, new HttpCredentialsAdapter(userCredentials)).setApplicationName("eng-ly-chat").build();
        } catch (Exception e) {
            log.error("Cannot create service for google drive: {}", e.getMessage());
            return GoogleDriveUtility.createDriveServiceDefault();
        }
    }
}
