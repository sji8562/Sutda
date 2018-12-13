package SutDa;

import java.awt.*;
import javax.swing.*;
import java.util.HashSet;
import java.util.Random;
import java.util.*;
import sun.audio.*;
import java.io.*;
import java.awt.event.*;
import java.io.*;

class A extends Thread {
	A() {
	}
}

public class Sutda extends JPanel implements ActionListener {

	// 1카드(2명의 카드위치) //중앙 위 아래
	int locate_x[] = { 380, 380 };
	int locate_y[] = { 10 , 460 };

	// 2카드 //중앙 위 아래
	int locate_x2[] = { 520, 520 };
	int locate_y2[] = { 10 , 220 };

	int random_num_Array[] = new int[20]; // 1~20까지 순서없이저장
	int a = 0;
	Image hide_img; // 화투뒷그림
	Image card_img[] = new Image[4];
	Image select_img[] = new Image[6];

	int u_money; // 천만원 //나중에 파일로 부터 불러올것임.
	int before_money = u_money;
	int cal_money = 0; // 게임에 참가한후 이득 혹은 손실 금액.
	int pan_money = 100; // 판돈
	int bet_money = 20; // 배팅액

	int pe = 1; // 버튼을 누를때마다 1씩증가, pe가 3이되면 카드를 모두 받은상태.
	int po = 2; // g.drawImage메소드때문인지(?) 여러번실행되는곳을 대처하기 위해 사용.
	int user_add_money = 0;
	Font Win_Font = new Font("Serif", Font.BOLD, 30);

	// 게임에 사용할 버튼들
	JButton bt1 = new JButton("다이");
	JButton bt2 = new JButton("콜");
	int exit = 0;
	int key = 0; // 유저가 어떤버튼을 눌렀는지에 따라 배팅및 소리가 선택됨.
	int end = 11; // 모든 게임이 끝나면 버튼을 눌러도 소용없도록
	// Graphics g;
	int state; // state에 따라 상황이 바뀐다.

	int player[] = new int[2];
	int player_order[] = new int[2];
	String class_name[] = new String[2];
	String s1;
	int sun; // 선 나중에 파일로 부터 불러올것임.
	int re_bet = 0;
	int connect = 0;

	Sutda() {

	}

	Sutda(int money, int order) { // 생성자.
		u_money = money;
		sun = order;
		state = 0; // 시작할때 state는 0이다.
		u_money -= bet_money; // 판돈을 내고 시작한다.

		playSound("bgm.mp3"); // 시작음악.
		HashSet set = new HashSet(); // 중복숫자체크
		Random rand = new Random();
		int num; // 랜덤으로 입력받을 수

		// 20개의 숫자를 중복없이 저장하는 부분.
		for (int i = 0; i < 20; i++) {
			num = Math.abs(rand.nextInt()) % 20 + 1;
			boolean setChanged = set.add(new Integer(num));
			if (setChanged == false)
				i--; // 중복이면 다시
			else
				random_num_Array[i] = num; // 중복아니면 저장.
		} // 저장완료

		repaint();

	}

