package com.demandlane.booklending.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@MappedSuperclass
public class Auditable {

    @Column(name = "is_active", nullable = false)
    protected Boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    protected OffsetDateTime createdAt;

    @Column(name = "created_by")
    protected UUID createdBy;

    @Column(name = "updated_at", nullable = false)
    protected OffsetDateTime updatedAt;

    @Column(name = "updated_by")
    protected UUID updatedBy;

    @Column(name = "deleted_at")
    protected OffsetDateTime deletedAt;

    @Column(name = "deleted_by")
    protected UUID deletedBy;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}
