package mysuika.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.jbox2d.dynamics.Body;

import mysuika.logic.GameManager;
import mysuika.model.FruitBlueprint;
import mysuika.physics.PhysicsWorld;

/**
 * GamePanel クラス
 * 
 * ゲーム画面（プレイフィールド）を描画するパネルです。
 * フィールド内の全フルーツの描画、ガイド表示、落下予測線、ゲームオーバー表示など
 * ゲームのビジュアルを一括して管理します。
 * マウスの左右移動でガイド位置を動かすことができます。
 * 
 * 作成者: 岡本
 * 作成日: 2025-06-02
 */
public class GamePanel extends JPanel {
	// ゲーム全体の管理クラスへの参照
	private GameManager manager;
	// ガイド（落下位置）のX座標（ピクセル単位）。初期値は画面中央
	private int cursorX; 
	
	/**
	 * コンストラクタ
	 * @param manager ゲームロジック管理クラス
	 */
	GamePanel(GameManager manager) {
		this.manager = manager;
		this.cursorX = 200; // 初期位置は中央
		setBackground(Color.WHITE);// 背景色
		setBorder(new LineBorder(Color.GRAY, 2)); // 枠線
		// マウスの左右移動でガイド位置をリアルタイム更新
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if (GameManager.isGameover) return; // ゲームオーバー時は操作不可
				cursorX = e.getX();
				// 画面端からはみ出さないように制限
				cursorX = Math.max(0, Math.min(getWidth() - 1, cursorX));
				repaint(); // 再描画
			}
		});
	}
	
	/**
	 * パネルの描画処理
	 * フィールド内の全フルーツ、ガイド、落下予測線、ゲームオーバー表示などを描画
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		 drawFruits(g);		// フィールド内のフルーツを描画
		 drawGuide(g);		// ガイド（落下予測線と仮フルーツ）を描画
		 drawGameOver(g);	// ゲームオーバー表示
	}
	
	/**
	 * フィールド内の全フルーツを描画
	 * @param g グラフィックスオブジェクト
	 */
	private void drawFruits(Graphics g) {
		for (Body body : manager.getWorld().getList()) {
			FruitBlueprint blueprint = manager.getWorld().RenderData(body);
			int scale = blueprint.getScale();
			g.setColor(blueprint.getColor());
			g.fillOval((int)blueprint.getX(), (int)blueprint.getY(), scale, scale); // 本体
			g.setColor(Color.BLACK);
			g.drawOval((int)blueprint.getX(), (int)blueprint.getY(), scale, scale); // 枠線
		}
	}
	
	/**
	 * ガイド（落下予測線と仮フルーツ）を描画
	 * @param g グラフィックスオブジェクト
	 */
	private void drawGuide(Graphics g) {
		// 落下中でなければガイドを描画
		if (manager.getConst() != -1) {
			int guideY = 50;  // 上部から50pxの位置
			g.setColor(Color.BLACK);
			g.drawLine(cursorX, guideY, cursorX, getHeight()); // 落下予測線
			// ガイド用フルーツの描画
			float scale =  GameManager.TYPES[manager.getConst()].getRadius() * PhysicsWorld.SCALE;
			int fX = (int)(cursorX - scale);
			int fY = (int)(guideY - scale);
			int fS = (int)(scale * 2);
			g.setColor(GameManager.TYPES[manager.getConst()].getColor());
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
		if (GameManager.isGameover) {
			g.setColor(Color.BLACK);
			g.setFont(new Font("Arial", Font.BOLD, 40));//フォント
			g.drawString("GAME OVER", getWidth() / 4, getHeight() / 2);
		}
	}
	
	// 以下、ゲッター
	public int getCursorX() {
		return cursorX;
	}
}