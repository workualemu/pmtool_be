package com.wojet.pmtool.repository.specification;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import com.wojet.pmtool.model.Task;

import java.util.List;

public class TaskSpecifications {

  public static Specification<Task> byFilter(
      Long projectId,
      List<Integer> levels,
      List<Long> tagIds,
      List<String> statuses,
      List<String> priorities,
      String titleContains,
      Long assignedToId,
      Long reportedById
  ) {
    return (root, query, cb) -> {
      Predicate p = cb.equal(root.get("projectId"), projectId);

      if (levels != null && !levels.isEmpty()) {
        p = cb.and(p, root.get("level").in(levels));
      }
      if (statuses != null && !statuses.isEmpty()) {
        p = cb.and(p, root.get("status").in(statuses));
      }
      if (priorities != null && !priorities.isEmpty()) {
        p = cb.and(p, root.get("priority").in(priorities));
      }
      if (titleContains != null && !titleContains.isBlank()) {
        String like = "%" + titleContains.toLowerCase() + "%";
        p = cb.and(p, cb.like(cb.lower(root.get("title")), like));
      }
      if (assignedToId != null) {
        p = cb.and(p, cb.equal(root.get("assignedToId"), assignedToId));
      }
      if (reportedById != null) {
        p = cb.and(p, cb.equal(root.get("reportedById"), reportedById));
      }

      if (tagIds != null && !tagIds.isEmpty()) {
        Subquery<Long> sq = query.subquery(Long.class);
        Root<Task> t2 = sq.from(Task.class);
        Join<Object, Object> tt = t2.join("tags");

        sq.select(t2.get("id"))
            .where(
                cb.equal(t2.get("id"), root.get("id")),
                tt.get("id").in(tagIds) // any of the selected tags
        );

        p = cb.and(p, cb.exists(sq));
      }

      return p;
    };
  }
}
