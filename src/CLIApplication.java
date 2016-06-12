import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * @author pkogler
 * @version 1.0
 * @date 12.06.2016
 *
 * This Class acts as the CLI - Handler
 * It will bes started when running the Programm.
 * It fetches all User Input and processes it with the Help
 * of other Classes
 */
public class CLIApplication {
    /*
     * Static / Final Attributes
     */

    /*
     * Regular Expresseion
     * Ip Address like 10.0.0.10:80
     */
    private static final Pattern IPPATTERN = Pattern.compile("^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}):(\\d{1,5})$");

    /*
     * Standard Port
     */
    private static final String STDPORT = "61616";

    /*
     * Usage String
     */
    private static final String USAGE = "Usage:\t\tosmom <ip-address:port>\n" +
            "Example:\tosmom 127.0.0.1:61616\n";


    /*
     * Non Static Attributes
     */

    /*
     * Ip Address
     * the User specified
     */
    private String ip;

    /*
     * Status of the actual Connection
     */
    private String status;

    /**
     * Constructor for CLIApplication
     * Gets the arguments passed by the User and fetches its content
     * It also validates whether the Input was correct or not.
     *
     * @param args Arguments Passed by the User
     */
    public CLIApplication(String[] args) {
        /*
         * Check if all Arguments are correct
         */
        //Checking arg length
        if (args.length != 1) {
            System.err.println("Please enter the correct amount of arguments\n" + this.USAGE);
            System.exit(0);
        }

        /*
         * Verifying IP - Address Syntax
         */
        if (this.verifyIpAddress(args[0])) {
            this.ip = args[0];
        } else {
            System.err.println("Sorry but youre IP - Address was incorrect\n" +
                    "You can use for Example: " +
                    "\n\t<10.0.0.10:61616>" +
                    "\n\t<localhost>");
        }

        //Argumnets Correct !
        System.out.println("All Arguments were Correct :)\n" +
                "Now the connection to the Message Broker will be established ...");

        String url = "tcp://" + args[0];

        ConnectionHandler connectionHandler = new ConnectionHandler(url);

        boolean inCommunication = false;
        char os = ' ';
        String ipAddr = "";

        Scanner scanner = new Scanner(System.in);
        String input;

        showHelp();

        showCommands();

        this.status = "Not Connected - Use /connect to Connect";

        while (true) {
            System.out.print(">");
            input = scanner.nextLine();
            input = input.trim();
            if (input.matches("/help")) {
                showCommands();
            } else if (input.matches("/status")) {
                System.out.println(this.status);
            } else if (input.matches("/connect (\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) Windows")) {
                ipAddr = input.substring(8, input.lastIndexOf(" "));
                this.status = "Connecting to Windows Clinet\n" +
                        "IP Address:\t" + ipAddr;
                System.out.println(this.status);
                connectionHandler.setReceiver(ipAddr);
                os = 'W';
                connectionHandler.sendTak("W");
                //connectionHandler.os = 'W';
            } else if (input.matches("/connect (\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) Linux")) {
                ipAddr = input.substring(8, input.lastIndexOf(" "));
                this.status = "Connected to Linux Clinet\n" +
                        "IP Address:\t" + ipAddr;
                System.out.println(this.status);
                connectionHandler.setReceiver(ipAddr);
                connectionHandler.sendTak("L");
                os = 'L';
                //connectionHandler.os = 'L';
            } else if (os == 'W') {
                if (input.matches("/dir ([a-zA-Z]:)?(\\\\[a-zA-Z0-9_.-]+)+\\\\?")) {
                    connectionHandler.sendTak(input.substring(1, input.length()));
                } else if (input.matches("/disconnect")) {
                    os = ' ';
                    this.status = "Not Connected - Use /connect to Connect";
                    ipAddr = "";
                } else if (input.equals("")) {
                } else {
                    System.out.println("Invalid Command");
                    showWinCommands();
                }
            } else if (os == 'L') {
                if (input.matches("/ls (/[^/ ]*)+/?$")) {
                    connectionHandler.sendTak(input.substring(1, input.length()));
                } else if (input.matches("/disconnect")) {
                    os = ' ';
                    this.status = "Not Connected - Use /connect to Connect";
                    ipAddr = "";
                } else if (input.matches("/ifconfig")) {
                    connectionHandler.sendTak(input.substring(1, input.length()));
                } else if (input.matches("/rmdir (/[^/ ]*)+/?$")) {
                    connectionHandler.sendTak(input.substring(1, input.length()));
                } else if (input.matches("/mkdir (/[^/ ]*)+/?$")) {
                    connectionHandler.sendTak(input.substring(1, input.length()));
                } else if (input.matches("/rm (/[^/ ]*)+/?$")) {
                    connectionHandler.sendTak(input.substring(1, input.length()));
                } else if (input.matches("/cd (/[^/ ]*)+/?$")) {
                    connectionHandler.sendTak(input.substring(1,input.length()));
                } else if (input.equals("")){

                } else {
                    System.out.println("Invalid Command");
                    showLinCommands();
                }
            } else if (input.equals("")) {
            } else {
                System.out.println("Invalid Command");
                showCommands();
            }
        }
    }

    private void showWinCommands() {
        String output = printLineBreak('-') +
                "Windows Commands:\n" +
                "\t/dir <dir>\n" +
                printLineBreak('-');
        System.out.print(output);
    }

    private void showLinCommands() {
        String output = printLineBreak('-') +
                "Linux Commands:\n" +
                "\t/ls <dir>\n" +
                "\t/rm <file>\n" +
                "\t/mkdir <dir name>\n" +
                "\t/rmdir <dirname>\n" +
                "\t/ifconfig\n" +
                printLineBreak('-');
        System.out.print(output);
    }

    /**
     * This Method shows all available Coammands
     */
    private void showCommands() {
        String output = printLineBreak('-') +
                "Available Commands:\n" +
                "General Commands:\n" +
                "\t/help - Show help Message\n" +
                "\t/connect <IP-Address> <OS-System> - Establish connection with User\n" +
                "\t/status - Show actual Status" +
                "\n\nCommands when connected to another User\n" +
                "\tLinux Commands:\n" +
                "\t\t/ls <dir> - List all Items in specified Directory\n" +
                "\tWindows Commands:\n" +
                "\t\t/dir <dir>\n" +
                printLineBreak('-');
        System.out.print(output);
    }

    /**
     * Show HelpMessage
     */
    private void showHelp() {
        String output = printLineBreak('#') +
                "HELP:\n"+printLineBreak('-') +
                this.USAGE  +
                "" + printLineBreak('#');
        System.out.println(output);
    }


    /**
     * This method verifys the Ip Address
     *
     * @param ip The IP-Address specified by the user
     * @return
     */
    private boolean verifyIpAddress(String ip) {
        if (IPPATTERN.matcher(ip).matches()) {
            return true;
        } else if (ip.equals("localhost")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * This Method uses jline to find out the width of the console
     * and prints the given Seperator over the whole width out
     *
     * @param seperator
     * @return output
     */
    public static String printLineBreak(char seperator) {
        return "------------------------------------------\n";
    }


}
