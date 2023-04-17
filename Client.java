/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package envio.de.archivos.en.chat;



import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.*;

public class Client {

    static void append(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private JFrame frame;
    private JTextField messageField;
    private JTextArea chatArea;
    private JButton sendButton;
    private JButton attachButton;

    public void start(String serverAddress, int port) {
        try {
            socket = new Socket(serverAddress, port);
            System.out.println("Conectado al servidor: " + serverAddress + " en el puerto " + port);

            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            createGUI(); // Crear la interfaz gráfica de usuario

            // Lectura de mensajes del servidor
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                appendMessage("Servidor: " + inputLine);
            }

        } catch (IOException e) {
            System.err.println("Error en el cliente: " + e.getMessage());
        } finally {
            close();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
        appendMessage("Tú: " + message);
    }

    public void sendFile(String filePath) {
        try {
            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
            out.println("FILE"); // Enviamos una señal al servidor indicando que se enviará un archivo
            out.println(Paths.get(filePath).getFileName()); // Enviamos el nombre del archivo
            out.println(fileBytes.length); // Enviamos el tamaño del archivo
            out.flush();
            //out.write(fileBytes, 0, fileBytes.length); // Enviamos los bytes del archivo
            out.flush();
            appendMessage("Tú: Enviando archivo: " + filePath);
        } catch (IOException e) {
            System.err.println("Error al enviar archivo: " + e.getMessage());
        }
    }

    private void createGUI() {
        // Crear la ventana de chat
        frame = new JFrame("Chat Cliente");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

        // Crear el área de chat
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        frame.add(chatScrollPane, BorderLayout.CENTER);

        // Crear el campo de mensaje
        messageField = new JTextField();
        sendButton = new JButton("Enviar");
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String message = messageField.getText();
                sendMessage(message);
                messageField.setText("");
            }
        });

        // Crear el botón de adjuntar archivo
        attachButton = new JButton("Adjuntar archivo");
        attachButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                    sendFile(filePath);
                }
            }
        });

        // Agregar los componentes a la ventana
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(messageField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);
        panel.add(attachButton, BorderLayout.WEST);
        frame.add(panel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

   
    private void appendMessage(String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                chatArea.append(message + "\n");
            }
        });
    }

    private void close() {
        try {
            if (out != null)
                out.close();
            if (in != null)
                in.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            System.err.println("Error al cerrar el cliente: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Configurar la dirección del servidor y el puerto
        String serverAddress = "localhost"; // Cambiar a la dirección IP del servidor si se ejecuta en una red
        int port = 12345; // Cambiar al puerto en el que el servidor esté escuchando

        Client client = new Client();
        client.start(serverAddress, port);
    }
}

                            

