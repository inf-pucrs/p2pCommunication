import java.io.*;
import java.net.*;
import java.util.*;

public class p2pPeerHeartbeat extends Thread {
	protected DatagramSocket socket = null;
	protected DatagramPacket pacote = null;
	protected InetAddress endereco = null;
	protected byte[] texto = new byte[1024];
	protected int porta;

	public p2pPeerHeartbeat(String[] args) throws IOException {
		// envia um pacote
		texto = ("heartbeat " + args[1]).getBytes();
		endereco = InetAddress.getByName(args[0]);
		porta = Integer.parseInt(args[2]) + 100;
		// cria um socket datagrama
		socket = new DatagramSocket(porta);
	}

	public void run() {
		byte[] texto2 = new byte[1024];

		while (true) {
			try {
				pacote = new DatagramPacket(texto, texto.length, endereco, 8080);
				socket.send(pacote);
			} catch (IOException e) {
				socket.close();
			}
			
			try {
				Thread.sleep(5000);
			} catch(InterruptedException e) {
			}
			System.out.println("\npulse!");
		}
	}
}
