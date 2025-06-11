package com.wojet.pmtool.payload;

import java.time.LocalDateTime;

import com.wojet.pmtool.payload.audit.AuditableDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO extends AuditableDTO {

    private Long clientId;
    private String clientName;
    private String description;
}
