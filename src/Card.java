public class Card {
    private final String suit; // 定义一个用于存储花色的字符串变量
    private final int value;   // 定义一个用于存储牌值的整数变量

    public Card(String suit, int value) { // 构造函数，传入花色和牌值
        this.suit = suit; // 将传入的花色赋值给suit变量
        this.value = value; // 将传入的牌值赋值给value变量
    }

    public String getSuit() { // 获取花色的方法
        return suit; // 返回花色
    }

    public int getValue() { // 获取牌值的方法
        return value; // 返回牌值
    }

    @Override
    public String toString() { // 重写toString方法，用于打印Card对象时显示花色和牌值
        return "Card{" +
                "suit='" + suit + '\'' +
                ", value=" + value +
                '}';
    }
}
