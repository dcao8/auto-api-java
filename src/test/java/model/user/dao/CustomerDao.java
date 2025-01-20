package model.user.dao;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "customers")
@Data
public class CustomerDao {
    @Id
    private UUID id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String birthday;
    private String email;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @OneToMany(mappedBy = "customerId", cascade = CascadeType.ALL)
    private List<CustomerAddressDao> addresses;
}