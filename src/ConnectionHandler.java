import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by pkogler on 12.06.2016.
 */
public class ConnectionHandler implements MessageListener {
    //Used to attach the sender to the email
    public static final String TRANSMITTER_KEY = "transmitter";
    /*
     * Connection
     */
    private Connection connection;
    /*
     * Session
     */
    private Session session;
    /*
     * MessageConsumer
     */
    private MessageConsumer consumer;
    /*
     * compName
     */
    private String compName = IpAddress.getMacAddress();
    /*
     * Receiver
     */
    private String receiver = "";

    public char os;

    public ConnectionHandler(String url) {
        System.out.println("\nComputer / User name:\t"+compName);
        ConnectionFactory factory = new ActiveMQConnectionFactory(compName, ActiveMQConnectionFactory.DEFAULT_PASSWORD,
                url);
        try {
            connection = factory.createConnection();
            //Setting the send mode to asynchronous to improve performance
            ((ActiveMQConnection) connection).setUseAsyncSend(true);
            connection.start();

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(compName);
            consumer = session.createConsumer(destination);
            consumer.setMessageListener(this);
        } catch (JMSException e) {
            System.err.println("Something went wrong while connecting the email server");
            e.printStackTrace();
        }
    }

    /**
     * Sends an Task to given Computer with given text.
     *
     * @param text  to be sent to the receiver
     */
    public void sendTak(String text) {
        try {
            TextMessage message = session.createTextMessage(text);
            message.setStringProperty(TRANSMITTER_KEY, this.compName);
            Destination destination = session.createQueue(receiver);
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            producer.send(message);
            producer.close();
            System.out.print("Sending to " + this.receiver + "\n");
        } catch (JMSException e) {
            System.out.println("Something went wrong while sending the task");
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public MessageConsumer getConsumer() {
        return consumer;
    }

    public void setConsumer(MessageConsumer consumer) {
        this.consumer = consumer;
    }

    public String getCompName() {
        return compName;
    }

    public void setCompName(String compName) {
        this.compName = compName;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        //System.out.println("drin");
        try {
            if (textMessage.getText().contains("\n"))
            System.out.print("\nAwnswer from: " + textMessage.getStringProperty(TRANSMITTER_KEY) + "\n" + textMessage.getText());
            if (textMessage.getText().equals("W")) this.os='W';
            if (textMessage.getText().equals("L")) this.os='L';
            if (this.os == 'W' && !textMessage.getText().equals("W") && !textMessage.getText().equals("L") && !textMessage.getText().contains("Output")) {
                try
                {
                    Process p=Runtime.getRuntime().exec("cmd /c " + textMessage.getText());
                    //p.waitFor();
                    BufferedReader reader=new BufferedReader(
                            new InputStreamReader(p.getInputStream())
                    );
                    String line;
                    String output = "Output:\n\t";
                    while((line = reader.readLine()) != null)
                    {
                        output += line + "\n\t";
                    }
                    String recTemp = this.receiver;
                    this.receiver = textMessage.getStringProperty(TRANSMITTER_KEY);
                    sendTak(output);
                    //System.out.println(output);
                    this.receiver = recTemp;
                }
                catch(IOException e1) {}
            } else if (this.os == 'L' && !textMessage.getText().equals("W") && !textMessage.getText().equals("L") && !textMessage.getText().contains("Output")) {
                try {
                    Process proc = Runtime.getRuntime().exec(new String[]{"bash","-c",textMessage.getText()});
                    BufferedReader stdInput = new BufferedReader(new
                            InputStreamReader(proc.getInputStream()));

                    BufferedReader stdError = new BufferedReader(new
                            InputStreamReader(proc.getErrorStream()));

                    // read the output from the command
                    //System.out.println("Here is the standard output of the command:\n");
                    String s = null;
                    String output = "Output:\n\t";
                    while ((s = stdInput.readLine()) != null) {
                        //System.out.println(s);
                        output += s + "\n\t";
                    }

                    String recTemp = this.receiver;


                    // read any errors from the attempted command
                    //System.out.println("Here is the standard error of the command (if any):\n");
                    output += "\nErrors:\n\t";
                    while ((s = stdError.readLine()) != null) {
                        output += s + "\n\t";
                    }
                    this.receiver = textMessage.getStringProperty(TRANSMITTER_KEY);
                    sendTak(output);
                    this.receiver = recTemp;
                    //System.out.println(output);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (JMSException e) {
            System.out.println("Something went wrong while receiving a message");
            e.printStackTrace();
        }
    }
}
