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

        // Argumnets Correct !
        System.out.println("All Arguments were Correct :)\n" +
                "Now the connection to the Message Broker will be established ...");

        /*
         * Creating URL
         */
        String url = "tcp://" + args[0];

        /*
         * Creating the Connection handler
         */
        ConnectionHandler connectionHandler = new ConnectionHandler(url);

        /*
         * Variables to determin whether Windows or Linux is used
         * for Communication and specific IP - Address
         */
        char os = ' ';
        String ipAddr = "";
        boolean freeMode = false;

        /*
         * Initializing the Scanner
         */
        Scanner scanner = new Scanner(System.in);
        String input;

        // Show help
        showHelp();

        // Show all Available Commands
        showCommands();

        // Which Status the Programm is in
        this.status = "Not Connected - Use /connect to Connect";

        while (true) {
            /*
             * Showing an Terminal like Sign
             */
            System.out.print(">");
            /*
             * Reading nextLine from Scanner
             */
            input = scanner.nextLine();
            // Trim Input
            input = input.trim();

                /*
                 * When User types /help
                 * The Help Text will be showed
                 */
            if (input.matches("/help")) {
                showCommands();


                /*
                 * When User types /status
                 * The Status will be showed
                 */
            } else if (input.matches("/status")) {
                System.out.println(this.status);


                /*
                 * When User types /connect ... Windows
                 * A connection to the specified Host will be established
                 */
            } else if (input.matches("/connect (\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) Windows")) {
                // Getting Ip - Address from Command
                ipAddr = input.substring(8, input.lastIndexOf(" "));
                // Setting Status
                this.status = "Connecting to Windows Clinet\n" +
                        "IP Address:\t" + ipAddr;
                // Showing current Status
                System.out.println(this.status);
                /*
                  * Set the receiver so every
                  * Output is going to specified Host
                  */
                connectionHandler.setReceiver(ipAddr);
                // Setting Os system to Windows
                os = 'W';
                // Send Task so its clear that Windows will be Used
                connectionHandler.sendTak("W");


                /*
                 * When the User types /connect ... Linux
                 * A connection to the specified Host will be established
                 */
            } else if (input.matches("/connect (\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) Linux")) {
                // Setting IP-Address
                ipAddr = input.substring(8, input.lastIndexOf(" "));
                // Setting and showing Statuss
                this.status = "Connected to Linux Clinet\n" +
                        "IP Address:\t" + ipAddr;
                System.out.println(this.status);

                // Setting Receiver
                connectionHandler.setReceiver(ipAddr);
                // Setting Os and sending it to Receiver
                connectionHandler.sendTak("L");
                os = 'L';


                /*
                 * Avalable or valid Commands for Windows
                 */
            } else if (os == 'W') {


                    /*
                     * When using /dir ...
                     * Task will be send to specified Receiver
                     */
                if (input.matches("/dir ([a-zA-Z]:)?(\\\\[a-zA-Z0-9_.-]+)+\\\\?")) {
                    // sending the task
                    connectionHandler.sendTak(input.substring(1, input.length()));


                    /*
                     * When using the Command /disconnect
                     * Disconnecting from Current Host
                     */
                } else if (input.matches("/disconnect")) {
                    // Reseting the os Variable
                    os = ' ';
                    // Clearing Status
                    this.status = "Not Connected - Use /connect to Connect";


                    /*
                     * When using the Command /ipconfig
                     * Network interfaces will be showed
                     */
                } else if (input.matches("/ipconfig")) {
                    // sending task to receiver
                    connectionHandler.sendTak(input.substring(1, input.length()));


                    /*
                     * When Command /move is used
                     * Moving Directories or Files
                     */
                } else if (input.matches("/move ([a-zA-Z]:)?(\\\\[a-zA-Z0-9_.-]+)+\\\\? ([a-zA-Z]:)?(\\\\[a-zA-Z0-9_.-]+)+\\\\?")) {
                    // Sending Task
                    connectionHandler.sendTak(input.substring(1, input.length()));


                    /*
                     * Avoiding Errors when typing Nothing
                     */
                } else if (input.equals("")) {


                    /*
                     * When attempting to use the /free Command
                     */
                } else if (input.matches("/free")) {
                    /*
                     * ENabling / Disabling /free Mode
                     * Toggling
                     */
                    if (freeMode) {
                        freeMode = false;
                    } else {
                        freeMode = true;
                    }
                } else {


                    /*
                     * When Free Mode Active every Command could be
                     * executed
                     */
                    if (freeMode) {
                        // sending every Input as Task
                        connectionHandler.sendTak(input);
                    } else {


                        /*
                         * When no Matched Found
                         * Invalid Command will be displayed
                         */
                        System.out.println("Invalid Command");
                        showWinCommands();
                    }
                }


                /*
                 * When Linux was Specified as OS
                 */
            } else if (os == 'L') {


                /*
                 * When Using the /ls Command
                 */
                if (input.matches("/ls (/[^/ ]*)+/?$")) {
                    // Sending Command as task
                    connectionHandler.sendTak(input.substring(1, input.length()));

                    /*
                     * When /disconnect is used
                     */
                } else if (input.matches("/disconnect")) {
                    os = ' ';
                    this.status = "Not Connected - Use /connect to Connect";


                    /*
                     * When ifconfig is used
                     */
                } else if (input.matches("/ifconfig")) {
                    connectionHandler.sendTak(input.substring(1, input.length()));


                    /*
                     * When /rmdir is used
                     */
                } else if (input.matches("/rmdir (/[^/ ]*)+/?$")) {
                    connectionHandler.sendTak(input.substring(1, input.length()));


                    /*
                     * When mkdir is used
                     */
                } else if (input.matches("/mkdir (/[^/ ]*)+/?$")) {
                    connectionHandler.sendTak(input.substring(1, input.length()));


                    /*
                     * When /rm is used
                     */
                } else if (input.matches("/rm (/[^/ ]*)+/?$")) {
                    connectionHandler.sendTak(input.substring(1, input.length()));


                    /*
                     * When /cd is used
                     */
                } else if (input.matches("/cd (/[^/ ]*)+/?$")) {
                    connectionHandler.sendTak(input.substring(1, input.length()));


                    /*
                     * Avoidiung errors when nothing but a \n (retunr)
                     * was entered
                     */
                } else if (input.equals("")) {


                     /*
                     * ENabling / Disabling /free Mode
                     * Toggling
                     */
                } else if (input.matches("/free")) {
                    if (freeMode) {
                        freeMode = false;
                    } else {
                        freeMode = true;
                    }


                     /*
                     * When Free Mode Active every Command could be
                     * executed
                     */
                } else {
                    if (freeMode) {
                        connectionHandler.sendTak(input);


                        /*
                        * When Free Mode Active every Command could be
                        * executed
                        */
                    } else {
                        System.out.println("Invalid Command");
                        showLinCommands();
                    }
                }
            } else if (input.equals("")) {


                /*
                 * When no Match was found
                 */
            } else {
                System.out.println("Invalid Command");
                showCommands();
            }
        }
    }

    /**
     * This Method shows all WindowsCommands
     */
    private void showWinCommands() {
        String output = printLineBreak('-') +
                "Windows Commands:\n" +
                "\t/dir <dir>\n" +
                printLineBreak('-');
        System.out.print(output);
    }

    /**
     * This Method shows all Linux Commands
     */
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
                printLineBreak('-') +
                "General Commands:\n" +
                "\t/help - Show help Message\n" +
                "\t/connect <IP-Address> <OS-System> - Establish connection with User\n" +
                "\t/status - Show actual Status" +
                "\n\nCommands when connected to another User\n" +
                "\tLinux Commands:\n" +
                "\t\t/ls <dir> - List all Items in specified Directory\n" +
                "\t\t/rm <file>\n" +
                "\t\t/mkdir <dir name>\n" +
                "\t\t/rmdir <dirname>\n" +
                "\t\t/ifconfig\n" +
                "\tWindows Commands:\n" +
                "\t\t/dir <dir>\n\n" +
                "\tEnabling free Mode (Emulating ssh - Connection)\n" +
                "\t\t/free\n" +
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
