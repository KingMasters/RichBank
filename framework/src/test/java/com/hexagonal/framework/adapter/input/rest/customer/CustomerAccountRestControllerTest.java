package com.hexagonal.framework.adapter.input.rest.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hexagonal.application.dto.LoginCommand;
import com.hexagonal.application.dto.RegisterAccountCommand;
import com.hexagonal.application.ports.input.customer.account.LoginInputPort;
import com.hexagonal.application.ports.input.customer.account.LogoutInputPort;
import com.hexagonal.application.ports.input.customer.account.RegisterAccountInputPort;
import com.hexagonal.application.ports.input.customer.account.UpdatePersonalInformationInputPort;
import com.hexagonal.application.ports.input.customer.account.ViewOrderHistoryInputPort;
import com.hexagonal.entity.Customer;
import com.hexagonal.vo.Email;
import org.junit.jupiter.api.DisplayName;
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

@WebMvcTest(CustomerAccountRestController.class)
@DisplayName("CustomerAccountRestController Contract Tests")
class CustomerAccountRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RegisterAccountInputPort registerAccountInputPort;

    @MockBean
    private LoginInputPort loginInputPort;

    @MockBean
    private LogoutInputPort logoutInputPort;

    @MockBean
    private UpdatePersonalInformationInputPort updatePersonalInformationInputPort;

    @MockBean
    private ViewOrderHistoryInputPort viewOrderHistoryInputPort;

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
        
        when(registerAccountInputPort.execute(any(RegisterAccountCommand.class)))
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
        
        when(loginInputPort.execute(any(LoginCommand.class))).thenReturn(customer);

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

