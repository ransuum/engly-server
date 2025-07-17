package com.engly.engly_server.googledrive;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.UserCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.InputStreamReader;

@Configuration
public class GoogleDriveConfig {
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    @Value("classpath:googleDriveCreds.json")
    Resource resourceFile;
    @Value("${google.drive.refreshToken}")
    String refreshToken;

    /**
     * Authorizes the installed application to access user's protected data.
     */
    @Bean
    public UserCredentials userCredentials() throws Exception {
        // Load client secrets.
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JSON_FACTORY, new InputStreamReader(resourceFile.getInputStream()));


        // Build credentials directly with refresh token
        UserCredentials credentials = UserCredentials.newBuilder()
                .setClientId(clientSecrets.getWeb().getClientId())
                .setClientSecret(clientSecrets.getWeb().getClientSecret())
                .setRefreshToken(refreshToken)
                .build();

        // Refresh the access token
        credentials.refresh();

        System.out.println("Access Token: " + credentials.getAccessToken().getTokenValue());
        return credentials;
    }

    @Bean
    public Drive createDriveService(UserCredentials userCredentials) throws Exception {
        return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, new HttpCredentialsAdapter(userCredentials)).setApplicationName("eng-ly-chat").build();
    }
}
