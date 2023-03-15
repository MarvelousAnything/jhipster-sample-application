package com.marvelousanything.jhipster.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.marvelousanything.jhipster.domain.Ingredient;
import com.marvelousanything.jhipster.repository.IngredientRepository;
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
 * Spring Data Elasticsearch repository for the {@link Ingredient} entity.
 */
public interface IngredientSearchRepository extends ElasticsearchRepository<Ingredient, Long>, IngredientSearchRepositoryInternal {}

interface IngredientSearchRepositoryInternal {
    Stream<Ingredient> search(String query);

    Stream<Ingredient> search(Query query);

    void index(Ingredient entity);
}

class IngredientSearchRepositoryInternalImpl implements IngredientSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final IngredientRepository repository;

    IngredientSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, IngredientRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Ingredient> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<Ingredient> search(Query query) {
        return elasticsearchTemplate.search(query, Ingredient.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Ingredient entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
