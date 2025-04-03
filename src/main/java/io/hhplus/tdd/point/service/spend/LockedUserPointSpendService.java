package io.hhplus.tdd.point.service.spend;

import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.service.UserPointLockManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.Lock;

@Service
@RequiredArgsConstructor
class LockedUserPointSpendService implements UserPointSpendService {

    @Qualifier("simpleUserPointSpendService")
    private final UserPointSpendService delegate;

    private final UserPointLockManager lockManager;

    @Override
    public UserPoint spend(long userId, long amount) {
        Lock lock = lockManager.getLock(userId);
        lock.lock();

        try {
            return delegate.spend(userId, amount);
        } finally {
            lock.unlock();
            lockManager.removeLock(userId);
        }
    }

}
