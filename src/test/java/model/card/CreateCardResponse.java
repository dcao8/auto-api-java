package model.card;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCardResponse {
    private String cardNumber;
    private String cardHolder;
    private String expiredDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateCardResponse that = (CreateCardResponse) o;
        return Objects.equals(cardNumber, that.cardNumber) &&
                Objects.equals(cardHolder, that.cardHolder) &&
                Objects.equals(expiredDate, that.expiredDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardNumber, cardHolder, expiredDate);
    }
}
