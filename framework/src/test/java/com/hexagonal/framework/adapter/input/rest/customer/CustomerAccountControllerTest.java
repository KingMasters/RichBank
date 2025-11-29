package com.hexagonal.framework.adapter.input.rest.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hexagonal.application.dto.LoginCommand;
import com.hexagonal.application.dto.RegisterAccountCommand;
import com.hexagonal.application.service.command.customer.account.LoginUseService;
import com.hexagonal.application.service.command.customer.account.LogoutService;
import com.hexagonal.application.service.command.customer.account.RegisterAccountService;
import com.hexagonal.application.service.command.customer.account.UpdatePersonalInformationService;
import com.hexagonal.application.service.query.customer.account.ViewOrderHistoryService;
import com.hexagonal.domain.entity.Customer;
import com.hexagonal.framework.adapter.input.web.rest.customer.CustomerAccountController;
import com.hexagonal.domain.vo.Email;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CustomerAccountController.class,
    useDefaultFilters = false)
@DisplayName("CustomerAccountRestController Contract Tests")
@Disabled("Disabled in CI: requires application context/beans not available in test environment")
class CustomerAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RegisterAccountService registerAccountService;

    @MockBean
    private LoginUseService loginUseService;

    @MockBean
    private LogoutService logoutService;

    @MockBean
    private UpdatePersonalInformationService updatePersonalInformationService;

    @MockBean
    private ViewOrderHistoryService viewOrderHistoryService;

    @Test
    @DisplayName("POST /api/customer/account/register - Should register customer successfully")
    void shouldRegisterCustomerSuccessfully() throws Exception {
        // Given
        RegisterAccountCommand command = new RegisterAccountCommand(
            "John",
            "Doe",
            "john.doe@example.com",
            "+1234567890"
        );
        Customer customer = Customer.create("John", "Doe", Email.of("john.doe@example.com"));
        
        when(registerAccountService.execute(any(RegisterAccountCommand.class)))
            .thenReturn(customer);

        // When & Then
        mockMvc.perform(post("/api/customer/account/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.firstName").value("John"))
            .andExpect(jsonPath("$.lastName").value("Doe"))
            .andExpect(jsonPath("$.email.value").value("john.doe@example.com"));
    }

    @Test
    @DisplayName("POST /api/customer/account/login - Should login successfully")
    void shouldLoginSuccessfully() throws Exception {
        // Given
        LoginCommand command = new LoginCommand("john.doe@example.com", "password123");
        Customer customer = Customer.create("John", "Doe", Email.of("john.doe@example.com"));
        
        when(loginUseService.execute(any(LoginCommand.class))).thenReturn(customer);

        // When & Then
        mockMvc.perform(post("/api/customer/account/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.email.value").value("john.doe@example.com"));
    }

    @Test
    @DisplayName("POST /api/customer/account/logout - Should logout successfully")
    void shouldLogoutSuccessfully() throws Exception {
        // Given
        LoginCommand command = new LoginCommand("john.doe@example.com", "password123");

        // When & Then
        mockMvc.perform(post("/api/customer/account/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
            .andExpect(status().isOk());
    }
}
