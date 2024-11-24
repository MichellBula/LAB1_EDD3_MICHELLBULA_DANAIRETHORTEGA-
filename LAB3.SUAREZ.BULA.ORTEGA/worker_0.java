import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class worker_0 {
    public static void main(String[] args) {
        String worker1Ip = "192.168.1.8"; // Cambia según tu configuración
        int worker1Port = 5000;
        int worker0Port = 12345;

        try (ServerSocket serverSocket = new ServerSocket(worker0Port)) {
            System.out.println("WORKER 0 ESPERANDO CONEXIÓN EN EL PUERTO " + worker0Port + "...");

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                        ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

                    System.out.println("¡CONEXIÓN ESTABLECIDA CON CLIENTE!");

                    // Recibir datos del cliente
                    Map<String, Object> data = (Map<String, Object>) in.readObject();
                    int tiempo_max = (int) data.get("tiempo_maximo");
                    System.out.println("DATOS RECIBIDOS: ");
                    System.out.println("Tiempo: " + data.get("tiempo_maximo"));
                    System.out.println(" ");

                    // Crear un ExecutorService para gestionar el tiempo de ejecución
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    Future<List<Integer>> future = executor.submit(() -> procesarVector(data));
                    long tiempoInicio = System.nanoTime()/1000;
                    try {
                        // Esperar a que termine el procesamiento o se alcance el tiempo límite
                        List<Integer> vectorOrdenado = future.get(tiempo_max, TimeUnit.SECONDS);
                        long tiempoFin = System.nanoTime()/1000;
                        long duracion = tiempoFin - tiempoInicio;
                        // Enviar el resultado de Worker0
                        Map<String, Object> respuesta = new HashMap<>();
                        respuesta.put("mensaje", "VECTOR PROCESADO CON ÉXITO EN WORKER 0");
                        respuesta.put("vector_ordenado", vectorOrdenado);
                        if (duracion>tiempo_max){
                            duracion= tiempo_max;
                            respuesta.put("tiempo_ejecucion", duracion);
                        }else{
                            respuesta.put("tiempo_ejecucion", duracion);
                        }
                        data.put("vector_ordenado", vectorOrdenado);
                        out.writeObject(respuesta);
                        System.out.println("RESPUESTA FINAL ENVIADA EXITOSAMENTE AL CLIENTE");
                    } catch (TimeoutException e) {
                        // Si se agota el tiempo, se cancela la tarea y se pasa el control a Worker1
                        System.out.println("¡TIMEOUT ALCANZADO!, redirigiendo a Worker 1...");

                        // Cancelar la tarea si aún está en ejecución
                        future.cancel(true);

                        // Enviar los datos a Worker 1
                        try (Socket worker1Socket = new Socket(worker1Ip, worker1Port);
                                ObjectOutputStream worker1Out = new ObjectOutputStream(worker1Socket.getOutputStream());
                                ObjectInputStream worker1In = new ObjectInputStream(worker1Socket.getInputStream())) {

                            System.out.println("¡CONECTADO CORRECTAMENTE A WORKER 1!");
                            // Enviar datos a Worker 1
                            Map<String, Object> respuesta = new HashMap<>();
                            respuesta.put("mensaje", "VECTOR PROCESADO CON ÉXITO");
                            worker1Out.writeObject(data);
                            System.out.println("¡DATOS ENVIADOS A WORKER 1 CON ÉXITO!");

                            // Recibir respuesta de Worker 1
                            Map<String, Object> response = (Map<String, Object>) worker1In.readObject();
                            // Enviar la respuesta de Worker 1 al cliente
                            out.writeObject(response);
                            System.out.println("RESPUESTA FINAL ENVIADA EXITOSAMENTE AL CLIENTE");
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                    executor.shutdownNow(); // Finalizar el ExecutorService
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static List<Integer> procesarVector(Map<String, Object> data) {
        List<Integer> vector = (List<Integer>) data.get("vector");
        int opcion = (int) data.get("opcion"); // Opción del algoritmo de ordenamiento
        List<Integer> resultado = new ArrayList<>(vector);
        Ordenamiento ordenamiento = new Ordenamiento(); // Crear instancia de la clase Ordenamiento
        List<Integer> ordenado_enviar = new ArrayList<>();
        // Según la opción seleccionada, se llama al algoritmo de ordenamiento
        // correspondiente
        switch (opcion) {
            case 1: // MergeSort
                ordenamiento.mergeSort(resultado, 0, resultado.size() - 1);
                break;
            case 2: // HeapSort
                ordenado_enviar = ordenamiento.heapSort(resultado);
                break;
            case 3: // QuickSort
                ordenamiento.quickSort(resultado, 0, resultado.size() - 1);
                break;
            default:
                System.out.println("OPCIÓN NO VÁLIDA... ORDENANDO POR DEFECTO CON MERGESORT");
                ordenamiento.mergeSort(resultado, 0, resultado.size() - 1);
                break;
        }
        return resultado;
    }
}