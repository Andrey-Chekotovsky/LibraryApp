package net.chekotovsky.LibraryApp.models;

import lombok.*;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Book {
    @Id
    @EqualsAndHashCode.Exclude()
    private long id;
    private String name;
    private int yearOfIssue;
    @EqualsAndHashCode.Exclude()
    private Author author;
    @EqualsAndHashCode.Exclude()
    private Genre genre;
    private String description;
    private int numOfPages;
    private int amountInStock;

}
