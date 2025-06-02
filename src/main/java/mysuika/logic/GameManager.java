package mysuika.logic;

import org.jbox2d.dynamics.Body;

import mysuika.model.FruitType;
import mysuika.physics.PhysicsWorld;
import mysuika.ui.GameFrame;

/**
 * GameManager クラス
 * 
 * ゲーム全体の進行管理を担うクラスです。
 * フルーツの生成・落下・衝突判定・スコア管理・ゲームオーバー判定など
 * ゲームの主要なロジックを一括して管理します。
 * 
 * 作成者: 岡本
 * 作成日: 2025-06-02
 */
public class GameManager {
	/* 利用可能なフルーツの種類一覧
	 * 配列のインデックスをtypeとして参照する
	 * (例:int型変数typeが0の時に配列のindex0にあるチェリーの情報を参照する)
	 */
	public static final FruitType[] TYPES = FruitType.values();
	// ランダムに出現するフルーツのサイズ範囲（0～4までのランダムとして使用）
	static final int NEXT_FURITSIZE = 5;
	// ゲームオーバーとなる高さ（物理ワールド上で18m相当）
	static final float GAMEOVER_LINE = 17.0f;
	// ゲームオーバー状態を管理するフラグ
	public static boolean isGameover = false;
	/* フルーツ落下中かどうかを管理するフラグ
	 * 落下中はプレイヤーの入力による新たなフルーツを出現させない
	 */
	static boolean isDrop = false; 
	// ゲームウィンドウのフレーム
	private GameFrame frame;
	// 物理演算を管理するクラス
	private PhysicsWorld physics;
	// 現在のフルーツ型、次に出現するフルーツ型、スコア
	private int constType, nextType, score;
	
	/**
	 * コンストラクタ
	 * @param gameframe ゲーム画面のフレーム
	 */
	public GameManager(GameFrame gameframe){
		this.frame = gameframe;
		this.score = 0;
		this.constType = randType();// 現在のフルーツ型をランダムに設定
		this.nextType = randType();	// 次のフルーツ型をランダムに設定
		
		this.physics = new PhysicsWorld(this);
		this.physics.init(); // 物理ワールドの初期化
	}
	
	/**
     * 落下中フルーツの衝突検知後の処理
     * - ガイドとNEXTのフルーツを更新
     * - パネルの再描画
     * - 落下中フラグの解除
     */
	public void CollisionDetection() {
		this.constType = nextType;
		this.nextType = randType();
		frame.getSidePanel().repaint();
		frame.getGamePanel().repaint();
		physics.clearDrop();// 監視中のBodyをnullにする
		isDrop = false; // 落下中フラグをfalseに
	}
	
	/**
	 * ガイドからフルーツを落下させる処理
	 * - 落下中は入力を無視
	 * - フルーツの物理Bodyを生成し、落下開始
	 * - ガイド表示を一時的に非表示（落下中フラグがtrueの間）
	 */
	public void Dropped() {
		if (isDrop) return;// 既に落下中なら何もしない
		isDrop = true;// 落下中フラグを立てる
		 // ガイド位置から物理ワールド座標へ変換
		float x = frame.getGamePanel().getCursorX() / 30.0f;
		float y = (frame.getGamePanel().getHeight() - 50) / 30.0f;
		// フルーツを物理ワールドに生成し、落下開始。Bodyが実際に生成されるのはここ
		physics.setDrop(physics.spawnFruit(x, y,constType));
		 // 落下中はガイドに何も表示しない
		this.constType = -1;
		frame.getGamePanel().repaint();
	}
	
	 /**
	  * ゲームオーバー判定
	  * - 全フルーツの座標をチェックし、上部ラインを超えていればゲームオーバー
	  * - ゲームオーバー時はタイマー停止
	  */
	public void isGameOver() {
		for (Body fruit : physics.getList()) {
			if (isDrop)  continue; // 落下中のフルーツは処理をスキップ
			float y = fruit.getPosition().y;// フルーツの高さ座標
			float fruitRadius = TYPES[(int) fruit.getUserData()].getRadius();// フルーツの半径
			 // フルーツの上端がゲームオーバーラインを超えたか判定
			if (y + fruitRadius > GAMEOVER_LINE) {
				isGameover = true;
				break;
			}
		}
		if(isGameover)frame.getTimer().stop();	// ゲーム停止
	}
	
	/**
	 * フルーツのtypeをランダムに返す
	 * @return 0～NEXT_FURITSIZE-1 の整数
	 */
	private int randType() {
		return (int)(Math.random() *  NEXT_FURITSIZE);
	}
	
	// 以下、ゲッター・セッター
	public GameFrame getFrame() {
		return this.frame;
	}
	public PhysicsWorld getWorld() {
		return physics;
	}
	public int getScore() {
		return score;
	}
	public void addScore(int add) {
		this.score += add;
	}
	public int getConst() {
		return constType;
	}
	public void setConst(int type) {
		this.constType = type;
	}
	public int getNext() {
		return nextType;
	}
	public void setNext(int type) {
		this.nextType = type;
	}
	
}