package io.hhplus.tdd.point.service.spend;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.validator.UserPointValidator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@MockitoSettings
class SimpleUserPointSpendServiceTest {

    @InjectMocks
    private SimpleUserPointSpendService sut;

    @Mock
    private UserPointTable userPointTable;
    @Mock
    private PointHistoryTable pointHistoryTable;
    @Mock
    private UserPointValidator userPointValidator;

    @ParameterizedTest
    @CsvSource(textBlock = """
            10000 | 10000
            9999  | 9000
            5000  | 3000
            1000  | 500
            100   | 1
            1     | 1
            """
            , delimiter = '|')
    void spend(long currentPoint, long amount) {
        // given
        long userId = new Random().nextLong(1L, Long.MAX_VALUE);
        long decreasedPoint = currentPoint - amount;

        given(userPointTable.selectById(userId))
                .willReturn(new UserPoint(userId, currentPoint, System.currentTimeMillis()));
        given(userPointTable.insertOrUpdate(eq(userId), eq(decreasedPoint)))
                .willReturn(new UserPoint(userId, decreasedPoint, System.currentTimeMillis()));

        // when
        UserPoint spentUserPoint = sut.spend(userId, amount);

        // then
        verify(userPointValidator, times(1))
                .validate(any(UserPoint.class), eq(amount));
        verify(userPointTable, times(1))
                .selectById(userId);
        verify(userPointTable, times(1))
                .insertOrUpdate(userId, decreasedPoint);
        verify(pointHistoryTable, times(1))
                .insert(eq(userId), eq(amount), eq(TransactionType.USE), anyLong());
        assertThat(spentUserPoint)
                .isNotNull()
                .returns(userId, UserPoint::id)
                .returns(decreasedPoint, UserPoint::point);
    }

}
