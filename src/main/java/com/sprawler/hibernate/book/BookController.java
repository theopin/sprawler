package com.sprawler.hibernate.book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("bookController")
@RequestMapping("/books")
public class BookController {
    @Autowired
    @Qualifier("bookService")
    private BookService bookService;

    @GetMapping("/new")
    public Object createNewBook() {

        Book newBook = new Book();
        newBook.setName("GHEE");

        return bookService.createNewEntity(newBook);

    }

    @GetMapping
    public List<Book> getAllBookList() {
        return bookService.list();
    }
}