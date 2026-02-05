package com.bena.api.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@Slf4j
public class FirebaseConfig {

    @Value("${firebase.credentials.path:}")
    private String firebaseCredentialsPath;

    @PostConstruct
    public void initialize() {
        if (FirebaseApp.getApps() != null && !FirebaseApp.getApps().isEmpty()) {
            return;
        }

        String resolvedPath = firebaseCredentialsPath;
        if (resolvedPath == null || resolvedPath.isBlank()) {
            resolvedPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
        }
        if (resolvedPath == null || resolvedPath.isBlank()) {
            resolvedPath = System.getenv("FIREBASE_CREDENTIALS_PATH");
        }

        if (resolvedPath == null || resolvedPath.isBlank()) {
            Path localSecrets = Paths.get("secrets", "firebase-admin.json");
            if (Files.exists(localSecrets)) {
                resolvedPath = localSecrets.toAbsolutePath().toString();
            }
        }

        if (resolvedPath == null || resolvedPath.isBlank()) {
            throw new IllegalStateException("Firebase Admin credentials are required but were not provided");
        }

        Path credentialsPath = Paths.get(resolvedPath);
        if (!Files.exists(credentialsPath)) {
            throw new IllegalStateException("Firebase Admin credentials file not found at: " + credentialsPath.toAbsolutePath());
        }

        try (FileInputStream serviceAccount = new FileInputStream(credentialsPath.toFile())) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
            log.info("Firebase initialized successfully");
        } catch (Exception e) {
            log.error("Firebase initialization failed", e);
            throw new IllegalStateException("Firebase Admin initialization failed", e);
        }
    }
}
