package me.uc_hussein.ultrastpa.model;
import java.util.UUID;
public record TeleportRequest(UUID id, UUID sender, UUID target, RequestType type, long createdAt, long expiresAt) {}