	public void paint(Graphics g) {
		super.paint(g);
		// 게임판 선긋기.
		g.drawLine(0, 440, 1000, 440);
		g.drawLine(0, 200, 1000, 200);

		// 계급 선긋기.
		g.drawRect(200, 450, 120, 204);
		g.drawLine(270, 450, 270, 654);
		for (int i = 0; i < 12; i++) {
			g.drawLine(200, 467 + (17 * i), 320, 467 + (17 * i));
		}
		// 계급 글쓰기.
		g.drawString("1.삼팔광땡", 205, 464);
		g.drawString("2.땡", 205, 481);
		g.drawString("3.알리", 205, 498);
		g.drawString("4.독사", 205, 515);
		g.drawString("5.구삥", 205, 532);
		g.drawString("6.장삥", 205, 549);
		g.drawString("7.장사", 205, 566);
		g.drawString("8.사륙", 205, 583);
		g.drawString("9.갑오", 205, 600);
		g.drawString("10.끗,망통", 205, 617);
		g.drawString("사구,땡잡이", 205, 634);
		g.drawString("암행어사", 205, 651);

		// 방장
		g.drawString("* 이기면 배팅액의 두배.", 10, 500);
		g.drawString("* 지면 배팅액 소실.", 10, 520);
		g.drawString("* 매너 콜배팅", 10, 540);

		// 카드 뒷그림 불러오기.
		hide_img = Toolkit.getDefaultToolkit().getImage("hide_img.jpg");
		// 2명에게 줄 카드 4장 불러오기.(섯다는 20장중 4장만 사용함.)
		for (int i = 0; i < 4; i++) {
			card_img[i] = Toolkit.getDefaultToolkit().getImage("" + random_num_Array[i] + ".jpg");
		}

		if (state != 10) { // state10 에서는 보여줄필요없다.
			// 플레이어
			g.drawString("Com",345, 90);
			g.drawString("나", 345, 540);
			

			// 2명에게 처음 2장을 나눠준다.
			for (int i = 0; i < 2; i++) {
				if (i == 1) { // 유저
					g.drawImage(card_img[2], locate_x[2], locate_y[2], this);
					continue;
				} // 컴퓨터
				g.drawImage(hide_img, locate_x[i], locate_y[i], this);

			}
		}
		for (int i = 0; i < 2; i++) {
			player[i] = what_class(random_num_Array[i], random_num_Array[i + 2]);
			player_order[i] = i;
		}
		// re_bet==1때는 패에 맞는 배팅을 하는데 그림도 맞도록..
		if (key == 1)
			money_change1(454);
		else if (key == 2) { // 콜선택

			g.setColor(Color.pink); // 핑크색
			g.fillRect(339, 70, 30, 30); // 네모그림
			g.setColor(Color.BLACK); // 검은색
			if (re_bet == 0) {
				g.drawString("콜", 341, 90);
			} // +2, +20 //콜글자
			else {
				g.drawString("" + s1, 341, 90);
			}

			g.setColor(Color.pink);
			g.fillRect(490, 405, 30, 30);
			g.setColor(Color.BLACK);
			g.drawString("콜", 492, 425);// +2, +20 //콜글자

		}

		if (state == 6) {
			if (key == 1)
				money_change1(454);
			else if (key == 2) { // 콜선택
				// max();max();max();max();max();
				money_change2();
				money_change2(454);
			}
			pe++;
			if (pe == 3) {
				state = 7;
			}

		} // state == 6 //두번실행됨.

		if (state >= 7) {
			// player[i] = what_class(random_num_Array[i], random_num_Array[i+5]);
			// 표에서 계급에 맞는 색깔 칠하기.
			int draw_sign = 100;
			if (player[1] == 10000)
				draw_sign = 0;
			else if (player[1] == 9999 || (player[1] <= 101 && player[1] >= 92)) {
				draw_sign = 1;
			} else if (player[1] == 90)
				draw_sign = 2;
			else if (player[1] == 89)
				draw_sign = 3;
			else if (player[1] == 88)
				draw_sign = 4;
			else if (player[1] == 87)
				draw_sign = 5;
			else if (player[1] == 86)
				draw_sign = 6;
			else if (player[1] == 85)
				draw_sign = 7;
			else if (player[1] == 80)
				draw_sign = 8;
			else if (player[1] <= 78 && player[1] >= 1)
				draw_sign = 9;
			else if (player[1] == 91 || player[1] == 102 || player[1] == 1002)
				draw_sign = 10;
			else if (player[1] == 1003)
				draw_sign = 11;

			g.setColor(Color.red);
			for (int i = 0; i < 12; i++) {
				if (draw_sign == i) {
					g.fillRect(271, 451 + (17 * i), 49, 16);
				}
			}
			g.setColor(Color.BLACK);
		}

		// pe는 3이 되었고, 그러므로 state는 7이됨. 모든 카드를 받는부분.
		if (state == 7) {

			// 유저가 받는 두번째카드, for안에 두니 늦게 그려져서 밖에 두었다.
			g.drawImage(card_img[7], locate_x2[1], locate_y2[1], this);
			for (int i = 0; i < 2; i++) {
				if (i == 1) {
					continue;
				}
				// 두번째 카드들을 그려준다.(뒷그림)
				g.drawImage(hide_img, locate_x2[i], locate_y2[i], this);
			}
			// 그림을 그려서그런지(??왜그럴까) state7부분이 여러번 실행되어.
			po--; // 이런부분을 넣었다.
			if (po == 0)
				po = 1; // 결국엔 po는 1이 된다.

			// System.out.println("po" + po); //위줄 확인.
			// 이상태에서 버튼을 한번더 누르면 state는 9가 된다.
			// 여기서 바로 if(po==1)state=9; 이렇게 넘어가려 한다면,
			// 패를 보여주기전에 두번 배팅이된다.
		} // state==7
		if (state == 9) {
			re_bet++; // 이것이 처음 실행된다면, 0=>1; 되며, 컴의 선택배팅처음이다.

			if (key == 1)
				money_change1(454);
			else if (key == 2) { // 콜선택
				s1 = betting(player[0], player_order[0]);
				money_change2(454);
				// max();max();max();max();max();
				// money_change2();money_change2();money_change2(454);money_change2();money_change2();
			}

			state = 10; // 카드를 공개하는 state10으로
		} // state==9

		// 카드공개
		if (state == 10) { // 모든카드를 보여준다.

			// 카드를 그리되 "다이"가 된것은 뒷장을 보여준다.
			for (int i = 0; i < 2; i++) {
				if (s1 == "다이" && i == 0) {
					g.drawImage(hide_img, locate_x[0], locate_y[0], this);
					g.drawImage(hide_img, locate_x2[0], locate_y2[0], this);
					continue;
				} 
				g.drawImage(card_img[i], locate_x[i], locate_y[i], this);
				g.drawImage(card_img[i + 2], locate_x2[i], locate_y2[i], this);
			}

			// 두 수를 넣었을때 어떤패인지를 반환하는 숫자 함수 호출.

			for (int i = 0; i < 2; i++) {
				player[i] = what_class(random_num_Array[i], random_num_Array[i + 2]);
				player_order[i] = i;
				class_name[i] = what_class_name(player[i]);
			}

			g.setFont(new Font("Serif", Font.BOLD, 18));
			if (s1 != "다이")
				g.drawString("" + class_name[0], 140, 200);
			else {
				player[0] = 0;
			} // "다이"라면 계산에서 제외된다.0이 젤낮다.
			

			// 높은 순서대로 정렬
			int p_temp;
			int p_o_temp;
			String c_n_temp;
			for (int i = 0; i <= 3; i++) {
				for (int j = i + 1; j <= 4; j++) {
					if (player[i] < player[j]) {
						p_temp = player[i];
						player[i] = player[j];
						player[j] = p_temp;

						p_o_temp = player_order[i];
						player_order[i] = player_order[j];
						player_order[j] = p_o_temp;

						c_n_temp = class_name[i];
						class_name[i] = class_name[j];
						class_name[j] = c_n_temp;
					}
				}
			}

			// System.out.println(player[0] + " " + player[1] + " " + player[2] + " "
			// +player[3] + " " +player[4]);
			// System.out.println(player_order[0] + " " + player_order[1] + " " +
			// player_order[2] + " " +player_order[3] + " " +player_order[4]);

			g.setFont(Win_Font);
			/*
			 * player[0] = 10000; player_order[0] = 2; class_name[0] = "삼팔광땡";
			 */
			a++;
			//////////////////// 숫자의 크기를 가지고 승패를 좌우.
			if (player[0] == 10000) { // 삼팔광땡 이면
				if (player_order[0] == 2)
					user_add_money++;
				g.drawString("**" + class_name[0] + "**", 400, 60);
				g.drawString("" + (player_order[0] + 1) + "p의 승리", 400, 100);
				sun = player_order[0];
				if (a == 1)
					v_38kwang3();
				// System.out.println(player_order[0] + "가 승리");
			} else if (player[0] == 9999) { // 광땡 이면
				if (player_order[0] == 2)
					user_add_money++;
				g.drawString("**" + class_name[0] + "**", 400, 60);
				g.drawString("" + (player_order[0] + 1) + "p의 승리", 400, 100);
				sun = player_order[0];
				if (a == 1)
					v_kwang1();
				// System.out.println(player_order[0] + "가 승리");
			} else if (player[0] == 1003) { // 암행어사 이고

				if (player[1] == 101) { // player[1]가 장땡이면
					if (player_order[0] == 2)
						user_add_money++;
					g.drawString("**" + class_name[0] + "**", 400, 60);
					g.drawString("" + (player_order[0] + 1) + "p의 승리", 400, 100);
					sun = player_order[0];
					// System.out.println(player_order[0] + "가 승리");
				} else if (player[1] == 102) { // player[1]가 멍텅구리사구 이면
					g.drawString("**" + class_name[0] + "**", 400, 60);
					g.drawString("산 사람 모두 재경기", 400, 100);
					// System.out.println("산 사람 모두 재경기");
				} else if (player[1] <= 100 && player[1] >= 92) { // player[1]가 삥땡~구땡이면
					if (player_order[1] == 2)
						user_add_money++;
					g.drawString("**" + class_name[1] + "**", 400, 60);
					g.drawString("" + (player_order[1] + 1) + "p의 승리", 400, 100);
					sun = player_order[1];
					// System.out.println(player_order[1] + "가 승리");
				} else if (player[1] == 91) {
					g.drawString("**" + class_name[1] + "**", 400, 60);
					g.drawString("산 사람 모두 재경기", 400, 100);
					// System.out.println("산 사람 모두 재경기");
				} else if (player[1] <= 90 && player[1] >= 85) { // player[1]가 알리 이하이고
																	// 사륙이상이면
					if (player[1] == player[2]) {
						g.drawString("**" + class_name[1] + "**", 400, 60);
						g.drawString("" + (player_order[1] + 1) + "p와" + (player_order[2] + 1) + "p만 재경기", 400, 100);
						// System.out.println(player_order[1] +"와"+ player_order[2]+"만 재경기");
					} else {
						if (player_order[1] == 2)
							user_add_money++;
						g.drawString("**" + class_name[1] + "**", 400, 60);
						g.drawString("" + (player_order[1] + 1) + "p의 승리", 400, 100);
						sun = player_order[1];
						// System.out.println(player_order[1] + "가 승리");
					}
				} else if (player[1] <= 80) { // 갑오이하이면
					if (player[1] == player[2]) {
						if (player[2] == player[3]) {
							if (player[3] == player[4]) {
								g.drawString("**" + class_name[1] + "**", 400, 60);
								g.drawString(
										" 재경기",
										400, 100);
								// System.out.println(player_order[1]+
								// player_order[2]+player_order[3]+player_order[4] +"만 재경기");
							} else {
								g.drawString("**" + class_name[1] + "**", 400, 60);
								g.drawString("" + (player_order[1] + 1) + "p와" + (player_order[2] + 1) + "p와"
										+ (player_order[3] + 1) + "p만 재경기", 400, 100);
								// System.out.println(player_order[1] +"와"+ player_order[2]+player_order[3]+"만
								// 재경기");
							}
						} else {
							g.drawString("**" + class_name[1] + "**", 400, 60);
							g.drawString("" + (player_order[1] + 1) + "p와" + (player_order[2] + 1) + "p만 재경기", 400,
									100);
							// System.out.println(player_order[1] +"와"+ player_order[2]+"만 재경기");
						}
					} else {
						if (player_order[1] == 2)
							user_add_money++;
						g.drawString("**" + class_name[1] + "**", 400, 60);
						g.drawString("" + (player_order[1] + 1) + "p의 승리", 400, 100);
						sun = player_order[1];
						// System.out.println(player_order[1] + "가 승리");
					}
				}
				// 암행어사
			} else if (player[0] == 1002) { // 삼칠땡잡이 이면
				if (player[1] == 101) {
					if (player_order[1] == 2)
						user_add_money++;
					g.drawString("**" + class_name[1] + "**", 400, 60);
					g.drawString("" + (player_order[1] + 1) + "p의 승리", 400, 100);
					sun = player_order[1];
					// System.out.println(player_order[1] + "가 승리");
				} else if (player[1] <= 100 && player[1] >= 92) {
					if (player_order[0] == 2)
						user_add_money++;
					g.drawString("**" + class_name[0] + "**", 400, 60);
					g.drawString("" + (player_order[0] + 1) + "p의 승리", 400, 100);
					sun = player_order[0];
					// System.out.println(player_order[0] + "가 승리");
				} else if (player[1] == 91 || player[1] == 102) {
					g.drawString("**" + class_name[1] + "**", 400, 60);
					g.drawString("산 사람 모두 재경기", 400, 100);
					// System.out.println("산 사람 모두 재경기");
				} else if (player[1] <= 90 && player[1] >= 85) { // player[1]가 알리 이하이고
																	// 사륙이상이면
					if (player[1] == player[2]) {
						g.drawString("**" + class_name[1] + "**", 400, 60);
						g.drawString("" + (player_order[1] + 1) + "p와" + (player_order[2] + 1) + "p만 재경기", 400, 100);
						// System.out.println(player_order[1] +"와"+ player_order[2]+"만 재경기");
					} else {
						if (player[1] == 2)
							user_add_money++;
						g.drawString("**" + class_name[1] + "**", 400, 60);
						g.drawString("" + (player_order[1] + 1) + "p의 승리", 400, 100);
						sun = player_order[1];
						// System.out.println(player_order[1] + "가 승리");
					}
				} else if (player[1] <= 80) { // 갑오이하이면
					if (player[1] == player[2]) {
						if (player[2] == player[3]) {
							if (player[3] == player[4]) {
								g.drawString("**" + class_name[1] + "**", 400, 60);
								g.drawString(
										"" + (player_order[1] + 1) + "p와" + (player_order[2] + 1) + "p와"
												+ (player_order[3] + 1) + "p와" + (player_order[4] + 1) + "p만 재경기",
										400, 100);
								// System.out.println(player_order[1]+
								// player_order[2]+player_order[3]+player_order[4] +"만 재경기");
							} else {
								g.drawString("**" + class_name[1] + "**", 400, 60);
								g.drawString("" + (player_order[1] + 1) + "p와" + (player_order[2] + 1) + "p와"
										+ (player_order[3] + 1) + "p만 재경기", 400, 100);
								// System.out.println(player_order[1] +"와"+ player_order[2]+player_order[3]+"만
								// 재경기");
							}
						} else {
							g.drawString("**" + class_name[1] + "**", 400, 60);
							g.drawString("" + (player_order[1] + 1) + "p와" + (player_order[2] + 1) + "p만 재경기", 400,
									100);
							// System.out.println(player_order[1] +"와"+ player_order[2]+"만 재경기");
						}

					} else {
						if (player_order[1] == 2)
							user_add_money++;
						g.drawString("**" + class_name[1] + "**", 400, 60);
						g.drawString("" + (player_order[1] + 1) + "p의 승리", 400, 100);
						sun = player_order[1];
						// System.out.println(player_order[1] + "가 승리");
					}
				}
				// 삼칠땡잡이
			} else if (player[0] == 102) { // 멍텅구리사구
				g.drawString("**" + class_name[0] + "**", 400, 60);
				g.drawString("산 사람 모두 재경기", 400, 100);
				// System.out.println("산 사람 모두 재경기");
			} else if (player[0] == 91) { // 보통사구
				g.drawString("**" + class_name[0] + "**", 400, 60);
				g.drawString("산 사람 모두 재경기", 400, 100);
				// System.out.println("산 사람 모두 재경기");
			} else if (player[0] <= 101 && player[0] >= 92) { // 땡이면//같은땡은 하나만 나올수 있음.
				if (player_order[0] == 2)
					user_add_money++;
				g.drawString("**" + class_name[0] + "**", 400, 60);
				g.drawString("" + (player_order[0] + 1) + "p의 승리", 400, 100);
				sun = player_order[0];
				// System.out.println(player_order[0] + "가 승리");

			} else if (player[0] <= 90 && player[0] >= 85) { // 알리 이하이고
																// 사륙이상이면
				if (player[0] == player[1]) { // 알리~사륙은 같은패가 두번까지만 나올수 있음.
					g.drawString("**" + class_name[0] + "**", 400, 60);
					g.drawString("" + (player_order[0] + 1) + "p와" + (player_order[1] + 1) + "p만 재경기", 400, 100);
					// System.out.println(player_order[0] +"와"+ player_order[1]+"만 재경기");
				} else {
					if (player[0] == 2)
						user_add_money++;
					g.drawString("**" + class_name[0] + "**", 400, 60);
					g.drawString("" + (player_order[0] + 1) + "p의 승리", 400, 100);
					sun = player_order[0];
					// System.out.println(player_order[0] + "가 승리");
				}

			} else if (player[0] <= 80) { // 갑오이하이면
				if (player[0] == player[1]) {
					if (player[1] == player[2]) {
						if (player[2] == player[3]) {
							if (player[3] == player[4]) {
								g.drawString("**" + class_name[0] + "**", 400, 60);
								g.drawString("" + (player_order[0] + 1) + "p와" + (player_order[1] + 1) + "p와"
										+ (player_order[2] + 1) + "p와" + (player_order[3] + 1) + (player_order[4] + 1)
										+ "p만 재경기", 400, 100);
								// System.out.println(player_order[0]+
								// player_order[1]+player_order[2]+player_order[3]+player_order[4] +"만 재경기");
							} else {
								g.drawString("**" + class_name[0] + "**", 400, 60);
								g.drawString(
										"" + (player_order[0] + 1) + "p와" + (player_order[1] + 1) + "p와"
												+ (player_order[2] + 1) + "p와" + (player_order[3] + 1) + "p만 재경기",
										400, 100);
								// System.out.println(player_order[0]+
								// player_order[1]+player_order[2]+player_order[3] +"만 재경기");
							}
						} else {
							g.drawString("**" + class_name[0] + "**", 400, 60);
							g.drawString("" + (player_order[0] + 1) + "p와" + (player_order[1] + 1) + "p와"
									+ (player_order[2] + 1) + "p만 재경기", 400, 100);
							// System.out.println(player_order[0] +"와"+ player_order[1]+player_order[2]+"만
							// 재경기");
						}
					} else {
						g.drawString("**" + class_name[0] + "**", 400, 60);
						g.drawString("" + (player_order[0] + 1) + "p와" + (player_order[1] + 1) + "p만 재경기", 400, 100);
						// System.out.println(player_order[0] +"와"+ player_order[1]+"만 재경기");
					}

				} else {
					if (player_order[0] == 2)
						user_add_money++;
					g.drawString("**" + class_name[0] + "**", 400, 60);
					g.drawString("" + (player_order[0] + 1) + "p의 승리", 400, 100);
					sun = player_order[0];
					// System.out.println(player_order[0] + "가 승리");
				}
			}
			//////////////////// 숫자의 크기를 가지고 승패를 좌우.

			if (user_add_money == 1) { // 이기면 돈이 계산됨.
				// 결과 = (총번돈) - (배팅액)
				// 결과 = (처음돈 + 판돈) - (처음돈 - 배팅후 처음돈)
				u_money = (before_money + pan_money) - (before_money - u_money);
				System.out.println("u_money = " + u_money);

				// FileOutputStream fos = new FileOutputStream("read.txt");
				// DataOutputStream dos = new DataOutputStream(fos);

				// dos.writeInt(10000000);
				// dos.writeByte(0);
				// fos.close();
			}

			connect = 11;
			end = 10; // end가 10이되면 버튼을 눌려도 반응없음
		} // state==10

		// 유저돈, 이득손실, 총액, 배팅액 //repaint시 매번 그려진다.
		g.setColor(Color.BLACK);
		g.setFont(new Font("Serif", Font.BOLD, 19));
		g.drawString("" + u_money, 550, 635);

		g.setFont(new Font("Serif", Font.CENTER_BASELINE, 15));
		g.setColor(Color.RED);
		g.drawString("" + cal_money, 550, 655);

		g.setColor(Color.black);
		g.setFont(new Font("Serif", Font.BOLD, 23));
		g.drawString("총      액  :     " + pan_money, 380, 360);

		g.setFont(new Font("Serif", Font.CENTER_BASELINE, 23));
		g.drawString("배 팅 액  :     " + bet_money, 380, 400);

		if (state == 0) { // 처음은 state가 0이다.
			// 버튼들 위치 와 크기 지정.
			bt1.setBounds(700, 480, 80, 60);
			bt2.setBounds(790, 480, 80, 60);

			// 버튼들 리스너에 추가
			bt1.addActionListener(this);
			bt2.addActionListener(this);

			// 판넬에 버튼 붙이기.
			this.add(bt1);
			this.add(bt2);

		} // state == 0

	}

