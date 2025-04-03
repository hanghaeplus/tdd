package io.hhplus.tdd.point.service.charge;

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
class SimpleUserPointChargeServiceTest {

    @InjectMocks
    private SimpleUserPointChargeService sut;

    @Mock
    private UserPointTable userPointTable;
    @Mock
    private PointHistoryTable pointHistoryTable;
    @Mock
    private UserPointValidator userPointValidator;

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
    void charge(long currentPoint, long amount) {
        // given
        long userId = new Random().nextLong(1L, Long.MAX_VALUE);
        long increasedPoint = currentPoint + amount;

        given(userPointTable.selectById(userId))
                .willReturn(new UserPoint(userId, currentPoint, System.currentTimeMillis()));
        given(userPointTable.insertOrUpdate(eq(userId), eq(increasedPoint)))
                .willReturn(new UserPoint(userId, increasedPoint, System.currentTimeMillis()));

        // when
        UserPoint chargedUserPoint = sut.charge(userId, amount);

        // then
        verify(userPointValidator, times(1))
                .validate(any(UserPoint.class), eq(amount));
        verify(userPointTable, times(1))
                .selectById(userId);
        verify(userPointTable, times(1))
                .insertOrUpdate(userId, increasedPoint);
        verify(pointHistoryTable, times(1))
                .insert(eq(userId), eq(amount), eq(TransactionType.CHARGE), anyLong());
        assertThat(chargedUserPoint)
                .isNotNull()
                .returns(userId, UserPoint::id)
                .returns(increasedPoint, UserPoint::point);
    }

}
