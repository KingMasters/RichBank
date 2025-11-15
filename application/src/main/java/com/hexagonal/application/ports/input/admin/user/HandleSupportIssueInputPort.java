package com.hexagonal.application.ports.input.admin.user;

import com.hexagonal.application.usecases.admin.user.HandleSupportIssueUseCase;
import com.hexagonal.application.ports.output.SupportTicketRepositoryOutputPort;
import com.hexagonal.application.dto.SupportIssueCommand;

public class HandleSupportIssueInputPort implements HandleSupportIssueUseCase {
    private final SupportTicketRepositoryOutputPort supportRepository;

    public HandleSupportIssueInputPort(SupportTicketRepositoryOutputPort supportRepository) {
        this.supportRepository = supportRepository;
    }

    @Override
    public void execute(SupportIssueCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("SupportIssueCommand cannot be null");
        }
        supportRepository.createTicket(command.getCustomerId(), command.getSubject(), command.getDescription());
    }
}

