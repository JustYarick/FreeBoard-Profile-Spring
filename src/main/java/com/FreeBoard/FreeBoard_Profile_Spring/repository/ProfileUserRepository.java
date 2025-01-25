package com.FreeBoard.FreeBoard_Profile_Spring.repository;

import com.FreeBoard.FreeBoard_Profile_Spring.Entity.ProfileUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProfileUserRepository extends JpaRepository<ProfileUserEntity, Long> {
    Optional<ProfileUserEntity> findByEmail(String email);

    Optional<ProfileUserEntity> findByUserId(UUID userId);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Override
    <S extends ProfileUserEntity> S save(S entity);
}
