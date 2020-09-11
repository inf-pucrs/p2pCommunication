import java.io.*;
import java.net.*;
import java.util.*;

public class p2pServer {
	public static void main(String[] args) throws IOException {
		String recebido;
		InetAddress endereco;
		int porta;
		byte[] texto = new byte[1024];
		byte[] msg = new byte[1024];
		DatagramSocket socket = new DatagramSocket(8080);
		DatagramPacket pacote;
		
		List<String> msgList = new ArrayList<>();
		List<InetAddress> msgAddr = new ArrayList<>();
		List<Integer> msgPort = new ArrayList<>();
		
		while (true) {
			try {
				// recebe datagrama
				pacote = new DatagramPacket(texto, texto.length);
				socket.setSoTimeout(500);
				socket.receive(pacote);
				System.out.print("recebi!");
				
				// processa o que foi recebido, adicionando a uma lista
				recebido = new String(pacote.getData(), 0, pacote.getLength());
				String vars[] = recebido.split("\\s");
				endereco = pacote.getAddress();
				porta = pacote.getPort();
				
				System.out.print(vars[0]);
				
				if (vars[0].equals("login") && vars.length > 1) {
					msgList.add(vars[1]);
					msgAddr.add(endereco);
					msgPort.add(porta);
					System.out.print("\ncadastrado: " + vars[1]);
					
					// envia a resposta de volta ao cliente
					msg = "OK".getBytes();
					pacote = new DatagramPacket(msg, msg.length, endereco, porta);
					socket.send(pacote);
				}
				
				if (vars[0].equals("list") && vars.length > 1) {
					for (int j = 0; j < msgList.size(); j++) {
						if (msgList.get(j).equals(vars[1])) {
							System.out.print("\nitems: " + msgList.size());
							for (int i = 0; i < msgList.size(); i++) {
								String msg_str = new String(msgList.get(i) + " " + msgAddr.get(i).toString() + " " + msgPort.get(i).toString());
								System.out.print("\n" + msg_str);
								msg = msg_str.getBytes();

								// envia a resposta de volta ao cliente
								pacote = new DatagramPacket(msg, msg.length, endereco, porta);
								socket.send(pacote);
							}
							break;
						}
					}
				}
			} catch (IOException e) {
				System.out.print(".");
			}
		}
	}
}
