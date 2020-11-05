package org.group.sensim.eval.reader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class serves as reading json files in different ways.
 */
public class JsonReader {

    public JsonReader() {
    }

    public static void main(String[] args) throws IOException, ParseException {

        JsonReader jreader = new JsonReader();
        jreader.extractSnippetsWithRelevance("./src/main/resources/eval/20130403-place_of_birth.json");

    }

    /**
     * Processes the filePath and returns the text from the file and its relevance (boolean of judgments for relation presence).
     *
     * @param pathJsonFile
     * @return
     */
    public Map<String, Boolean> extractSnippetsWithRelevance(String pathJsonFile) throws IOException {

        File file = new File(pathJsonFile);
        BufferedReader br = new BufferedReader(new FileReader(file));
        Map<String, Boolean> textRelevanceMap = new HashMap<>();

        String jLine;
        while ((jLine = br.readLine()) != null) {
            JSONParser parser = new JSONParser();
            try {
                Object obj = parser.parse(jLine);

                JSONObject mainJsonObject = (JSONObject) obj;
                JSONArray evidences = (JSONArray) mainJsonObject.get("evidences");
                JSONObject urlSnippet = (JSONObject) evidences.get(0);
                String snippetText = (String) urlSnippet.get("snippet");
                boolean relationPresent = relationPresentCountJudgments(mainJsonObject);
                //System.out.println("snippetText: " + snippetText + ", relation present: " + relationPresent);

                textRelevanceMap.put(snippetText, relationPresent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return textRelevanceMap;
    }

    /**
     * true, if the 'yes' judgments dominate. Otherwise false.
     *
     * @param mainJsonObject - jsonObject containing the 'judgments'
     * @return
     */
    private boolean relationPresentCountJudgments(JSONObject mainJsonObject) {
        int countYes = 0;
        int countNo = 0;
        JSONArray judgments = (JSONArray) mainJsonObject.get("judgments");
        Iterator<JSONObject> judgmentsIter = judgments.iterator();
        while (judgmentsIter.hasNext()) {
            String judgment = (String) judgmentsIter.next().get("judgment");
            if (judgment.equals("yes")){
                countYes++;
            }
            else if (judgment.equals("no")){
                countNo++;
            }
        }
        if (countYes > countNo) {
            //System.out.println("Snippet text contains a relation. Returning true;");
            return true;
        }
        //System.out.println("Snippet text does not contain a relation. Returning false;");
        return false;
    }
}