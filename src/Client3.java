import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client3 {
    public static void main(String[] args) {
        System.out.println("Client 3 started"); // 打印客户端1已启动
        try {
            Socket socket = new Socket("localhost", Server.PORT); // 使用Server.PORT而不是硬编码的端口号来创建套接字
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // 创建输入流读取服务器发来的消息
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // 创建输出流向服务器发送消息

            Scanner scanner = new Scanner(System.in); // 创建一个扫描器来读取用户的输入

            for (int roundNumber = 1; roundNumber <= 13; roundNumber++) {
                System.out.println(in.readLine()); // 从服务器读取回合数
                System.out.print("Enter the value of the card you want to play [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13]: ");
                int cardValue = scanner.nextInt(); // 读取用户输入的牌值
                out.println(cardValue); // 向服务器发送牌值

                // 从服务器读取消息
                System.out.println(in.readLine()); // 读取回合获胜者
                System.out.println(in.readLine()); // 读取你的分数
                System.out.println(in.readLine()); // 读取你剩余的牌
            }

            // 从服务器读取最终消息
            System.out.println(in.readLine()); // 读取游戏结束消息
            System.out.println(in.readLine()); // 读取最终获胜者

            socket.close(); // 关闭套接字
        } catch (IOException e) {
            e.printStackTrace(); // 打印错误堆栈信息
        }
    }
}
