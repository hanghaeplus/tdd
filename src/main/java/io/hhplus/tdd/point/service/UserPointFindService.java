package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPointFindService {

    private final UserPointTable userPointTable;

    public UserPoint findUserPoint(long userId) {
        return userPointTable.selectById(userId);
    }

}
