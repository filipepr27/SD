import java.io.*;
import java.net.*;

class Servidor extends Thread {
    private final int portaServidor;

    public Servidor(int portaServidor) {
        this.portaServidor = portaServidor;
    }

    public void run() {
        try (ServerSocket servidor = new ServerSocket(portaServidor)) {
            while (true) {
                try (Socket socket = servidor.accept();
                        ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                    Mensagem mensagem = (Mensagem) in.readObject();

                    for (int i = 0; i < 3; i++) {
                        try (Socket clienteSocket = new Socket("localhost", 5000 + i);
                                ObjectOutputStream out = new ObjectOutputStream(clienteSocket.getOutputStream())) {
                            out.writeObject(mensagem);
                        }
                    }
                } catch (ClassNotFoundException e) {
                }
            }
        } catch (IOException e) {
        }
    }
}