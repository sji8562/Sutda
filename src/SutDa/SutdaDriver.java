package SutDa;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFrame;

public class SutdaDriver {
	public static void main(String[] args) throws IOException {

		while (true) {
			boolean available = false;
			Sutda su;

			JFrame f = new JFrame("Sutda"); // JFrame을 하나 생성한 후
			FileInputStream fis = new FileInputStream("Money.txt");
			DataInputStream dis = new DataInputStream(fis);
			int money = dis.readInt();
			int order = dis.read();
			dis.close();
			su = new Sutda(money, order); // JPanel을 상속받은 SwingImage 객체 생성

			f.setTitle("섯다");
			f.getContentPane().add(su); // ContentPane에 SwingImage 객체를 넣습니다.
			f.setSize(1000, 700);
			f.show();

			while (true) {
				if (su.get_connect() == 11) {
					available = true;
					break;
				} else { // state 11이 아니면

					try {
						Thread.sleep(1000); // 딜레이시간.
					} catch (Exception e) {
					}

				}
			}

			if (available == true) {
				FileOutputStream fos = new FileOutputStream("Money.txt");
				DataOutputStream dos = new DataOutputStream(fos);

				dos.writeInt(su.getu_money());
				dos.writeByte(su.get_sun());
				fos.close();
			}
			try {
				Thread.sleep(3500); // 딜레이시간.
			} catch (Exception e) {
			}
			f.dispose();

		}

	}
}
