import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

public class ClientGame {
    private Socket socket; // 客户端套接字
    private BufferedReader in; // 从服务器读取输入流
    private PrintWriter out; // 向服务器发送输出流
    private final Scanner userInput; // 用户输入

    public ClientGame(String serverAddress, int port) throws IOException {
        socket = new Socket(serverAddress, port); // 连接到服务器
        in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // 初始化从服务器读取的输入流
        out = new PrintWriter(socket.getOutputStream(), true); // 初始化向服务器发送的输出流
        this.userInput = new Scanner(System.in); // 初始化用户输入
    }

    public void play() {
        Set<Integer> remainingCards = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13)); // 玩家剩余的牌
        boolean gameOver = false; // 游戏是否结束的标志

        while (!gameOver) { // 当游戏未结束时
            System.out.println("Your remaining cards: " + remainingCards); // 显示玩家剩余的牌
            try {
                String message = in.readLine(); // 从服务器读取一条消息

                if (message.startsWith("Round ")) { // 如果消息是新的一轮开始
                    System.out.println(message); // 显示消息
                    int cardValue; // 玩家选择的牌的值
                    while (true) {
                        System.out.print("Enter the value of the card you want to play " + remainingCards + ": "); // 提示用户输入
                        cardValue = userInput.nextInt(); // 读取用户输入的牌值

                        if (remainingCards.contains(cardValue)) { // 如果输入的牌值在剩余的牌中
                            remainingCards.remove(cardValue); // 从剩余的牌中移除该牌
                            break; // 跳出循环
                        } else {
                            System.out.println("Invalid input. Please enter a valid card value."); // 提示用户输入无效，重新输入
                        }
                    }

                    out.println(String.valueOf(cardValue)); // 向服务器发送玩家选择的牌值
                } else if (message.startsWith("Game over!")) { // 如果消息是游戏结束
                    System.out.println(message); // 显示消息
                    gameOver = true; // 设置游戏结束标志为true
                } else if (message.startsWith("Remaining cards:")) { // 如果消息是剩余牌信息
                    String cardsStr = message.substring("Remaining cards:".length()).trim(); // 提取剩余牌信息
                    cardsStr = cardsStr.replaceAll("\\[|\\]", ""); // 移除方括号
                    String[] cardStrings = cardsStr.split(", "); // 用逗号分隔剩余牌值
                    remainingCards.clear(); // 清空剩余牌集合
                    for (String card : cardStrings) { // 遍历剩余牌值
                        remainingCards.add(Integer.parseInt(card)); // 将剩余牌值添加到剩余牌集合中
                    }
                } else {
                    System.out.println(message); // 显示其他消息
                }
            } catch (IOException e) {
                System.err.println("Error receiving message from server: " + e.getMessage()); // 如果接收服务器消息时出错，显示错误信息
                break; // 跳出循环
            }
        }
    }

    public static void main(String[] args) {
        try {
            ClientGame clientGame = new ClientGame("127.0.0.1", 9898); // 创建一个新的客户端游戏实例，连接到本地服务器的端口9898
            clientGame.play(); // 开始游戏
        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage()); // 如果连接服务器时出错，显示错误信息
        }
    }
}

