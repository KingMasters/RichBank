package com.hexagonal.application.dto;

import lombok.Value;
import com.hexagonal.domain.vo.ID;

@Value
public class SupportIssueCommand {
    ID customerId;
    String subject;
    String description;
    String contactEmail; // optional
}

