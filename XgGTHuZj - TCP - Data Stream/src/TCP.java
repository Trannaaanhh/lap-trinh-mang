import java.io.*;
import java.net.*;
import  java.util.*;

public class TCP{
    public static void main(String[] args){
        String serverHost = "203.162.10.109";
        int serverPort = 2207;

        String studentCode = "B22DCVT034";
        String qCode = "XgGTHuZj";
        String message = studentCode + ";" + qCode;

        try(Socket socket = new Socket(serverHost, serverPort)){
            socket.setSoTimeout(5000);

            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            // a.Gửi mã sinh viên và mã câu hỏi theo định dạng "studentCode;qCode".
            dos.writeUTF(message);
            dos.flush();
            System.out.println("Sent: " + message);

            // b.Nhận lần lượt:
            //•	Một số nguyên k là độ dài đoạn.
            //•	Chuỗi chứa mảng số nguyên, các phần tử được phân tách bởi dấu phẩy ",".
            int k = dis.readInt();
            String arrayStr = dis.readUTF();
            System.out.println("Received k: " + k);
            System.out.println("Received array: " + arrayStr);
            // Chuyển chuỗi mảng thành INT
            String[] parts = arrayStr.split(",");
            int[] arr = new int[parts.length];
            for(int i = 0 ; i < parts.length; i++){
                arr[i] = Integer.parseInt(parts[i].trim());
            }

            //c.Thực hiện chia mảng thành các đoạn có độ dài k và đảo ngược mỗi đoạn, sau đó gửi mảng đã xử lý lên server.
            // Chia mảng và đảo ngược từng đoạn
            for(int i = 0; i < arr.length; i += k){
                int left = i;
                int right = Math.min(i + k - 1, arr.length -1);
                while(left < right){
                    int temp = arr[left];
                    arr[left] = arr[right];
                    arr[right] = temp;
                    left++;
                    right--;
                }
            }
            // Chuyển mảng thành chuỗi
            StringBuilder result = new StringBuilder();
            for(int i = 0; i < arr.length; i++){
                result.append(arr[i]);
                if (i < arr.length - 1) result.append(",");
            }
            String resultStr = result.toString();
            System.out.println("Processed result: " + resultStr);
            // Gửi mảng đã xử lý lên server
            dos.writeUTF(resultStr);
            dos.flush();
            System.out.println("Sent Result to server.");

            //d. Đóng kết nối và kết thúc chương trình
            dis.close();
            dos.close();
            socket.close();
            System.out.println("Connection closed.");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}