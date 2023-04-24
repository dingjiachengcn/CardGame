import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ServerGame {
    private final List<ClientHandler> clientHandlers; // 客户端处理器列表
    private final List<Card> serverCards; // 服务器的牌列表
    private int currentRound; // 当前回合数
    private HashMap<Integer, Integer> scores; // 存储每个客户端的得分
    private HashMap<Integer, HashSet<Integer>> clientRemainingCards; // 存储每个客户端剩余的牌

    public ServerGame(List<Socket> sockets) {
        clientHandlers = new ArrayList<>(); // 初始化客户端处理器列表
        for (int i = 0; i < sockets.size(); i++) {
            ClientHandler handler = new ClientHandler(sockets.get(i), i + 1);
            clientHandlers.add(handler); // 为每个套接字创建客户端处理器并添加到列表中
        }
        serverCards = createShuffledSpadesDeck(); // 创建并洗牌
        currentRound = 0; // 初始化当前回合数为0
        scores = new HashMap<>(); // 初始化客户端得分映射
        clientRemainingCards = new HashMap<>(); // 初始化客户端剩余牌映射
        for (int i = 1; i <= sockets.size(); i++) {
            scores.put(i, 0); // 初始化每个客户端的得分为0
            HashSet<Integer> remainingCards = new HashSet<>();
            for (int j = 1; j <= 13; j++) {
                remainingCards.add(j); // 将1到13的牌添加到剩余牌集合中
            }
            clientRemainingCards.put(i, remainingCards); // 将剩余牌集合添加到映射中
        }
    }

    private List<Card> createShuffledSpadesDeck() {
        List<Card> deck = new ArrayList<>(); // 初始化牌组
        for (int i = 1; i <= 13; i++) {
            deck.add(new Card("Spades", i)); // 添加黑桃牌到牌组中
        }
        Collections.shuffle(deck); // 洗牌
        return deck; // 返回洗好的牌组
    }

    public void playGame() {
        for (int roundNumber = 1; roundNumber <= 13; roundNumber++) {
            playRound(roundNumber); // 播放指定回合数的游戏

            for (ClientHandler clientHandler : clientHandlers) {
                clientHandler.send("Round " + roundNumber); // 向每个客户端发送回合信息
            }
           // playRound(roundNumber); // 播放指定回合数的游戏
        }

        // 游戏循环结束后
        System.out.println("Game over!"); // 输出游戏结束信息
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.send("Game over!"); // 向每个客户端发送游戏结束信息
        }

        Set<Integer> winners = determineWinners(); // 确定赢家
        System.out.println("Final winners: " + winners); // 输出最终赢家信息
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.send("Final winners: " + winners); // 向每个客户端发送最终赢家信息
        }
    }

    private void playRound(int roundNumber) {
        System.out.println("Round " + roundNumber); // 输出当前回合数信息
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.send("Round " + roundNumber); // 向每个客户端发送当前回合数信息
        }

        // 获取客户端出的牌
        Map<Integer, Integer> playedCards = new HashMap<>();
        for (ClientHandler clientHandler : clientHandlers) {
            int playedCard = clientHandler.receiveInt(); // 接收客户端发送的牌值
            playedCards.put(clientHandler.getClientNumber(), playedCard); // 将客户端号和牌值存入playedCards映射中
        }

        // 判断回合获胜者
        int maxCardValue = Collections.max(playedCards.values()); // 获取最大的牌值
        Set<Integer> roundWinners = playedCards.entrySet().stream()
                .filter(entry -> entry.getValue() == maxCardValue) // 筛选出牌值最大的客户端
                .map(Map.Entry::getKey) // 提取客户端编号
                .collect(Collectors.toSet()); // 将客户端编号存入集合中
        updateScores(roundWinners, maxCardValue); // 更新回合获胜者的得分

        System.out.println("Round " + roundNumber + " winners: " + roundWinners); // 输出回合获胜者信息
        for(ClientHandler clientHandler : clientHandlers) {
            clientHandler.send("Round " + roundNumber + " winners: " + roundWinners); // 向每个客户端发送回合获胜者信息
        }

        // 更新客户端的剩余牌
        for (ClientHandler clientHandler : clientHandlers) {
            int clientNumber = clientHandler.getClientNumber(); // 获取客户端编号
            int playedCard = playedCards.get(clientNumber); // 获取客户端出的牌
            HashSet<Integer> remainingCards = clientRemainingCards.get(clientNumber); // 获取客户端剩余的牌
            remainingCards.remove(playedCard); // 从剩余牌中移除出的牌
            clientRemainingCards.put(clientNumber, remainingCards); // 更新客户端剩余牌映射
        }

        // 向客户端发送更新后的得分和剩余牌信息
        for (ClientHandler clientHandler : clientHandlers) {
            int clientNumber = clientHandler.getClientNumber(); // 获取客户端编号
            int score = scores.get(clientNumber); // 获取客户端得分
            HashSet<Integer> remainingCards = clientRemainingCards.get(clientNumber); // 获取客户端剩余的牌

            clientHandler.send("Your score: " + score); // 向客户端发送得分信息
            clientHandler.send("Your remaining cards: " + remainingCards); // 向客户端发送剩余牌信息
        }
    }

    private void updateScores(Set<Integer> roundWinners, int maxCardValue) {
        int points = maxCardValue - 1; // 计算得分（最大牌值减1）
        for (int winner : roundWinners) {
            int updatedScore = scores.get(winner) + points; // 更新回合获胜者的得分
            scores.put(winner, updatedScore); // 将更新后的得分存入得分映射中
        }
    }

    private Set<Integer> determineWinners() {
        int maxScore = Collections.max(scores.values()); // 获取最高得分
        return scores.entrySet().stream()
                .filter(entry -> entry.getValue() == maxScore) // 筛选出得分最高的客户端
                .map(Map.Entry::getKey) // 提取客户端编号
                .collect(Collectors.toSet()); // 将客户端编号存入集合中
    }

    public static void main(String[] args) {
        // 实现服务器套接字，并将套接字传递给构造函数
        // 为每个客户端处理器启动一个新线程
        // 开始游戏
    }
}

class ClientHandler {
    private final Socket socket; // 客户端套接字
    private final int clientNumber; // 客户端编号
    private final BufferedReader in; // 输入流
    private final PrintWriter out; // 输出流

    public ClientHandler(Socket socket, int clientNumber) {
        this.socket = socket;
        this.clientNumber = clientNumber;

        BufferedReader tempIn = null;
        PrintWriter tempOut = null;
        try {
            tempIn = new BufferedReader(new InputStreamReader(socket.getInputStream())); // 初始化输入流
            tempOut = new PrintWriter(socket.getOutputStream(), true); // 初始化输出流
        } catch (IOException e) {
            e.printStackTrace();
        }

        in = tempIn;
        out = tempOut;
    }

    public int getClientNumber() {
        return clientNumber; // 返回客户端编号
    }

    public void send(String message) {
        out.println(message); // 向客户端发送信息
    }

    public String receive() {
        try {
            return in.readLine(); // 从客户端接收信息
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int receiveInt() {
        return Integer.parseInt(receive()); // 从客户端接收整数
    }
}


