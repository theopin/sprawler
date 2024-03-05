package com.sprawler.hibernate.book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("bookService")
public class BookService {

    @Autowired
    @Qualifier("bookRepository")
    private BookRepository bookRepositoryObject;

    public List<Book> list() {
        return bookRepositoryObject.findAll();
    }
}