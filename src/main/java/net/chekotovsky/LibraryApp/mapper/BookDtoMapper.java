package net.chekotovsky.LibraryApp.mapper;

import net.chekotovsky.LibraryApp.DTO.BookDto;
import net.chekotovsky.LibraryApp.models.Book;
import org.springframework.stereotype.Component;

@Component
public class BookDtoMapper {
    public BookDto map(Book book)
    {
        return BookDto.builder()
                .genre(book.getGenre())
                .id(book.getId())
                .name(book.getName())
                .amountInStock(book.getAmountInStock())
                .author(book.getAuthor())
                .description(book.getDescription())
                .numOfPages(book.getNumOfPages())
                .yearOfIssue(book.getYearOfIssue())
                .build();
    }
    public Book map(BookDto bookDto)
    {
        return Book.builder()
                .genre(bookDto.getGenre())
                .id(bookDto.getId())
                .name(bookDto.getName())
                .amountInStock(bookDto.getAmountInStock())
                .author(bookDto.getAuthor())
                .description(bookDto.getDescription())
                .numOfPages(bookDto.getNumOfPages())
                .yearOfIssue(bookDto.getYearOfIssue())
                .build();
    }
}
