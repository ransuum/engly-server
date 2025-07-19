package com.engly.engly_server.googleDrive;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.UserCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleDriveConfig {
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
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
    public UserCredentials userCredentials() throws Exception {
        // Build credentials directly with refresh token
        UserCredentials credentials = UserCredentials.newBuilder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRefreshToken(refreshToken)
                .build();

        // Refresh the access token
        credentials.refresh();
        return credentials;
    }

    @Bean
    public Drive createDriveService(UserCredentials userCredentials) throws Exception {
        return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, new HttpCredentialsAdapter(userCredentials)).setApplicationName("eng-ly-chat").build();
    }
}
