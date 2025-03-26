package io.hhplus.tdd.point.validator;

import io.hhplus.tdd.point.UserPoint;

public interface UserPointValidator {

    void validate(UserPoint userPoint, long amount);

}
