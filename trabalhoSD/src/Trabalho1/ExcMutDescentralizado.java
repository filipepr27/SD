import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.util.*;

public class ExcMutDescentralizado {

    private static int NUM_CLIENTS = 3;
    private static String REDE = "localhost";
    static private InetAddress address;
    private static Queue<Cliente> filaRequisicoes = new LinkedList<>();
    private static List<Cliente> clientes = new ArrayList<>();

    public static void main(String[] args) {
        for (int i = 0; i < NUM_CLIENTS; i++) {
            Cliente cliente = new Cliente(i, 40000 + i, false);
            System.out.println(cliente.id + " " + cliente.porta);
            clientes.add(cliente);
            new Thread(() -> startClient(cliente)).start();
        }
    }

    private static void startClient(Cliente cliente) {
        try {
            System.out.println("Cliente " + cliente.id + " iniciando...");

            try {
                DatagramSocket socket = new DatagramSocket(cliente.porta);
                address = InetAddress.getByName(REDE);
                socket.setSoTimeout(5000);

                while (true) {
                    Random random = new Random();
                    int sorteio = random.nextInt(6);
                    if (sorteio == 5 && cliente.requisitando_recurso == false) {
                        System.out.println("CLIENTE " + cliente.id + " SOLICITANDO RECURSO");
                        cliente.requisitando_recurso = true;
                        cliente.enviarMensagemMulticast(clientes, socket, "REQUEST", address);
                    }

                    byte[] receiveBuf = new byte[256];
                    DatagramPacket receivePacket = new DatagramPacket(receiveBuf, 256);

                    try {
                        socket.receive(receivePacket);
                        String recebido = new String(receivePacket.getData(), 0, receivePacket.getLength());
                        System.out.println("CLIENTE " + cliente.id + " RECEBEU " + recebido);

                        String[] split = recebido.split(" ");

                        String mensagem = split[0];
                        int idReq = Integer.parseInt(split[1]);
                        Cliente clienteReq = null;

                        for (Cliente client : clientes) {
                            if (client.id == idReq) {
                                clienteReq = client;
                                break;
                            }
                        }

                        if ("REQUEST".equals(mensagem)) {
                            if (cliente.requisitando_recurso == false) {
                                String resposta = "OK " + cliente.id;

                                byte[] sendBuf = resposta.getBytes();
                                DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, address,
                                        clienteReq.porta);
                                socket.send(sendPacket);

                            } else if (idReq > cliente.id) {
                                // SE EU ESTIVER REQUISITANDO MAS O ID DELE FOR MAIOR, ME COLOCO NA FILA E MANDO
                                // OK

                                System.out.println(
                                        "CLIENTE " + cliente.id + " ADICIONANDO NA FILA COM PORTA " + cliente.porta);

                                filaRequisicoes.add(cliente);
                                String resposta = "OK " + cliente.id;
                                address = InetAddress.getByName(REDE);

                                byte[] sendBuf = resposta.getBytes();
                                DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, address,
                                        clienteReq.porta);
                                socket.send(sendPacket);
                            }
                            // SE EU ESTIVER REQUISITANTO E O MEU ID FOR MAIOR, SÓ ESPERO CHEGAR O OK
                        } else if ("OK".equals(mensagem) && (cliente.requisitando_recurso == true)) {
                            cliente.removeRespostaPendente(clienteReq);

                            if (cliente.respostas_pendentes.isEmpty()) {
                                System.out.println("CLIENTE " + cliente.id + " USANDO RECURSO");
                                cliente.requisitando_recurso = false;

                                int usandoRecurso = random.nextInt(10);
                                while (usandoRecurso != 5) {
                                    usandoRecurso = random.nextInt(10);
                                }

                                if (filaRequisicoes.isEmpty()) {
                                    cliente.enviarMensagemMulticast(clientes, socket, "OK", address);
                                } else {
                                    Cliente proxCliente = filaRequisicoes.poll();

                                    System.out.println(
                                            "CLIENTE " + proxCliente.id + " NA FILA COM PORTA " + proxCliente.porta
                                                    + " AUTORIZADO");

                                    String resposta = "OK " + cliente.id;

                                    byte[] sendBuf = resposta.getBytes();
                                    DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, address,
                                            proxCliente.porta);
                                    socket.send(sendPacket);
                                }
                            }
                        }

                    } catch (Exception e) {
                    }

                }
            } catch (Exception e) {
            }

        } catch (Exception e) {
        }
    }
}
