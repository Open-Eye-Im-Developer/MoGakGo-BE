package io.oeid.mogakgo.domain.chat.infrastructure;

import io.oeid.mogakgo.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRoomJpaRepository extends JpaRepository<ChatRoom, String>,
    ChatRoomCustomRepository {

}
