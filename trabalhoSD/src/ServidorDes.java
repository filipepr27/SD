import java.io.*;
import java.net.*;
import java.util.Base64;

public class ServidorDes implements Runnable {
    @Override
    public void run() {
        try {

            ServerSocket s = new ServerSocket(40000);
            while (true) {
                System.out.print("Esperando alguém se conectar...\n");
                Socket conexao = s.accept();
                System.out.println(" Conectou!");
                BufferedReader entrada = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
                PrintStream saida = new PrintStream(conexao.getOutputStream());

                String linha = entrada.readLine();
                System.out.println("Recebendo: " + linha);

                while (linha != null && !(linha.trim().equals(""))) {
                    String[] received = linha.split("@key@");
                    byte[] keyReceived = decode(received[1]);
                    System.out.println("Chave decifrada pelo servidor: " + encode(keyReceived));
                    byte[] encryptedString = decode(received[0]);

                    DES des = new DES(keyReceived);
                    System.out.println("Mensagem decifrada pelo servidor: " + des.decrypt(encryptedString));

                    String resposta = "Tudo certo meu patrao";

                    saida.println(encode(des.encrypt(resposta)) + "@key@" + encode(keyReceived));
                    linha = entrada.readLine();
                }
                conexao.close();
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    public static byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }
}