	public void actionPerformed(ActionEvent ae) {

		if (sun == 2) { // 사용자가 선일때
			if (ae.getSource() == bt1) { // 다이
				if (end != 10) {
					connect = 11;
					end = 10;
				}
			} else if (ae.getSource() == bt2) { // 콜
				if (end != 10) {
					key = 2;
					if (state == 0)
						state = 6; // 콜가 눌러질때마다 state6으로
					if (po == 1)
						state = 9; // 모든카드를 받으면 po1이된다.
									// state9에서 마지막배팅후, state10으로..
					repaint();
				}
			}
		} else { // 사용자가 선이 아닐때
			if (ae.getSource() == bt1) { // 다이
				if (end != 10) {
					connect = 11;
					end = 10;
				}
			} else if (ae.getSource() == bt2) { // 콜
				if (end != 10) {
					key = 2;
					if (state == 0)
						state = 6; // 콜가 눌러질때마다 state6으로
					if (po == 1)
						state = 9; // 모든카드를 받으면 po1이된다.
									// state9에서 마지막배팅후, state10으로..
					repaint();
				}
			}
		}

	}

	//////// 컴퓨터 배팅
	public void money_change0() { // 컴다이
		die();
		// 다이하면 어떻게~된다.
	}

	public void money_change2() { // 콜
		max();
		bet_money *= 2; // 배팅액 2배
		pan_money = pan_money + bet_money;
	}
	//////// 컴퓨터 배팅

