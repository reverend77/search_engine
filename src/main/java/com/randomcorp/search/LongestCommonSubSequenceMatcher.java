package com.randomcorp.search;

import com.randomcorp.file.image.FileImage;
import com.randomcorp.processing.vocabulary.Word;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This search engine uses an algorithm based on longest common substring.
 */
public class LongestCommonSubSequenceMatcher implements Matcher {

    @Override
    public SearchResult search(FileImage fileImage, Query query) {
        final Map<Integer, List<List<Word>>> matches = new HashMap<>();

        for (List<Word> line : fileImage.getLines()){
            final Map<Integer, List<List<Word>>> matchesPart = longestCommonSubSequence(query.getWords(), line);
            mergeMatches(matches, matchesPart);
        }

        return new SearchResult(matches);
    }

    private void mergeMatches(Map<Integer, List<List<Word>>> result, Map<Integer, List<List<Word>>> part){
        for(Map.Entry<Integer, List<List<Word>>> entry : part.entrySet()){
            result.putIfAbsent(entry.getKey(), new ArrayList<>());
            result.get(entry.getKey()).addAll(entry.getValue());
        }
    }

    private Map<Integer, List<List<Word>>> longestCommonSubSequence(List<Word> queryWords, List<Word> fileLine) {

        final Map<Integer, List<List<Word>>> matches = new HashMap<>();

        final int[][] cache = new int[queryWords.size()][fileLine.size()];
        for (int m = 0; m < queryWords.size(); m++) {

            for (int n = 0; n < fileLine.size(); n++) {

                if (queryWords.get(m).equals(fileLine.get(n))) {
                    final int length = cache[m - 1][n - 1] + 1;
                    cache[m][n] = length;

                    final List<Word> match = new ArrayList<>();
                    for(int k = n; k < n + length; k++){
                        match.add(fileLine.get(k));
                    }

                    matches.putIfAbsent(length, new ArrayList<>());
                    matches.get(length).add(match);

                } else if (m != 0 && n != 0) {
                    cache[m][n] = Math.max(cache[m][n - 1], cache[m - 1][n]);
                }

            }
        }

        return matches;
    }
}
