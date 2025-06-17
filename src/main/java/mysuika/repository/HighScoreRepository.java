package mysuika.repository;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ハイスコア上位3件をCSVで読み書きするクラス
 */
public class HighScoreRepository {
	private static final String FILE_PATH    = System.getProperty("user.home") + "/.mysuika/highscore.csv";
	private static final int    MAX_ENTRIES  = 3;

	/**
	 * ハイスコア上位3件を読み込む
	 * @return スコアのリスト（降順ソート済み）
	 */
	public List<Integer> loadTopScores() {
		List<Integer> scores = new ArrayList<>();
		try (Stream<String> lines = Files.lines(Paths.get(FILE_PATH))) {
			scores = lines
					.map(String::trim)
					.map(this::safeParseInt)
					.filter(Objects::nonNull)
					.limit (MAX_ENTRIES)
					.sorted(Comparator.reverseOrder())
					.collect(Collectors.toList());
		} catch (IOException e) {
		// ファイルなしは空リスト
		}
		return scores;
	}

	/** 文字列を整数に変換。失敗したらnullを返す */
	private Integer safeParseInt(String s) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	/**
	 * 新しいスコアを追加して上位3件に入っていれば保存する
	 * @param newScore 新しいスコア
	 */
	public void saveScore(int newScore) {
		List<Integer> updatedScores = getUpdatedScores(newScore);
		prepareSaveDirectory();
		writeScoresToFile(updatedScores);
	}
	
	/** スコアを読み込み、新スコアを追加して上位3件に絞る */
	private List<Integer> getUpdatedScores(int newScore) {
		List<Integer> scores = loadTopScores();
		scores.add(newScore);
		scores.sort(Collections.reverseOrder());
		if (scores.size() > MAX_ENTRIES) scores = scores.subList(0, MAX_ENTRIES);
		return scores;
	}
	
	/** 保存先のディレクトリがなければ作成 */
	private void prepareSaveDirectory() {
		File file      = new File(FILE_PATH);
		File parentDir = file.getParentFile();
		if (parentDir != null && !parentDir.exists()) parentDir.mkdirs();
	}
	
	/** スコアリストをファイルに書き込む */
	private void writeScoresToFile(List<Integer> scores) {
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

