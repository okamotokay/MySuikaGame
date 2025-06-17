package mysuika.physics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import mysuika.logic.GameManager;
import mysuika.model.FruitBlueprint;

/**
 * PhysicsWorld クラス
 * 
 * ゲーム内の物理演算（重力、衝突、合体、消去など）を一括して管理します。
 * JBox2DライブラリのWorldをラップし、フルーツの生成・合体・消去・
 * 壁や床の生成・描画用データ変換など、ゲームロジックと物理世界をつなぐ役割を担います。
 * 
 * 作成者: 岡本
 * 作成日: 2025-06-02
 */
public class PhysicsWorld {
	private World                world;      // JBox2Dの物理ワールド本体
	private GameManager          manager;    // ゲーム全体の管理クラスへの参照
	private Body                 dropfruit;  // 現在落下中のフルーツの実体（衝突監視用）
	
	private List<Body>           activeFruitBodies    = new ArrayList<>(); // 物理ワールド内に存在する全フルーツのリスト
	private List<FruitBlueprint> pendingFruitSpawns   = new ArrayList<>(); // 合体後に生成予定のフルーツ情報
	private Set<Body>            pendingRemovalBodies = new HashSet<>();   // 削除予定のフルーツ（合体や消去時に使用）
	
	/**
	 * コンストラクタ
	 * @param gameManager ゲーム全体管理クラス
	 */
	public PhysicsWorld() {
		// 重力ベクトルを設定（下向きに強い重力）
		this.world   = new World(new Vec2(0.0f, -150.0f));
		// 衝突リスナーを設定
		world.setContactListener(new ContactListener() {
			@Override
			public void beginContact(Contact contact) {
				Body a = contact.getFixtureA().getBody();
				Body b = contact.getFixtureB().getBody();
				// 衝突したのが壁の場合は処理しない
				if(isWallOrFloor(a) || isWallOrFloor(b)) return;
				// 落下中フルーツが他のフルーツに衝突したらガイド更新
				if (a == getDrop() || b ==  getDrop()) manager.CollisionDetection();
				Merge(a, b);// 合体判定
			}
			@Override	public void endContact(Contact contact) {}
			@Override	public void preSolve(Contact contact, org.jbox2d.collision.Manifold oldManifold) {}
			@Override	public void postSolve(Contact contact, org.jbox2d.callbacks.ContactImpulse impulse) {}
		});
	}
	
	/**
	 * ゲームフィールド（床・壁）の生成
	 */
	public void init() {
		createGround();    //床
		createWall(0.0f);  // 左壁
		createWall(12.9f); // 右壁
	}
	
	/**
	 * 床（地面）を生成
	 */
	private void createGround() {
		BodyDef def = new BodyDef();
		def.position.set(6.5f, -0.9f); //フィールド中央下
		Body body  = world.createBody(def);
		PolygonShape box = new PolygonShape();
		box.setAsBox(7.0f, 1.0f); // 幅14, 高さ2
		FixtureDef fDef = new FixtureDef();
		fDef.shape    = box;
		fDef.density  = 1.0f;
		fDef.friction = 1.0f; // 摩擦係数
		body.createFixture(fDef);
	}
	
	/**
	 * 壁を生成
	 * @param f 壁のX座標（左端0.0f, 右端12.9f）
	 */
	private void createWall(float f) {
		BodyDef def = new BodyDef();
		def.position.set(f, 9.0f);  // 上下中央
		Body body = world.createBody(def);
		PolygonShape box = new PolygonShape();
		box.setAsBox(0.1f, 20.0f);  // 幅0.2, 高さ40
		body.createFixture(box, 0.0f);
		body.setUserData("wall");   // 衝突判定用ラベル
	}
	
	/**
	 * フルーツの物理Bodyを生成
	 * @param x X座標
	 * @param y Y座標
	 * @param type フルーツ種別
	 * @return 生成したBody
	 */
	private Body createFruitBody(float x, float y, int type) {
		Body body = createDynamicBody(x, y); // ボディを生成
		FixtureDef fDef = createFruitFixtureDef(type); // フィクスチャ定義を生成
		body.createFixture(fDef); // フィクスチャをボディにアタッチ
		setFruitDamping(body); // 減衰パラメータを適応
		return body;
	}
	
	/**
	 * 指定位置に動的ボディを生成
	 */
	private Body createDynamicBody(float x, float y) {
		BodyDef bDef = new BodyDef();
		bDef.type = BodyType.DYNAMIC; // 動的Body
		bDef.position.set(x, y);
		return world.createBody(bDef);
	}
	
	/**
	 * フルーツ種別に応じたフィクスチャ定義を生成
	 */
	private FixtureDef createFruitFixtureDef(int type) {
		// 円形シェイプ（半径はフルーツ種別ごとに設定）
		CircleShape circle = new CircleShape();
		circle.m_radius = GameManager.TYPES[type].getRadius();
		// フィクスチャ定義（物理特性をセット）
		FixtureDef fDef  = new FixtureDef();
		fDef.shape       = circle;               // 円形に
		fDef.density     =  densityFruits(type); // 質量を一定にするため密度を調整
		fDef.friction    = 0.3f;                 // 摩擦は低め
		fDef.restitution = 0.0f;                 // 弾まない
		return fDef;
	}
	
