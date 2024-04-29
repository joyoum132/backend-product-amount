package antigravity.strategy.discount;

public class WithPercent implements DiscountStrategy {
    @Override
    public int applyDiscount(int price, int amount) {
        double target = (100-amount) / 100.0;
        return (int) (price * target);
    }
}