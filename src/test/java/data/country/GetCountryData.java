package data.country;

public class GetCountryData {
    public static final String GET_ALL_COUNTRIES = """
            [
                {
                    "name": "Viet Nam",
                    "code": "VN"
                },
                {
                    "name": "USA",
                    "code": "US"
                },
                {
                    "name": "Canada",
                    "code": "CA"
                },
                {
                    "name": "UK",
                    "code": "GB"
                },
                {
                    "name": "France",
                    "code": "FR"
                },
                {
                    "name": "Japan",
                    "code": "JP"
                },
                {
                    "name": "Brazil",
                    "code": "BR"
                },
                {
                    "name": "India",
                    "code": "IN"
                },
                {
                    "name": "China",
                    "code": "CN"
                }
            ]
            """;

    public static final String GET_ALL_COUNTRIES_WITH_GDP = """
            [
                {
                     "name": "Viet Nam",
                     "code": "VN",
                     "gdp": 223.9
                 },
                 {
                     "name": "Canada",
                     "code": "CA",
                     "gdp": 1930
                 },
                 {
                     "name": "UK",
                     "code": "GB",
                     "gdp": 2827
                 },
                 {
                     "name": "USA",
                     "code": "US",
                     "gdp": 21427.5
                 },
                 {
                     "name": "France",
                     "code": "FR",
                     "gdp": 2718
                 },
                 {
                     "name": "Japan",
                     "code": "JP",
                     "gdp": 5081
                 },
                 {
                     "name": "India",
                     "code": "IN",
                     "gdp": 2875
                 },
                 {
                     "name": "China",
                     "code": "CN",
                     "gdp": 14342.9
                 },
                 {
                     "name": "Brazil",
                     "code": "BR",
                     "gdp": 1868
                 }
            ]
            """;

    public static final String GET_ALL_COUNTRIES_WITH_PRIVATE = """
            [
                {
                         "name": "Viet Nam",
                         "code": "VN",
                         "gdp": 223.9,
                         "private": 60
                     },
                     {
                         "name": "USA",
                         "code": "US",
                         "gdp": 21427.5,
                         "private": 80
                     },
                     {
                         "name": "Canada",
                         "code": "CA",
                         "gdp": 1930,
                         "private": 70
                     },
                     {
                         "name": "UK",
                         "code": "GB",
                         "gdp": 2827,
                         "private": 75
                     },
                     {
                         "name": "France",
                         "code": "FR",
                         "gdp": 2718,
                         "private": 65
                     },
                     {
                         "name": "Japan",
                         "code": "JP",
                         "gdp": 5081,
                         "private": 85
                     },
                     {
                         "name": "India",
                         "code": "IN",
                         "gdp": 2875,
                         "private": 55
                     },
                     {
                         "name": "China",
                         "code": "CN",
                         "gdp": 14342.9,
                         "private": 90
                     },
                     {
                         "name": "Brazil",
                         "code": "BR",
                         "gdp": 1868,
                         "private": 50
                     }
            ]
            """;
}
