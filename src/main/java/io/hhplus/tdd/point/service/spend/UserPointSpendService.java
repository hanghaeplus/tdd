package io.hhplus.tdd.point.service.spend;

import io.hhplus.tdd.point.UserPoint;

public interface UserPointSpendService {

    UserPoint spend(long userId, long amount);

}
