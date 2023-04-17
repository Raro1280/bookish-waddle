/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package envio.de.archivos.en.chat;

import java.io.*;
import java.net.*;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class Server {
    private ServerSocket serverSocket;
    private JTextArea chatArea;

    public Server(JTextArea chatArea) {
        this.chatArea = chatArea;
    }

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            appendMessage("Servidor iniciado en el puerto " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                appendMessage("Cliente conectado: " + clientSocket.getInetAddress().getHostAddress());

                // Crear un hilo para manejar la comunicación con el cliente
                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            appendMessage("Error en el servidor: " + e.getMessage());
        }
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
            if (serverSocket != null)
                serverSocket.close();
        } catch (IOException e) {
            System.err.println("Error al cerrar el servidor: " + e.getMessage());
        }
    }

    // Clase interna para manejar la comunicación con un cliente
    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                String message;
                while ((message = in.readLine()) != null) {
                    appendMessage("Cliente: " + message);
                    out.println("Mensaje recibido: " + message);
                }

                // Cerrar la conexión con el cliente
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                appendMessage("Error en la comunicación con el cliente: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        int port = 12345; // Puerto en el que el servidor escucha

        // Crear la interfaz gráfica del servidor
        JFrame frame = new JFrame("Servidor");
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        frame.add(new JScrollPane(chatArea));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setVisible(true);

        // Iniciar el servidor
        Server server = new Server(chatArea);
        server.start(port);
    }
}
