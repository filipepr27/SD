public class Trabalho2 {

    public static void main(String[] args) throws InterruptedException {

        ServidorDes servidor = new ServidorDes();
        Thread serverThread = new Thread(servidor);
        serverThread.start();

        Thread.sleep(1000);

        ClienteDes cliente = new ClienteDes();
        Thread clientThread = new Thread(cliente);
        clientThread.start();
    }
}
