import java.io.*;
import java.net.*;
import java.time.*;	
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
		String peerId = null;

		int checkHeartbeatsEvery = 5;
		List<String> msgList = new ArrayList<>();
		List<InetAddress> msgAddr = new ArrayList<>();
		List<Integer> msgPort = new ArrayList<>();	
		HashMap<String, LocalDateTime> heartbeatTracker = new HashMap<>();
		long peerInactiveTimeThreshold = 11; // 11 segundos

		int counter = 0;
		while (true) {
			try {
				counter++;
				if (counter >= checkHeartbeatsEvery) {
					List<String> toRemove = new ArrayList<>();
					for (Map.Entry<String, LocalDateTime> entry : heartbeatTracker.entrySet()) {
						LocalDateTime now = LocalDateTime.now();
						peerId = entry.getKey();
						LocalDateTime lastHeartbeat = entry.getValue();
						Duration elapsedTimeSinceLastHeartbeat = Duration.between(lastHeartbeat, now);
						if (elapsedTimeSinceLastHeartbeat.toSeconds() > peerInactiveTimeThreshold) {
							int peerIndex = -1;
							for (int i = 0; i < msgList.size(); i++) {
								if (msgList.get(i).equals(peerId)) {
									System.out.print(msgList.get(i));
									peerIndex = i;
									break;
								}
							}
							// System.out.print("REMOVAL");
							// System.out.print(peerIndex);
							if (peerIndex != -1) {
								toRemove.add(peerId);
								msgAddr.remove(peerIndex);
								msgList.remove(peerIndex);
								msgPort.remove(peerIndex);
								System.out.print("Removed "+peerId);
							}
						}
					}
					for (String p : toRemove) {
						heartbeatTracker.remove(p);
					}
					counter = 0;
				}
				peerId = null;

				// recebe datagrama
				pacote = new DatagramPacket(texto, texto.length);
				socket.setSoTimeout(500);
				socket.receive(pacote);
				System.out.println("recebi:");
				recebido = new String(pacote.getData(), 0, pacote.getLength());
				System.out.print("'"+recebido+"'");
				String vars[] = recebido.split("\\s");
				endereco = pacote.getAddress();
				porta = pacote.getPort();

				if (vars.length > 1) {
					peerId = vars[1];
				}

				if (vars[0].equals("heartbeat")) {
					LocalDateTime now = LocalDateTime.now();
					heartbeatTracker.put(peerId, now);
				}
				
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
			} finally {
				socket.close();
			}
		}
	}
}
