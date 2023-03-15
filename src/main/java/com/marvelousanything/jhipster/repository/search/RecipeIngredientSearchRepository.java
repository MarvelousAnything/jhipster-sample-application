package com.marvelousanything.jhipster.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.marvelousanything.jhipster.domain.RecipeIngredient;
import com.marvelousanything.jhipster.repository.RecipeIngredientRepository;
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
 * Spring Data Elasticsearch repository for the {@link RecipeIngredient} entity.
 */
public interface RecipeIngredientSearchRepository
    extends ElasticsearchRepository<RecipeIngredient, Long>, RecipeIngredientSearchRepositoryInternal {}

interface RecipeIngredientSearchRepositoryInternal {
    Stream<RecipeIngredient> search(String query);

    Stream<RecipeIngredient> search(Query query);

    void index(RecipeIngredient entity);
}

class RecipeIngredientSearchRepositoryInternalImpl implements RecipeIngredientSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final RecipeIngredientRepository repository;

    RecipeIngredientSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, RecipeIngredientRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<RecipeIngredient> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<RecipeIngredient> search(Query query) {
        return elasticsearchTemplate.search(query, RecipeIngredient.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(RecipeIngredient entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
