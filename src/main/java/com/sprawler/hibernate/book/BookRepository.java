package com.sprawler.hibernate.book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("bookRepository")
public interface BookRepository extends JpaRepository<Book, Long> {
}
