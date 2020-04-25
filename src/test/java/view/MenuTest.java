package view;

import org.junit.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;

public class MenuTest {

    @Test
    public void getJsonFromDBTest() throws FileNotFoundException {
        String mainMenuFile = "{\n" +
                "  \"options\" : [\n" +
                "    \"user menu\",\n" +
                "    \"product menu\",\n" +
                "    \"off menu\"\n" +
                "  ],\n" +
                "  \"name\" : \"Main Menu\",\n" +
                "  \"parentMenu\" : \"null\",\n" +
                "  \"isThereParentMenu\" : \"false\"\n" +
                "}";

        String userMenuFile = "{\n" +
                "  \"options\" : [\n" +
                "    \"Register\",\n" +
                "    \"Login\"\n" +
                "  ],\n" +
                "  \"name\" : \"User Menu\",\n" +
                "  \"parentMenu\" : \"Main Menu\",\n" +
                "  \"isThereParentMenu\" : \"true\"\n" +
                "}";

        try {
            Assert.assertEquals(mainMenuFile, Menu.getJsonFromDB("Main Menu"));
            Assert.assertEquals(userMenuFile, Menu.getJsonFromDB("User Menu"));
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }
}
