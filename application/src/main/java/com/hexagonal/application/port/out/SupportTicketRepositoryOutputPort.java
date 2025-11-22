package com.hexagonal.application.port.out;

import com.hexagonal.vo.ID;

public interface SupportTicketRepositoryOutputPort {
    void createTicket(ID customerId, String subject, String description);
}

