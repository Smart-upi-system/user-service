package com.uws.user_service.repository;

import com.uws.user_service.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile,String> {

    @Query("SELECT u FROM UserProfile u where u.upiID= :upiId ")
    UserProfile findByUpiId(@Param("upiId") String upiId);

    @Query("SELECT u FROM UserProfile u where u.userId= :userId ")
    UserProfile findByUserId(@Param("userId") String userId);
}
