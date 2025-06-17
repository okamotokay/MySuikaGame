package  mysuika.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import mysuika.logic.GameManager;

/**
 * GameFrame クラス
 * 
 * スイカゲームのメインウィンドウを構成するクラスです。
 * ゲーム画面（GamePanel）とサイドパネル（SidePanel）をレイアウトし、
 * ウィンドウ全体の外観を構築します
 * 
 * 
 * 作成者: 岡本
 * 作成日: 2025-06-02
 */
public class GameFrame extends JFrame {
	
	private GameManager manager;     // ゲーム全体のロジック管理
	private JPanel      contentPane; // メインパネル（土台）
	private GamePanel   gamePanel;   // ゲーム描画用のパネル
	private SidePanel   sidePanel;   // NEXTやスコア表示用のサブパネル
	
	/**
	 * コンストラクタ
	 * ウィンドウの初期設定とゲーム開始処理を行う
	 */
	public GameFrame(GameManager manager) {
		this.manager = manager;
		setTitle("スイカゲーム ver 1.1");        // ウィンドウタイトル
		setSize(600, 600);                        // ウィンドウサイズ
		setDefaultCloseOperation(EXIT_ON_CLOSE);  //閉じるボタン
		setResizable(false);                      //ウィンドウサイズ変更の不可
		setLocationRelativeTo(null);              // 画面中央に表示
		setupPanels();                            // ゲームの初期化
	}
	
	/**
	 * UI構築（ゲーム画面・サイドパネル）
	 */
	private void setupPanels() {
		// ゲーム描画パネルの生成・設定
		this.gamePanel   = new GamePanel(manager);
		gamePanel.setPreferredSize(new Dimension(400, 600));
		// サイドパネルの生成・設定
		this.sidePanel   = new SidePanel(manager);
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

	//以下、ゲッター
	public SidePanel getSidePanel() {
		return sidePanel;
	}
	public GamePanel getGamePanel() {
		return gamePanel;
	}

}