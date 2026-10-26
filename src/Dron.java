import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class Dron {

	public static void main(String[]args) throws Exception{
		
		
		InetAddress address =InetAddress.getByName("192.168.10.1");
		
		try(DatagramSocket socket =new DatagramSocket()){
			
			//最大3秒間、応答を待つ
			socket.setSoTimeout(3000);
			
			//送信する命令
			String command ="command";
			
			byte[] data =command.getBytes(StandardCharsets.UTF_8);
			
			//送信用パケット
			DatagramPacket packet =new DatagramPacket(data,data.length,address,8889);
			
			//ドローンへ送信
			socket.send(packet);
			
			//受診するための配列
			byte [] buffer =new byte [1024];
			
			//受信用パケット
			DatagramPacket responsePacket=new DatagramPacket(buffer,buffer.length);
			
			//応答を受信
			socket.receive(responsePacket);
			
			//byte配列から文字列に変換
			String response =new String(
					responsePacket.getData(),
					responsePacket.getOffset(),
					responsePacket.getLength(),
					StandardCharsets.UTF_8);
			
			System.out.println(response);
		}
		
		
		
		
		
	}
}
