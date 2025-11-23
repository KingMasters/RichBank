package com.hexagonal.application.port.out;

import com.hexagonal.vo.ID;

public interface SupportTicketRepositoryPort {
    void createTicket(ID customerId, String subject, String description);
}

