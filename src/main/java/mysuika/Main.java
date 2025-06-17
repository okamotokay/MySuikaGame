package mysuika;

import javax.swing.SwingUtilities;

import mysuika.logic.GameManager;
import mysuika.physics.PhysicsWorld;
import mysuika.ui.GameFrame;

/**
 * Main クラス
 * 
* アプリケーションのエントリポイントです。
 * ゲームに必要なオブジェクト（PhysicsWorld, GameManager, GameFrame）を初期化し、
 * Swingのイベントディスパッチスレッド上でゲームウィンドウを表示します。
 * 
 * 作成者: 岡本
 * 更新日: 2025-06-02
 * 
 */
public class Main {
	
	public static void main(String[] args) {
		PhysicsWorld physics = new PhysicsWorld();
		GameManager manager  = new GameManager(physics);
		physics.setGameManager(manager);
		// Swingアプリケーションのスレッドセーフな起動
		SwingUtilities.invokeLater(() -> {
		GameFrame frame = new GameFrame(manager);
		manager.setFrame(frame);
		frame.setVisible(true);
		});
	}
}
