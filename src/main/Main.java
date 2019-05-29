package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;

public class Main {
	public static void main(String[] args) {
		new Main();
	}

	static JFrame frm;

	public Main() {
		frm = new JFrame("밤샘 도우미");
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frm.setVisible(true);

		APanel ap = new APanel();
		ap.setPreferredSize(new Dimension(500, 500));
		ap.setBackground(Color.black);
		frm.setContentPane(ap);
		frm.repaint();
		frm.pack();
		Dimension ssz = Toolkit.getDefaultToolkit().getScreenSize();
		frm.setLocation((ssz.width - frm.getWidth()) / 2, (ssz.height - frm.getHeight()) / 2);
	}

	public static class APanel extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private static Font noto;
		private static Font agency;
		private static BufferedImage line1;
		private static BufferedImage line2;
		private static BufferedImage snows;
		static {
			try {
				noto = Font.createFont(Font.TRUETYPE_FONT,
						Main.class.getResourceAsStream("/main/NotoSansCJKtc-Regular.ttf"));
				agency = Font.createFont(Font.TRUETYPE_FONT, Main.class.getResourceAsStream("/main/AgencyFB-Bold.otf"));
				line1 = ImageIO.read(Main.class.getResourceAsStream("/main/popup_line_01.png"));
				line2 = ImageIO.read(Main.class.getResourceAsStream("/main/popup_line_02.png"));
				snows = ImageIO.read(Main.class.getResourceAsStream("/main/snows.png"));
				snows = Scalr.resize(snows, Method.ULTRA_QUALITY, 500, 500);
			} catch (Exception e) {
			}
		}

		{
			new Thread(new Runnable() {

				@Override
				public void run() {
					boolean hv = false;
					while (isDisplayable() || !hv) {
						hv = hv || isDisplayable();
						repaint();
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}).start();
		}

		@Override
		protected void paintComponent(Graphics g) {
			// TODO Auto-generated method stub
			super.paintComponent(g);

			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			g2.setColor(Color.white);
			g2.setFont(noto.deriveFont(0, 35f));
			FontMetrics fm = g2.getFontMetrics();
			String txt = "새벽 7시까지 남은 시간";
			g2.drawString(txt, (getWidth() - fm.stringWidth(txt)) / 2, fm.getHeight());
			int sw = fm.stringWidth(txt) / 2;
			int dh = fm.getHeight() + 10;

			g2.drawImage(line1, getWidth() / 2 - sw, dh, sw, 3, null);
			g2.drawImage(line2, getWidth() / 2, dh, sw, 3, null);

			ZonedDateTime now = ZonedDateTime.now();
			ZonedDateTime newy = ZonedDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), 7, 0, 0, 0, ZoneId.systemDefault());
			if(now.getHour() >= 7) {
				newy = newy.plusDays(1);
			}
			Duration dur = Duration.between(now, newy);
			long sec = dur.getSeconds();

			g2.setFont(agency.deriveFont(0, 50f));
			fm = g2.getFontMetrics();
			txt = formatDate(sec);
			g2.drawString(txt, (getWidth() - fm.stringWidth(txt)) / 2, dh + 5 + fm.getHeight());

			if (sec < 0 && sec > -30) {
				g2.setColor(new Color(0x000000));
				g2.fillRect(0, 0, getWidth(), getHeight());
				if (since == 0)
					since = System.currentTimeMillis() + 450;
				long min = (since - System.currentTimeMillis());
				min = 450 - Math.max(min, 0);
				float mult = min / 450f;
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, mult));
				
				Shape dc = g2.getClip();
				g2.setClip(new Rectangle(0, 0, getWidth(), Math.round(getHeight() * mult)));
//				g2.drawImage(snows, 0, 0, null);
				
				g2.setClip(dc);
				
				
				g2.setColor(new Color(255, 215, 0));
				g2.setFont(noto.deriveFont(0, 50f));
				fm = g2.getFontMetrics();
				txt = "드디어 행복한";
				g2.drawString(txt, (getWidth() - fm.stringWidth(txt)) / 2, dh + 5 + fm.getHeight() * 2);

				txt = "아침이 밝았습니다!";
				g2.drawString(txt, (getWidth() - fm.stringWidth(txt)) / 2, dh + 5 + fm.getHeight() * 3);
				
				txt = "당신은 피곤하겠지만";
				g2.drawString(txt, (getWidth() - fm.stringWidth(txt)) / 2, dh + 5 + fm.getHeight() * 4);
			}
		}
	}

	static long since = 0;

	public static String formatDate(long secs) {
		String bstr = "";
		if (secs < 60)
			return secs + bstr;
		else {
			long mins = secs / 60;
			secs = secs % 60;
			bstr = ":" + packLength(secs + "");
			if (mins < 60)
				return mins + bstr;
			else {
				long hours = mins / 60;
				mins = mins % 60;
				bstr = ":" + packLength(mins + "") + bstr;
				if (hours < 24)
					return hours + bstr;
				else {
					long days = hours / 24;
					hours = hours % 24;
					bstr = ":" + packLength(hours + "") + bstr;
					return days + bstr;
				}
			}
		}
	}

	public static String packLength(String str) {
		if (str.length() == 1)
			return "0" + str;
		else
			return str;
	}
}
