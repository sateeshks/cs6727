package com.kailas.dpm.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.kailas.dpm.domain.DPMUserIdentity;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.UserIdentity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class AppUser implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="user_id")
    private Long user_id;
    @CreationTimestamp
    @Column(name="create_time" ,nullable=false)
    private LocalDateTime createdDateTime;

    @UpdateTimestamp
    private LocalDateTime updatedDateTime;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof AppUser)) return false;

        AppUser appUser = (AppUser) o;

        return new EqualsBuilder().append(getUser_id(), appUser.getUser_id()).append(getEmail(), appUser.getEmail()).append(getName(), appUser.getName()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getUser_id()).append(getEmail()).append(getName()).toHashCode();
    }

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Lob
    @Column(nullable = false, length = 64)
    private ByteArray handle;

    @OneToMany( mappedBy ="user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(nullable = true ,name ="profile_id")
    private List<UserProfile> profiles  = new ArrayList<>();
    public AppUser(DPMUserIdentity user) {
        this.handle = user.getId();
        this.name = user.getName();  
        this.email = user.getEmail();
    }
    public AppUser(UserIdentity user) {
        this.handle = user.getId();
        this.name = user.getDisplayName();  
        this.email = user.getName();//We are using this field as email -from yubico its userName
    }
    public DPMUserIdentity toDPMUserIdentity() {
        return DPMUserIdentity.builder()
                .name(getName())
                .email(getEmail())
                .id(getHandle())
                .build();
    }
    public UserIdentity toUserIdentity() {
        return UserIdentity.builder()
                .name(getName())
                .displayName(getEmail())
                .id(getHandle())
                .build();
    }
    @JsonManagedReference
    public List<UserProfile> getProfiles(){
        return profiles;
    }
}
