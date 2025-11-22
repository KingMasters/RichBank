package com.hexagonal.application.port.in.admin.user;

import com.hexagonal.application.dto.SupportIssueCommand;

public interface HandleSupportIssueInputPort {
    void execute(SupportIssueCommand command);
}

