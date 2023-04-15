package com.kailas.dpm.repo;

import com.kailas.dpm.entities.AppUser;
import com.kailas.dpm.entities.Authenticator;
import com.yubico.webauthn.data.ByteArray;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AuthenticatorRepository extends CrudRepository<Authenticator, Long> {
    Optional<Authenticator> findByCredentialId(ByteArray credentialId);
    List<Authenticator> findAllByUser (AppUser user);
    List<Authenticator> findAllByCredentialId(ByteArray credentialId);
}
