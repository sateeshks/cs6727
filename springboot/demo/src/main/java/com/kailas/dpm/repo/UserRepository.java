package com.kailas.dpm.repo;

import com.kailas.dpm.entities.AppUser;
import com.yubico.webauthn.data.ByteArray;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<AppUser, Long> {
    AppUser findByName(String name);

    AppUser findAppUserByEmail(String email);
    AppUser findAppUserByEmailAndName(String email,String name);
    AppUser findByHandle(ByteArray handle);

    AppUser findByEmail(String email);
}