	/////// 사용자 배팅
	public void money_change1(int user) { // 다이
		die();
		// connect=11;
		// 게임종료
	}

	public void money_change2(int user) { // 콜
		max();

		bet_money *= 2; // 배팅액 2배
		u_money -= bet_money;
		pan_money = pan_money + bet_money;
	}
	/////// 사용자 배팅

	// 배팅소리들
	public void die() {
		playSound("다이.wav");
		try {
			Thread.sleep(400); // 딜레이시간.
		} catch (Exception e) {
		}
	}

	public void max() {
		playSound("콜.wav");
		try {
			Thread.sleep(400);//
		} catch (Exception e) {
		}

	}

	public void v_38kwang3() {
		playSound("v_38kwang3.wav");
	}

	public void v_kwang1() {
		playSound("v_kwang1.wav");
	}

	public int what_class(int num1, int num2) {
		int result = 0;

		if (num1 > num2) {
			int temp;
			temp = num1;
			num1 = num2;
			num2 = temp;
		}
		// System.out.println("num1 = "+num1);
		// System.out.println("num2 = "+num2);
		if (num1 == 5 && num2 == 15) {
			result = 10000; // 38광땡
		} else if ((num1 == 1 && num2 == 5) || (num1 == 1 && num2 == 15)) {
			result = 9999; // 광땡
		} else if (num1 == 19 && num2 == 20) {
			result = 101; // 장땡
		} else if (num1 == 17 && num2 == 18) {
			result = 100; // 구땡
		} else if (num1 == 15 && num2 == 16) {
			result = 99; // 팔땡
		} else if (num1 == 13 && num2 == 14) {
			result = 98; // 칠땡
		} else if (num1 == 11 && num2 == 12) {
			result = 97; // 육땡
		} else if (num1 == 9 && num2 == 10) {
			result = 96; // 오땡
		} else if (num1 == 7 && num2 == 8) {
			result = 95; // 사땡
		} else if (num1 == 5 && num2 == 6) {
			result = 94; // 삼땡
		} else if (num1 == 3 && num2 == 4) {
			result = 93; // 이땡
		} else if (num1 == 1 && num2 == 2) {
			result = 92; // 일땡////////////////////////
		} else if ((num1 == 7 && num2 == 18) || (num1 == 8 && num2 == 18)) {
			result = 91; // 보통사구(낮은사구)
		} else if ((num1 == 7 && num2 == 17) || (num1 == 8 && num2 == 17)) {
			result = 102; // 멍텅구리사구(높은사구)
		} else if (num1 == 5 && num2 == 13) {
			result = 1002; // 삼칠땡잡이
		} else if (num1 == 7 && num2 == 13) {
			result = 1003; // 암행어사/////////////////////
		} else if ((num1 == 1 && num2 == 3) || (num1 == 1 && num2 == 4) || (num1 == 2 && num2 == 3)
				|| (num1 == 2 && num2 == 4)) {
			result = 90; // 알리
		} else if ((num1 == 1 && num2 == 7) || (num1 == 1 && num2 == 8) || (num1 == 2 && num2 == 7)
				|| (num1 == 2 && num2 == 8)) {
			result = 89; // 독사
		} else if ((num1 == 1 && num2 == 17) || (num1 == 1 && num2 == 18) || (num1 == 2 && num2 == 17)
				|| (num1 == 2 && num2 == 18)) {
			result = 88; // 구삥
		} else if ((num1 == 1 && num2 == 19) || (num1 == 1 && num2 == 20) || (num1 == 2 && num2 == 19)
				|| (num1 == 2 && num2 == 20)) {
			result = 87; // 장삥
		} else if ((num1 == 7 && num2 == 19) || (num1 == 7 && num2 == 20) || (num1 == 8 && num2 == 19)
				|| (num1 == 8 && num2 == 20)) {
			result = 86; // 장사
		} else if ((num1 == 7 && num2 == 11) || (num1 == 7 && num2 == 12) || (num1 == 8 && num2 == 11)
				|| (num1 == 8 && num2 == 12)) {
			result = 85; // 사륙
		}

		if (result < 85) {
			if (num1 % 2 == 1)
				num1++;
			if (num2 % 2 == 1)
				num2++;
			int num3 = (num1 / 2) + (num2 / 2);
			if (num3 >= 10)
				num3 = num3 - 10; // 10을 빼준다.

			if (num3 == 9) {
				result = 80; // 갑오
			} else if (num3 == 8) {
				result = 78; // 여덟끗
			} else if (num3 == 7) {
				result = 77; // 일곱끗
			} else if (num3 == 6) {
				result = 76; // 여섯끗
			} else if (num3 == 5) {
				result = 75; // 다섯끗
			} else if (num3 == 4) {
				result = 74; // 네끗
			} else if (num3 == 3) {
				result = 73; // 세끗
			} else if (num3 == 2) {
				result = 72; // 두끗
			} else if (num3 == 1) {
				result = 71; // 한끗
			} else if (num3 == 0) {
				result = 1; // 망통
			}
		}

		return result;
	}

