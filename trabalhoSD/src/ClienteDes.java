import java.io.*;
import java.net.*;
import java.util.Base64;

public class ClienteDes implements Runnable {
    @Override
    public void run() {
        try {
            byte[] key = DES.generateKey();
            DES des = new DES(key);
            System.out.println("Chave gerada pelo Cliente: " + encode(key));
            System.out.println();

            Socket conexao = new Socket("localhost", 40000);
            System.out.println("Conectado!");

            BufferedReader entrada = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
            PrintStream saida = new PrintStream(conexao.getOutputStream());
            String linha;
            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                System.out.print("> ");
                linha = encode(des.encrypt(teclado.readLine())) + "@key@" + encode(key);
                System.out.println("Enviando: " + linha);
                saida.println(linha);
                linha = entrada.readLine();
                if (linha == null) {
                    System.out.println("Conexão encerrada!");
                    break;
                }
                System.out.println("Recebendo: " + linha);
                String[] response = linha.split("@key@");
                byte[] keyResponse = decode(response[1]);
                System.out.println("Chave decifrada pelo cliente: " + encode(keyResponse));
                byte[] encryptedString = decode(response[0]);

                DES desResponse = new DES(keyResponse);
                String mensagemDecriptada = desResponse.decrypt(encryptedString);
                System.out.println("Resposta do servidor: " + mensagemDecriptada + " com chave: " + response[1]);
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
