import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private final List<Card> cards; // 定义一个用于存储卡牌的列表

    public Deck(String suit) { // 构造函数，传入花色
        cards = new ArrayList<>(); // 初始化卡牌列表
        for (int i = 1; i <= 13; i++) { // 遍历1到13，表示卡牌的点数
            cards.add(new Card(suit, i)); // 创建一个新的卡牌对象，并添加到列表中
        }
        Collections.shuffle(cards); // 洗牌，打乱卡牌顺序
    }

    public Card draw() { // 抽取一张牌的方法
        return cards.isEmpty() ? null : cards.remove(0); // 如果卡牌列表为空，返回null；否则，移除并返回列表中的第一张牌
    }
}
