import java.io.*;

class Mensagem implements Serializable {
    private static final long serialVersionUID = 1L;
    public int[] relogioVetorial;
    public double valor;
    public String tipo;

    public Mensagem(int[] relogioVetorial, double valor, String tipo) {
        this.relogioVetorial = relogioVetorial.clone();
        this.valor = valor;
        this.tipo = tipo;
    }
}