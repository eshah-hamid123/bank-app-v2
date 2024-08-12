package com.assignment.BankingApp;

import com.assignment.BankingApp.account.AccountService;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BankingAppApplicationTests {

	private static final Logger logger = LoggerFactory.getLogger(BankingAppApplicationTests.class);
	@Autowired
	private MockMvc mockMvc;

	@Mock
	private AccountService accountService;

	@Order(1)
	@Test
	public void testCreateUserSuccess() throws Exception {
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v2/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"admin\",\"password\":\"Admin123*\"}"))
				.andExpect(status().isOk())
				.andReturn();

		String authToken = result.getResponse().getHeader("Authorization");
		logger.info("Authorization Header: {}", authToken);
		String userJson = "{\"username\":\"testUser\",\"password\":\"Test1234\",\"email\":\"testuser@example.com\",\"address\":\"Test Address\",\"balance\":1000,\"accountNumber\":\"1234567890\"}";

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v2/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(userJson)
						.with(csrf())
						.header("Authorization", authToken))
				.andExpect(status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$.username").value("testUser"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.email").value("testuser@example.com"));
	}

	@Order(2)
	@Test
	public void testCreateUserDuplicateUsernameFailure() throws Exception {
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v2/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"admin\",\"password\":\"Admin123*\"}"))
				.andExpect(status().isOk())
				.andReturn();

		String authToken = result.getResponse().getHeader("Authorization");

		String userJson = "{\"username\":\"testUser\",\"password\":\"Test1234\",\"email\":\"newemail@example.com\",\"address\":\"Test Address\",\"balance\":1000,\"accountNumber\":\"12345678\"}";

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v2/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(userJson)
						.with(csrf())
						.header("Authorization", authToken))
				.andExpect(status().isConflict())
				.andExpect(MockMvcResultMatchers.content().string("Username already exists"));
	}

	@Test
	public void testCreateUserInvalidInputFailure() throws Exception {
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v2/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"admin\",\"password\":\"Admin123*\"}"))
				.andExpect(status().isOk())
				.andReturn();

		String authToken = result.getResponse().getHeader("Authorization");
		String userJson = "{\"username\":\"\",\"password\":\"\",\"email\":\"invalidemail\",\"address\":\"\",\"balance\":1000,\"accountNumber\":\"12345678\"}";

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v2/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(userJson)
						.with(csrf())
						.header("Authorization", authToken))
				.andExpect(status().isBadRequest());
	}

	@Order(4)
	@Test
	public void testUpdateUserSuccess() throws Exception {
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v2/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"admin\",\"password\":\"Admin123*\"}"))
				.andExpect(status().isOk())
				.andReturn();

		String authToken = result.getResponse().getHeader("Authorization");

		String updatedUserJson = "{\"username\":\"updatedUser\",\"password\":\"Updated1234\",\"email\":\"updateduser@example.com\",\"address\":\"Updated Address\",\"balance\":2000,\"accountNumber\":\"87654321\"}";

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v2/users/2")
						.contentType(MediaType.APPLICATION_JSON)
						.content(updatedUserJson)
						.with(csrf())
						.header("Authorization", authToken))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.content().string("Information updated successfully"));
	}


	@Test
	public void testUpdateUserNotFoundFailure() throws Exception {
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v2/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"admin\",\"password\":\"Admin123*\"}"))
				.andExpect(status().isOk())
				.andReturn();

		String authToken = result.getResponse().getHeader("Authorization");

		String updatedUserJson = "{\"username\":\"updatedUser\",\"password\":\"Updated1234\",\"email\":\"updateduser@example.com\",\"address\":\"Updated Address\",\"balance\":2000,\"accountNumber\":\"87654321\"}";

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v2/users/999")
						.contentType(MediaType.APPLICATION_JSON)
						.content(updatedUserJson)
						.with(csrf())
						.header("Authorization", authToken))
				.andExpect(status().isNotFound());
	}

	@Order(8)
	@Test
	public void testDeleteUserSuccess() throws Exception {
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v2/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"admin\",\"password\":\"Admin123*\"}"))
				.andExpect(status().isOk())
				.andReturn();

		String authToken = result.getResponse().getHeader("Authorization");
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v2/users/2")
						.with(csrf())
						.header("Authorization", authToken))
				.andExpect(status().isNoContent());
	}

	@Test
	public void testDeleteUserNotFoundFailure() throws Exception {
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v2/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"admin\",\"password\":\"Admin123*\"}"))
				.andExpect(status().isOk())
				.andReturn();

		String authToken = result.getResponse().getHeader("Authorization");
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v2/users/999")
						.with(csrf())
						.header("Authorization", authToken))
				.andExpect(status().isInternalServerError())
				.andExpect(MockMvcResultMatchers.content().string("User with ID 999 not found"));
	}

	@Order(6)
	@Test
	public void testGetUserByIdSuccess() throws Exception {
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v2/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"admin\",\"password\":\"Admin123*\"}"))
				.andExpect(status().isOk())
				.andReturn();

		String authToken = result.getResponse().getHeader("Authorization");
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v2/users/2")
						.with(csrf())
						.header("Authorization", authToken))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.username").value("updatedUser"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.email").value("updateduser@example.com"));
	}


	@Order(7)
	@Test
	public void testGetAllAccountsSuccess() throws Exception {
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v2/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"admin\",\"password\":\"Admin123*\"}"))
				.andExpect(status().isOk())
				.andReturn();

		String authToken = result.getResponse().getHeader("Authorization");
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v2/users?page=0&size=10")
						.with(csrf())
						.header("Authorization", authToken))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray());
	}

	@Test
	public void testGetAllAccountsAccessDeniedFailure() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v2/users?page=0&size=10")
						.with(csrf()))
				.andExpect(status().isUnauthorized());
	}












