package com.bitewise.repository;

import com.bitewise.entity.Session;
import com.bitewise.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findByRefreshToken(String refreshToken);
    List<Session> findByUserAndIsActiveTrue(User user);
    void deleteByUserAndIsActiveFalse(User user);
    Optional<Session> findByUserAndRefreshToken(User user, String refreshToken);
}
