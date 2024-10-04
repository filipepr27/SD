import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class Cliente implements Runnable {
    public Integer id;
    public Integer porta;
    public boolean requisitando_recurso;
    public List<Cliente> respostas_pendentes = new ArrayList<>();

    public Cliente(int id, int porta, boolean requisitando_recurso) {
        this.id = id;
        this.porta = porta;
        this.requisitando_recurso = requisitando_recurso;
    }

    public void addRespostaPendente(Cliente cliente) {
        respostas_pendentes.add(cliente);
    }

    public void removeRespostaPendente(Cliente cliente) {
        respostas_pendentes.remove(cliente);
    }

    public void enviarMensagemMulticast(List<Cliente> clientes, DatagramSocket socket, String mensagem,
            InetAddress endereco) {
        System.out.println("CLIENTE " + this.id + " ENVIANDO MENSAGEM MULTICAST " + mensagem);

        try {
            for (int i = 0; i < clientes.size(); i++) {
                if (this.id != clientes.get(i).id) {
                    String requisicao = mensagem + " " + this.id;

                    byte[] sendBuf = requisicao.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, endereco,
                            clientes.get(i).porta);
                    socket.send(sendPacket);

                    if (mensagem.equals("REQUEST"))
                        addRespostaPendente(clientes.get(i));
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'run'");
    }
}