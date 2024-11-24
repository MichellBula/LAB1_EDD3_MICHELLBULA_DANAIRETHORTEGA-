import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class worker_1 {
    public static void main(String[] args) {
        int worker1Port = 5000;
        

        try (ServerSocket serverSocket = new ServerSocket(worker1Port)) {
            System.out.println("WORKER 1 ESPERANDO CONEXIÓN EN EL PUERTO " + worker1Port + "...");

            while (true) {
                try (Socket socket = serverSocket.accept();
                     ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                     ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

                    System.out.println("¡CONEXIÓN ESTABLECIDA CON WORKER 0!");

                    // Recibir datos de Worker 0
                    Map<String, Object> data = (Map<String, Object>) in.readObject();
                    System.out.println("DATOS RECIBIDOS: ");
                    System.out.println("Tiempo: "+data.get("tiempo_maximo"));
                    System.out.println(" ");
                    long tiempoInicio = System.nanoTime()/1000;                    
                    List<Integer> vector = (List<Integer>) data.get("vector");
                    int opcion = (int) data.get("opcion");
                    int tiempo_max = (int) data.get("tiempo_maximo");

                    // Crear un ExecutorService para gestionar el tiempo de ejecución
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    Future<List<Integer>> future = executor.submit(() -> procesarVector(vector, opcion));
                    List<Integer> vectorOrdenadonoterminado = procesarVector(vector, opcion);
                    try {
                        // Esperar a que termine el procesamiento o se alcance el tiempo límite
                        List<Integer> vectorOrdenado = future.get(tiempo_max, TimeUnit.SECONDS);

                        // Preparar respuesta
                        Map<String, Object> response = new HashMap<>();
                        long tiempoFin = System.nanoTime()/1000;
                        long duracion = tiempoFin - tiempoInicio;
                        response.put("mensaje", "VECTOR PROCESADO CON ÉXITO EN WORKER 1");
                        response.put("vector_ordenado", vectorOrdenado);
                        response.put("tiempo_ejecucion", tiempo_max);
                        // Enviar respuesta a Worker 0
                        out.writeObject(response);
                        System.out.println("¡RESPUESTA ENVIADA A WORKER 0!");
                        System.out.println("RESPUESTA FINAL ENVIADA EXITOSAMENTE AL CLIENTE");

                    } catch (TimeoutException e) {
                        // Si se agota el tiempo, enviar un mensaje de error o respuesta predeterminada
                        System.out.println("¡TIMEOUT ALCANZADO EN WORKER 1!, enviando respuesta");
                        long tiempoFin = System.nanoTime()/1000;
                        long duracion = tiempoFin - tiempoInicio;
                        Map<String, Object> response = new HashMap<>();
                        response.put("mensaje", "ERROR: TIEMPO MÁXIMO EXCEDIDO EN WORKER 1");
                        response.put("vector_ordenado", vectorOrdenadonoterminado);
                        System.out.println("skdjfd"+duracion);
                        response.put("tiempo_ejecucion", duracion);

                        // Enviar respuesta a Worker 0
                        out.writeObject(response);
                        System.out.println("RESPUESTA ENVIADA A WORKER 0...");
                        System.out.println("RESPUESTA FINAL ENVIADA EXITOSAMENTE AL CLIENTE");
                        // Cancelar la tarea en ejecución
                        future.cancel(true);
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

    private static List<Integer> procesarVector(List<Integer> vector, int opcion) {
        List<Integer> resultado = new ArrayList<>(vector);
        Ordenamiento ordenamiento = new Ordenamiento();  // Crear instancia de la clase Ordenamiento

        // Según la opción seleccionada, se llama al algoritmo de ordenamiento correspondiente
        switch (opcion) {
            case 1: // Heapsort
                ordenamiento.heapSort(resultado);
                break;
            case 2: // QuickSort
                ordenamiento.quickSort(resultado, 0, resultado.size() - 1);
                break;
            case 3: // MergeSort
                ordenamiento.mergeSort(resultado, 0, resultado.size() - 1);
                break;
            default:
                System.out.println("OPCIÓN NO VÁLIDA... ORDENANDO POR DEFECTO CON MERGESORT");
                ordenamiento.mergeSort(resultado, 0, resultado.size() - 1);
                break;
        }

        return resultado;
    }
}