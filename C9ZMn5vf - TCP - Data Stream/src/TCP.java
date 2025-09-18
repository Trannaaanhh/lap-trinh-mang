import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCP {
    public static void main(String[] args) {
        String serverHost = "203.162.10.109";
        int serverPort = 2207;

        String studentCode = "B22DCVT034";
        String qCode = "C9ZMn5vf";
        String message = studentCode + ";" + qCode;

        try (Socket socket = new Socket(serverHost, serverPort)) {
            socket.setSoTimeout(5000); // timeout tối đa 5s

            // Tạo luồng vào/ra
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            // a. Gửi chuỗi "studentCode;qCode"
            dos.writeUTF(message);
            dos.flush();
            System.out.println("Sent: " + message);

            // b. Nhận lần lượt 2 số nguyên a và b từ server
            int a = dis.readInt();
            int b = dis.readInt();
            System.out.println("Received numbers: a = " + a + ", b = " + b);

            // c. Tính toán tổng và tích
            int sum = a + b;
            int product = a * b;
            System.out.println("Calculated sum = " + sum + ", product = " + product);

            // Gửi lần lượt tổng và tích cho server
            dos.writeInt(sum);
            dos.writeInt(product);
            dos.flush();
            System.out.println("Sent sum and product to server.");

            // d. Đóng kết nối
            dis.close();
            dos.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