//	@Order(2)
//	@Test
//	void testUpdateAccountSuccess() throws Exception {
//
//		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/v1/auth/login")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content("{\"username\":\"admin\",\"password\":\"Admin123*\"}"))
//				.andExpect(status().isOk())
//				.andReturn();
//
//		String responseBody = result.getResponse().getContentAsString();
//		String authToken = JsonPath.read(responseBody, "$.jwtToken");
//
//		mockMvc.perform(MockMvcRequestBuilders.put("/v1/accounts/edit-account/2")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content("{\"username\":\"testUser323\",\"password\":\"123456\",\"email\":\"saniaaa@example.com\",\"address\":\"lhr pak\",\"balance\":1000,\"accountNumber\":\"12567544\"}")
//						.with(csrf())
//						.header("Authorization", "Bearer " + authToken))
//				.andExpect(status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.username").value("testUser323"))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.email").value("saniaaa@example.com"));
//	}
//
//	@Order(3)
//	@Test
//	void testGetAllAccountsSuccess() throws Exception {
//
//		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/v1/auth/login")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content("{\"username\":\"admin\",\"password\":\"Admin123*\"}"))
//				.andExpect(status().isOk())
//				.andReturn();
//
//		String responseBody = result.getResponse().getContentAsString();
//		String authToken = JsonPath.read(responseBody, "$.jwtToken");
//
//		mockMvc.perform(MockMvcRequestBuilders.get("/v1/accounts/all-accounts")
//						.with(csrf())
//						.header("Authorization", "Bearer " + authToken))
//				.andExpect(status().isOk());
//	}
//
//	@Order(4)
//	@Test
//	void testGetAccountById() throws Exception {
//		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/v1/auth/login")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content("{\"username\":\"testUser323\",\"password\":\"123456\"}"))
//				.andExpect(status().isOk())
//				.andReturn();
//
//		String responseBody = result.getResponse().getContentAsString();
//		String authToken = JsonPath.read(responseBody, "$.jwtToken");
//		mockMvc.perform(MockMvcRequestBuilders.get("/v1/accounts/get-account/2")
//						.with(csrf())
//						.header("Authorization", "Bearer " + authToken))
//				.andExpect(status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.username").value("testUser323"));
//	}
//
//	@Order(8)
//	@Test
//	void testDeleteAccountSuccess() throws Exception {
//		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/v1/auth/login")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content("{\"username\":\"admin\",\"password\":\"Admin123*\"}"))
//				.andExpect(status().isOk())
//				.andReturn();
//
//		String responseBody = result.getResponse().getContentAsString();
//		String authToken = JsonPath.read(responseBody, "$.jwtToken");
//		mockMvc.perform(MockMvcRequestBuilders.delete("/v1/accounts/delete-account/2")
//						.with(csrf())
//						.header("Authorization", "Bearer " + authToken))
//				.andExpect(status().isNoContent());
//	}
//
//	@Order(5)
//	@Test
//	public void testCreateAccount2Success() throws Exception {
//
//		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/v1/auth/login")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content("{\"username\":\"admin\",\"password\":\"Admin123*\"}"))
//				.andExpect(status().isOk())
//				.andReturn();
//
//		String responseBody = result.getResponse().getContentAsString();
//		String authToken = JsonPath.read(responseBody, "$.jwtToken");
//		mockMvc.perform(MockMvcRequestBuilders.post("/v1/accounts/create-account")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content("{\"username\":\"test2\",\"password\":\"Test123*\",\"email\":\"stestuser263@example.com\",\"address\":\"lhr pak\",\"balance\":1000,\"accountNumber\":\"12345679\"}")
//						.with(csrf())
//						.header("Authorization", "Bearer " + authToken))
//				.andExpect(status().isCreated())
//
//				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.username").value("test2"))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.email").value("stestuser263@example.com"))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.address").value("lhr pak"))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.balance").value(1000))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.accountNumber").value("12345679"));
//	}
//
//	@Order(6)
//	@Test
//	public void testCreateTransaction() throws Exception {
//		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/v1/auth/login")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content("{\"username\":\"test2\",\"password\":\"Test123*\"}"))
//				.andExpect(status().isOk())
//				.andReturn();
//
//		String responseBody = result.getResponse().getContentAsString();
//		String authToken = JsonPath.read(responseBody, "$.jwtToken");
//		mockMvc.perform(MockMvcRequestBuilders.post("/v1/transactions/transfer-money")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content("{\"recieverAccountNumber\":\"12345678\",\"amount\":50, \"description\":\"testing\"}")
//						.with(csrf())
//						.header("Authorization", "Bearer " + authToken))
//				.andExpect(status().isCreated())
//
//				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE));
//	}
//
//
//	@Order(7)
//	@Test
//	void testGetAllTransactionsSuccess() throws Exception {
//
//		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/v1/auth/login")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content("{\"username\":\"admin\",\"password\":\"Admin123*\"}"))
//				.andExpect(status().isOk())
//				.andReturn();
//
//		String responseBody = result.getResponse().getContentAsString();
//		String authToken = JsonPath.read(responseBody, "$.jwtToken");
//
//		mockMvc.perform(MockMvcRequestBuilders.get("/v1/transactions/all-transactions")
//						.with(csrf())
//						.header("Authorization", "Bearer " + authToken))
//				.andExpect(status().isOk());
//	}
//
//	@Test
//	public void testInvalidLogin() throws Exception {
//		mockMvc.perform(MockMvcRequestBuilders.post("/v1/auth/login")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content("{\"username\":\"invalidUser\",\"password\":\"wrongPassword\"}"))
//				.andExpect(status().isUnauthorized());
//	}
//
//	@Test
//	public void testCreateAccountFailure_MissingFields() throws Exception {
//		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/v1/auth/login")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content("{\"username\":\"admin\",\"password\":\"Admin123*\"}"))
//				.andExpect(status().isOk())
//				.andReturn();
//
//		String responseBody = result.getResponse().getContentAsString();
//		String authToken = JsonPath.read(responseBody, "$.jwtToken");
//
//		mockMvc.perform(MockMvcRequestBuilders.post("/v1/accounts/create-account")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content("{\"username\":\"\",\"password\":\"\",\"email\":\"\",\"address\":\"\",\"balance\":null,\"accountNumber\":\"\"}")
//						.with(csrf())
//						.header("Authorization", "Bearer " + authToken))
//				.andExpect(status().isInternalServerError());
//	}
//
//	@Test
//	public void testUpdateAccountFailure_NotFound() throws Exception {
//		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/v1/auth/login")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content("{\"username\":\"admin\",\"password\":\"Admin123*\"}"))
//				.andExpect(status().isOk())
//				.andReturn();
//
//		String responseBody = result.getResponse().getContentAsString();
//		String authToken = JsonPath.read(responseBody, "$.jwtToken");
//
//		mockMvc.perform(MockMvcRequestBuilders.put("/v1/accounts/edit-account/999")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content("{\"username\":\"testUser323\",\"password\":\"123456\",\"email\":\"saniaaa@example.com\",\"address\":\"lhr pak\",\"balance\":1000,\"accountNumber\":\"12567544\"}")
//						.with(csrf())
//						.header("Authorization", "Bearer " + authToken))
//				.andExpect(status().isNotFound());
//	}
//
//	@Test
//	public void testGetAccountByIdFailure_NotFound() throws Exception {
//		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/v1/auth/login")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content("{\"username\":\"admin\",\"password\":\"Admin123*\"}"))
//				.andExpect(status().isOk())
//				.andReturn();
//
//		String responseBody = result.getResponse().getContentAsString();
//		String authToken = JsonPath.read(responseBody, "$.jwtToken");
//
//		mockMvc.perform(MockMvcRequestBuilders.get("/v1/accounts/get-account/999")
//						.with(csrf())
//						.header("Authorization", "Bearer " + authToken))
//				.andExpect(status().isNotFound());
//	}
//
//	@Test
//	public void testDeleteAccountFailure_Unauthorized() throws Exception {
//		mockMvc.perform(MockMvcRequestBuilders.delete("/v1/accounts/delete-account/2")
//						.with(csrf()))
//				.andExpect(status().isUnauthorized());
//	}
//
//	@Order(8)
//	@Test
//	public void testCreateTransactionFailure_InsufficientBalance() throws Exception {
//		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/v1/auth/login")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content("{\"username\":\"test2\",\"password\":\"Test123*\"}"))
//				.andExpect(status().isOk())
//				.andReturn();
//
//		String responseBody = result.getResponse().getContentAsString();
//		String authToken = JsonPath.read(responseBody, "$.jwtToken");
//
//		mockMvc.perform(MockMvcRequestBuilders.post("/v1/transactions/transfer-money")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content("{\"recieverAccountNumber\":\"12345678\",\"amount\":100000, \"description\":\"testing\"}")
//						.with(csrf())
//						.header("Authorization", "Bearer " + authToken))
//				.andExpect(status().isBadRequest());
//	}

}
