package com.sprawler.hibernate.book;

import com.sprawler.hibernate.book.Book;
import com.sprawler.hibernate.book.BookService;
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

    @GetMapping
    public List<Book> getAllBookList() {
        return bookService.list();
    }
}