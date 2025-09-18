package TCP;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class UDP {
    public static void main(String[] args) {
        String serverHost = "203.162.10.109";
        int serverPort = 2208;
        String studentCode = "B22DCVT034";
        String qCode = "Wd1FY112";

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(serverHost, serverPort), 5000);
            socket.setSoTimeout(5000); // tối đa 5s cho mỗi request

            // Luồng vào/ra
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            // 1. Gửi "studentCode;qCode"
            String request = studentCode + ";" + qCode;
            writer.write(request);
            writer.newLine(); // gửi dấu xuống dòng để server nhận biết kết thúc
            writer.flush();
            System.out.println("Sent: " + request);

            // 2. Nhận danh sách domain từ server
            String response = reader.readLine();
            System.out.println("Received: " + response);

            // 3. Lọc tên miền .edu
            String[] domains = response.split(",");
            List<String> eduDomains = new ArrayList<>();
            for (String d : domains) {
                d = d.trim();
                if (d.endsWith(".edu")) {
                    eduDomains.add(d);
                }
            }

            String sendBack = String.join(", ", eduDomains);

            // 4. Gửi danh sách .edu về server
            writer.write(sendBack);
            writer.newLine();
            writer.flush();
            System.out.println("Sent back .edu domains: " + sendBack);

            // 5. Đóng kết nối
            socket.close();
            System.out.println("Connection closed.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
