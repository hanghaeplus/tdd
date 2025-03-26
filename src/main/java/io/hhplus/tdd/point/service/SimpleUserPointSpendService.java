package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.validator.UserPointValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class SimpleUserPointSpendService implements UserPointSpendService {

    private final UserPointTable userPointTable;

    private final PointHistoryTable pointHistoryTable;

    @Qualifier("userPointSpendValidator")
    private final UserPointValidator userPointValidator;

    @Override
    public UserPoint spend(long userId, long amount) {
        UserPoint selected = userPointTable.selectById(userId);

        userPointValidator.validate(selected, amount);

        long decreasedPoint = selected.point() - amount;
        UserPoint saved = userPointTable.insertOrUpdate(selected.id(), decreasedPoint);

        pointHistoryTable.insert(selected.id(), amount, TransactionType.USE, System.currentTimeMillis());

        return saved;
    }

}
