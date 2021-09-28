import java.io.*;
import java.lang.reflect.Array;
import java.net.Socket;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestConcurrentRequests {
    public static ArrayList<Long> times = new ArrayList();

    public static void main(String args[]) throws Exception{
        for (int i = 0; i < 50; i++) {
            new Request(times).start();
        }
    }

    private static class Request extends Thread {
        ArrayList<Long> times = new ArrayList();
        public Request(ArrayList<Long> times){
            this.times = times;
        }
        private int calculateAverage(List<Long> times) {
            int sum = 0;
            for (int i=0; i< times.size(); i++) {
                sum += i;
            }
            return sum / times.size();
        }
        public void run() {
            try {
                long total_time = 0;

                for (int i = 0; i < 500; i++) {
                    long start = System.nanoTime();
                    Socket socket = new Socket("localhost", 30302);
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    out.flush();
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                    out.writeObject("test5.txt");
                    FileResponse res = (FileResponse) in.readObject();
                    long elapsedTime = System.nanoTime() - start;
                    byte[] mybytearray = res.getRequestData();
                    Path shared_directory = Path.of("C:/Users/Pablo/Desktop/test3");
                    Path pathToFile = shared_directory.resolve("test5.txt");
                    OutputStream fileOutputStream = new FileOutputStream(pathToFile.toFile());
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                    bufferedOutputStream.write(mybytearray, 0, mybytearray.length);
                    bufferedOutputStream.flush();
                    fileOutputStream.close();
                    bufferedOutputStream.close();
                    total_time += elapsedTime;
                    out.close();
                    in.close();
                    socket.close();
                    System.out.println("PArtial time: " + elapsedTime);
                }

                System.out.println("Total time: " + total_time);
                System.out.println("Average time: " + (total_time / 500));
                Collections.addAll(times,(long)total_time / 500);
                System.out.println("Current average: "+calculateAverage(times));
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
