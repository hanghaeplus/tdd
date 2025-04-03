package io.hhplus.tdd.point;

import io.hhplus.tdd.point.service.PointHistoryService;
import io.hhplus.tdd.point.service.UserPointFindService;
import io.hhplus.tdd.point.service.charge.UserPointChargeService;
import io.hhplus.tdd.point.service.spend.UserPointSpendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
public class PointController {

    private final UserPointFindService userPointFindService;
    @Qualifier("lockedUserPointChargeService")
    private final UserPointChargeService userPointChargeService;
    @Qualifier("lockedUserPointSpendService")
    private final UserPointSpendService userPointSpendService;
    private final PointHistoryService pointHistoryService;

    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{userId}")
    public UserPoint point(
            @PathVariable long userId
    ) {
        return userPointFindService.findUserPoint(userId);
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{userId}/histories")
    public List<PointHistory> history(
            @PathVariable long userId
    ) {
        return pointHistoryService.findPointHistories(userId);
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{userId}/charge")
    public UserPoint charge(
            @PathVariable long userId,
            @RequestBody long amount
    ) {
        return userPointChargeService.charge(userId, amount);
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{userId}/use")
    public UserPoint use(
            @PathVariable long userId,
            @RequestBody long amount
    ) {
        return userPointSpendService.spend(userId, amount);
    }

}
