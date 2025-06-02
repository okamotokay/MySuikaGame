package mysuika.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

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

	// ゲーム全体の管理クラスへの参照
	private GameManager manager;
	
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
		drawNextSection(g);		// NEXTフルーツ表示
		drawScoreSection(g);	// スコア表示
		drawFruitRingSection(g);// 進化の輪表示
	}
	/**
	 * NEXTフルーツのラベルとグラフィックを描画
	 * @param g グラフィックスオブジェクト
	 */
	private void drawNextSection(Graphics g) {
		viewText(g, "NEXT", 20);// ラベル描画
		drawNextFruit(g); 		// NEXTフルーツのグラフィック描画
	}
	
	/**
	 * スコアのラベルと現在スコアを描画
	 * @param g グラフィックスオブジェクト
	 */
	private void drawScoreSection(Graphics g) {
		viewText(g, "SCORE", 160);					// ラベル描画
		viewText(g, "" + manager.getScore(), 210);	// スコア数値描画
	}
	
	/**
	 * 進化の輪のラベルと全フルーツ一覧（円形配置）を描画
	 * @param g グラフィックスオブジェクト
	 */
	private void drawFruitRingSection(Graphics g) {
		viewText(g, "シンカの輪", 300);	// ラベル描画
		drawFruitRing(g);				// 全フルーツを円形に描画
	}
	
	/**
	 * NEXTフルーツのグラフィックを中央に描画
	 * @param g グラフィックスオブジェクト
	 */
	private void drawNextFruit(Graphics g) {
		//	ネクストフルーツのグラフィック描画
		float nextf = GameManager.TYPES[manager.getNext()].getRadius() * 30;// 半径(px)
		int fx = (int)(getWidth() / 2 - nextf); // 中央揃え
		int fy =  (int)(70);
		int fd = (int)(nextf * 2); // 直径
		g.setColor(GameManager.TYPES[manager.getNext()].getColor());
		g.fillOval(fx, fy, fd, fd); // 本体
		g.setColor(Color.BLACK);
		g.drawOval(fx, fy, fd, fd); // 枠線
	}
	
	/**
	 * 進化の輪（全フルーツ一覧）を円形に描画
	 * @param g グラフィックスオブジェクト
	 */
	private void drawFruitRing(Graphics g) {
		int fruitCount = GameManager.TYPES.length;
		int ringRadius = 70; // リングの半径
		int iconSize = 30;   // フルーツアイコンの直径
		int centerX = getWidth() / 2;
		int centerY = 350 + ringRadius; 
		for (int i = 0; i < fruitCount; i++) {
			 // 各フルーツの配置角度を計算
			double angle = 2 * Math.PI * i / fruitCount - Math.toRadians(72);
			int x = (int) (centerX + ringRadius * Math.cos(angle) - iconSize / 2);
			int y = (int) (centerY + ringRadius * Math.sin(angle) - iconSize / 2);
			g.setColor(GameManager.TYPES[i].getColor());
			g.fillOval(x, y, iconSize, iconSize); // 本体
			g.setColor(Color.BLACK);
			g.drawOval(x, y, iconSize, iconSize); // 枠線
		}
	}
	
	/**
	 * テキストを中央揃えで描画するユーティリティメソッド
	 * @param g グラフィックスオブジェクト
	 * @param text 描画する文字列
	 * @param y Y座標（上端からのオフセット）
	 */
	void viewText(Graphics g,String text,int y) {
		
		g.setFont(new Font("SansSerif", Font.BOLD, 25)); // フォント・サイズ指定
		FontMetrics fm = g.getFontMetrics();
		int textWidth = fm.stringWidth(text);
		int textX = (getWidth() - textWidth) / 2;
		int textY = fm.getAscent() + y;
		g.setColor(Color.BLACK);
		g.drawString(text, textX, textY);
	}
}
