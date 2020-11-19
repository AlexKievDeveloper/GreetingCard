package com.greetingcard.dto;

import lombok.Data;

@Data
public class UsersIdToDeleteFromCard {
    private int id;
}

/*    public static void main(String[] args) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        String json = "[{\"id\": 1}, {\"id\":2}]";
        UsersIdToDeleteFromCongratulation[] usersId = objectMapper.readValue(json, UsersIdToDeleteFromCongratulation[].class);

        for (UsersIdToDeleteFromCongratulation usersIdToDeleteFromCongratulation : usersId) {
            System.out.println(usersIdToDeleteFromCongratulation);
        }
        //https://stackoverflow.com/questions/6349421/how-to-use-jackson-to-deserialise-an-array-of-objects
    }*/
