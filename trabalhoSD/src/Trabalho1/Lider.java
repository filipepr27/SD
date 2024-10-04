import java.util.LinkedList;
import java.util.Queue;

public class Lider implements Runnable {

    public Queue<Cliente> filaRequisicoes;
    public Cliente cliente;
    public boolean recursoCedido;

    public Lider(Cliente cliente) {
        this.filaRequisicoes = new LinkedList<>();
        this.cliente = cliente;
        this.recursoCedido = false;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'run'");
    }

}
