package com.hexagonal.application.usecase.admin.user;

import com.hexagonal.application.port.in.admin.user.HandleSupportIssueUseCase;
import com.hexagonal.application.port.out.SupportTicketRepositoryPort;
import com.hexagonal.application.dto.SupportIssueCommand;

public class HandleSupportIssueUseCaseHandler implements HandleSupportIssueUseCase {
    private final SupportTicketRepositoryPort supportRepository;

    public HandleSupportIssueUseCaseHandler(SupportTicketRepositoryPort supportRepository) {
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

