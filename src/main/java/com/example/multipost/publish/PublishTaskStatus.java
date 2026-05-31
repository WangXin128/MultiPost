package com.example.multipost.publish;

public enum PublishTaskStatus {
    SCHEDULED,
    PENDING,
    PUBLISHING,
    SUCCESS,
    FAILED,
    RETRYING,
    UNKNOWN
}
