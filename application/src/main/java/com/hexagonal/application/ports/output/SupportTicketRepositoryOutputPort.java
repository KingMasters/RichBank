package com.hexagonal.application.ports.output;

import com.hexagonal.vo.ID;

public interface SupportTicketRepositoryOutputPort {
    void createTicket(ID customerId, String subject, String description);
}

