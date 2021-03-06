package com.randomcorp.file.image;

import com.randomcorp.file.normalization.LineSplitter;
import com.randomcorp.processing.vocabulary.VocabularyRegistry;
import com.randomcorp.processing.vocabulary.Word;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public final class FileImage {

    private final String name;

    private final Map<Word, Set<Long>> wordOccurrences;

    private FileImage(List<List<Word>> lines, String name) {

        this.name = name;

        final Map<Word, Set<Long>> indexes = new HashMap<>();
        long index = 0;
        for (List<Word> line : lines) {
            for (Word word : line) {
                if (!indexes.containsKey(word)) {
                    indexes.put(word, new HashSet<>());
                }
                indexes.get(word).add(index);
                index++;
            }
        }
        indexes.replaceAll((k, v) -> Collections.unmodifiableSet(v));
        this.wordOccurrences = Collections.unmodifiableMap(indexes);
    }


    public static FileImage of(File textFile, VocabularyRegistry registry, LineSplitter lineSplitter) throws IOException {
        final List<List<Word>> lines = new ArrayList<>();

        String line = null;
        final BufferedReader reader = new BufferedReader(new FileReader(textFile));
        while ((line = reader.readLine()) != null) {
            final List<String> seperateWords = lineSplitter.split(line);
            final List<Word> registeredWords = seperateWords.stream()
                    .map(registry::registerAsWord).collect(Collectors.toList());

            lines.add(Collections.unmodifiableList(registeredWords));
        }

        reader.close();
        return new FileImage(lines, textFile.getName());
    }

    public Map<Word, Set<Long>> getWordOccurrences() {
        return wordOccurrences;
    }

    public String getName() {
        return name;
    }
}
