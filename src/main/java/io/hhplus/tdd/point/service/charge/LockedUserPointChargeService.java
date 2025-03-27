package io.hhplus.tdd.point.service.charge;

import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.service.UserPointLockManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.Lock;

@Service
@RequiredArgsConstructor
class LockedUserPointChargeService implements UserPointChargeService {

    @Qualifier("simpleUserPointChargeService")
    private final UserPointChargeService delegate;

    private final UserPointLockManager lockManager;

    @Override
    public UserPoint charge(long userId, long amount) {
        Lock lock = lockManager.getLock(userId);
        lock.lock();

        try {
            return delegate.charge(userId, amount);
        } finally {
            lock.unlock();
            lockManager.removeLock(userId);
        }
    }

}
