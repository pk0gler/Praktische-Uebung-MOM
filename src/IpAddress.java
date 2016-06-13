/**
 * Created by pkogler on 12.06.2016.
 */

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
/**
 * @author pkogler
 * @version 1.0
 * @date 12.06.2016
 *
 * This Class acts as the Main - Class
 * This Class contains the Main Method and invokes
 * the CLIApplication
 */
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
