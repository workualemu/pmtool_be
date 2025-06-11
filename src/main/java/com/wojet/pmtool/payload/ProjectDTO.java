package com.wojet.pmtool.payload;

import com.wojet.pmtool.payload.audit.AuditableDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO extends AuditableDTO {

    private Long id;
    private String title;
    private String description;
    private String startDate;
    private String endDate;
    private String status; // e.g., "In Progress", "Completed", "On Hold"
    private Long clientId;
    private String clientName;
}
