package data.graphql;

public class GraphQlData {
    public static final String EXPECTED_COUNTRY_QUERY_DATA = """
            {
                    "data": {
                        "br": {
                            "name": "Vietnam",
                            "native": "Việt Nam",
                            "capital": "Hanoi",
                            "emoji": "🇻🇳",
                            "currency": "VND",
                            "languages": [
                                {
                                    "code": "vi",
                                    "name": "Vietnamese"
                                }
                            ]
                        },
                        "vn": {
                            "name": "Brazil",
                            "native": "Brasil",
                            "capital": "Brasília",
                            "emoji": "🇧🇷",
                            "currency": "BRL"
                        }
                    }
                }
            """;

    public static final String GET_COUNTRIES_QUERY = """
            query GetCountry($vncode: ID!, $brcode: ID!) {
              br: country(code: $vncode) {
                name
                native
                capital
                emoji
                currency
                languages {
                  code
                  name
                }
              }
              vn: country(code: $brcode) {
                name
                native
                capital
                emoji
                currency
              }
            }
            fragment commonFields on Country {
              name
              native
              capital
              emoji
              currency
            }
            
            """;
}