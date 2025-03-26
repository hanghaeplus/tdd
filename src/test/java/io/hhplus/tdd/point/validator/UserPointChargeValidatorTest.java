package io.hhplus.tdd.point.validator;

import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserPointChargeValidatorTest {

    private UserPointChargeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new UserPointChargeValidator();
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            0     | 10000
            1     | 9999
            1000  | 500
            2000  | 3000
            5000  | 5000
            9999  | 1
            """
            , delimiter = '|')
    @DisplayName("포인트 충전에 성공한다")
    void success(long currentPoint, long amount) {
        // given
        long userId = new Random().nextLong(1L, Long.MAX_VALUE);
        UserPoint userPoint = new UserPoint(userId, currentPoint, System.currentTimeMillis());

        // when & then
        validator.validate(userPoint, amount);
    }

    @ParameterizedTest
    @ValueSource(longs = {-10000, -1000, -100, -10, -1, 0})
    @DisplayName("충전할 금액이 양수가 아니면 실패한다")
    void fail1(long amount) {
        // given
        long userId = new Random().nextLong(1L, Long.MAX_VALUE);
        UserPoint userPoint = UserPoint.empty(userId);

        // when & then
        assertThatThrownBy(() -> validator.validate(userPoint, amount))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            0     | 10001
            1     | 10000
            2000  | 9000
            5000  | 5500
            9999  | 2
            10000 | 1
            """
            , delimiter = '|')
    @DisplayName("충전 후 금액이 최대 포인트를 초과하면 실패한다")
    void fail2(long currentPoint, long amount) {
        // given
        long userId = new Random().nextLong(1L, Long.MAX_VALUE);
        UserPoint userPoint = new UserPoint(userId, currentPoint, System.currentTimeMillis());

        // when & then
        assertThatThrownBy(() -> validator.validate(userPoint, amount))
                .isInstanceOf(IllegalStateException.class);
    }

}
