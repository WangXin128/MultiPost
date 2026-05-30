package com.example.multipost.adapter;

import java.util.List;

public record PlatformValidationResult(boolean valid, List<String> errors) {
    public static PlatformValidationResult ok() {
        return new PlatformValidationResult(true, List.of());
    }

    public static PlatformValidationResult failed(List<String> errors) {
        return new PlatformValidationResult(false, errors);
    }
}
