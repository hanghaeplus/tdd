package io.hhplus.tdd.point.validator;

import io.hhplus.tdd.point.UserPoint;
import org.springframework.stereotype.Component;

@Component
class UserPointChargeValidator implements UserPointValidator {

    private static final long MAX_POINT = 10_000L;

    @Override
    public void validate(UserPoint userPoint, long amount) {
        if (userPoint == null) {
            return;
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        }

        long increasedPoint = userPoint.point() + amount;
        if (increasedPoint <= MAX_POINT) {
            throw new IllegalStateException("Too much point: %d".formatted(increasedPoint));
        }
    }

}
