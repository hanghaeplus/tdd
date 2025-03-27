package io.hhplus.tdd.point.service.charge;

import io.hhplus.tdd.point.UserPoint;

public interface UserPointChargeService {

    UserPoint charge(long userId, long amount);

}
