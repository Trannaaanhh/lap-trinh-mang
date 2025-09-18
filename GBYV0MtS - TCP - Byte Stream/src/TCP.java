import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TCP {
    public static void main(String[] args) {
        String serverAddress = "203.162.10.109";
        int port = 2206;
        String studentCode = "B22DCVT034";
        String qCode = "GBYV0MtS";

        try (Socket socket = new Socket(serverAddress, port)) {
            socket.setSoTimeout(5000);

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            // a. Gửi mã sinh viên và mã câu hỏi
            String request = studentCode + ";" + qCode + "\n";
            out.write(request.getBytes());
            out.flush();
            System.out.println("Sent: " + request.trim());

            // b. Nhận dữ liệu từ server
            byte[] buffer = new byte[1024];
            int bytesRead = in.read(buffer);
            if (bytesRead == -1) {
                System.out.println("Không nhận được dữ liệu từ server!");
                return;
            }
            String response = new String(buffer, 0, bytesRead).trim();
            System.out.println("From server: " + response);

            // c. Tính tổng các số nguyên tố
            String[] parts = response.split(",");
            int sumPrimes = 0;
            for (String p : parts) {
                int num = Integer.parseInt(p.trim());
                if (isPrime(num)) {
                    sumPrimes += num;
                }
            }

            // d. Gửi kết quả lên server
            String result = sumPrimes + "\n";
            out.write(result.getBytes());
            out.flush();
            System.out.println("Sent: " + sumPrimes);

            System.out.println("End program");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Hàm kiểm tra số nguyên tố
    private static boolean isPrime(int n) {
        if (n < 2) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }
}
