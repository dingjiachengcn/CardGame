import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class Server {
    public static final int PORT = 49152; // 设置服务器端口号

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) { // 创建一个新的服务器套接字，并绑定端口
            System.out.println("Server is running..."); // 打印服务器正在运行的信息

            Socket client1 = serverSocket.accept(); // 接受客户端1的连接
            System.out.println("Client 1 connected."); // 打印客户端1已连接的信息

            Socket client2 = serverSocket.accept(); // 接受客户端2的连接
            System.out.println("Client 2 connected."); // 打印客户端2已连接的信息

            Socket client3 = serverSocket.accept(); // 接受客户端3的连接
            System.out.println("Client 3 connected."); // 打印客户端3已连接的信息

            List<Socket> clients = Arrays.asList(client1, client2, client3); // 将客户端套接字存入列表
            ServerGame game = new ServerGame(clients); // 创建一个新的ServerGame实例，传入客户端列表
            game.playGame(); // 开始游戏

        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage()); // 打印启动服务器时遇到的错误信息
            e.printStackTrace(); // 打印错误堆栈跟踪
        }
    }
}
