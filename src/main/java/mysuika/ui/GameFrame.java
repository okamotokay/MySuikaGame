package  mysuika.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import mysuika.logic.GameManager;

/**
 * GameFrame クラス
 * 
 * スイカゲームのメインウィンドウ（フレーム）を管理するクラスです。
 * ゲーム画面やサイドパネルのレイアウト、ユーザー入力（マウス・キーボード）、
 * ゲームループ（Timerによる定期更新）など、UI全体の制御を行います。
 * 
 * 作成者: 岡本
 * 作成日: 2025-06-02
 */
public class GameFrame extends JFrame {
	
	static final int FPS = 16;	// 約60fpsでゲームを更新するタイマー間隔（ミリ秒）
	private GameManager manager;	// ゲーム全体のロジック管理
	private JPanel contentPane;	// メインパネル（土台）
	private GamePanel gamePanel;	// ゲーム描画用のパネル
	private SidePanel sidePanel;	// NEXTやスコア表示用のサブパネル
	private Timer timer;		// ゲームループ用タイマー
	
	/**
	 * コンストラクタ
	 * ウィンドウの初期設定とゲーム開始処理を行う
	 */
	public GameFrame() {
		setTitle("スイカゲーム ver 1.1");		// ウィンドウタイトル
		setSize(600, 600);				// ウィンドウサイズ
		setDefaultCloseOperation(EXIT_ON_CLOSE);	//閉じるボタン
		setResizable(true);				//ウィンドウサイズ変更の不可
		setLocationRelativeTo(null); 			// 画面中央に表示
		startNewGame();					// ゲームの初期化
	}
	/**
	 * ゲーム開始時の初期化処理
	 * UI構築、リスナー登録、タイマー開始
	 */
	private void startNewGame(){
		setupPanels();
		setupListeners();
		setupTimer();
	}
	
	/**
	 * UI構築（ゲーム画面・サイドパネル）
	 */
	private void setupPanels() {
		// ゲームロジック管理クラスの生成
		this.manager = new GameManager(this);
		// ゲーム描画パネルの生成・設定
		this.gamePanel = new GamePanel(manager);
		gamePanel.setPreferredSize(new Dimension(400, 600));
		// サイドパネルの生成・設定
		this.sidePanel = new SidePanel(manager);
		sidePanel.setPreferredSize(new Dimension(200, 600));
		// メインパネルのレイアウト設定
		this.contentPane = new JPanel(new BorderLayout());
		contentPane.setPreferredSize(new Dimension(600, 600));
		contentPane.add(gamePanel, BorderLayout.CENTER);
		contentPane.add(sidePanel, BorderLayout.EAST);
		add(contentPane);
		// ゲームパネルのフォーカス設定
		gamePanel.setFocusable(true);
	}
	
	/**
	 * 入力リスナー登録（マウス・キーボード）
	 */
	private void setupListeners() {
		// --- ユーザー入力リスナーの設定 ---
		// マウスクリックでフルーツを落下
		gamePanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				manager.dropFruit();//ドロップメソッドを呼ぶ
			}
		});
		
		// --- キーボード操作の設定 ---
		// ・下キーでフルーツを落下
		// ・左右キーでガイド（落下位置）を移動
		gamePanel.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_DOWN -> manager.dropFruit();
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
			manager.getWorld().step();	// 物理ワールド更新
			gamePanel.repaint();		// ゲーム画面再描画
			manager.isGameOver();		// ゲームオーバー判定
		});
		timer.start();
		requestFocusInWindow(); // gamePanelにフォーカスを当てる
	}
	
	//以下、ゲッター
	public SidePanel getSidePanel() {
		return sidePanel;
	}
	public GamePanel getGamePanel() {
		return gamePanel;
	}
	public Timer getTimer() {
		return timer;
	}
}