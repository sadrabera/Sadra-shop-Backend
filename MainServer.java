
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MainServer {

    public static void main(String[] args)  {

        ServerSocket serverSocket = null;
        Logger logger=Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        try {
             serverSocket=new ServerSocket(8080);
        } catch (IOException e) {
            logger.log(Level.WARNING, Arrays.toString(e.getStackTrace()));
        }
        JSONObject users = null;
        try {
            Object obj=new JSONParser().parse(new FileReader("src/Data/users.json"));
            users=(JSONObject)obj;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        while(true){
            try {
                System.out.println("Waiting for connection...");
                assert serverSocket != null;
                Socket anotherClientSocket=serverSocket.accept();
                System.out.println("accepted");
                new Thread(new SideServerThread(anotherClientSocket,users)).start();  //Creating another thread for each client

            } catch (IOException e) {
                logger.log(Level.WARNING, Arrays.toString(e.getStackTrace()));
            }
        }


    }
}
