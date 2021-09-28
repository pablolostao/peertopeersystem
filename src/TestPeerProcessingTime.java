

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.util.HashSet;

public class TestPeerProcessingTime {

    public static void main(String args[]) throws Exception{
        long total_time = 0;

        for (int i=0;i<500;i++){
            long start = System.nanoTime();
            Socket socket = new Socket("localhost",30302);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            out.writeObject("test5.txt");
            FileResponse res = (FileResponse) in.readObject();
            long elapsedTime = System.nanoTime() - start;
            byte [] mybytearray  = res.getRequestData();
            Path shared_directory = Path.of("C:/Users/Pablo/Desktop/test3");
            Path pathToFile = shared_directory.resolve("test5.txt");
            OutputStream fileOutputStream = new FileOutputStream(pathToFile.toFile());
            BufferedOutputStream bufferedOutputStream= new BufferedOutputStream(fileOutputStream);
            bufferedOutputStream.write(mybytearray, 0 ,mybytearray.length );
            bufferedOutputStream.flush();
            fileOutputStream.close();
            bufferedOutputStream.close();
            total_time += elapsedTime;
            out.close();
            in.close();
            socket.close();
            System.out.println("PArtial time: "+elapsedTime);
        }

        System.out.println("Total time: "+total_time);
        System.out.println("Average time: "+(total_time/500));




    }
}
