package com.app.travelboard.storage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

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
