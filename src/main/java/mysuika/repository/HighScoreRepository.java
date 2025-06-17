package mysuika.repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ハイスコア上位3件をCSVで読み書きするクラス
 */
public class HighScoreRepository {
private static final String FILE_PATH = System.getProperty("user.home") + "/.mysuika/highscore.csv";
private static final int MAX_ENTRIES = 3;

/**
* ハイスコア上位3件を読み込む
* @return スコアのリスト（降順ソート済み）
*/
	public List<Integer> loadTopScores() {
		List<Integer> scores = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
			String line;
			while ((line = br.readLine()) != null && scores.size() < MAX_ENTRIES) {
				try {
					scores.add(Integer.parseInt(line.trim()));
				} catch (NumberFormatException e) {
					// 不正な行は無視
				}
			}
		} catch (IOException e) {
			// ファイルなしの場合は空リスト返す
		}
	scores.sort(Collections.reverseOrder()); // 降順にソート
	return scores;
	}

	/**
	 * 新しいスコアを追加して上位3件に入っていれば保存する
	 * @param newScore 新しいスコア
	 */
	public void saveScore(int newScore) {
		List<Integer> scores = loadTopScores();
		scores.add(newScore);
		scores.sort(Collections.reverseOrder());
		if (scores.size() > MAX_ENTRIES) {
			scores = scores.subList(0, MAX_ENTRIES);
		}
		//ファイルが存在しなかったら新規作成する
		File file = new File(FILE_PATH);
		File parentDir = file.getParentFile();
		if (parentDir != null && !parentDir.exists()) {
			parentDir.mkdirs();
		}
		// ファイルへ書き込み
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
			for (int score : scores) {
				bw.write(String.valueOf(score));
				bw.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