	public String what_class_name(int number) {
		String name = "";

		if (number == 10000) {
			name = "삼팔광땡";
		} else if (number == 9999) {
			name = "광땡";
		} else if (number == 1003) {
			name = "암행어사";
		} else if (number == 1002) {
			name = "삼칠땡잡이";
		} else if (number == 102) {
			name = "멍텅구리사구";
		} else if (number == 101) {
			name = "장땡";
		} else if (number == 100) {
			name = "구땡";
		} else if (number == 99) {
			name = "팔땡";
		} else if (number == 98) {
			name = "칠땡";
		} else if (number == 97) {
			name = "육땡";
		} else if (number == 96) {
			name = "오땡";
		} else if (number == 95) {
			name = "사땡";
		} else if (number == 94) {
			name = "삼땡";
		} else if (number == 93) {
			name = "이땡";
		} else if (number == 92) {
			name = "일땡";
		} else if (number == 91) {
			name = "사구";
		} else if (number == 90) {
			name = "알리";
		} else if (number == 89) {
			name = "독사";
		} else if (number == 88) {
			name = "구삥";
		} else if (number == 87) {
			name = "장삥";
		} else if (number == 86) {
			name = "장사";
		} else if (number == 85) {
			name = "사륙";
		} else if (number == 80) {
			name = "갑오";
		} else if (number == 78) {
			name = "여덟끗";
		} else if (number == 77) {
			name = "일곱끗";
		} else if (number == 76) {
			name = "여섯끗";
		} else if (number == 75) {
			name = "다섯끗";
		} else if (number == 74) {
			name = "네끗";
		} else if (number == 73) {
			name = "세끗";
		} else if (number == 72) {
			name = "두끗";
		} else if (number == 71) {
			name = "한끗";
		} else if (number == 1) {
			name = "망통";
		}
		// 다이
		// else if(number == 0){
		// name = "다이";
		// }

		return name;
	}

