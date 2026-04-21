package com.skaly.fashion_backend.cart;

import com.skaly.fashion_backend.testsupport.PostgresIntegrationSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(properties = {
        "spring.datasource.hikari.maximum-pool-size=10",
        "spring.flyway.enabled=true"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class FlywayRollbackTest extends PostgresIntegrationSupport {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void simulateMigrationFailureAndValidateRollback() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS test_rollback_table;");
        String testEmail = java.util.UUID.randomUUID().toString() + "@example.com";
        // 1. Manually insert some data into a table created in V1
        jdbcTemplate.execute(
                "INSERT INTO users (id, email, role, created_at) VALUES (gen_random_uuid(), '" + testEmail
                        + "', 'USER', now())");

        // 2. We simulate a failed migration query containing multiple statements (DDL +
        // DML)
        // PostgreSQL supports transactional DDL. If this whole block runs in one
        // transaction,
        // the failure at the end will roll back the table creation.
        String failingMigrationScript = """
                    BEGIN;
                    CREATE TABLE test_rollback_table (id uuid PRIMARY KEY, name VARCHAR(50));
                    INSERT INTO test_rollback_table (id, name) VALUES (gen_random_uuid(), 'Valid data');
                    -- This simulates a constraint violation in the same transaction
                    INSERT INTO users (id, email, role, created_at) VALUES (gen_random_uuid(), '%s', 'USER', now());
                    COMMIT;
                """.formatted(testEmail);

        Throwable thrown = catchThrowable(() -> {
            jdbcTemplate.execute(failingMigrationScript);
        });

        // Clear the aborted transaction context so we can query again
        try {
            jdbcTemplate.execute("ROLLBACK;");
        } catch (Exception ignored) {
        }

        // 3. Validate that an exception was actually thrown (Duplicate key on email)
        assertThat(thrown).isNotNull();

        // 4. VALIDATE ROLLBACK SAFETY: The new table SHOULD NOT exist!
        Integer tableCount = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM information_schema.tables WHERE table_name = 'test_rollback_table'",
                Integer.class);

        assertThat(tableCount).isEqualTo(0); // Proves Atomicity (A in ACID) for schema migrations in Postgres!
    }
}
