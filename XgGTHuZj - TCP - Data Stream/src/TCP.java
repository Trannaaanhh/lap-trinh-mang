import java.io.*;
import java.net.*;
import  java.util.*;

public class TCP {
    public static void main(String[] args) {
        String serverHost = "203.162.10.109";
        int serverPort = 2207;

        String studentCode = "B22DCVT034";
        String qCode = "XgGTHuZj";
        String message = studentCode + ";" + qCode;

        try (Socket socket = new Socket(serverHost, serverPort)) {
            socket.setSoTimeout(5000); // tối đa 5s

            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            // a. Gửi mã sinh viên;qCode
            dos.writeUTF(message);
            dos.flush();
            System.out.println("Sent: " + message);

            // b. Nhận k và chuỗi mảng số nguyên
            int k = dis.readInt();
            String arrayStr = dis.readUTF();
            System.out.println("Received k = " + k);
            System.out.println("Received array: " + arrayStr);

            // Chuyển chuỗi thành mảng int
            String[] parts = arrayStr.split(",");
            int[] arr = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                arr[i] = Integer.parseInt(parts[i].trim());
            }

            // c. Chia mảng thành đoạn dài k và đảo ngược từng đoạn
            for (int i = 0; i < arr.length; i += k) {
                int left = i;
                int right = Math.min(i + k - 1, arr.length - 1);
                while (left < right) {
                    int temp = arr[left];
                    arr[left] = arr[right];
                    arr[right] = temp;
                    left++;
                    right--;
                }
            }

            // Chuyển lại thành chuỗi
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < arr.length; i++) {
                result.append(arr[i]);
                if (i < arr.length - 1) result.append(",");
            }
            String resultStr = result.toString();
            System.out.println("Processed result: " + resultStr);

            // Gửi chuỗi kết quả lên server
            dos.writeUTF(resultStr);
            dos.flush();
            System.out.println("Sent result to server.");

            // d. Đóng kết nối
            dis.close();
            dos.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
