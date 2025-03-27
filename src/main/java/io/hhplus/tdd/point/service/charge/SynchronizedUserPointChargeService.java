package io.hhplus.tdd.point.service.charge;

import io.hhplus.tdd.point.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class SynchronizedUserPointChargeService implements UserPointChargeService {

    @Qualifier("simpleUserPointChargeService")
    private final UserPointChargeService delegate;

    @Override
    public UserPoint charge(long userId, long amount) {
        synchronized (this) {
            return delegate.charge(userId, amount);
        }
    }

}
