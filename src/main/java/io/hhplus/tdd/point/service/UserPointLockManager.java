package io.hhplus.tdd.point.service;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class UserPointLockManager {

    private final Map<Long, Lock> lockMap = new ConcurrentHashMap<>();

    public Lock getLock(long userId) {
        return lockMap.computeIfAbsent(userId, id -> new ReentrantLock(true));
    }

    public void removeLock(long userId) {
        lockMap.remove(userId);
    }

}
