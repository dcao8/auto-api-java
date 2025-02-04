package model.user.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {
    private String firstName;
    private String lastName;
    private String middleName;
    private String birthday;
    private String email;
    private String phone;
    private List<UserAddressRequest> addresses;

    public static UserRequest getDefault() {
        return UserRequest.builder()
                .firstName("Jos")
                .lastName("Doe")
                .middleName("Smith")
                .birthday("01-23-2000")
                .email("api@auto.com")
                .phone("0123456789")
                .addresses(List.of(UserAddressRequest.getDefault()))
                .build();
    }

    public static UserRequest getUpdateUserInfo() {
        return UserRequest.builder()
                .firstName("Jose")
                .lastName("Doee")
                .middleName("Smithe")
                .birthday("01-23-2001")
                .email("api@auto.com")
                .phone("0123456788")
                .addresses(List.of(UserAddressRequest.getDefault()))
                .build();
    }
}