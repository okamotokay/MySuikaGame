package mysuika.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import mysuika.logic.GameManager;

/**
 * GamePanel クラス
 * 
 * ゲーム画面（プレイフィールド）を描画するパネルです。
 *  * ゲーム画面やサイドパネルのレイアウト、ユーザー入力（マウス・キーボード）、
 * ゲームループ（Timerによる定期更新）など、UI全体の制御をここで行います。
 * 
 * フィールド内の全フルーツの描画、ガイド表示、落下予測線、ゲームオーバー表示など
 * ゲームのビジュアルを一括して管理します。
 * マウスの左右移動でガイド位置を動かすことができます。
 * 
 * 作成者: 岡本
 * 作成日: 2025-06-02
 */
public class GamePanel extends JPanel {
	public  static final float SCALE = 30;     // 物理ワールドの1mを30pxに変換するスケール係数
	private static final int   FPS = 16;       // 約60fpsでゲームを更新するタイマー間隔（ミリ秒）
	private static final int   CURSOR_X = 200; // ガイドのX座標の初期値をゲーム画面中央に
	private GameManager        manager;        // ゲーム全体の管理クラスへの参照
	private int                cursorX;        // ガイドのX座標（ピクセル単位）
	private Timer              timer;          // ゲームループ用タイマー
	
	/**
	 * コンストラクタ
	 * @param manager ゲームロジック管理クラス
	 */
	GamePanel(GameManager manager) {
		this.manager = manager;
		this.cursorX = CURSOR_X; // 初期位置は中央
		setBackground(Color.WHITE);// 背景色
		setBorder(new LineBorder(Color.GRAY, 2)); // 枠線
		setupListeners();
		setupTimer();
	}
	
	/**
	 * 入力リスナー登録（マウス・キーボード）
	 */
	private void setupListeners() {
		// --- ユーザー入力リスナーの設定 ---
		// マウスクリックでフルーツを落下
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				manager.dropFruit();//ドロップメソッドを呼ぶ
			}
		});
		// マウスの左右移動でガイド位置をリアルタイム更新
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if (manager.siGameOver()) return; // ゲームオーバー時は操作不可
				cursorX = e.getX();
				// 画面端からはみ出さないように制限
				cursorX = Math.max(0, Math.min(getWidth() - 1, cursorX));
				repaint(); // 再描画
			}
		});
		// --- キーボード操作の設定 ---
		// ・下キーでフルーツを落下
		// ・左右キーでガイド（落下位置）を移動
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_DOWN  -> manager.dropFruit();
				case KeyEvent.VK_LEFT  -> manager.moveGuideLeft();
				case KeyEvent.VK_RIGHT -> manager.moveGuideRight();
			}}
		});
	}

	/**
	 * ゲームループ（タイマー）
	 * FPS間隔で物理演算・描画・ゲームオーバー判定を繰り返す
	 */
	private void setupTimer() {
		this.timer = new Timer(FPS, e -> {
			manager.getWorld().step(); // 物理ワールド更新
			repaint();                 // ゲーム画面再描画
			manager.isGameOver();      // ゲームオーバー判定
		});
		timer.start();
		requestFocusInWindow(); // gamePanelにフォーカスを当てる
	}
	
	/**
	 * パネルの描画処理
	 * フィールド内の全フルーツ、ガイド、落下予測線、ゲームオーバー表示などを描画
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawFruits(g);   // フィールド内のフルーツを描画
		drawGuide(g);    // ガイド（落下予測線と仮フルーツ）を描画
		drawGameOver(g); // ゲームオーバー表示
	}
	
	/**
	 * フィールド内の全フルーツを描画
	 * @param g グラフィックスオブジェクト
	 */
	private void drawFruits(Graphics g) {
		for (Body body : manager.getWorld().getActiveFruitBodies()) {
			Vec2  pos        = body.getPosition();
			Color fruitColor = GameManager.TYPES[(int)body.getUserData()].getColor();
			int radius       = (int)(GameManager.TYPES[(int)body.getUserData()].getRadius() * SCALE);
			int diameter     = 2 * radius;
			int x            = (int)(pos.x * SCALE) - radius;
			int y            = (int)(getHeight() - pos.y * SCALE) - radius;
			g.setColor(fruitColor);
			g.fillOval(x, y, diameter, diameter); // 本体
			g.setColor(Color.BLACK);
			g.drawOval(x, y, diameter, diameter); // 枠線
		}
	}
	
	/**
	 * ガイド（落下予測線と仮フルーツ）を描画
	 * @param g グラフィックスオブジェクト
	 */
	private void drawGuide(Graphics g) {
		// 落下中でなければガイドを描画
		if (manager.getConst() != -1) {
			int guideY = GameManager.GUIDE_Y; // 上部から50pxの位置
			g.setColor(Color.BLACK);
			g.drawLine(cursorX, guideY, cursorX, getHeight()); // 落下予測線
			// ガイド用フルーツの描画
			Color fruitColor = GameManager.TYPES[manager.getConst()].getColor();
			float scale      = GameManager.TYPES[manager.getConst()].getRadius() * SCALE;
			int   fX         = (int)(cursorX - scale);
			int   fY         = (int)(guideY - scale);
			int   fS         = (int)(scale * 2); // 半径×2
			g.setColor(fruitColor);
			g.fillOval(fX, fY, fS, fS); // ガイドフルーツ本体
			g.setColor(Color.BLACK);
			g.drawOval(fX, fY, fS, fS); // ガイドフルーツ枠線
		}
	}
	
	/**
	 * ゲームオーバー時の表示を描画
	 * @param g グラフィックスオブジェクト
	 */
	private void drawGameOver(Graphics g) {
		if (manager.siGameOver()) {
			g.setColor(Color.BLACK);
			String text = "GAME OVER";
			g.setColor(Color.BLACK);
			g.setFont(new Font("Arial", Font.BOLD, 40));
			// フォントメトリクスを使って文字列の幅と高さを取得
			FontMetrics fm = g.getFontMetrics();
			int textWidth  = fm.stringWidth(text);
			int textHeight = fm.getAscent(); // ベースラインから上方向の高さ
			// パネルの中央に文字列の中心が来るように位置を調整
			g.drawString(text, (getWidth()  - textWidth) / 2, (getHeight() + textHeight) / 2);
		}
	}
	
	// 以下、セッターゲッター
	public int getCursorX() {
		return cursorX;
	}
	public void setCursorX(int newX) {
		this.cursorX = newX;
	}
	public Timer getTimer() {
		return timer;
	}
}