package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.entity.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {

    @Query(value = "MERGE INTO LIKES_LOCATIONS (LOCATION_ID, USER_ID) values (:locationId, :userId)", nativeQuery = true)
    void addLike(Long userId, Long locationId);

    @Query(value = "DELETE FROM LIKES_LOCATIONS WHERE LOCATION_ID = :locationId AND USER_ID = :userId ", nativeQuery = true)
    void deleteLike(Long userId, Long locationId);

    @Query(value = "SELECT EXISTS (" +
            "SELECT * FROM LIKES_LOCATIONS WHERE LOCATION_ID = :locationId AND USER_ID = :userId)", nativeQuery = true)
    boolean checkLikeExisting(Long userId, Long locationId);


}
