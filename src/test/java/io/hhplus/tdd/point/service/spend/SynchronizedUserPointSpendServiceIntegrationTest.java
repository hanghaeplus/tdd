package io.hhplus.tdd.point.service.spend;

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
class SynchronizedUserPointSpendServiceIntegrationTest {

    @Autowired
    private SynchronizedUserPointSpendService sut;

    @Autowired
    private UserPointTable userPointTable;
    @Autowired
    private PointHistoryTable pointHistoryTable;

    @Test
    @DisplayName("단일 사용자가 요청한 만큼 포인트가 충전된다")
    void spend1() {
        // given
        int threadCount = 10;
        long userId = new Random().nextLong(1L, Long.MAX_VALUE);
        long amount = 100L;

        userPointTable.insertOrUpdate(userId, amount * threadCount);

        // when & then
        ConcurrentExecutorUtils.execute(threadCount, i -> sut.spend(userId, amount));

        // then
        UserPoint selected = userPointTable.selectById(userId);
        assertThat(selected)
                .isNotNull()
                .matches(it -> it.point() == 0);

        List<PointHistory> histories = pointHistoryTable.selectAllByUserId(userId);
        assertThat(histories)
                .isNotNull()
                .hasSize(threadCount)
                .allMatch(history -> history.amount() == amount);
    }

    @Test
    @DisplayName("여러 사용자가 요청하면 각자 자기 몫만큼 포인트가 충전된다")
    void spend2() {
        // given
        int threadCount = 10;
        List<SpendRequest> requests = IntStream.range(0, threadCount)
                .mapToObj(i -> new SpendRequest(i + 1, 100L * (i + 1)))
                .toList();

        requests.forEach(request -> userPointTable.insertOrUpdate(request.userId(), 10000L));

        // when & then
        ConcurrentExecutorUtils.execute(threadCount, i -> {
            SpendRequest request = requests.get(i);
            sut.spend(request.userId(), request.amount());
        });

        // then
        for (SpendRequest request : requests) {
            long userId = request.userId();
            long amount = request.amount();
            long balance = 10000L - amount;

            UserPoint selected = userPointTable.selectById(userId);
            assertThat(selected)
                    .isNotNull()
                    .matches(it -> it.point() == balance);

            List<PointHistory> histories = pointHistoryTable.selectAllByUserId(userId);
            assertThat(histories)
                    .isNotNull()
                    .hasSize(1)
                    .allMatch(history -> history.amount() == amount);
        }

    }

    // -------------------------------------------------------------------------------------------------

    private record SpendRequest(long userId, long amount) {
    }

}
