package com.kailas.dpm.repo;

import com.kailas.dpm.entities.AppUser;
import com.kailas.dpm.entities.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProfileRepo  extends JpaRepository<UserProfile, Long> {
  List<UserProfile> findByUser(AppUser user);
}
