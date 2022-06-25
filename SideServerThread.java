import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SideServerThread implements Runnable{
    private Socket socket;
    JSONObject users;
    SideServerThread(Socket socket, JSONObject users) {
        this.socket=socket;
        this.users=users;
    }
    @Override
    public void run() {
        DataInputStream in;
        DataOutputStream out;
        try {
            in=new DataInputStream(socket.getInputStream());
            out=new DataOutputStream(socket.getOutputStream());
            StringBuilder request=new StringBuilder();
            int c;
            while((c=in.read())!=0){
                request.append((char)c);
            }
            String mainRequest=request.toString();
            if (mainRequest.startsWith("login")){
                String[] loginRequest=mainRequest.split(":");
                String phoneNumber=loginRequest[1];
                String password=loginRequest[2];
                if (users.containsKey(phoneNumber)){
                    String user=users.get(phoneNumber).toString();
                    if (password.equals(user.substring(user.indexOf("password")+10,user.indexOf(",")).replace("\"",""))){
                        out.writeBytes("login success");
                    }
                    else{
                        out.writeBytes("login failed");
                    }
                }

                else{
                    out.writeBytes("login failed");
                }
            }
            else if (mainRequest.startsWith("register")){
                String[] registerRequest=mainRequest.split(" ");
                String username=registerRequest[1];
                String password=registerRequest[2];
                if (username.equals("admin") && password.equals("admin")){
                    out.writeUTF("register failed");
                }
                else{
                    out.writeUTF("register success");
                }
            }
            else if (mainRequest.startsWith("get")){
                String[] getRequest=mainRequest.split(" ");
                String username=getRequest[1];
                String password=getRequest[2];
                if (username.equals("admin") && password.equals("admin")){
                    out.writeUTF("get success");
                }
                else{
                    out.writeUTF("get failed");
                }
            }
            else if (mainRequest.startsWith("put")){
                String[] putRequest=mainRequest.split(" ");
                String username=putRequest[1];
                String password=putRequest[2];
                if (username.equals("admin") && password.equals("admin")){
                    out.writeUTF("put success");
                }
                else{
                    out.writeUTF("put failed");
                }
            }
            else if (mainRequest.startsWith("delete")){
                String[] deleteRequest=mainRequest.split(" ");
                String username=deleteRequest[1];
                String password=deleteRequest[2];
                if (username.equals("admin") && password.equals("admin")){
                    out.writeUTF("delete success");
                }
                else{
                    out.writeUTF("delete failed");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
