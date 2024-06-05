package com.alura.literatura.principal;

import com.alura.literatura.model.Author;
import com.alura.literatura.model.Book;
import com.alura.literatura.repository.BookRepository;
import com.alura.literatura.service.GutendexClient;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class Principal {

    @Autowired
    private GutendexClient client;

    @Autowired
    private BookRepository bookRepository;

    public void showMenu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Seleccione una opción:");
            System.out.println("1. Buscar libros por título");
            System.out.println("2. Listar todos los libros");
            System.out.println("3. Listar libros por idioma");
            System.out.println("4. Listar todos los autores");
            System.out.println("5. Listar autores vivos en un determinado año");
            System.out.println("9. limpiar libreria");
            System.out.println("0. Salir");

            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1:
                    searchBooks(scanner);
                    break;
                case 2:
                    listAllBooks();
                    break;
                case 3:
                    listBooksByLanguage();
                    break;
                case 4:
                    listAuthors();
                    break;
                case 5:
                    listLivingAuthorsByYear(scanner);
                    break;
                case 9:
                    clearLibrary();
                    break;
                case 0:
                    System.out.println("Saliendo...");
                    return;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
    }

    private void searchBooks(Scanner scanner) {
        System.out.print("Ingrese el título del libro: ");
        String title = scanner.nextLine();
        try {
            Optional<Book> existingBook = bookRepository.findByTitle(title);
            if (existingBook.isPresent()) {
                System.out.println("------------------------------");
                System.out.println("El libro ya está guardado en la base de datos.");
                System.out.println("------------------------------");
            } else {
                Book book = client.searchBookByTitle(title);
                if (book != null) {
                    bookRepository.save(book);
                    System.out.println("------------------------------");
                    System.out.println("Libro guardado exitosamente.");
                    System.out.println("------------------------------");
                } else {
                    System.out.println("------------------------------");
                    System.out.println("No se encontró ningún libro con el título proporcionado.");
                    System.out.println("------------------------------");
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Transactional
    private void listAllBooks() {
        List<Book> books = bookRepository.findAll();
        for (Book book : books) {
            System.out.println("------------------------------");
            System.out.println("Título: " + book.getTitle());

            if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
                for (Author author : book.getAuthors()) {
                    System.out.println("Autor: " + author.getName());
                    System.out.println("Año de Nacimiento: " + author.getBirthYear());
                    System.out.println("Año de Fallecimiento: " + author.getDeathYear());
                }
            } else {
                System.out.println("Autor: Desconocido");
            }

            System.out.println("Idioma: " + book.getLanguage());
            System.out.println("Número de Descargas: " + book.getDownloadCount());
            System.out.println("------------------------------");
        }
    }

    private void listBooksByLanguage() {
        System.out.println("Ingrese el idioma:");
        Scanner scanner = new Scanner(System.in);
        String language = scanner.nextLine();

        List<Book> books = bookRepository.findByLanguage(language); // Asegúrate de tener este método en tu repositorio
        for (Book book : books) {
            System.out.println("------------------------------");
            System.out.println("Título: " + book.getTitle());
            System.out.println("Idioma: " + book.getLanguage());

            if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
                for (Author author : book.getAuthors()) {
                    System.out.println("Autor: " + author.getName());
                }
            } else {
                System.out.println("Autores: No se encontraron autores");
            }

            System.out.println("Número de Descargas: " + book.getDownloadCount());
            System.out.println("------------------------------");
        }
    }

    private void listAuthors() {
        List<Book> books = bookRepository.findAll();
        for (Book book : books) {
            List<Author> authors = book.getAuthors();
            if (authors != null && !authors.isEmpty()) {
                for (Author author : authors) {
                    System.out.println("------------------------------");
                    System.out.println("Nombre del Autor: " + author.getName());
                    System.out.println("Año de Nacimiento: " + author.getBirthYear());
                    System.out.println("Año de Fallecimiento: " + author.getDeathYear());
                    System.out.println("------------------------------");
                }
            } else {
                System.out.println("------------------------------");
                System.out.println("No se encontraron autores");
                System.out.println("------------------------------");
            }
        }
    }

    private void listLivingAuthorsByYear(Scanner scanner) {
        System.out.print("Ingrese el año para encontrar autores vivos: ");
        int year = scanner.nextInt();
        List<Book> books = bookRepository.findAll();
        for (Book book : books) {
            if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
                for (Author author : book.getAuthors()) {
                    if (author.getDeathYear() == null || author.getDeathYear() > year) {
                        System.out.println("------------------------------");
                        System.out.println("Nombre del Autor: " + author.getName());
                        System.out.println("Año de Nacimiento: " + author.getBirthYear());
                        System.out.println("Año de Fallecimiento: " + author.getDeathYear());
                        System.out.println("------------------------------");
                    }
                }
            }
        }
    }

    private void clearLibrary() {
        bookRepository.deleteAll();
        System.out.println("------------------------------");
        System.out.println("Todos los libros han sido eliminados de la base de datos.");
        System.out.println("------------------------------");
    }
}
