import java.io.*;
import java.net.*;
import java.util.*;

public class ExclusaoMutCentralizado {

    private static int NUM_CLIENTS = 3;
    private static String REDE = "localhost";
    private static InetAddress address;
    private static Random random = new Random();
    private static Lider LIDER = null;

    public static void main(String[] args) {
        // new Thread(ExclusaoMutuaDescentralizado::startServidor).start();

        ArrayList<Cliente> clientes = new ArrayList<>();

        for (int i = 0; i < NUM_CLIENTS; i++) {
            Cliente cliente = new Cliente(i, 40000 + i, false);
            System.out.println(cliente.id + " " + cliente.porta);
            clientes.add(cliente);
        }

        int indexLider = random.nextInt(3);
        Cliente liderEscolhido = clientes.remove(indexLider);
        LIDER = new Lider(liderEscolhido);
        System.out.println("LIDER " + LIDER.cliente.id + " " + LIDER.cliente.porta);
        new Thread(() -> startLider(clientes)).start();

        for (int i = 0; i < NUM_CLIENTS - 1; i++) {
            Cliente cliente = clientes.get(i);
            System.out.println(cliente.porta);
            new Thread(() -> startClient(cliente)).start();
        }
    }

    private static void startLider(ArrayList<Cliente> clientes) {
        try {
            DatagramSocket socket = new DatagramSocket(LIDER.cliente.porta);
            Thread.sleep(2000); // Aguarda o servidor iniciar

            while (true) {

                byte[] receiveBuf = new byte[256];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuf, 256);

                socket.receive(receivePacket);

                String recebido = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("LIDER RECEBEU " + recebido);

                String[] split = recebido.split(" ");
                String mensagem = split[0];
                Cliente clienteReq = null;

                if ("REQUEST".equals(mensagem)) {
                    for (Cliente cliente : clientes) {
                        if (cliente.id == Integer.parseInt(split[1])) {
                            clienteReq = cliente;
                            break;
                        }
                    }
                    if (!LIDER.recursoCedido) {
                        // recurso livre
                        String resposta = "OK";
                        System.out.println("LIDER " + LIDER.cliente.id + " LIBERANDO RECURSO PARA " + clienteReq.id);
                        address = InetAddress.getByName(REDE);

                        LIDER.recursoCedido = true;

                        byte[] sendBuf = resposta.getBytes();
                        DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, address,
                                clienteReq.porta);
                        socket.send(sendPacket);

                    } else {
                        // SE O RECURSO ESTIVER SENDO UTILIZADO, COLOCO NA FILA
                        System.out.println(
                                "CLIENTE " + clienteReq.id + " ADICIONANDO NA FILA COM PORTA " + clienteReq.porta);

                        LIDER.filaRequisicoes.add(clienteReq);
                    }
                } else if ("OK".equals(mensagem)) {
                    if (!LIDER.filaRequisicoes.isEmpty()) {
                        Cliente proxCliente = LIDER.filaRequisicoes.poll();

                        System.out
                                .println("CLIENTE " + proxCliente.id + " NA FILA COM PORTA " + proxCliente.porta
                                        + " AUTORIZADO");

                        String resposta = "OK";
                        address = InetAddress.getByName(REDE);

                        byte[] sendBuf = resposta.getBytes();
                        DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, address,
                                proxCliente.porta);
                        socket.send(sendPacket);
                    } else {
                        LIDER.recursoCedido = false;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getMessage());
        }
    }

    private static void startClient(Cliente cliente) {
        try {
            System.out.println("CLIENTE NA PORTA " + cliente.porta + " INICIADO.");
            DatagramSocket socket = new DatagramSocket(cliente.porta);
            Thread.sleep(2000); // Aguarda o servidor iniciar

            while (true) {
                Random random = new Random();

                int sorteio = random.nextInt(10);
                if (sorteio == 5) {
                    System.out.println("CLIENTE " + cliente.id + " SOLICITANDO RECURSO");
                    cliente.requisitando_recurso = true;

                    String requisicao = "REQUEST " + cliente.id;
                    address = InetAddress.getByName(REDE);
                    byte[] sendBuf = requisicao.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, address,
                            LIDER.cliente.porta);
                    socket.send(sendPacket);

                    String resposta = "";
                    byte[] receiveBuf = new byte[256];
                    DatagramPacket receivePacket = new DatagramPacket(receiveBuf, 256);

                    while (!resposta.equals("OK")) {
                        System.out.println("CLIENTE " + cliente.id + " AGUARDANDO RESPOSTA.");
                        socket.receive(receivePacket);
                        resposta = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    }

                    cliente.requisitando_recurso = false;
                    System.out.println("CLIENTE " + cliente.id + " USANDO O RECURSO.");

                    Thread.sleep(3000);

                    System.out.println("CLIENTE " + cliente.id + " LIBERANDO O RECURSO.");

                    String confirmacao = "OK";
                    byte[] sendBuf2 = confirmacao.getBytes();
                    DatagramPacket sendPacket2 = new DatagramPacket(sendBuf2, sendBuf2.length, address,
                            LIDER.cliente.porta);
                    socket.send(sendPacket2);
                }
            }
        } catch (InterruptedException | IOException e) {
        }
    }
}
