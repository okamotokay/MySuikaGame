package mysuika.model;

import java.awt.Color;

/**
 * FruitType 列挙型
 * 
 * 各フルーツの「スコア（合体時得点）」「半径（物理サイズ）」「色（描画用）」をまとめて管理します。
 * 配列のインデックス（ordinal値）をtypeとして参照することで、ゲーム内で一意に識別できます。
 * 
 * 作成者: 岡本
 * 作成日: 2025-06-02
 */
public enum FruitType {
	// --- フルーツの定義 ---
	// (スコア, 半径[m], 色)
	cherry      // さくらんぼ
		(1,  0.3f, new Color(220, 0  , 80 )), // #FF0050
	strawberry  // いちご
		(3,  0.4f, new Color(255, 57 , 20 )), // #FF3914
	grapes      // ぶどう
		(6,  0.5f, new Color(138, 43 , 226)), // #8A2BE2
	dekopon     // デコポン
		(10, 0.8f, new Color(255, 170, 0  )), // #FFA900
	persimmon   // 柿
		(15, 1.1f, new Color(255, 120, 0  )), // #FF7800
	apple       // りんご
		(21, 1.4f, new Color(255, 0  , 0  )), // #FF0000
	pear        // 梨
		(28, 1.7f, new Color(255, 255, 153)), // #FFFF00
	peach       // 桃
		(36, 2.1f, new Color(255, 192, 203)), // #FFC0CB
	pineapple   // パイナップル
		(45, 2.4f, new Color(255, 239, 0  )), // #FFEF00
	melon       // メロン
		(55, 3.0f, new Color(0  , 255, 127)), // #00FF7F
	watermelon  // スイカ
		(66, 4.0f, new Color(0  , 128, 0  )); // #008000

	// --- フィールド ---
	private int   scores; // 合体時のスコア
	private float radius; // 半径（物理ワールド上のm単位）
	private Color color;  // 描画用の色
	
	/**
	 * コンストラクタ
	 * @param scores 合体時のスコア
	 * @param radius 半径（物理ワールド上のm単位）
	 * @param color  描画用の色
	 */
	FruitType (int scores, float  radius, Color color) {//コンストラクタ
		this.scores  = scores;
		this. radius =  radius;
		this.color   = color;
	}
	
	//以下、ゲッター
	public float getRadius() {
		return this. radius;
	}
	public Color getColor() {
		return this.color;
	}
	public int getScores() {
		return this.scores;
	}
}
