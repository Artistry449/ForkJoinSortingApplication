import java.util.Arrays;
import java.util.concurrent.*;;

class ParentTask <T extends Comparable<T>> extends RecursiveAction{
    private final static int THRESHOLD = 100;
    protected T[] appData;
    protected int left;
    protected int right;

    public ParentTask(T[] arr, int left, int right){
        this.appData = arr;
        this.left = left;
        this.right = right;
    }

    public static <T extends Comparable<T>> void sortUsingMergeSortApp(T[] array){
        ForkJoinPool pool = new ForkJoinPool();
        MergeSortApp<T> task = new MergeSortApp<>(array, 0, array.length - 1);
        pool.invoke(task);
    }

    public static <T extends Comparable<T>> void sortUsingQuickSortApp(T[] array){
        ForkJoinPool pool = new ForkJoinPool();
        QuickSortApp<T> task = new QuickSortApp<>(array, 0, array.length - 1);
        pool.invoke(task);
    }

    @Override
    protected void compute() {
        
    }

    private static <T extends Comparable<T>> void insertionSort(T[] arr, int left, int right) {
    for (int i = left + 1; i <= right; ++i) {
        T key = arr[i];
        int j = i - 1;
        while (j >= left && arr[j].compareTo(key) > 0) {
            arr[j + 1] = arr[j];
            j = j - 1;
        }
        arr[j + 1] = key;
    }
}
    public static class MergeSortApp <T extends Comparable <T>> extends ParentTask<T>{


        public MergeSortApp(T[] array, int left, int right){
            super(array, left, right);
        }

        @Override
        protected void compute(){
            if(right - left < THRESHOLD){
                insertionSort(appData, left, right);
            }
            else {
                int mid = ((right - left) / 2) + left;
                MergeSortApp<T> leftTask = new MergeSortApp<>(appData, left, mid);
                MergeSortApp<T> rightTask = new MergeSortApp<>(appData, mid+1, right);

                invokeAll(leftTask, rightTask);

                // leftTask.fork();
                // rightTask.fork();

                // leftTask.join();
                // rightTask.join();

                merge(appData, left, mid, right);
            }
        }

        private void merge(T[] data, int start, int mid, int end){
            int n1 = mid - start + 1;
            int n2 = end - mid;
    
            T[] arr1 = Arrays.copyOfRange(data, start, mid + 1);
            T[] arr2 = Arrays.copyOfRange(data, mid + 1, end + 1);

            // for(int i=0; i<n1; i++){
            //     arr1[i] = data[i];
            // }
    
            // for(int i=0; i<n2; i++){
            //     arr2[i] = data[i];
            // }
    
            int i = 0;
            int j = 0;
            int k = start;
    
            while(i < n1 && j < n2){
                if(arr1[i].compareTo(arr2[j]) <= 0){
                    data[k] = arr1[i];
                    i++;
                }
                else {
                    data[k] = arr2[j]; 
                    j++;
                }
                k++;
            }
    
            while(i < n1){
                data[k] = arr1[i];
                i++;
                k++;
            }
            
            while(j < n2){
                data[k] = arr2[j];
                j++;
                k++;
            }
        }
    }

    public static class QuickSortApp <T extends Comparable <T>> extends ParentTask<T>{

        public QuickSortApp(T[] arr, int left, int right) {
            super(arr, left, right);
        }
        
        @Override
        protected void compute(){
            if(right - left < THRESHOLD){
                insertionSort(appData, left, right);
            }
            else {
                int pivot = partition(appData, left, right);
                QuickSortApp leftTask = new QuickSortApp<>(appData, left, pivot - 1);
                QuickSortApp rightTask = new QuickSortApp<>(appData, pivot + 1, right);

                invokeAll(leftTask, rightTask);

                // leftTask.fork();
                // rightTask.fork();

                // leftTask.join();
                // rightTask.join();
            }
        }

        private int partition(T[] arr, int low, int high){
            T pivot = arr[high];

            int i = low - 1;

            for(int j = low; j<high; j++){
                T compareElement = arr[j];
                if(compareElement.compareTo(pivot) < 0){
                    i++;
                    swap(arr, i, j);
                }
            }

            swap(arr, i+1, high);
            return i+1;
        }

        private void swap(T[] arr, int i, int j) {
            T temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }    
    }
}