	// re_bet==1때는 패에 맞는 배팅을 하는데 그림도 맞도록..
	// draw_betting(player[0]);
	// draw_betting(player[1]);
	// draw_betting(player[2]);
	// draw_betting(player[3]);
	// draw_betting(player[4]); 넣으면 문꾸가 반환됨.
	public String betting(int num, int user_num){	//컴퓨터의 선택 배팅
		String what_choice="";
		int probability;
		Random rand = new Random();
		probability = Math.abs(rand.nextInt())%100 + 1;	//1~100중 하나
		if(re_bet == 1 && sun == user_num){	//컴퓨터의 선만 할수 있는 배팅
			if(num == 1003){					//암행어사
				if(probability <=70){
					what_choice ="콜";
					money_change2();	//콜	70%
				}else if(probability <= 100){
					what_choice ="다이";
					money_change0();	//다이	10%
				}						
			}else if(num ==1002){				//삼칠땡잡이
				if(probability <=80){
					what_choice ="콜";
					money_change2();	//콜
				}else if(probability <= 100){
					what_choice ="다이";
					money_change0();	//다이
				}		
			}else if(num == 102){				//멍텅구리사구
				if(probability <=50){
					what_choice ="콜";
					money_change2();	//콜
				}else if(probability <= 100){
					what_choice ="다이";
					money_change0();	//다이
				}
			}else if(num == 91){				//사구
				if(probability <=40){
					what_choice ="콜";
					money_change2();	//콜
				}else if(probability <= 100){
					what_choice ="다이";
					money_change0();	//다이
				}
			}else if(num <=10000 && num >= 99){	//삼팔광땡부터 팔땡까지
				if(probability <=96){
					what_choice ="콜";
					money_change2();	//콜
				}	//삥
									//다이는 안함
				
			}else if(num <=98 && num >=96){					//칠땡~오땡까지
				if(probability <=96){
					what_choice ="콜";
					money_change2();	//콜
				}			//다이는 안함
			}else if(num <=95 && num >=92){		//사땡~삥땡까지
				if(probability <=90){
					what_choice ="콜";
					money_change2();	//콜
				}
			}else if(num <=90 && num >= 88){	//알리~구삥
				if(probability <=75){
					what_choice ="콜";
					money_change2();	//콜
				}else if(probability == 100){
					what_choice ="다이";
					money_change0();	//다이
				}
			}else if(num <=87 && num >= 85){	//장삥~사륙
				if(probability <=44){
					what_choice ="콜";
					money_change2();	//콜
				}else if(probability <= 100){
					what_choice ="다이";
					money_change0();	//다이
				}
			}else if(num <=80 && num >= 78){	//갑오~여덟끗
				if(probability <=11){
					what_choice ="콜";
					money_change2();	//콜
				}else if(probability <= 100){
					what_choice ="다이";
					money_change0();	//다이
				}
			}else if(num <=77 && num >= 75){	//일곱끗~다섯끗
				if(probability <=4){
					what_choice ="콜";
					money_change2();	//콜
				}else if(probability <= 100){
					what_choice ="다이";
					money_change0();	//다이
				}
			}else if(num <=74 && num >= 1){	//네끗~망통
				if(probability <=2){
					what_choice ="콜";
					money_change2();	//콜
				}else if(probability <= 100){
					what_choice ="다이";
					money_change0();	//다이
				}
			}
		}else

	{ // 컴퓨터의 선택 선이 아닐 때
		if (num == 1003) { // 암행어사
			if (probability <= 70) {
				what_choice = "콜";
				money_change2(); // 콜
			} 
		} else if (num == 1002) { // 삼칠땡잡이
			if (probability <= 80) {
				what_choice = "콜";
				money_change2(); // 콜
			} 
		} else if (num == 102) { // 멍텅구리사구
			if (probability <= 54) {
				what_choice = "콜";
				money_change2(); // 콜
			}else if (probability <= 100) {
				what_choice = "다이";
				money_change0(); // 다이
			}
		} else if (num == 91) { // 사구
			if (probability <= 44) {
				what_choice = "콜";
				money_change2(); // 콜
			}  else if (probability <= 100) {
				what_choice = "다이";
				money_change0(); // 다이
			}
		} else if (num <= 10000 && num >= 99) { // 삼팔광땡부터 팔땡까지
			if (probability <= 97) {
				what_choice = "콜";
				money_change2(); // 콜
			}
		} else if (num <= 98 && num >= 96) { // 칠땡~오땡까지
			if (probability <= 97) {
				what_choice = "콜";
				money_change2(); // 콜
			} 
		} else if (num <= 95 && num >= 92) { // 사땡~삥땡까지
			if (probability <= 92) {
				what_choice = "콜";
				money_change2(); // 콜
			} 
		} else if (num <= 90 && num >= 88) { // 알리~구삥
			if (probability <= 79) {
				what_choice = "콜";
				money_change2(); // 콜
			} else if (probability <= 100) {
				what_choice = "다이";
				money_change0(); // 다이
			}
		} else if (num <= 87 && num >= 85) { // 장삥~사륙
			if (probability <= 48) {
				what_choice = "콜";
				money_change2(); // 콜
			}  else if (probability <= 100) {
				what_choice = "다이";
				money_change0(); // 다이
			}
		} else if (num <= 80 && num >= 78) { // 갑오~여덟끗
			if (probability <= 15) {
				what_choice = "콜";
				money_change2(); // 콜
			}else if (probability <= 100) {
				what_choice = "다이";
				money_change0(); // 다이
			}
		} else if (num <= 77 && num >= 75) { // 일곱끗~다섯끗
			if (probability <= 8) {
				what_choice = "콜";
				money_change2(); // 콜
			}  else if (probability <= 100) {
				what_choice = "다이";
				money_change0(); // 다이
			}
		} else if (num <= 74 && num >= 1) { // 네끗~망통
			if (probability <= 6) {
				what_choice = "콜";
				money_change2(); // 콜
			}  else if (probability <= 100) {
				what_choice = "다이";
				money_change0(); // 다이
			}
		}
	}return what_choice;
	}

	public int getu_money(){
		return u_money;
	}

	public int get_connect() {
		return connect;
	}

	public int get_sun() {
		return sun;
	}

	// 사운드 메소드.
	public void playSound(String file_url) {
		try {
			File file = new File(file_url);
			FileInputStream fis = new FileInputStream(file);
			AudioStream as = new AudioStream(fis);
			AudioPlayer.player.start(as);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("sound exception");
		}
	}

}
