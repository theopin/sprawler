package com.sprawler.hibernate;

import com.sprawler.hibernate.book.Book;
import com.sprawler.hibernate.book.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hibernate")
public class HibernateController {

    @Autowired
    @Qualifier("bookService")
    private BookService bookService;

    @GetMapping("/books")
    public List<Book> getAllBookList() {
        return bookService.list();
    }
}
