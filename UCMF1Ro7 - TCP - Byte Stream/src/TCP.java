import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

public class TCP {
    public static void main(String[] args) {
        String serverAddress = "203.162.10.109";
        int port = 2206;
        String studentCode = "B22DCVT034";
        String qCode = "UCMF1Ro7";

        try (Socket socket = new Socket(serverAddress, port)) {
            socket.setSoTimeout(5000);

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            // a. Send studentCode;qCode
            String request = studentCode + ";" + qCode + "\n";
            out.write(request.getBytes());
            out.flush();

            // b. Receive data from server
            byte[] buffer = new byte[65536];
            int bytesRead = in.read(buffer);
            if (bytesRead == -1) {
                System.out.println("No data received from server.");
                return;
            }
            String response = new String(buffer, 0, bytesRead).trim();
            System.out.println("Received from server: " + response);

            // c. Process data and prepare result
            String result = processResponse(response);
            System.out.println("Result to send: " + result);

            // d. Send result to server
            out.write(result.getBytes());
            out.flush();

            System.out.println("Done. Connection closed.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String processResponse(String response) {
        int[] numbers = Arrays.stream(response.split(","))
                .map(String::trim)
                .mapToInt(Integer::parseInt)
                .toArray();

        long totalSum = Arrays.stream(numbers).sum();
        long leftSum = 0;
        long minDiff = Long.MAX_VALUE;
        int bestPos = -1;
        long bestLeft = 0, bestRight = 0;

        for (int i = 0; i < numbers.length; i++) {
            long rightSum = totalSum - leftSum - numbers[i];
            long diff = Math.abs(leftSum - rightSum);
            if (diff < minDiff || (diff == minDiff && (bestPos == -1 || i < bestPos))) {
                minDiff = diff;
                bestPos = i;
                bestLeft = leftSum;
                bestRight = rightSum;
            }
            leftSum += numbers[i];
        }
        return bestPos + "," + bestLeft + "," + bestRight + "," + minDiff;
    }
}