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

			JFrame f = new JFrame("Sutda"); // JFrame�� �ϳ� ������ ��
			FileInputStream fis = new FileInputStream("Money.txt");
			DataInputStream dis = new DataInputStream(fis);
			int money = dis.readInt();
			int order = dis.read();
			dis.close();
			su = new Sutda(money, order); // JPanel�� ��ӹ��� SwingImage ��ü ����

			f.setTitle("����");
			f.getContentPane().add(su); // ContentPane�� SwingImage ��ü�� �ֽ��ϴ�.
			f.setSize(1000, 700);
			f.show();

			while (true) {
				if (su.get_connect() == 11) {
					available = true;
					break;
				} else { // state 11�� �ƴϸ�

					try {
						Thread.sleep(1000); // �����̽ð�.
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
				Thread.sleep(3500); // �����̽ð�.
			} catch (Exception e) {
			}
			f.dispose();

		}

	}
}
