import java.io.*;
import java.net.*;
import java.util.Arrays;

class Agencia extends Thread {
    private final int id;
    private double saldo;
    private final int[] relogioVetorial;
    private final InetAddress enderecoServidor;
    private final int portaServidor;

    public Agencia(int id, double saldoInicial, InetAddress enderecoServidor, int portaServidor) {
        this.id = id;
        this.saldo = saldoInicial;
        this.relogioVetorial = new int[3]; // Três agências, então três posições no vetor.
        this.enderecoServidor = enderecoServidor;
        this.portaServidor = portaServidor;
    }

    private void enviarMensagem(Mensagem mensagem) throws IOException {
        try (Socket socket = new Socket(enderecoServidor, portaServidor);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
            out.writeObject(mensagem);
        }
    }

    private void atualizarSaldo(Mensagem mensagem) {

        for (int i = 0; i < relogioVetorial.length; i++) {
            relogioVetorial[i] = Math.max(relogioVetorial[i], mensagem.relogioVetorial[i]);
        }

        if (mensagem.tipo.equals("deposito")) {
            saldo += mensagem.valor;
        } else if (mensagem.tipo.equals("juros")) {
            saldo += saldo * (mensagem.valor / 100.0);
        }

        relogioVetorial[id]++;
    }

    private void processarMensagens() {
        try (ServerSocket servidor = new ServerSocket(5000 + id)) {
            while (true) {
                try (Socket socket = servidor.accept();
                        ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                    Mensagem mensagem = (Mensagem) in.readObject();
                    atualizarSaldo(mensagem);
                    System.out.println("Agencia " + id + " - Saldo: R$" + saldo + " - Relogio Vetorial: "
                            + Arrays.toString(relogioVetorial));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        new Thread(this::processarMensagens).start();

        try {
            Thread.sleep(1000); // Aguarda um segundo para sincronizar as operações.

            // agencia 1 faz deposito
            if (id == 1) {
                relogioVetorial[id]++;
                double deposito = 500.0;
                Mensagem mensagem = new Mensagem(relogioVetorial, deposito, "deposito");
                enviarMensagem(mensagem);
            }

            // agencia 2 calcula juros
            if (id == 2) {
                relogioVetorial[id]++;
                double juros = 5.0;
                Mensagem mensagem = new Mensagem(relogioVetorial, juros, "juros");
                enviarMensagem(mensagem);
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}