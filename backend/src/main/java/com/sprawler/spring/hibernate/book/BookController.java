package com.sprawler.spring.hibernate.book;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController("bookController")
@RequestMapping("/books")
public class BookController {

    private static final Logger LOGGER = LogManager.getLogger(BookController.class);

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
        LOGGER.info("Getting book of id: " + id);
        Optional<Book> book = bookService.getEntityById(id);
        return book.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create a new book
    @PostMapping
    public ResponseEntity<Book> createNewBook(@RequestBody Book book) {
        LOGGER.info("Creating a new book");
        Book createdBook = bookService.createEntity(book);
        return ResponseEntity.ok(createdBook);
    }

    // Update an existing book
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateExistingBook(@PathVariable Long id,
                                                   @RequestBody Book bookDetails) {
        try {
            LOGGER.info("Updating book of id: " + id);
            Book updatedBook = bookService.updateEntity(id, bookDetails);
            return ResponseEntity.ok(updatedBook);
        } catch (Exception e) {
            LOGGER.error("Failed to update book due to: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a book by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExistingBook (@PathVariable Long id) {
        LOGGER.info("Deleting book of id: " + id);
        bookService.deleteEntity(id);
        return ResponseEntity.noContent().build();
    }
}