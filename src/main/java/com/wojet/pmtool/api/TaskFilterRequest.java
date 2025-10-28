package com.wojet.pmtool.api;

import java.util.List;

public record TaskFilterRequest (
    Long projectId,                 // REQUIRED
    List<Integer> levels,           // e.g., [1,3]
    List<Long> tagIds,              // match ALL provided tags (see note)
    List<String> statuses,          // e.g., ["To Do","In Progress","Done"]
    List<String> priorities,        // e.g., ["High","Medium"]
    String titleContains,           // substring anywhere
    Long assignedToId,
    Long reportedById,
    boolean includeAncestors        // if false, behave like normal filter
) {}
