package com.hexagonal.application.service.command.admin.user;

import com.hexagonal.application.common.UseCase;
import com.hexagonal.application.port.in.admin.user.HandleSupportIssueUseCase;
import com.hexagonal.application.port.out.SupportTicketRepositoryPort;
import com.hexagonal.application.dto.SupportIssueCommand;

@UseCase
public class HandleSupportIssueService implements HandleSupportIssueUseCase {
    private final SupportTicketRepositoryPort supportRepository;

    public HandleSupportIssueService(SupportTicketRepositoryPort supportRepository) {
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

