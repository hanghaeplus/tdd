package io.hhplus.tdd.point.service.charge;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.test.ConcurrentExecutorUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LockedUserPointChargeServiceIntegrationTest {

    @Autowired
    private LockedUserPointChargeService sut;

    @Autowired
    private UserPointTable userPointTable;
    @Autowired
    private PointHistoryTable pointHistoryTable;

    @Test
    @DisplayName("요청 수만큼 포인트가 충전된다")
    void charge1() {
        // given
        int threadCount = 10;
        long userId = new Random().nextLong(1L, Long.MAX_VALUE);
        long amount = 100L;

        // when & then
        ConcurrentExecutorUtils.execute(threadCount, () -> sut.charge(userId, amount));

        // then
        UserPoint selected = userPointTable.selectById(userId);
        assertThat(selected)
                .isNotNull()
                .matches(it -> it.point() == amount * threadCount);

        List<PointHistory> histories = pointHistoryTable.selectAllByUserId(userId);
        assertThat(histories)
                .isNotNull()
                .hasSize(threadCount)
                .allMatch(history -> history.amount() == amount);
    }

}
