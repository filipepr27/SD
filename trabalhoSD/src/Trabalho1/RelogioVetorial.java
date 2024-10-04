import java.net.*;

public class RelogioVetorial {
    public static void main(String[] args) throws Exception {
        int portaServidor = 4000;
        InetAddress enderecoServidor = InetAddress.getByName("localhost");

        Servidor servidor = new Servidor(portaServidor);
        servidor.start();

        double saldoInicial = 1000.0;
        Agencia agencia1 = new Agencia(0, saldoInicial, enderecoServidor, portaServidor);
        Agencia agencia2 = new Agencia(1, saldoInicial, enderecoServidor, portaServidor);
        Agencia agencia3 = new Agencia(2, saldoInicial, enderecoServidor, portaServidor);

        agencia1.start();
        agencia2.start();
        agencia3.start();

        agencia1.join();
        agencia2.join();
        agencia3.join();
    }
}