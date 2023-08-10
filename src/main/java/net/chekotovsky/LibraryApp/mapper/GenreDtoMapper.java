package net.chekotovsky.LibraryApp.mapper;

import net.chekotovsky.LibraryApp.DTO.GenreDto;
import net.chekotovsky.LibraryApp.models.Genre;
import org.springframework.stereotype.Component;

@Component
public class GenreDtoMapper {
    public GenreDto map(Genre genre)
    {
        return GenreDto.builder()
                .id(genre.getId())
                .description(genre.getDescription())
                .name(genre.getName())
                .build();
    }
    public Genre map(GenreDto genreDto)
    {
        return Genre.builder()
                .id(genreDto.getId())
                .description(genreDto.getDescription())
                .name(genreDto.getName())
                .build();
    }
}
