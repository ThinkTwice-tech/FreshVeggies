/*
package dev.ckdevops.freshgreenery;

import com.google.auth.oauth2.GoogleCredentials;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AuthTokenGenerator {
    public static String getAuthToken() {
        try {
            // Load service account credentials from JSON file
            GoogleCredentials credentials = GoogleCredentials
                    .fromStream(new FileInputStream("C:/Android Projects/FreshGreeneryApp-master/FreshGreeneryApp-master/FreshGreenery/service-account-file.json"))
                    .createScoped("https://www.googleapis.com/auth/cloud-platform");

            // Refresh the credentials if expired
            credentials.refreshIfExpired();

            // Get the OAuth token
            String accessToken = credentials.getAccessToken().getTokenValue();
            System.out.println("Access Token: " + accessToken);

            return accessToken;

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to generate OAuth token");
        }
    }
}
*/
