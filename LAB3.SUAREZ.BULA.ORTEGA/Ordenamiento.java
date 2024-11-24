import java.util.*;

public class Ordenamiento {

    // Método HeapSort
    
    public List heapSort(List<Integer> lista) {
        int n = lista.size();
        // Construir el heap
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(lista, n, i);
        }
        // Extraer elementos del heap
        for (int i = n - 1; i > 0; i--) {
            // Mover el elemento actual a la raíz
            Collections.swap(lista, 0, i);

            // Llamar a heapify para reestructurar el heap
            heapify(lista, i, 0);
        }
        return lista;
    }

    private void heapify(List<Integer> lista, int n, int i) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < n && lista.get(left) > lista.get(largest)) {
            largest = left;
        }

        if (right < n && lista.get(right) > lista.get(largest)) {
            largest = right;
        }

        if (largest != i) {
            Collections.swap(lista, i, largest);
            heapify(lista, n, largest);
        }
    }

    // Método QuickSort
    public void quickSort(List<Integer> lista, int low, int high) {
        if (low < high) {
            int pi = partition(lista, low, high);

            // Ordenar elementos antes y después de la partición
            quickSort(lista, low, pi - 1);
            quickSort(lista, pi + 1, high);
        }
    }

    private int partition(List<Integer> lista, int low, int high) {
        int pivot = lista.get(high);
        int i = (low - 1);

        for (int j = low; j < high; j++) {
            if (lista.get(j) <= pivot) {
                i++;
                Collections.swap(lista, i, j);
            }
        }

        Collections.swap(lista, i + 1, high);
        return i + 1;
    }

    // Método MergeSort
    public void mergeSort(List<Integer> lista, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;

            // Recursión sobre la mitad izquierda y derecha
            mergeSort(lista, left, mid);
            mergeSort(lista, mid + 1, right);

            // Combina las dos mitades ordenadas
            merge(lista, left, mid, right);
        }
    }

    private void merge(List<Integer> lista, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        // Crear arrays temporales
        List<Integer> L = new ArrayList<>(n1);
        List<Integer> R = new ArrayList<>(n2);

        // Copiar datos a los arrays temporales L[] y R[]
        for (int i = 0; i < n1; ++i) L.add(i, lista.get(left + i));
        for (int j = 0; j < n2; ++j) R.add(j, lista.get(mid + 1 + j));

        // Fusionar los arrays temporales
        int i = 0, j = 0;
        int k = left;
        while (i < n1 && j < n2) {
            if (L.get(i) <= R.get(j)) {
                lista.set(k, L.get(i));
                i++;
            } else {
                lista.set(k, R.get(j));
                j++;
            }
            k++;
        }

        // Copiar los elementos restantes de L[] si los hay
        while (i < n1) {
            lista.set(k, L.get(i));
            i++;
            k++;
        }

        // Copiar los elementos restantes de R[] si los hay
        while (j < n2) {
            lista.set(k, R.get(j));
            j++;
            k++;
        }
    }
}
