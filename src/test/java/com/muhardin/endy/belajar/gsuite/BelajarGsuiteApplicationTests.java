package com.muhardin.endy.belajar.gsuite;

import com.google.api.services.directory.model.User;
import com.google.api.services.directory.model.Users;
import com.muhardin.endy.belajar.gsuite.dto.CreateUserResult;
import com.muhardin.endy.belajar.gsuite.service.GSuiteAdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BelajarGsuiteApplicationTests {

	@Autowired private GSuiteAdminService gSuiteAdminService;

	@Test
	void testListUsers() throws Exception {
		Users users = gSuiteAdminService.listUsers();
		for (User u : users.getUsers()) {
			System.out.println("Customer ID : " + u.getCustomerId());
			System.out.println("Full name : " + u.getName().getFullName());
			System.out.println("Aliases : " + u.getAliases());
			System.out.println("Primary Email : " + u.getPrimaryEmail());
			System.out.println("Emails : " + u.getEmails());
			System.out.println("Organizations : " + u.getOrganizations());
			System.out.println("Org Unit Path : " + u.getOrgUnitPath());
		}
	}

	@Test
	void testCreateUser() {
		String username = "12345.adminsdktestuser";
		String firstname = "12345";
		String lastname = "Admin SDK Test User";
		CreateUserResult result = gSuiteAdminService.createStudentUser(firstname, lastname, username);
		System.out.println(result);
	}
}
