package net.chekotovsky.LibraryApp.DTO;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.chekotovsky.LibraryApp.models.Author;
import net.chekotovsky.LibraryApp.models.Genre;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class BookDto {
    private long id;
    private String name;
    private int yearOfIssue;
    private Author author;
    private Genre genre;
    private String description;
    private int numOfPages;
    private int amountInStock;
}
