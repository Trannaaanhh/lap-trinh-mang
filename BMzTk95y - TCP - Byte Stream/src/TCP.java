import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TCP {
    public static void main(String[] args) {
        String serverName = "203.162.10.109";
        int port = 2206;

        String studentCode = "B22DCVT034";
        String qCode = "BMzTk95y";
        String message = studentCode + ";" + qCode;

        try (Socket socket = new Socket(serverName, port)) {
            socket.setSoTimeout(5000);

            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();

            // a. Gửi mã sinh viên và mã câu hỏi
            os.write((message + "\n").getBytes(StandardCharsets.UTF_8));
            os.flush();
            System.out.println("Sent to server: " + message);

            // b. Nhận chuỗi số nguyên từ server
            byte[] buffer = new byte[8192];
            int len = is.read(buffer);
            if (len == -1) {
                throw new IOException("Server closed without sending data");
            }
            String data = new String(buffer, 0, len, StandardCharsets.UTF_8).trim();
            System.out.println("Received from server: " + data);

            // Parse dãy số nguyên
            String[] parts = data.split(",");
            int[] arr = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                arr[i] = Integer.parseInt(parts[i].trim());
            }

            // c. Tính trung bình và tìm 2 số gần nhất với 2*avg
            double avg = Arrays.stream(arr).average().orElse(0.0);
            double target = 2 * avg;
            int num1 = arr[0], num2 = arr[1];
            double bestDiff = Math.abs((num1 + num2) - target);

            for (int i = 0; i < arr.length; i++) {
                for (int j = i + 1; j < arr.length; j++) {
                    int sum = arr[i] + arr[j];
                    double diff = Math.abs(sum - target);
                    if (diff < bestDiff) {
                        bestDiff = diff;
                        num1 = Math.min(arr[i], arr[j]);
                        num2 = Math.max(arr[i], arr[j]);
                    }
                }
            }

            // Format kết quả
            String result = num1 + "," + num2;
            System.out.println("Result to send: " + result);

            // Gửi kết quả lên server
            os.write((result + "\n").getBytes(StandardCharsets.UTF_8));
            os.flush();

            // d. Đóng kết nối
            is.close();
            os.close();
            socket.close();
            System.out.println("Connection closed");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
