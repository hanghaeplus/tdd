package io.hhplus.tdd.point.service.spend;

import io.hhplus.tdd.point.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class SynchronizedUserPointSpendService implements UserPointSpendService {

    @Qualifier("simpleUserPointSpendService")
    private final UserPointSpendService delegate;

    @Override
    public UserPoint spend(long userId, long amount) {
        synchronized (this) {
            return delegate.spend(userId, amount);
        }
    }

}
