package com.muhardin.endy.belajar.gsuite.service;

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
import com.google.api.services.directory.model.User;
import com.google.api.services.directory.model.UserName;
import com.google.api.services.directory.model.Users;
import com.muhardin.endy.belajar.gsuite.dto.CreateUserResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

@Service @Slf4j
public class GSuiteAdminService {
    private static final List<String> SCOPES = Arrays.asList(
            DirectoryScopes.ADMIN_DIRECTORY_USER,
            DirectoryScopes.ADMIN_DIRECTORY_ORGUNIT_READONLY);

    private static final String EMAIL_DOMAIN_SUFFIX = "student.tazkia.ac.id";
    private static final String STUDENT_ORG_UNIT_PATH = "/Students";

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${gsuite.folder}")
    private String dataStoreFolder;

    @Value("${gsuite.credential.file}")
    private String credentialFile;

    private Directory gsuiteDirectoryService;

    @PostConstruct
    public void inisialisasiOauth() throws Exception {
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

        gsuiteDirectoryService = new Directory.Builder(httpTransport, gsonFactory, credential)
                .setApplicationName(applicationName)
                .build();
    }

    public Users listUsers() throws IOException {
        return gsuiteDirectoryService.users()
                .list()
                .setDomain(EMAIL_DOMAIN_SUFFIX)
                .setMaxResults(1)
                .execute();
    }

    public CreateUserResult createStudentUser(String firstname, String lastname, String username) {
        CreateUserResult result = new CreateUserResult();

        try {
            String generatedEmail = username + "@" + EMAIL_DOMAIN_SUFFIX;
            String generatedPassword = "abcd1234";

            User newUser = new User();
            newUser.setOrgUnitPath(STUDENT_ORG_UNIT_PATH);
            newUser.setPrimaryEmail(generatedEmail);
            newUser.setName(new UserName()
                    .setGivenName(firstname)
                    .setFamilyName(lastname));
            newUser.setChangePasswordAtNextLogin(true);
            newUser.setPassword(generatedPassword);

            User createdUser = gsuiteDirectoryService.users()
                    .insert(newUser).execute();

            log.info("Success create new user {}", createdUser);

            result.setSuccess(true);
            result.setGeneratedEmail(generatedEmail);
            result.setGeneratedPassword(generatedPassword);
        } catch (Exception err) {
            result.setSuccess(false);
            result.setErrorMessage(err.getMessage());
            log.error(err.getMessage(), err);
        }

        return result;
    }
}
