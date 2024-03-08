package io.oeid.mogakgo.domain.chat.infrastructure;

import io.oeid.mogakgo.domain.chat.entity.ChatUser;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatUserJpaRepository extends JpaRepository<ChatUser, Long> {

    @Query("select c from ChatUser c where c.chatRoom.id = ?1 and c.user.id = ?2 and c.availableYn = true")
    Optional<ChatUser> findByChatRoomIdAndUserId(UUID chatRoomId, Long userId);

    @Query("select c from ChatUser c where c.chatRoom.id = ?1 and (c.user.id <> ?2 and c.availableYn = true)")
    Optional<ChatUser> findReceiverByRoomIdAndUserId(UUID id, Long id1);
}