package com.hexagonal.application.port.in.admin.user;

import com.hexagonal.application.dto.SupportIssueCommand;

public interface HandleSupportIssueUseCase {
    void execute(SupportIssueCommand command);
}

