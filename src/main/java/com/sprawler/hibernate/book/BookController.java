package com.sprawler.hibernate.book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController("bookController")
@RequestMapping("/books")
public class BookController {
    @Autowired
    @Qualifier("bookService")
    private BookService bookService;


    // Get all books
    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.getAllEntities();
    }

    // Get a book by ID
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Optional<Book> book = bookService.getEntityById(id);
        return book.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create a new book
    @PostMapping
    public ResponseEntity<Book> createNewBook(@RequestBody Book book) {
        Book createdBook = bookService.createEntity(book);
        return ResponseEntity.ok(createdBook);
    }

    // Update an existing book
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateExistingBook(@PathVariable Long id,
                                                   @RequestBody Book bookDetails) {
        try {
            Book updatedBook = bookService.updateEntity(id, bookDetails);
            return ResponseEntity.ok(updatedBook);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a book by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExistingBook (@PathVariable Long id) {
        bookService.deleteEntity(id);
        return ResponseEntity.noContent().build();
    }
}