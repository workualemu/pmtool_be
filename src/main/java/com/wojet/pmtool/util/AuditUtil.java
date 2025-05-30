
package com.wojet.pmtool.util;

import com.wojet.pmtool.model.User;
import com.wojet.pmtool.model.audit.Auditable;
import org.springframework.data.domain.AuditorAware;

import java.time.LocalDateTime;

public class AuditUtil {

  public static <T extends Auditable> void applyAuditOnCreate(
      T entity, AuditorAware<User> auditorAware) {

    User currentUser = auditorAware.getCurrentAuditor()
        .orElseThrow(() -> new RuntimeException("No authenticated user"));

    entity.setCreatedBy(currentUser);
    entity.setUpdatedBy(currentUser);
    entity.setCreatedAt(LocalDateTime.now());
    entity.setUpdatedAt(LocalDateTime.now());
  }

  public static <T extends Auditable> void applyAuditOnUpdate(
      T entity, T existing, AuditorAware<User> auditorAware) {

    User currentUser = auditorAware.getCurrentAuditor()
        .orElseThrow(() -> new RuntimeException("No authenticated user"));

    entity.setCreatedBy(existing.getCreatedBy());
    entity.setCreatedAt(existing.getCreatedAt());
    entity.setUpdatedBy(currentUser);
    entity.setUpdatedAt(LocalDateTime.now());
  }
}