class ForkJoinSortingApplication {
    public static void main(String args[]){

        System.out.println("\n\nFork-Join эрэмбэлэлтийн программд тавтай морилно уу");
        System.out.println();

        long startTime;
        long endTime;

        Integer[] array1 = {28,18,77,91,42,89,77,63,29,24,36,10,16,52,38,91,81,79,59,23,16,64,89,62,74,13,96,71,19,73,72,78,92,28,49,41,88,29,58,75,43,89,94,52,38,64,78,21,35,46,67,14,55,77,83,48,60,14,23,67,38,89,17,72,19,64,10,83,62,39,26,99,66,24,92,44,68,88,55,23,76,37,67,70,19,43,90,94,36,25,62,32,84,18,49,65,71,28,96,36,47,24,99,89,12,48,74,41,53,27,88,58,90,73,24,16,85,96,45,87,32,85,26,71,95,30,87,17,59,72,42,71,44,23,77,48,57,15,62,21,80,95,99,39,43,12,61,82,13,36,62,73,56,32,68,59,21,87,17,93,37,79,50,72,49,18,16,84,14,33,60,28,31,48,93,56,65,19,31,77,47,24,15,63,86,90,12,84,70,43,76,29,75,33,42,98,71,26,78,99,25,82,41,56,83,22,97,58,16,79,59,78,41,39,93,31,74,63,20,40,76,87,55,30,66,27,60,15,42,36,78,67,13,45,80,90,49,81,73,76,98,35,74,37,86,23,77,19,89,14,70,86,27,61,34,42,39,55,94,63,81,72,28,18,50,75,24,44,66,42,73,19,12,68,45,79,38,60,14,47,28,85,48,71,17,92,57,82,41,67,95,30,80,90,12,65,97,58,73,19,48,52,88,35,18,42,91,80,59,87,32,44,61,88,53,99,58,66,43,89,77,96,42,89,77,63,29,24,36,10,16,52,38,91,81,79,59,23,16,64,89,62,74,13,96,71,19,73,72,78,92,28,49,41,88,29,58,75,43,89,94,52,38,64,78,21,35,46,67,14,55,77,83,48,60,14,23,67,38,89,17,72,19,64,10,83,62,39,26,99,66,24,92,44,68,88,55,23,76,37,67,70,19,43,90,94,36,25,62,32,84,18,49,65,71,28,96,36,47,24,99,89,12,48,74,41,53,27,88,58,90,73,24,16,85,96,45,87,32,85,26,71,95,30,87,17,59,72,42,71,44,23,77,48,57,15,62,21,80,95,99,39,43,12,61,82,13,36,62,73,56,32,68,59,21,87,17,93,37,79,50,72,49,18,16,84,14,33,60,28,31,48,93,56,65,19,31,77,47,24,15,63,86,90,12,84,70,43,76,29,75,33,42,98,71,26,78,99,25,82,41,56,83,22,97,58,16,79,59,78,41,39,93,31,74,63,20,40,76,87,55,30,66,27,60,15,42,36,78,67,13,45,80,90,49,81,73,76,98,35,74,37,86,23,77,19,89,14,70,86,27,61,34,42,39,55,94,63,81,72,28,18,50,75,24,44,66,42,73,19,12,68,45,79,38,60,14,47,28,85,48,71,17,92,57,82,41,67,95,30,80,90,12,65,97,58,73,19,48,52,88,35,18,42,91,80,59,87,32,44,61,88,53,99,58,66,43,89,77,96, 1000};

        startTime = System.currentTimeMillis();
        ParentTask.sortUsingMergeSortApp(array1);
        endTime = System.currentTimeMillis();
        System.out.println("------> Mergesort алгоритм               ашиглавал: " + (endTime - startTime) + " мильсекунд");
        System.out.println();

        Integer[] array2 = {28,18,77,91,42,89,77,63,29,24,36,10,16,52,38,91,81,79,59,23,16,64,89,62,74,13,96,71,19,73,72,78,92,28,49,41,88,29,58,75,43,89,94,52,38,64,78,21,35,46,67,14,55,77,83,48,60,14,23,67,38,89,17,72,19,64,10,83,62,39,26,99,66,24,92,44,68,88,55,23,76,37,67,70,19,43,90,94,36,25,62,32,84,18,49,65,71,28,96,36,47,24,99,89,12,48,74,41,53,27,88,58,90,73,24,16,85,96,45,87,32,85,26,71,95,30,87,17,59,72,42,71,44,23,77,48,57,15,62,21,80,95,99,39,43,12,61,82,13,36,62,73,56,32,68,59,21,87,17,93,37,79,50,72,49,18,16,84,14,33,60,28,31,48,93,56,65,19,31,77,47,24,15,63,86,90,12,84,70,43,76,29,75,33,42,98,71,26,78,99,25,82,41,56,83,22,97,58,16,79,59,78,41,39,93,31,74,63,20,40,76,87,55,30,66,27,60,15,42,36,78,67,13,45,80,90,49,81,73,76,98,35,74,37,86,23,77,19,89,14,70,86,27,61,34,42,39,55,94,63,81,72,28,18,50,75,24,44,66,42,73,19,12,68,45,79,38,60,14,47,28,85,48,71,17,92,57,82,41,67,95,30,80,90,12,65,97,58,73,19,48,52,88,35,18,42,91,80,59,87,32,44,61,88,53,99,58,66,43,89,77,96,42,89,77,63,29,24,36,10,16,52,38,91,81,79,59,23,16,64,89,62,74,13,96,71,19,73,72,78,92,28,49,41,88,29,58,75,43,89,94,52,38,64,78,21,35,46,67,14,55,77,83,48,60,14,23,67,38,89,17,72,19,64,10,83,62,39,26,99,66,24,92,44,68,88,55,23,76,37,67,70,19,43,90,94,36,25,62,32,84,18,49,65,71,28,96,36,47,24,99,89,12,48,74,41,53,27,88,58,90,73,24,16,85,96,45,87,32,85,26,71,95,30,87,17,59,72,42,71,44,23,77,48,57,15,62,21,80,95,99,39,43,12,61,82,13,36,62,73,56,32,68,59,21,87,17,93,37,79,50,72,49,18,16,84,14,33,60,28,31,48,93,56,65,19,31,77,47,24,15,63,86,90,12,84,70,43,76,29,75,33,42,98,71,26,78,99,25,82,41,56,83,22,97,58,16,79,59,78,41,39,93,31,74,63,20,40,76,87,55,30,66,27,60,15,42,36,78,67,13,45,80,90,49,81,73,76,98,35,74,37,86,23,77,19,89,14,70,86,27,61,34,42,39,55,94,63,81,72,28,18,50,75,24,44,66,42,73,19,12,68,45,79,38,60,14,47,28,85,48,71,17,92,57,82,41,67,95,30,80,90,12,65,97,58,73,19,48,52,88,35,18,42,91,80,59,87,32,44,61,88,53,99,58,66,43,89,77,96, 1000};

        startTime = System.currentTimeMillis();
        ParentTask.sortUsingQuickSortApp(array2);
        endTime = System.currentTimeMillis();
        System.out.println("------> Quicksort алгоритм               ашиглавал: " + (endTime - startTime) + " мильсекунд");
        System.out.println();

        Integer[] array3 = {28,18,77,91,42,89,77,63,29,24,36,10,16,52,38,91,81,79,59,23,16,64,89,62,74,13,96,71,19,73,72,78,92,28,49,41,88,29,58,75,43,89,94,52,38,64,78,21,35,46,67,14,55,77,83,48,60,14,23,67,38,89,17,72,19,64,10,83,62,39,26,99,66,24,92,44,68,88,55,23,76,37,67,70,19,43,90,94,36,25,62,32,84,18,49,65,71,28,96,36,47,24,99,89,12,48,74,41,53,27,88,58,90,73,24,16,85,96,45,87,32,85,26,71,95,30,87,17,59,72,42,71,44,23,77,48,57,15,62,21,80,95,99,39,43,12,61,82,13,36,62,73,56,32,68,59,21,87,17,93,37,79,50,72,49,18,16,84,14,33,60,28,31,48,93,56,65,19,31,77,47,24,15,63,86,90,12,84,70,43,76,29,75,33,42,98,71,26,78,99,25,82,41,56,83,22,97,58,16,79,59,78,41,39,93,31,74,63,20,40,76,87,55,30,66,27,60,15,42,36,78,67,13,45,80,90,49,81,73,76,98,35,74,37,86,23,77,19,89,14,70,86,27,61,34,42,39,55,94,63,81,72,28,18,50,75,24,44,66,42,73,19,12,68,45,79,38,60,14,47,28,85,48,71,17,92,57,82,41,67,95,30,80,90,12,65,97,58,73,19,48,52,88,35,18,42,91,80,59,87,32,44,61,88,53,99,58,66,43,89,77,96,42,89,77,63,29,24,36,10,16,52,38,91,81,79,59,23,16,64,89,62,74,13,96,71,19,73,72,78,92,28,49,41,88,29,58,75,43,89,94,52,38,64,78,21,35,46,67,14,55,77,83,48,60,14,23,67,38,89,17,72,19,64,10,83,62,39,26,99,66,24,92,44,68,88,55,23,76,37,67,70,19,43,90,94,36,25,62,32,84,18,49,65,71,28,96,36,47,24,99,89,12,48,74,41,53,27,88,58,90,73,24,16,85,96,45,87,32,85,26,71,95,30,87,17,59,72,42,71,44,23,77,48,57,15,62,21,80,95,99,39,43,12,61,82,13,36,62,73,56,32,68,59,21,87,17,93,37,79,50,72,49,18,16,84,14,33,60,28,31,48,93,56,65,19,31,77,47,24,15,63,86,90,12,84,70,43,76,29,75,33,42,98,71,26,78,99,25,82,41,56,83,22,97,58,16,79,59,78,41,39,93,31,74,63,20,40,76,87,55,30,66,27,60,15,42,36,78,67,13,45,80,90,49,81,73,76,98,35,74,37,86,23,77,19,89,14,70,86,27,61,34,42,39,55,94,63,81,72,28,18,50,75,24,44,66,42,73,19,12,68,45,79,38,60,14,47,28,85,48,71,17,92,57,82,41,67,95,30,80,90,12,65,97,58,73,19,48,52,88,35,18,42,91,80,59,87,32,44,61,88,53,99,58,66,43,89,77,96, 1000};

        // startTime = System.currentTimeMillis();
        // Arrays.sort(array3);
        // endTime = System.currentTimeMillis();
        // System.out.println("------> Java-ийн default sort() алгоритм ашиглавал: " + (endTime - startTime));
        // System.out.println();

        System.out.println();
        System.out.println(Arrays.toString(array1));
        System.out.println();
        System.out.println(Arrays.toString(array2));
        // System.out.println();
        // System.out.println(Arrays.toString(array3));
        // System.out.println();
    }
}