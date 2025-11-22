package com.hexagonal.application.usecase.admin.user;

import com.hexagonal.application.port.in.admin.user.HandleSupportIssueInputPort;
import com.hexagonal.application.port.out.SupportTicketRepositoryOutputPort;
import com.hexagonal.application.dto.SupportIssueCommand;

public class HandleSupportIssueUseCase implements HandleSupportIssueInputPort {
    private final SupportTicketRepositoryOutputPort supportRepository;

    public HandleSupportIssueUseCase(SupportTicketRepositoryOutputPort supportRepository) {
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

