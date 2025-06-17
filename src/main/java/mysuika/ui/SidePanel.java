package mysuika.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JPanel;

import mysuika.logic.GameManager;

/**
 * SidePanel クラス
 * 
 * スコアやNEXTフルーツ、進化の輪（全フルーツ一覧）など
 * ゲーム情報を表示するサイドパネルです。
 * 
 * 作成者: 岡本
 * 作成日: 2025-06-02
 */
public class SidePanel extends JPanel {

	private final static int NEXT_LABEL  = 10;      // ネクストの文字のY座標
	private final static int NEXT_Y = 80;           // ネクストのフルーツ円の中心のY座標
	private final static int FRUITRING_LABEL = 130; // シンカの輪の文字のY座標
	private final static int FRUITRING_Y = 180;     // シンカの輪のドーナツのY座標
	private final static int FRUITRING_RADIUS = 50; // シンカの輪のドーナツの半径
	private final static int FRUITRING_ICON = 20;   // シンカの輪のアイコンの直径
	private final static int FRUITRING_ANGLE = 72;  // シンカの輪描画の開始位置の角度
	//private final static int HIGHSCORE_LABEL = 400;
	//private final static int HIGHSCORE_VALUE = 0;
	
	private GameManager manager; // ゲーム全体の管理クラスへの参照
	
	/**
	 * コンストラクタ
	 * @param manager ゲームロジック管理クラス
	 */
	SidePanel(GameManager manager){
		this.manager = manager;
	}
	
	/**
	 * パネルの描画処理
	 * NEXTフルーツ、スコア、進化の輪（全フルーツ一覧）を描画します。
	 * 各描画処理は目的ごとにメソッド分割しています。
	 */
	@Override
	protected void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		drawNextSection(g);      // NEXTフルーツ表示
		drawFruitRingSection(g); // 進化の輪表示
		drawScoreSection(g);     // スコア表示
		drawHighScoreSection(g); // ハイスコア表示
	}
	/**
	 * NEXTフルーツのラベルとグラフィックを描画
	 * @param g グラフィックスオブジェクト
	 */
	private void drawNextSection(Graphics g) {
		viewText(g, "ネクスト", NEXT_LABEL); // ラベル描画
		drawNextFruit(g, NEXT_Y); // NEXTフルーツのグラフィック描画
	}
	
	/**
	 * 進化の輪のラベルと全フルーツ一覧（円形配置）を描画
	 * @param g グラフィックスオブジェクト
	 */
	private void drawFruitRingSection(Graphics g) {
		viewText(g, "シンカの輪", FRUITRING_LABEL); // ラベル描画
		drawFruitRing(g, FRUITRING_Y); // 全フルーツを円形に描画
	}
	
	/**
	 * スコアのラベルと現在スコアを描画
	 * @param g グラフィックスオブジェクト
	 */
	private void drawScoreSection(Graphics g) {
		viewText(g, "スコア", 310);// ラベル描画
		viewText(g, "" + manager.getScore(), 350); // スコア数値描画
	}
	
	/**
	 * ハイスコアのラベルとハイスコアを描画
	 * @param g グラフィックスオブジェクト
	 */
	private void drawHighScoreSection(Graphics g) {
		viewText(g, "ハイスコア", 400);// ラベル描画
		drawHighScores(g);
	}
	
	/**
	 * NEXTフルーツのグラフィックを中央に描画
	 * @param g グラフィックスオブジェクト
	 */
	private void drawNextFruit(Graphics g, int y) {
		// 中心位置を決める（例：画面中央 + 高さ70px）
		int cx = getWidth() / 2;
		int cy = y; // 中心のy位置を固定（好みで調整）
		// フルーツの半径（ピクセル）
		float nextf = GameManager.TYPES[manager.getNext()].getRadius() * GamePanel.SCALE;
		int r = (int)nextf;
		// 描画のために左上座標を計算（中心から半径分ずらす）
		int fx = cx - r;
		int fy = cy - r;
		int fd = r * 2;
		g.setColor(GameManager.TYPES[manager.getNext()].getColor());
		g.fillOval(fx, fy, fd, fd); // 本体
		g.setColor(Color.BLACK);
		g.drawOval(fx, fy, fd, fd); // 枠線
	}
	
	/**
	 * 進化の輪（全フルーツ一覧）を円形に描画
	 * @param g グラフィックスオブジェクト
	 */
	private void drawFruitRing(Graphics g, int y) {
		int fruitCount = GameManager.TYPES.length;
		int ringRadius = FRUITRING_RADIUS; // リングの半径
		int iconSize   = FRUITRING_ICON;   // フルーツアイコンの直径 
		 // 各フルーツの配置角度を計算
		for (int i = 0; i < fruitCount; i++) {
			double angle = 2 * Math.PI * i / fruitCount - Math.toRadians(FRUITRING_ANGLE);
			int gx = (int) (getWidth() / 2 + ringRadius * Math.cos(angle) - iconSize / 2);
			int gy = (int) ((y + ringRadius) + ringRadius * Math.sin(angle) - iconSize / 2);
			g.setColor(GameManager.TYPES[i].getColor());
			g.fillOval(gx, gy, iconSize, iconSize); // 本体
			g.setColor(Color.BLACK);
			g.drawOval(gx, gy, iconSize, iconSize); // 枠線
		}
	}
	
	private void drawHighScores(Graphics g) {
	    viewText(g, "ハイスコア", 400);
	    List<Integer> topScores = manager.getTopScores();
	    int startY = 440;
	    int lineHeight = 30;
	    int rank = 1;
	    for (int score : topScores) {
	        viewText(g, rank + "位: " + score, startY);
	        startY += lineHeight;
	        rank++;
	    }
	}
	
	/**
	 * テキストを中央揃えで描画するユーティリティメソッド
	 * @param g グラフィックスオブジェクト
	 * @param text 描画する文字列
	 * @param y Y座標（上端からのオフセット）
	 */
	void viewText(Graphics g, String text, int y) {
		
		g.setFont(new Font("Yu Gothic UI Mono", Font.BOLD, 23));
		FontMetrics fm = g.getFontMetrics();
		int textWidth  = fm.stringWidth(text);
		int textX      = (getWidth() - textWidth) / 2;
		int textY      = fm.getAscent() + y;
		g.setColor(Color.BLACK);
		g.drawString(text, textX, textY);
	}
}
