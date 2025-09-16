import java.io.*;
import java.net.*;
import java.util.Random;

public class httpsv {

    // Server chạy ở cổng 2207
    static class Server extends Thread {
        @Override
        public void run() {
            try (ServerSocket serverSocket = new ServerSocket(2207)) {
                System.out.println("Server đang lắng nghe ở cổng 2207...");
                Socket socket = serverSocket.accept();
                System.out.println("Client đã kết nối: " + socket.getInetAddress());

                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                // a. Nhận chuỗi "studentCode;qCode"
                String msg = dis.readUTF();
                System.out.println("Server nhận: " + msg);

                // b. Gửi 2 số nguyên a, b (ví dụ random)
                Random rand = new Random();
                int a = rand.nextInt(100); // 0..99
                int b = rand.nextInt(100);
                dos.writeInt(a);
                dos.writeInt(b);
                dos.flush();
                System.out.println("Server gửi a = " + a + ", b = " + b);

                // c. Nhận lại tổng và tích từ client
                int sum = dis.readInt();
                int product = dis.readInt();
                System.out.println("Server nhận sum = " + sum + ", product = " + product);

                socket.close();
                System.out.println("Server đóng kết nối.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Client
    static class Client {
        public void runClient() {
            String studentCode = "B22DCVT034";
            String qCode = "C9ZMn5vf";

            try (Socket socket = new Socket("localhost", 2207)) {
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                // a. Gửi chuỗi
                String message = studentCode + ";" + qCode;
                dos.writeUTF(message);
                dos.flush();
                System.out.println("Client gửi: " + message);

                // b. Nhận 2 số nguyên
                int a = dis.readInt();
                int b = dis.readInt();
                System.out.println("Client nhận a = " + a + ", b = " + b);

                // c. Tính toán
                int sum = a + b;
                int product = a * b;

                // Gửi lại kết quả
                dos.writeInt(sum);
                dos.writeInt(product);
                dos.flush();
                System.out.println("Client gửi sum = " + sum + ", product = " + product);

                System.out.println("Client kết thúc.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Main
    public static void main(String[] args) throws InterruptedException {
        // Chạy server ở thread riêng
        Server server = new Server();
        server.start();

        // Chờ server khởi động
        Thread.sleep(500);

        // Chạy client
        Client client = new Client();
        client.runClient();
    }
}
