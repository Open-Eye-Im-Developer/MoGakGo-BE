package io.oeid.mogakgo.domain.matching.application;

import io.oeid.mogakgo.domain.matching.domain.entity.Matching;
import io.oeid.mogakgo.domain.matching.infrastructure.MatchingJpaRepository;
import io.oeid.mogakgo.domain.project_join_req.domain.entity.ProjectJoinRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MatchingService {

    private final MatchingJpaRepository matchingJpaRepository;

    @Transactional
    public Long create(ProjectJoinRequest projectJoinRequest) {
        Matching matching = Matching.builder()
            .project(projectJoinRequest.getProject())
            .sender(projectJoinRequest.getSender())
            .build();

        matchingJpaRepository.save(matching);

        return matching.getId();
    }
}
