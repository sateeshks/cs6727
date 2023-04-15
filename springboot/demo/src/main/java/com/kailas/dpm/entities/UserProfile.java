package com.kailas.dpm.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_profile")
@Data
@NoArgsConstructor
public class UserProfile {
    @ManyToOne(cascade = CascadeType.MERGE , fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser user;
    String domain = new String("default");
    boolean lowerCase =true;
    boolean  upperCase =true;
    boolean digits =true;
    boolean symbols =true;
    int length =8;
    int revision =1;
    String exclude =new String();
    @Transient
    List<String> rules = new ArrayList<>();

    @Column(name="create_time" ,nullable=false)
    @CreationTimestamp
    private LocalDateTime createdDateTime;

    @UpdateTimestamp
    private LocalDateTime updatedDateTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof UserProfile)) return false;

        UserProfile profile = (UserProfile) o;

        return new EqualsBuilder().append(isLowerCase(), profile.isLowerCase()).append(isUpperCase(), profile.isUpperCase()).append(isDigits(), profile.isDigits()).append(isSymbols(), profile.isSymbols()).append(getLength(), profile.getLength()).append(getUser(), profile.getUser()).append(getDomain(), profile.getDomain()).append(getExclude(), profile.getExclude()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getUser()).append(getDomain()).append(isLowerCase()).append(isUpperCase()).append(isDigits()).append(isSymbols()).append(getLength()).append(getExclude()).toHashCode();
    }

    public List<String> getRules(){
        if(isDigits())
            rules.add("digits");
        if(isLowerCase())
            rules.add("lowercase");
        if(isUpperCase())
            rules.add("uppercase");
        if(isSymbols())
            rules.add("symbols");
        return rules;
    }
    @Id
    @Column(name="p_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long p_id;
    @JsonBackReference
    public AppUser getUser(){
        return user;
    }
}
