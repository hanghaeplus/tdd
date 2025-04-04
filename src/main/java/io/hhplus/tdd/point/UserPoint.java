package io.hhplus.tdd.point;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    public UserPoint increase(long amount) {
        return new UserPoint(id, point + amount, System.currentTimeMillis());
    }

    public UserPoint decrease(long amount) {
        return new UserPoint(id, point - amount, System.currentTimeMillis());
    }

}
