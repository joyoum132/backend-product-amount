package antigravity.strategy.discount;

public class WithWon implements DiscountStrategy {
    @Override
    public int applyDiscount(int price, int amount) {
        return price > amount ? price - amount : price;
    }
}