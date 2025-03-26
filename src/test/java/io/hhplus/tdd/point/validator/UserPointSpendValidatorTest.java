package io.hhplus.tdd.point.validator;

import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserPointSpendValidatorTest {

    private UserPointSpendValidator validator;

    @BeforeEach
    void setUp() {
        validator = new UserPointSpendValidator();
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            10000 | 10000
            9999  | 9999
            5000  | 4500
            2000  | 1000
            1000  | 500
            2     | 1
            1     | 1
            """
            , delimiter = '|')
    @DisplayName("포인트 사용에 성공한다")
    void success(long currentPoint, long amount) {
        // given
        long userId = new Random().nextLong(1L, Long.MAX_VALUE);
        UserPoint userPoint = new UserPoint(userId, currentPoint, System.currentTimeMillis());

        // when & then
        validator.validate(userPoint, amount);
    }

    @ParameterizedTest
    @ValueSource(longs = {-10000, -1000, -100, -10, -1, 0})
    @DisplayName("사용할 금액이 양수가 아니면 실패한다")
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
            10000 | 10001
            9999  | 10000
            5000  | 9000
            2000  | 2500
            1000  | 1010
            0     | 1
            """
            , delimiter = '|')
    @DisplayName("사용 금액이 보유 포인트를 초과하면 실패한다")
    void fail2(long currentPoint, long amount) {
        // given
        long userId = new Random().nextLong(1L, Long.MAX_VALUE);
        UserPoint userPoint = new UserPoint(userId, currentPoint, System.currentTimeMillis());

        // when & then
        assertThatThrownBy(() -> validator.validate(userPoint, amount))
                .isInstanceOf(IllegalStateException.class);
    }

}
