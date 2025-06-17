package mysuika.model;

import java.awt.Color;

import mysuika.logic.GameManager;
import mysuika.ui.GamePanel;

/**
 * FruitBlueprint クラス
 * 
 * 合体処理などで新たに生成するフルーツの「座標」と「種別(type)」を一時的に保持するためのクラスです。
 * 
 * 例えば、2つの同種フルーツが合体した際、その場に一段階大きいフルーツを生成するために使用します。
 * 描画や物理演算用の情報取得メソッドも用意しています。
 * 
 * 作成者: 岡本
 * 作成日: 2025-06-02
 */
public class FruitBlueprint {
	private float     x, y;  // 生成するフルーツの座標（物理ワールド上のm単位）
	private int       type;  // フルーツの型番（FruitTypeのインデックス）
	private FruitType fruit; // フルーツ種別情報（半径・色など取得用）

	/**
	 * コンストラクタ
	 * @param x 生成位置のX座標（m単位）
	 * @param y 生成位置のY座標（m単位）
	 * @param type フルーツの型番（FruitTypeのインデックス）
	 */
	public FruitBlueprint(float x, float y, int type) {
		this.x     = x;
		this.y     = y;
		this.type  = type;
		this.fruit = GameManager.TYPES[type];
	}
	//以下、ゲッター
	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}
	public int getType() {
		return type;
	}
	public float getRadius() {
		return fruit.getRadius();
	}
	public Color getColor() {
		return fruit.getColor();
	}
	/**
	 * フルーツの直径をピクセル単位で取得
	 * @return 直径（px単位）
	 */
	public int getScale() {
		return (int)(fruit.getRadius() * 2 * GamePanel.SCALE);
	}
}
