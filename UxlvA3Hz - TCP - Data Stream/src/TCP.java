import java.net.*;
import java.io.*;

public class TCP {
    public static void main(String[] args){
        String serverHost = "203.162.10.109";
        int serverPort = 2207;

        String studentCode = "B22DCVT034";
        String qCode = "UxlvA3Hz";
        String message = studentCode + ";" + qCode;

        try (Socket socket = new Socket(serverHost, serverPort)) {
            socket.setSoTimeout(3000);

            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            // a. Gửi mã sinh viên;qCode
            dos.writeUTF(message);
            dos.flush();
            System.out.println("Sent: " + message);

            // b. Nhận chuỗi mảng số nguyên
            String arrStr = dis.readUTF();
            System.out.println("Received array: " + arrStr);

            // Chuyển chuỗi thành mảng số nguyên
            String[] parts = arrStr.split(",");
            int[] arr = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                arr[i] = Integer.parseInt(parts[i].trim());
            }

            // c. Tính số lần đổi chiều và tổng độ biến thiên
            int changes = 0;
            int variation = 0;
            int direction = 0; // 0 = chưa xác định, 1 = tăng, -1 = giảm

            for (int i = 1; i < arr.length; i++) {
                int diff = arr[i] - arr[i - 1];

                if (diff > 0) {
                    if (direction == -1) {
                        changes++;
                    }
                    direction = 1;
                } else if (diff < 0) {
                    if (direction == 1) {
                        changes++;
                    }
                    direction = -1;
                }

                variation += Math.abs(diff);
            }

            System.out.println("Number of changes: " + changes);
            System.out.println("Total variation: " + variation);

            // Gửi kết quả lên server
            dos.writeInt(changes);
            dos.writeInt(variation);
            dos.flush();
            System.out.println("Sent result to server.");

            // d. Đóng kết nối
            dis.close();
            dos.close();
            socket.close();
            System.out.println("Connection closed.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
