import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.math.*;

/** Primitive chat client.
 * This client connects to a server so that messages can be typed and forwarded
 * to all other clients.  Try it out in conjunction with ImprovedChatServer.java.
 * You will need to modify / update this program to incorporate the secure
 * elements as specified in the Assignment description.  Note that the PORT used
 * below is not the one required in the assignment -- for your SecureChatClient
 * be sure to change the port that so that it matches the port specified for the
 * secure  server.
 * Adapted from Dr. John Ramirez's CS 1501 Assignment 4
 */
public class SecureChatClient extends JFrame implements Runnable, ActionListener 
{
	//Use 8765 for the port
    public static final int PORT = 8765;

    ObjectInputStream myReader;
    ObjectOutputStream myWriter;
    
    JTextArea outputArea;
    JLabel prompt;
    JTextField inputField;

    String myName, serverName;
    Socket connection;
    SymCipher symC;

    public SecureChatClient () throws IOException
    {
        try {
        	//copied over from improvedchatclient
            myName = JOptionPane.showInputDialog(this, "Enter your user name: ");
            serverName = JOptionPane.showInputDialog(this, "Enter the server name: ");
            InetAddress addr = InetAddress.getByName(serverName);
            connection = new Socket(addr, PORT);   // Connect to server with new socket

            //replace buffered with objectstream
            myReader = new ObjectInputStream(
            		connection.getInputStream());   //get reader and writer
            myWriter = new ObjectOutputStream(
            		connection.getOutputStream());
            
            myWriter.flush(); //immediately calls the flush() method (this technicality prevents deadlock)

            //It receives the server's public key, E, as a BigInteger object.
            //It receives the server's public mod value, N, as a BigInteger object.
            BigInteger e = (BigInteger) myReader.readObject();
            BigInteger n = (BigInteger) myReader.readObject();
            
            System.out.println("\ne: " + e.toString() + "\n\n");
            System.out.println("n: " + n.toString() + "\n\n");

            // receives the server's preferred symmetric cipher
            String encryption = (String) myReader.readObject();

            //receives the server's preferred symmetric cipher (either "Sub" or "Add"), as a String object
            if (encryption.equals("Sub")){
            	symC = new Substitute();  
            	System.out.println("\nEncryption type: Substitute\n\n");
            }  
            else {
            	symC = new Add128(); 
            	System.out.println("\nEncryption type: Add128\n\n");
            }                         

            //gets the key from its cipher object using the getKey() method
            BigInteger intKey = new BigInteger (1, symC.getKey());
            //ensure that the BigInteger is positive
            int isPos = intKey.signum();
            if (isPos == -1) {
              System.out.println("Error creating cipher.\n\n");
            }
            
            intKey = intKey.modPow(e, n);
            System.out.println("Symmetric key: " + intKey.toString());
            //here returns to improved chat client, replace println with writeObject
            //converts the result into a BigInteger object
            myWriter.writeObject(intKey); 
            //immediately calls the flush() method (this technicality prevents deadlock)
            myWriter.flush(); 
            
            myWriter.writeObject(symC.encode(myName));
            myWriter.flush();
            
            this.setTitle(myName);      // Set title to identify chatter
            
            //exact same as original improvedchatclient
            Box b = Box.createHorizontalBox();  // Set up graphical environment for
            outputArea = new JTextArea(8, 30);  // user
            outputArea.setEditable(false);
            b.add(new JScrollPane(outputArea));
    
            outputArea.append("Welcome to the Chat Group, " + myName + "\n");
    
            inputField = new JTextField("");  // This is where user will type input
            inputField.addActionListener(this);
    
            prompt = new JLabel("Type your messages below:\n");
            Container c = getContentPane();
    
            c.add(b, BorderLayout.NORTH);
            c.add(prompt, BorderLayout.CENTER);
            c.add(inputField, BorderLayout.SOUTH);
    
            Thread outputThread = new Thread(this);  // Thread is to receive strings
            outputThread.start();                    // from Server
    
            //
            addWindowListener(
                new WindowAdapter()
                {
                    public void windowClosing(WindowEvent e)
                    { 
                        try {
                        //the message "CLIENT CLOSING" should be sent to the server
                        String currMsg = "CLIENT CLOSING";

                        System.out.println("\n\nClient closing...");
                        //System.out.printf("\nByte array: %s", Arrays.toString(currMsg.getBytes()));
                        
                        byte[] bytes = symC.encode(currMsg);
                        //System.out.printf("\nEncrypted byte array: %s", Arrays.toString(bytes));
                        
                        // Send encrypted message to the Server
                        //This message should be encrypted like all other messages, 
                        //but should not have any prefix
                        myWriter.writeObject(bytes); 
                        myWriter.flush();  //immediately calls the flush() method (this technicality prevents deadlock)
                        
                        
                        } catch (IOException ex) {
                          System.out.println("Problem starting client! Error code: 001");
                        }
                        System.exit(0);
                    }
                }
            );
    
            setSize(500, 200);
            setVisible(true);

        }
        catch (Exception e)
        {
            System.out.println("Problem starting client! Error code: 002");
        }
    }

    public void run()
    {
        byte [] bytes;
        String currMsg;
        while (true)
        {
             try {
                bytes = (byte[]) myReader.readObject();
             }
             catch (Exception e) {
                System.out.println(e +  ", closing client!");
                break;
             }
             System.out.println("\nMessage received...");
             System.out.printf("\nEncrypted byte array: %s", Arrays.toString(bytes));
            
             currMsg = symC.decode(bytes);
             System.out.printf("\nDecrypted byte array: %s", Arrays.toString(currMsg.getBytes()));
             System.out.printf("\nDecrypted message: %s", currMsg);

             outputArea.append(currMsg+"\n");
        }
        System.exit(0);
    }

    public void actionPerformed(ActionEvent e)
    {
        StringBuilder message = new StringBuilder();
        
        message.append(myName);                    // Add name to beginning of message
        message.append(": ");
        
        message.append(e.getActionCommand());      // Get input value
        
        String currMsg = message.toString();

        System.out.println("\nTrying to send message: " + currMsg);

        System.out.println("\n\nSending message...");
        System.out.printf("\nByte array: %s\n", Arrays.toString(currMsg.getBytes()));

        byte[] finMsg = symC.encode(currMsg);

        System.out.println("\nEncoded Array: " + finMsg.toString() + "]");
        
        inputField.setText("");
        
        // Send encrypted message to the Server
        try {
        myWriter.writeObject(finMsg);
        myWriter.flush();    //immediately calls the flush() method (this technicality prevents deadlock)
        } catch (IOException ex) {
          System.out.println("There was an error performing this action. Error code: 003");
        }
    }                                               

    public static void main(String [] args) throws IOException
    {
         SecureChatClient JR = new SecureChatClient();
         JR.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }
}