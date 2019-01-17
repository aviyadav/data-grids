package com.sample.cassandra.java.client;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.utils.UUIDs;
import com.sample.cassandra.java.client.domain.Book;
import com.sample.cassandra.java.client.repository.BookRepository;
import com.sample.cassandra.java.client.repository.KeyspaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CassandraClient {

    private static final Logger LOG = LoggerFactory.getLogger(CassandraClient.class);

    public static void main(String[] args) {
        CassandraConnector connector = new CassandraConnector();
        connector.connect("localhost", null);

        Session session = connector.getSession();

        KeyspaceRepository ksr = new KeyspaceRepository(session);
        ksr.createKeyspace("library", "SimpleStrategy", 5);
        ksr.useKeyspace("library");

        BookRepository br = new BookRepository(session);
        br.createTable();
        br.alterTablebooks("publisher", "text");

        br.createTableBooksByTitle();

        Book book = new Book(UUIDs.timeBased(), "Effective Java", "Joshua Bloch", "Programming");
        
//        LOG.info("Begin multiple insert....");
//        for (int i = 0; i < 25000; i++) {
//            book.setId(UUIDs.random());
//            br.insertbook(book);
//        }
//        LOG.info("End multiple insert....");
        
        LOG.info("Begin batch insert....");
        br.insertBookBatch(book);
        LOG.info("Finished batch insert....");

        br.selectAll().forEach(o -> LOG.info("Title in books: " + o.getTitle()));
        br.selectAllBookByTitle().forEach(o -> LOG.info("Title in booksByTitle: " + o.getTitle()));

        br.deletebookByTitle("Effective Java");
        br.deleteTable("books");
        br.deleteTable("booksByTitle");

        ksr.deleteKeyspace("library");

        connector.close();
    }
}
