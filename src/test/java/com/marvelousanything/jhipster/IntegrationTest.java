package com.marvelousanything.jhipster;

import com.marvelousanything.jhipster.JhipsterSampleApplicationApp;
import com.marvelousanything.jhipster.config.AsyncSyncConfiguration;
import com.marvelousanything.jhipster.config.EmbeddedElasticsearch;
import com.marvelousanything.jhipster.config.EmbeddedRedis;
import com.marvelousanything.jhipster.config.EmbeddedSQL;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { JhipsterSampleApplicationApp.class, AsyncSyncConfiguration.class })
@EmbeddedRedis
@EmbeddedElasticsearch
@EmbeddedSQL
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public @interface IntegrationTest {
}
