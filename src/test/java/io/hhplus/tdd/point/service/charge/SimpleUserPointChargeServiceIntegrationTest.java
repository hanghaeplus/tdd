package io.hhplus.tdd.point.service.charge;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.test.ConcurrentExecutorUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SimpleUserPointChargeServiceIntegrationTest {

    @Autowired
    private SimpleUserPointChargeService sut;

    @Autowired
    private UserPointTable userPointTable;

    @RepeatedTest(3)
    @DisplayName("요청 수만큼 포인트가 충전되지 않는다")
    void test() {
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
                .matches(it -> it.point() != amount * threadCount);
    }

}
