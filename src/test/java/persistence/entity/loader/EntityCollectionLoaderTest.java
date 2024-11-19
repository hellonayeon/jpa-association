package persistence.entity.loader;

import database.DatabaseServer;
import database.H2;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EntityCollectionLoaderTest {

    private DatabaseServer server;
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void beforeEach() throws SQLException {
        server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());

        createParentsTable();
        createChildrenTable();
    }

    @AfterEach
    void afterEach() {
        server.stop();

        dropTable("parents");
        dropTable("children");
    }

    private void dropTable(String tableName) {
        jdbcTemplate.execute(MessageFormat.format("drop table if exists {0}", tableName));
    }

    @Test
    @DisplayName("[성공] OneToMany 연관 관계가 있는 Entity 를 조회한다.")
    void loadCollection() {
        Parent parent = new Parent("parent");
        parent.addChild(new Child("childA"));
        parent.addChild(new Child("childB"));

        EntityCollectionLoader collectionLoader = new EntityCollectionLoader(jdbcTemplate);
        collectionLoader.loadCollection(Parent.class, parent);
    }

    private void createParentsTable() {
        jdbcTemplate.execute("""
                create table parents (
                    id bigint auto_increment primary key,
                    name varchar(255) not null
                );
                """
        );
    }

    private void createChildrenTable() {
        jdbcTemplate.execute("""
                create table children (
                    id bigint auto_increment primary key,
                    parent_id bigint,
                    name varchar(255) not null,
                );
                """
        );
    }

    @Entity
    @Table(name = "parents")
    static class Parent {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;

        @OneToMany(fetch = FetchType.EAGER)
        @JoinColumn(name = "parent_id")
        private List<Child> children;

        public Parent(String name) {
            this(name, Collections.emptyList());
        }

        private Parent(String name, List<Child> children) {
            this.name = name;
            this.children = children;
        }

        public void addChild(Child child) {
            children.add(child);
        }

    }


    @Entity
    @Table(name = "children")
    static class Child {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;

        public Child(String name) {
            this.name = name;
        }

    }

}
