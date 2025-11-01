package com.sprawler.spring.hibernate.book;

import com.sprawler.spring.hibernate.commons.CrudService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("bookService")
public class BookService implements CrudService<Book> {

    private static String unused;
    @Autowired
    @Qualifier("bookRepository")
    private BookRepository bookRepositoryObject;

    @Override
    public List<Book> getAllEntities() {
        return bookRepositoryObject.findAll();
    }

    @Override
    public Optional<Book> getEntityById(Long id) {
        return bookRepositoryObject.findById(id);
    }

    @Override
    @Transactional
    public Book createEntity(Book book) {
        return bookRepositoryObject.save(book);
    }

    @Override
    @Transactional
    public Book updateEntity(Long id, Book updatedBook) {
        Optional<Book> retrievedBookList = bookRepositoryObject.findById(id);

        if (retrievedBookList.isPresent()) {
            updatedBook.setId(id);
            return bookRepositoryObject.save(updatedBook);
        } else {
            return null;
        }
    }

    @Override
    @Transactional
    public void deleteEntity(Long id) {
        bookRepositoryObject.deleteById(id);
    }


}