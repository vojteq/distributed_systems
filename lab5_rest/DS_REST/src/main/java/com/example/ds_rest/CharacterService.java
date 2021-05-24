package com.example.ds_rest;

import com.example.ds_rest.model.CharacterInfo;
import com.example.ds_rest.model.CharacterListObject;
import com.example.ds_rest.model.CharacterResponse;
import com.example.ds_rest.model.StatsInfo;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;

@Service
public class CharacterService {

    private final DecimalFormat decimalFormat = new DecimalFormat("0.00");


    public CharacterInfo getOne(String name) {
        String[] nameArr = name.split(" ");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("https://swapi.dev/api/people/?search=")
                .append(nameArr[0]);
        for (int i = 1; i < nameArr.length; i++) {
            stringBuilder.append("+")
                    .append(nameArr[i]);
        }
        String url = stringBuilder.toString();
        RestTemplate restTemplate = new RestTemplate();
        CharacterResponse response = restTemplate.getForObject(url, CharacterResponse.class);
        LinkedHashMap<String, Object> character = response.getResults().get(0);
        CharacterInfo characterInfo = new CharacterInfo(
                (String) character.get("name"),
                Float.parseFloat((String) character.get("height")),
                Float.parseFloat((String) character.get("mass"))
        );
        return characterInfo;
    }

    private CharacterInfo jsonToCharacterInfo(JSONObject jsonObject) {
        if (((String) jsonObject.get("mass")).equals("unknown") || ((String) jsonObject.get("height")).equals("unknown")) {
            return null;
        }
        return new CharacterInfo(
                (String) jsonObject.get("name"),
                Float.parseFloat(((String) jsonObject.get("height")).replace(",", "")),
                Float.parseFloat(((String) jsonObject.get("mass")).replace(",", ""))
        );
    }


    public StatsInfo getStats() {
        int counter = 0;
        float sumHeight = 0.0f;
        float sumWeight = 0.0f;
        float sumBmi = 0.0f;
        CharacterListObject characterListObject;
        CharacterInfo characterInfo;
        RestTemplate restTemplate = new RestTemplate();
        StatsInfo statsInfo = new StatsInfo();
        String url = "https://swapi.dev/api/people/";

        while (url != null) {
            characterListObject = restTemplate.getForObject(url, CharacterListObject.class);
            if (characterListObject != null) {
                if (characterListObject.getNext() != null && characterListObject.getNext().charAt(4) != 's') {
                    characterListObject.setNext(characterListObject.getNext().replace("http", "https"));
                }
                for (JSONObject character : characterListObject.getResults()) {
                    characterInfo = jsonToCharacterInfo(character);
                    if (characterInfo != null) {
                        counter++;
                        sumHeight += characterInfo.getHeight();
                        sumWeight += characterInfo.getMass();
                        sumBmi += characterInfo.getBmi();

                        if (statsInfo.getHeaviest() == null || statsInfo.getHeaviest().getMass() < characterInfo.getMass()) {
                            statsInfo.setHeaviest(characterInfo);
                        }
                        if (statsInfo.getLightest() == null || statsInfo.getLightest().getMass() > characterInfo.getMass()) {
                            statsInfo.setLightest(characterInfo);
                        }
                        if (statsInfo.getTallest() == null || statsInfo.getTallest().getHeight() < characterInfo.getHeight()) {
                            statsInfo.setTallest(characterInfo);
                        }
                        if (statsInfo.getLowest() == null || statsInfo.getLowest().getHeight() > characterInfo.getHeight()) {
                            statsInfo.setLowest(characterInfo);
                        }
                    }
                }
                url = characterListObject.getNext();
            }
        }
        String tmp = decimalFormat.format(sumHeight / counter).replace(",", ".");
        statsInfo.setAvgHeight(Float.parseFloat(tmp));
        tmp = decimalFormat.format(sumWeight / counter).replace(",", ".");
        statsInfo.setAvgWeight(Float.parseFloat(tmp));
        tmp = decimalFormat.format(sumBmi / counter).replace(",", ".");
        statsInfo.setAvgBmi(Float.parseFloat(tmp));
        return statsInfo;
    }
}
