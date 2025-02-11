package tests;

import org.junit.jupiter.api.Test;

import static utils.ConfigUtils.getDotenv;

public class CheckEnvTests {

    @Test
    void checkEnv() {
        System.out.println(getDotenv().get("API_HOST"));
    }
}