import java.io.*;
import java.net.*;
import java.util.*;

public class cliente {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Solicitar el tamaño del vector
        System.out.println("----------------------------");
        System.out.print("Ingrese el tamaño del vector: ");
        int tamanoVector = scanner.nextInt();
        System.out.println("----------------------------");

        // Solicitar el tiempo máximo para resolver el problema
        System.out.println("----------------------------------------------");
        System.out.print("Tiempo máximo de resolución del problema (en segundos): ");
        int tiempoMaximo = scanner.nextInt();
        System.out.println("----------------------------------------------");

        // Mostrar menú de opciones
        int opcion = mostrarMenu(scanner);

        // Generar el vector de números aleatorios
        System.out.println("======================================="); 
        String rutaArchivo = "archivo.txt";
        System.out.println(rutaArchivo);
        List<Integer> vector = generarvector(rutaArchivo, tamanoVector);
        System.out.println("Su vector inicial (primeros 10 elementos): " + vector.subList(0, Math.min(10, vector.size())) + "...");
        System.out.println("=======================================");

        System.out.println("Los últimos 10 elementos de su vector: " +  vector.subList(Math.max(0, vector.size() - 10), vector.size()) + "...");
        System.out.println("=======================================");

        // Configurar conexión con Worker 0
        String worker0Ip = "192.168.1.8"; // Cambia según tu configuración
        int worker0Port = 12345;

        try (Socket socket = new Socket(worker0Ip, worker0Port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            System.out.println("¡Conectado a Worker 0 en " + worker0Ip + " puerto: " + worker0Port + "! ");

            // Crear datos a enviar
            Map<String, Object> data = new HashMap<>();
            data.put("opcion", opcion);
            data.put("vector", vector);
            data.put("tiempo_maximo", tiempoMaximo);

            // Enviar datos
            out.writeObject(data);
            System.out.println("¡Datos enviados correctamente a Worker 0!");

            // Recibir respuesta
            @SuppressWarnings("unchecked")
            Map<String, Object> response = (Map<String, Object>) in.readObject();
            System.out.println("=======================================");
            System.out.println("Respuesta recibida del Worker 0:");
            System.out.println(response.get("mensaje"));
            long tiempoEjecucion = (Long) response.get("tiempo_ejecucion")/100000;
            if ("VECTOR PROCESADO CON ÉXITO EN WORKER 1" == response.get("mensaje") || "ERROR: TIEMPO MÁXIMO EXCEDIDO EN WORKER 1"==response.get("mensaje") ){
                System.out.println("Tiempo de ejecución (segundos): "+response.get("tiempo_ejecucion"));
            }else{
                System.out.println("Tiempo de ejecución (segundos): "+response.get("tiempo_ejecucion"));
            }
            

            @SuppressWarnings("unchecked")
            List<Integer> vectorOrdenado = (List<Integer>) response.get("vector_ordenado");

            // Mostrar el resultado del vector ordenado
            if (vectorOrdenado != null && !vectorOrdenado.isEmpty()) {
                System.out.println("Vector ordenado (primeros 10 elementos): " +
                        vectorOrdenado.subList(0, Math.min(10, vectorOrdenado.size())) + "...");
                System.out.println("Los últimos 10 elementos de su vector: " +  vectorOrdenado.subList(Math.max(0, vectorOrdenado.size() - 10), vectorOrdenado.size()) + "...");
        System.out.println("=======================================");

            } else {
                System.out.println("Vector ordenado: vacío o no disponible.");
            }
            System.out.println("=======================================");

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error al conectar con Worker 0 o procesar la respuesta:");
            e.printStackTrace();
        }
    }

    public static List<Integer>generarvector(String rutaArchivo, int tamanoVector) {
        List<Integer> numeros = new ArrayList<>();
        //for(int i=0; i<=tamanoVector; i=i+1){
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                numeros.add(Integer.parseInt(linea.trim()));
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
    //}
        return numeros;
    }

    private static int mostrarMenu(Scanner scanner) {
        System.out.println("=======================================");
        System.out.println("Seleccione el algoritmo de ordenamiento:");
        System.out.println("1. MergeSort");
        System.out.println("2. HeapSort");
        System.out.println("3. QuickSort");
        System.out.print("Opción seleccionada: ");
        int opcion = scanner.nextInt();
        System.out.println("=======================================");
        return opcion;
    }
}