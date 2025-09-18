import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

public class TCP {

    public static void main(String[] args) {
        // --- Cấu hình kết nối ---
        String serverAddress = "203.162.10.109";
        int port = 2206;

        // --- Thông tin sinh viên và mã câu hỏi ---
        String studentCode = "B22DCVT034";
        String qCode = "UCMF1Ro7";

        try (Socket socket = new Socket(serverAddress, port)) {
            socket.setSoTimeout(5000);

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            // a. Gửi mã sinh viên + mã câu hỏi
            String request = studentCode + ";" + qCode + "\n";
            out.write(request.getBytes());
            out.flush();
            System.out.println("Đã gửi: " + request.trim());

            // b. Nhận dữ liệu từ server
            byte[] buffer = new byte[65536];
            int bytesRead = in.read(buffer);
            if (bytesRead == -1) {
                System.out.println("Không nhận được dữ liệu từ server.");
                return;
            }

            String response = new String(buffer, 0, bytesRead).trim();
            System.out.println("Đã nhận từ server: " + response);

            // c. Xử lý dữ liệu để lấy kết quả
            String result = processResponse(response);

            // d. Gửi kết quả lên server
            out.write(result.getBytes());
            out.flush();
            System.out.println("Đã gửi kết quả: " + result.trim());

            System.out.println("Hoàn thành và đóng kết nối.");

        } catch (Exception ex) {
            System.err.println("Đã xảy ra lỗi: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Xử lý chuỗi dữ liệu từ server và trả về kết quả dạng:
     * "vị trí,tổng trái,tổng phải,độ lệch\n"
     */
    private static String processResponse(String response) {
        int[] numbers = Arrays.stream(response.split(","))
                .map(String::trim)
                .mapToInt(Integer::parseInt)
                .toArray();

        long totalSum = 0;
        for (int num : numbers) totalSum += num;

        long leftSum = 0;
        long minDiff = Long.MAX_VALUE;
        int bestPos = -1;
        long bestLeft = 0, bestRight = 0;

        for (int i = 0; i < numbers.length; i++) {
            long rightSum = totalSum - leftSum - numbers[i];
            long diff = Math.abs(leftSum - rightSum);

            // Log each step for debugging
            System.out.printf("%d,%d,%d,%d |%d| ", i + 1, leftSum, rightSum, diff, bestPos);
            if (diff < minDiff || (diff == minDiff && (bestPos == -1 || i + 1 < bestPos))) {
                minDiff = diff;
                bestPos = i + 1;
                bestLeft = leftSum;
                bestRight = rightSum;
                System.out.printf("%d,%d,%d,%d |%d| ", bestPos, bestLeft, bestRight, minDiff, bestPos);
            }
            leftSum += numbers[i];
        }
        System.out.println();

        return bestPos + "," + bestLeft + "," + bestRight + "," + minDiff + "\n";
    }
}
