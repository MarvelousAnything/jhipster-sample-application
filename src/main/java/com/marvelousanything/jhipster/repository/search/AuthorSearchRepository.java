package com.marvelousanything.jhipster.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.marvelousanything.jhipster.domain.Author;
import com.marvelousanything.jhipster.repository.AuthorRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data Elasticsearch repository for the {@link Author} entity.
 */
public interface AuthorSearchRepository extends ElasticsearchRepository<Author, Long>, AuthorSearchRepositoryInternal {}

interface AuthorSearchRepositoryInternal {
    Stream<Author> search(String query);

    Stream<Author> search(Query query);

    void index(Author entity);
}

class AuthorSearchRepositoryInternalImpl implements AuthorSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final AuthorRepository repository;

    AuthorSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, AuthorRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Author> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<Author> search(Query query) {
        return elasticsearchTemplate.search(query, Author.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Author entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
