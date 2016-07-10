package com.mitchellbosecke.seniorcommander;

import java.io.*;
import java.util.*;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public class PhraseProvider {

    private final static Map<String, List<String>> phrases;

    public enum Category {
        ADVICE, GREETING, CLOSE_CALL, GRIEF
    }

    static {
        InputStream inputStream = null;

        phrases = new HashMap<>();
        try {
            inputStream = PhraseProvider.class.getClassLoader().getResourceAsStream("phrases.properties");
            if (inputStream != null) {
                //properties.load(inputStream);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line = null;
                while((line = reader.readLine()) != null){
                    line = line.trim();

                    if("".equals(line) || line.startsWith("#")){
                        continue;
                    }

                    String[] split = line.split("=");
                    String category = split[0];
                    String phrase = split[1];

                    if(!phrases.containsKey(category)){
                        phrases.put(category, new ArrayList<>());
                    }

                    phrases.get(category).add(phrase);
                }

            } else {
                throw new FileNotFoundException("property file 'phrases.properties' not found in the classpath");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getPhrase(Category category) {
        List<String> categoryPhrases = phrases.get(category.name().toLowerCase());
        Random generator = new Random();
        int index = generator.nextInt(categoryPhrases.size());
        return categoryPhrases.get(index);
    }
}