	/**
	 * ボディの減衰パラメータを設定
	 * （回転・移動が徐々に止まるようにする）
	 */
	private void setFruitDamping(Body body) {
		body.setAngularDamping(15.0f); // 回転をかなり減衰
		body.setLinearDamping(1.0f);   // 移動をわずかに減衰
	}
	
	/**
	 * フルーツの密度を計算
	 * @param type フルーツ種別
	 * @return 密度値
	 */
	private float densityFruits(int type) {
		float radius = GameManager.TYPES[type].getRadius();
		// 目標質量3.0を一定にするため、密度 = 3 / (π × 半径^2)
		return (float)(3.0f / (Math.PI * radius * radius));
	}

	/**
	 * Bodyが壁か床かどうか判定
	 * @param body 判定対象
	 * @return true: 壁
	 */
	private boolean isWallOrFloor(Body body) {
		return "wall".equals( body.getUserData());
	}
	
	/**
	 * フルーツをワールドに追加
	 * @param x X座標
	 * @param y Y座標
	 * @param type フルーツ種別
	 * @return 生成したBody
	 */
	public Body spawnFruit(float x, float y, int type) {
		Body body = createFruitBody(x, y, type);
		body.setUserData(type); // 種別を記録
		activeFruitBodies.add(body);
		return body;
	}
	
	/**
	 * 物理演算ワールドを1ステップ進め、合体・消去処理を実行
	 */
	public void step() {
		world.step(1.0f / 60.0f, 6, 2); // ステップ実行
		removeBody();                   // 削除予約のBodyを削除
		addBody();                      // 合体予定のBodyを生成
	}
	
	/**
	 * 
	 * 削除予約されたボディを物理ワールドから削除
	 */
	private void removeBody() {
		if (!pendingRemovalBodies.isEmpty()) {
			for (Body b : pendingRemovalBodies) {
				activeFruitBodies.remove(b);
				world.destroyBody(b);
			}
			pendingRemovalBodies.clear();
		}
	}
	
	/**
	 * 
	 * 追加予約されたボディを物理ワールに生成
	 */
	private void addBody() {
		if (!pendingFruitSpawns.isEmpty()) {
			for (FruitBlueprint b : pendingFruitSpawns)
				spawnFruit(b.getX(), b.getY(), b.getType());
				pendingFruitSpawns.clear();
		}
	}
	
	/**
	 * スイカ以外のフルーツの合体処理
	 * @param typeA フルーツ種別
	 * @param a 合体元Body
	 * @param b 合体元Body
	 */
	private void mergeFruits(int typeA,Body a,Body b) {
		manager.addScore(GameManager.TYPES[typeA].getScores()); // スコア加算
		// 既に削除予定なら何もしない
		if (pendingRemovalBodies.contains(a) || pendingRemovalBodies.contains(b)) return;
		// 合体後の位置は2体の中点
		Vec2 pos = a.getPosition().add(b.getPosition()).mul(0.5f);
		// 1段階上のフルーツ生成予約
		pendingFruitSpawns.add(new FruitBlueprint(pos.x, pos.y, typeA + 1));
		// 元のフルーツは削除予約
		pendingRemovalBodies.add(a);
		pendingRemovalBodies.add(b);
	}
	
	/**
	 * スイカ同士の合体（消去）処理
	 * @param typeA フルーツ種別（スイカ）
	 * @param a 合体元Body
	 * @param b 合体元Body
	 */
	private void mergeWatermelon(int typeA,Body a,Body b) {
		manager.addScore(GameManager.TYPES[typeA].getScores());
		// 既に削除予定なら何もしない
		if (pendingRemovalBodies.contains(a) || pendingRemovalBodies.contains(b)) return;
		pendingRemovalBodies.add(a);
		pendingRemovalBodies.add(b);
	}
	
	/**
	 * 衝突時の合体判定・処理の振り分け
	 * @param a 衝突Body
	 * @param b 衝突Body
	 */
	private void Merge(Body a, Body b) {
		// 両方ともフルーツで、同一でなければ判定
		if (activeFruitBodies.contains(a) && activeFruitBodies.contains(b) && a != b) {
			int typeA = (int)a.getUserData();
			int typeB = (int)b.getUserData();
			if (typeA == 10 && typeB == 10) { // スイカ同士
				mergeWatermelon(typeA,a,b);
			}else if (typeA == typeB && typeA < 10) { // 同種（スイカ未満）
				mergeFruits(typeA,a,b);
			}
		}
		// サイドパネル再描画
		manager.getFrame().getSidePanel().repaint();
	}
	
	/**
	 * フルーツ生成用のパラメーターを作成
	 * @param body 物理Body
	 * @return 生成前フルーツデータ
	 */
	public FruitBlueprint RenderData(Body body) {
		Vec2  pos    = body.getPosition();
		int   type   = (int)body.getUserData();
		float height = manager.getFrame().getGamePanel().getHeight();
		// 物理ワールド座標→画面座標に変換
		int x = (int) (pos.x);
		int y = (int) (height - pos.y);
		return new FruitBlueprint(x,y,type);
	}
	
	/** 衝突監視用Bodyをクリア */
	public void clearDrop() {
		this.dropfruit = null;
	}
	
	// 以下、ゲッター・セッターなど
	public void setGameManager(GameManager manager) {
		this.manager = manager;
	}
	public List<Body> getActiveFruitBodies() {
		return activeFruitBodies;
	}
	public Body getDrop() {
		return dropfruit;
	}
	public void setDrop(Body b) {
		this.dropfruit = b;
	}
}
