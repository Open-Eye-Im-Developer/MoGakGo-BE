package io.oeid.mogakgo.domain.chat.infrastructure;

import io.oeid.mogakgo.domain.chat.entity.ChatRoom;
import io.oeid.mogakgo.domain.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRoomJpaRepository extends JpaRepository<ChatRoom, String>,
    ChatRoomCustomRepository {

    @Query("select c from ChatRoom c where c.id = ?1 and (c.creator = ?2 or c.sender = ?2)")
    Optional<ChatRoom> findByIdAndUser(String id, User user);
}
