package com.auctions.persistence.repository;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES;

@DataJpaTest
@AutoConfigureEmbeddedDatabase(type = POSTGRES)
public abstract class AbstractRepositoryIT {

    @BeforeAll
    @FlywayTest(locationsForMigrate = "filesystem:db/migration")
    public static void beforeAll() {}
}
