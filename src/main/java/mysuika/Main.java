package mysuika;

import javax.swing.SwingUtilities;

import mysuika.ui.GameFrame;

/**
 * Main クラス
 * 
 * スイカゲームのエントリポイント（起動用クラス）です。
 * mainメソッドからSwingのイベントディスパッチスレッド上でGameFrameを起動し、
 * ゲームウィンドウを表示します。
 * 
 * 作成者: 岡本
 * 更新日: 2025-06-02
 * 
 */
public class Main {
	
	public static void main(String[] args) {
		// Swingアプリケーションのスレッドセーフな起動
		SwingUtilities.invokeLater(() -> new  GameFrame().setVisible(true));
	}
}
