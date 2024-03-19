package io.oeid.mogakgo.domain.chat.infrastructure;

import io.oeid.mogakgo.domain.chat.entity.ChatRoom;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRoomJpaRepository extends JpaRepository<ChatRoom, String>,
    ChatRoomCustomRepository {

    Optional<ChatRoom> findByProject_Id(Long id);
}
