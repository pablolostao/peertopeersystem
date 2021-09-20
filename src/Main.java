import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
    public static void main(String args[]) throws Exception{

        System.out.println("Welcome. To create a server type 'server' or type 'client' to create a client.");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String type = reader.readLine().trim().toLowerCase();
        if(type == "server"){
            Server s = new Server();
        }
        else if(type == "client"){
            Client c = new Client();
        }
        else{
            System.out.println("You can only choose 'server' or 'client'");
        }
    }
}
