package com.wojet.pmtool.util;

import com.wojet.pmtool.model.audit.Auditable;
import org.springframework.data.domain.AuditorAware;

import java.time.Clock;
import java.time.Instant;

/**
 * Manual auditing helpers.
 * NOTE: If you use Spring Data JPA auditing
 * (@CreatedDate/@LastModifiedDate/@CreatedBy/@LastModifiedBy)
 * you typically don't need to call these; the framework will populate fields on
 * save/flush.
 * Keep these only for places where you build detached entities yourself
 * (imports, batch, etc.).
 */
public final class AuditUtil {

  // Pluggable clock for tests
  private static Clock clock = Clock.systemUTC();

  public static void setClock(Clock newClock) {
    clock = newClock == null ? Clock.systemUTC() : newClock;
  }

  private AuditUtil() {
  }

  /** Apply created/updated fields on a brand-new entity. */
  public static <T extends Auditable> void applyAuditOnCreate(
      T entity, AuditorAware<Long> auditorAware) {

    Long userId = auditorAware.getCurrentAuditor()
        .orElseThrow(() -> new IllegalStateException("No authenticated user"));
    Instant now = Instant.now(clock);

    entity.setCreatedById(userId);
    entity.setUpdatedById(userId);

    // Timestamps (UTC instants)
    entity.setCreatedAt(now);
    entity.setUpdatedAt(now);
  }

  /** Preserve created*, update updated* for a modifying operation. */
  public static <T extends Auditable> void applyAuditOnUpdate(
      T target, T existing, AuditorAware<Long> auditorAware) {

    Long userId = auditorAware.getCurrentAuditor()
        .orElseThrow(() -> new IllegalStateException("No authenticated user"));
    Instant now = Instant.now(clock);

    // Keep original creator + createdAt from the persisted entity
    target.setCreatedById(existing.getCreatedById());
    target.setCreatedAt(existing.getCreatedAt());

    // Update modifier and timestamp
    target.setUpdatedById(userId);
    target.setUpdatedAt(now);
  }
}
