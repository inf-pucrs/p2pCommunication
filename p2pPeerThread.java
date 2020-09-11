import java.io.*;
import java.net.*;
import java.util.*;

public class p2pPeerThread extends Thread {
	protected DatagramSocket socket = null;
	protected DatagramPacket pacote = null;
	protected InetAddress endereco = null;
	protected byte[] texto = new byte[1024];
	protected int porta;

	public p2pPeerThread(String[] args) throws IOException {
		// envia um pacote
		texto = args[1].getBytes();
		endereco = InetAddress.getByName(args[0]);
		porta = Integer.parseInt(args[2]);
		// cria um socket datagrama
		socket = new DatagramSocket(porta);
	}

	public void run() {
		byte[] texto2 = new byte[1024];

		try {
			pacote = new DatagramPacket(texto, texto.length, endereco, 8080);
			socket.send(pacote);
		} catch (IOException e) {
			socket.close();
		}

		while (true) {
			try {
				// obtem a resposta
				pacote = new DatagramPacket(texto2, texto2.length);
				socket.setSoTimeout(500);
				socket.receive(pacote);
				
				// mostra a resposta
				
				String resposta = new String(pacote.getData(), 0, pacote.getLength());
				System.out.println("recebido: " + resposta);
			} catch (IOException e) {
				System.out.print(".");
			}
		}

	}
}
