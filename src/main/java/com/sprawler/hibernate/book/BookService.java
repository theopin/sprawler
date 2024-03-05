package com.sprawler.hibernate.book;

import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("bookService")
public class BookService {

    @Autowired
    @Qualifier("bookRepository")
    private BookRepository bookRepositoryObject;

    @Autowired
    @Qualifier("sessionFactory")
    private SessionFactory sessionFactory;

    public List<Book> list() {
        return bookRepositoryObject.findAll();
    }

    @Transactional
    public Object createNewEntity(Object entity) {
        Session session = sessionFactory.getCurrentSession();
        return session.save(entity);
    }
}