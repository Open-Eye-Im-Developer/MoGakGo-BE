package io.oeid.mogakgo.domain.chat.application;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import io.oeid.mogakgo.domain.chat.entity.document.ChatIdSequence;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatIdSequenceGeneratorService {

    private final MongoOperations mongoOperations;

    public Long generateSequence(String collectionName) {
        ChatIdSequence counter = mongoOperations.findAndModify(
            query(where("_id").is(collectionName)), new Update().inc("seq", 1),
            options().returnNew(true).upsert(true), ChatIdSequence.class);
        return !Objects.isNull(counter) ? counter.getSeq() : 1;
    }
}
