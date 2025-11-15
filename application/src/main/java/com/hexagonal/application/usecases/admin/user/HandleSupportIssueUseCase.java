package com.hexagonal.application.usecases.admin.user;

import com.hexagonal.application.dto.SupportIssueCommand;

public interface HandleSupportIssueUseCase {
    void execute(SupportIssueCommand command);
}

