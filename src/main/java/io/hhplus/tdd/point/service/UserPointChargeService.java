package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.UserPoint;

public interface UserPointChargeService {

    UserPoint charge(long userId, long amount);

}
