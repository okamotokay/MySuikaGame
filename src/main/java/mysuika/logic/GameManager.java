package mysuika.logic;

import java.util.List;

import org.jbox2d.dynamics.Body;

import mysuika.model.FruitType;
import mysuika.physics.PhysicsWorld;
import mysuika.repository.HighScoreRepository;
import mysuika.ui.GameFrame;
import mysuika.ui.GamePanel;

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
	public  static final FruitType[] TYPES = FruitType.values();
	
	public  static final int     GUIDE_Y        = 50;        // ガイド表示の縦座標は上から50px
	private static final int     NEXT_FURITSIZE = 5;         // ランダムに出現するフルーツのサイズ範囲（0～4までのランダムとして使用）
	private static final int     GUIDE_MOVE     = 20;        // キーボード入力でガイドを左右に動かすのは20px
	private static final float   GAMEOVER_LINE  = 17.0f;     // ゲームオーバーとなる高さ（物理ワールド上で18m相当）
	private static boolean       isGameover     = false;     // ゲームオーバー状態を管理する。trueならゲームを終了する。
	private static boolean       isDrop         = false;     //フルーツ落下中かどうかを管理する。trueの間はフルーツを落下させる入力をを受け付けない
	private GameFrame            frame;                      // ゲームウィンドウのフレーム
	private PhysicsWorld         physics;                    // 物理演算を管理するクラス
	private int                  constType, nextType, score; // 現在のフルーツ型、次に出現するフルーツ型、スコア
	private List<Integer>        topScores;
	private HighScoreRepository  repo;
	
	/**
	 * コンストラクタ
	 * @param gameframe ゲーム画面のフレーム
	 */
	public GameManager(PhysicsWorld physics){
		this.physics   = physics;
		this.score     = 0;
		this.repo      = new HighScoreRepository();
		this.topScores = repo.loadTopScores();
		this.constType = randType(); // 現在のフルーツ型をランダムに設定
		this.nextType  = randType(); // 次のフルーツ型をランダムに設定
		physics.init(); // 物理ワールドの初期化
	}
	
	/**
	 * 落下中フルーツの衝突検知後の処理
	 * - ガイドとNEXTのフルーツを更新
	 * - パネルの再描画
	 * - 落下中フラグの解除
	 */
	public void CollisionDetection() {
		this.constType = nextType;
		this.nextType  = randType();
		frame.getSidePanel().repaint();
		frame.getGamePanel().repaint();
		physics.clearDrop(); // 監視中のBodyをnullにする
		isDrop = false; // 落下中フラグをfalseに
	}
	
	/**
	 * ガイドからフルーツを落下させる処理
	 * - 落下中は入力を無視
	 * - フルーツの物理Bodyを生成し、落下開始
	 * - ガイド表示を一時的に非表示（落下中フラグがtrueの間）
	 */
	public void dropFruit() {
		if (isDrop) return;// 既に落下中なら何もしない
		isDrop = true;// 落下中フラグを立てる
		// ガイド位置から物理ワールド座標へ変換
		float x = frame.getGamePanel().getCursorX() / GamePanel.SCALE;
		float y = (frame.getGamePanel().getHeight() - GUIDE_Y) / GamePanel.SCALE;
		// フルーツを物理ワールドに生成し、落下開始。同時に監視用フィールドにsetする。
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
		for (Body fruit : physics.getActiveFruitBodies()) {
			if (isDrop)  continue; // 落下中のフルーツは処理をスキップ
			// フルーツの上端がゲームオーバーラインを超えたか判定
			if (fruit.getPosition().y + TYPES[(int) fruit.getUserData()].getRadius() > GAMEOVER_LINE) {
				isGameover = true;
				break;
			}
		}
		if(isGameover) {
			frame.getGamePanel().getTimer().stop();	// ゲーム停止
			updateHighScores();
		}
	}
	
	/**
	 * ガイド（カーソル）を左に移動
	 */
	public void moveGuideLeft() {
		GamePanel panel = frame.getGamePanel();
		int newX = panel.getCursorX() - GUIDE_MOVE;
		// 左端に到達したらそれ以上行かない
		if (newX < 0) newX = 0;
		panel.setCursorX(newX);
		panel.repaint();
	}

	/**
	 * ガイド（カーソル）を右に移動
	 */
	public void moveGuideRight() {
		GamePanel panel = frame.getGamePanel();
		int maxX = panel.getWidth(); // パネルの右端
		int newX = panel.getCursorX() + GUIDE_MOVE;
		// 右端に到達したらそれ以上行かない
		if (newX > maxX) newX = maxX;
		panel.setCursorX(newX);
		panel.repaint();
	}
	/**
	 * スコアがハイスコア上位3件に入るかを判定し、該当する場合はCSVに保存します。
	 * 保存後は、ハイスコアリストを再読み込みして最新状態に更新します。
	 * 
	 */
	public void updateHighScores() {
		if (topScores.isEmpty() || score > topScores.get(topScores.size() - 1) || topScores.size() < 3) {
			repo.saveScore(score);
			topScores = repo.loadTopScores();
		}
	}
	
	/**
	 * フルーツのtypeをランダムに返す
	 * @return 0～NEXT_FURITSIZE-1 の整数
	 */
	private int randType() {
		return (int)(Math.random() * NEXT_FURITSIZE);
	}
	
	// 以下、ゲッター・セッター
	public void setFrame(GameFrame gameframe) {
		this.frame = gameframe;
	}
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
	public List<Integer> getTopScores() {
		return topScores;
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
	public boolean siGameOver() {
		return isGameover;
	}
	
}