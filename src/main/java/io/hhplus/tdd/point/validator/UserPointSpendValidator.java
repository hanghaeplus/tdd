package io.hhplus.tdd.point.validator;

import io.hhplus.tdd.point.UserPoint;
import org.springframework.stereotype.Component;

@Component
class UserPointSpendValidator implements UserPointValidator {

    @Override
    public void validate(UserPoint userPoint, long amount) {
        if (userPoint == null) {
            return;
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        }

        long decreasedPoint = userPoint.point() - amount;
        if (decreasedPoint < 0) {
            throw new IllegalStateException("Not enough point: %d".formatted(decreasedPoint));
        }
    }

}
