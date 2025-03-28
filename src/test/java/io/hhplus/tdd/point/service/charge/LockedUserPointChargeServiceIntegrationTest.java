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
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class LockedUserPointChargeServiceIntegrationTest {

    @Autowired
    private LockedUserPointChargeService sut;

    @Autowired
    private UserPointTable userPointTable;
    @Autowired
    private PointHistoryTable pointHistoryTable;

    @Test
    @DisplayName("단일 사용자가 요청한 만큼 포인트가 충전된다")
    void charge1() {
        // given
        int threadCount = 10;
        long userId = new Random().nextLong(1L, Long.MAX_VALUE);
        long amount = 100L;

        // when & then
        ConcurrentExecutorUtils.execute(threadCount, i -> sut.charge(userId, amount));

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

    @Test
    @DisplayName("여러 사용자가 요청하면 각자 자기 몫만큼 포인트가 충전된다")
    void charge2() {
        // given
        int threadCount = 10;
        List<ChargeRequest> requests = IntStream.range(0, threadCount)
                .mapToObj(i -> new ChargeRequest(i + 1, 100L * (i + 1)))
                .toList();

        // when & then
        ConcurrentExecutorUtils.execute(threadCount, i -> {
            ChargeRequest request = requests.get(i);
            sut.charge(request.userId(), request.amount());
        });

        // then
        for (ChargeRequest request : requests) {
            long userId = request.userId();
            long amount = request.amount();

            UserPoint selected = userPointTable.selectById(userId);
            assertThat(selected)
                    .isNotNull()
                    .matches(it -> it.point() == amount);

            List<PointHistory> histories = pointHistoryTable.selectAllByUserId(userId);
            assertThat(histories)
                    .isNotNull()
                    .hasSize(1)
                    .allMatch(history -> history.amount() == amount);
        }

    }

    // -------------------------------------------------------------------------------------------------

    private record ChargeRequest(long userId, long amount) {
    }

}
