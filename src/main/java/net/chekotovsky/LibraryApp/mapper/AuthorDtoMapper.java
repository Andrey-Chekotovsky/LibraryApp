package net.chekotovsky.LibraryApp.mapper;

import net.chekotovsky.LibraryApp.DTO.AuthorDto;
import net.chekotovsky.LibraryApp.models.Author;
import org.springframework.stereotype.Component;

@Component
public class AuthorDtoMapper {
    public AuthorDto map(Author author)
    {
        return AuthorDto.builder()
                .id(author.getId())
                .description(author.getDescription())
                .fullName(author.getFullName())
                .build();
    }
    public Author map(AuthorDto authorDto)
    {
        return Author.builder()
                .id(authorDto.getId())
                .description(authorDto.getDescription())
                .fullName(authorDto.getFullName())
                .build();
    }
}
