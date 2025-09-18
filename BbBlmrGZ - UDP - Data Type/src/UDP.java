import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class UDP {
    public static void main(String[] args) {
        String serverAddress = "203.162.10.109"; // server thật
        int port = 2207;
        String msv = "B22DCVT034";
        String qCode = "BbBlmrGZ";

        try (DatagramSocket socket = new DatagramSocket()) {
            // a. Gửi thông điệp: ";studentCode;qCode"
            String request = ";" + msv + ";" + qCode;
            byte[] sendData = request.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(
                    sendData, sendData.length,
                    InetAddress.getByName(serverAddress), port
            );
            socket.send(sendPacket);
            System.out.println("Sent: " + request);

            // b. Nhận dữ liệu từ server
            byte[] buffer = new byte[4096];
            DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(receivePacket);
            String response = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
            System.out.println("From server: " + response);

            // Format: requestId;a1,a2,...,a50
            String[] parts = response.split(";", 2);
            if (parts.length < 2) {
                System.err.println("Sai định dạng dữ liệu từ server!");
                return;
            }
            String requestId = parts[0];
            String[] numbers = parts[1].split(",");

            int[] arr = Arrays.stream(numbers)
                    .mapToInt(Integer::parseInt)
                    .toArray();

            int max = Arrays.stream(arr).max().getAsInt();
            int min = Arrays.stream(arr).min().getAsInt();

            // c. Gửi lại: "requestId;max,min"
            String result = requestId + ";" + max + "," + min;
            byte[] resultData = result.getBytes();
            DatagramPacket resultPacket = new DatagramPacket(
                    resultData, resultData.length,
                    InetAddress.getByName(serverAddress), port
            );
            socket.send(resultPacket);
            System.out.println("Sent: " + result);

            // d. Đóng socket
            socket.close();
            System.out.println("End program");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
