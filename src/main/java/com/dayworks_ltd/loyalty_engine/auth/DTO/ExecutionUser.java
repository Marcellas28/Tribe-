package com.dayworks_ltd.loyalty_engine.auth.DTO;

import com.dayworks_ltd.loyalty_engine.auth.enums.UserPermission;
import com.dayworks_ltd.loyalty_engine.auth.enums.UserRole;

import java.util.Set;

public record ExecutionUser(Long userId, UserRole role, Set<UserPermission> permissions) {
}
