

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashSet;

public class TestIndexerProcessingTime {

    public static void main(String args[]) throws Exception{
        long total_time = 0;
        Socket socket = new Socket("localhost",30301);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        out.writeObject(12);
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        String response = (String) in.readObject();
        System.out.println(response);
        for (int i=0;i<500;i++){
            IndexerRequest request = new IndexerRequest();
            request.setRequestType(IndexerRequestType.LOOKFOR);
            request.setRequestData("test5.txt");
            long start = System.nanoTime();
            out.writeObject(request);
            HashSet<String> set = (HashSet<String>) in.readObject();
            long elapsedTime = System.nanoTime() - start;
            total_time += elapsedTime;
            if (set==null || set.isEmpty()){
                System.out.println("File does not exist in the server");
            }else{
                System.out.println("You can find the file test5.txt in the following peers: "+set.toString());
            }
            System.out.println("PArtial time: "+elapsedTime);
        }
        System.out.println("Total time: "+total_time);
        System.out.println("Average time: "+(total_time/500));




    }
}
