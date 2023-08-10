package net.chekotovsky.LibraryApp.models;

import lombok.*;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Author {
    @Id
    @EqualsAndHashCode.Exclude()
    long id;
    private String fullName;
    private String description;

}
