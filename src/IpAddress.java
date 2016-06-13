/**
 * Created by pkogler on 12.06.2016.
 */

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

public class IpAddress {

   public static String getMacAddress() {
       InetAddress ip = null;
       try {
           ip = InetAddress.getLocalHost();
           NetworkInterface network = NetworkInterface.getByInetAddress(ip);
           return ip.getHostAddress();
       } catch (UnknownHostException e) {
           e.printStackTrace();
       } catch (SocketException e) {
           e.printStackTrace();
       }
       return "Fail";
   }
}
