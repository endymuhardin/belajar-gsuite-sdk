package com.muhardin.endy.belajar.gsuite.service;

import com.google.api.services.directory.Directory;
import com.google.api.services.directory.model.User;
import com.google.api.services.directory.model.UserName;
import com.google.api.services.directory.model.Users;
import com.muhardin.endy.belajar.gsuite.dto.CreateUserResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service @Slf4j
public class GSuiteAdminService {

    private static final String EMAIL_DOMAIN_SUFFIX = "student.tazkia.ac.id";
    private static final String STUDENT_ORG_UNIT_PATH = "/Students";
    private static final Integer PASSWORD_LENGTH = 12;

    @Autowired
    private Directory gsuiteDirectoryService;

    public Users listUsers(Integer maxResults) throws IOException {
        return gsuiteDirectoryService.users()
                .list()
                .setDomain(EMAIL_DOMAIN_SUFFIX)
                .setMaxResults(maxResults)
                .execute();
    }

    public CreateUserResult createStudentUser(String firstname, String lastname, String username) {
        CreateUserResult result = new CreateUserResult();

        try {
            String generatedEmail = username + "@" + EMAIL_DOMAIN_SUFFIX;
            String generatedPassword = RandomStringUtils.randomAlphanumeric(PASSWORD_LENGTH);

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
