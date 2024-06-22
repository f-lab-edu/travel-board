package com.app.travelboard.storage.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "account_id", foreignKey = @ForeignKey(name = "FK_user_account_id"))
    private Account account;

    @Column(length = 50)
    private String nickname;

    @Column(length = 512)
    private String profileImageUrl;

    @Column(length = 300)
    private String bio;

}
