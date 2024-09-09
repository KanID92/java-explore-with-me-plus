package ru.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.entity.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    List<Event> findAllByInitiatorId(long initiatorId, Pageable pageable);

    Optional<Event> findByInitiatorIdAndId(long initiatorId, long eventId);

    @Query(value = "MERGE INTO LIKES_EVENTS (USER_ID, EVENT_ID) values (:userId, :eventId)", nativeQuery = true)
    void addLike(Long userId, Long eventId);

    @Query(value = "DELETE FROM LIKES_EVENTS WHERE USER_ID = :userId AND EVENT_ID = :eventId", nativeQuery = true)
    void deleteLike(Long userId, Long eventId);


    @Query(value = "SELECT EXISTS (" +
            "SELECT * FROM LIKES_EVENTS WHERE USER_ID = :userId AND EVENT_ID = :eventId)", nativeQuery = true)
    boolean checkLikeExisting(Long userId, Long eventId);

    @Query(value = "SELECT COUNT(*) FROM LIKES_EVENTS WHERE EVENT_ID = :eventId", nativeQuery = true)
    long countLikesByEventId(Long eventId);

}
