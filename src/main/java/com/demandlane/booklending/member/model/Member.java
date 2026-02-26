package com.demandlane.booklending.member.model;

import com.demandlane.booklending.common.model.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;

import java.util.UUID;

@Data
@Where(clause = "is_active = true")
@Entity
@Table(name = "members")
@EqualsAndHashCode(callSuper = true)
public class Member extends Auditable {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "phone_number", length = 100)
    private String phoneNumber;
}
