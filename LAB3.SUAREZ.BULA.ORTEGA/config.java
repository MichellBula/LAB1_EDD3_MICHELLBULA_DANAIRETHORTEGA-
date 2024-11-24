import java.util.HashMap;

public class config {
    public static void main(String[] args) {
        HashMap<String, Object> configParams = new HashMap<>();

        configParams.put("worker_0_ip", "192.168.1.8");
        configParams.put("worker_0_port", 12345);
        configParams.put("worker_1_ip", "192.168.1.45");
        configParams.put("worker_1_port", 5000);
        configParams.put("client_ip", "192.168.1.44");
        configParams.put("client_port", 8080);
        configParams.put("EXIT_MESSAGE", "exit");

        // Imprimir valores del HashMap
        configParams.forEach((key, value) -> System.out.println(key + ": " + value));
    }
}
