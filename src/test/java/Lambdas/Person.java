package Lambdas;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
public class Person {

    String firstName;
    String lastName;
    int age;
}
