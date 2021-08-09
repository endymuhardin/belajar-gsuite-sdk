package com.muhardin.endy.belajar.gsuite.config;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.directory.Directory;
import com.google.api.services.directory.DirectoryScopes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

@Configuration @Slf4j
public class GSuiteConfiguration {
    private static final List<String> SCOPES = Arrays.asList(
            DirectoryScopes.ADMIN_DIRECTORY_USER,
            DirectoryScopes.ADMIN_DIRECTORY_ORGUNIT_READONLY);

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${gsuite.folder}")
    private String dataStoreFolder;

    @Value("${gsuite.credential.file}")
    private String credentialFile;

    @Bean
    public Directory gsuiteDirectory()  throws Exception {
        GsonFactory gsonFactory = GsonFactory.getDefaultInstance();

        FileDataStoreFactory fileDataStoreFactory =
                new FileDataStoreFactory(new File(dataStoreFolder));

        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        String clientSecretFile = dataStoreFolder + File.separator + credentialFile;

        log.info("Loading client secret file : {}", clientSecretFile);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(gsonFactory,
                new InputStreamReader(new FileInputStream(clientSecretFile)));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, gsonFactory, clientSecrets, SCOPES)
                .setDataStoreFactory(fileDataStoreFactory)
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

        return new Directory.Builder(httpTransport, gsonFactory, credential)
                .setApplicationName(applicationName)
                .build();
    }
}
