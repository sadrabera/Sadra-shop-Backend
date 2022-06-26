import java.util.HashMap;
import java.util.Map;

public class UsersClass {
    public String phoneNumber;
    public String password;
    public String username;
    public String email;
    public String address;
    public String storeAddress;

    public UsersClass(String phoneNumber, String username, String password, String email, String storeAddress) {
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.username = username;
        this.email = email;
        this.storeAddress= storeAddress;
    }
    @Override
    public String toString() {
        return "UsersClass{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", password='" + password + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", storeAddress='" + storeAddress + '\'' +
                '}';
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
class AllUsers{
    public  Map<String,UsersClass> users=new HashMap<>();

    public synchronized void addUser(UsersClass user){
        users.put(user.phoneNumber,user);
    }
    public synchronized void removeUser(String phoneNumber){
        users.remove(phoneNumber);
    }
    public synchronized  UsersClass getUser(String phoneNumber){
        return users.get(phoneNumber);
    }
    public synchronized boolean containsUser(String phoneNumber){
        return users.containsKey(phoneNumber);
    }
}
