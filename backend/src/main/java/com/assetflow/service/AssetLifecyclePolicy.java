package com.assetflow.service;

import com.assetflow.entity.AssetStatus;
import com.assetflow.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Component
public class AssetLifecyclePolicy {

    private static final Map<AssetStatus, Set<AssetStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(AssetStatus.class);

    static {
        ALLOWED_TRANSITIONS.put(AssetStatus.AVAILABLE, EnumSet.of(
                AssetStatus.ALLOCATED, AssetStatus.RESERVED, AssetStatus.UNDER_MAINTENANCE,
                AssetStatus.LOST, AssetStatus.RETIRED));

        ALLOWED_TRANSITIONS.put(AssetStatus.ALLOCATED, EnumSet.of(
                AssetStatus.AVAILABLE, AssetStatus.UNDER_MAINTENANCE, AssetStatus.LOST));

        ALLOWED_TRANSITIONS.put(AssetStatus.RESERVED, EnumSet.of(
                AssetStatus.AVAILABLE, AssetStatus.ALLOCATED, AssetStatus.LOST));

        ALLOWED_TRANSITIONS.put(AssetStatus.UNDER_MAINTENANCE, EnumSet.of(
                AssetStatus.AVAILABLE, AssetStatus.RETIRED, AssetStatus.DISPOSED, AssetStatus.LOST));

        ALLOWED_TRANSITIONS.put(AssetStatus.LOST, EnumSet.of(
                AssetStatus.AVAILABLE, AssetStatus.RETIRED));

        ALLOWED_TRANSITIONS.put(AssetStatus.RETIRED, EnumSet.of(
                AssetStatus.DISPOSED));

        ALLOWED_TRANSITIONS.put(AssetStatus.DISPOSED, EnumSet.noneOf(AssetStatus.class));
    }

    public void validateTransition(AssetStatus from, AssetStatus to) {
        if (from == to) {
            return;
        }
        if (!ALLOWED_TRANSITIONS.get(from).contains(to)) {
            throw new ApiException(
                    "Cannot change asset status from " + from + " to " + to,
                    HttpStatus.CONFLICT
            );
        }
    }
}
