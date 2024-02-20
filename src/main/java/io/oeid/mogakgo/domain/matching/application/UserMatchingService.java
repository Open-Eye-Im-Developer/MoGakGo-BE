package io.oeid.mogakgo.domain.matching.application;

import io.oeid.mogakgo.domain.matching.infrastructure.MatchingJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserMatchingService {

    private final MatchingJpaRepository matchingJpaRepository;

    public boolean hasProgressMatching(Long userId) {
        return !matchingJpaRepository.findProgressOneByUserId(userId, PageRequest.of(0, 1))
            .isEmpty();
    }

}
