package com.bloemist.configuration;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class GoogleConfiguration {

  private static final String TOKENS_DIRECTORY_PATH = ".tokens";
  private static final List<String> SCOPES =
      List.of(DriveScopes.DRIVE, DriveScopes.DRIVE_FILE);
  public static final String OFFLINE = "offline";
  public static final int PORT = 8888;
  public static final String BLOEMIST = "BloemistDesktop";
  public static final String APPDATA = String
      .join("\\", System.getenv("APPDATA"), TOKENS_DIRECTORY_PATH);

  @Value("${google.credential.path}")
  private String clientSecret;

  @Value("${application.user}")
  private String userName;

  @Bean
  public NetHttpTransport getHttpTransport() throws GeneralSecurityException, IOException {
    return GoogleNetHttpTransport.newTrustedTransport();
  }

  @Bean
  public GoogleClientSecrets getGoogleGoogleClientSecrets() throws IOException {

    return GoogleClientSecrets.load(GsonFactory.getDefaultInstance(),
        new InputStreamReader(new ClassPathResource(clientSecret).getInputStream()));
  }

  @Bean
  @Primary
  Credential getGoogleCredential(final NetHttpTransport httpTransport,
      GoogleClientSecrets googleClientSecrets) throws IOException {

    GoogleAuthorizationCodeFlow flow = getGoogleAuthorizationCodeFlow(
        httpTransport, googleClientSecrets);

    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(PORT).build();

    Credential credential = getAuthorize(flow, receiver);

    if (Objects.isNull(credential.getExpiresInSeconds()) ||
        credential.getExpiresInSeconds() < 200) {
      try {
        credential.refreshToken();
      } catch (TokenResponseException ex) {
        Files.delete(Path.of(String.join("/", APPDATA, "StoredCredential")));
        flow = getGoogleAuthorizationCodeFlow(httpTransport, googleClientSecrets);
        credential = getAuthorize(flow, receiver);
      }
    }

    return credential;
  }

  @Bean
  Drive getGoogleDrive(final NetHttpTransport httpTransport, Credential googleCredential) {
    return new Drive.Builder(httpTransport, GsonFactory.getDefaultInstance(), googleCredential)
        .setApplicationName(BLOEMIST)
        .build();
  }

  private Credential getAuthorize(GoogleAuthorizationCodeFlow flow, LocalServerReceiver receiver)
      throws IOException {
    return new AuthorizationCodeInstalledApp(flow, receiver)
        .authorize(userName);
  }

  private static GoogleAuthorizationCodeFlow getGoogleAuthorizationCodeFlow(
      NetHttpTransport httpTransport, GoogleClientSecrets googleClientSecrets) throws IOException {

    return new GoogleAuthorizationCodeFlow.Builder(
        httpTransport, GsonFactory.getDefaultInstance(), googleClientSecrets, SCOPES)
        .setDataStoreFactory(new FileDataStoreFactory(new File(APPDATA)))
        .setAccessType(OFFLINE)
        .setApprovalPrompt("force")
        .build();
  }
}
