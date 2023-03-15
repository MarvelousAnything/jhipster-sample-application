package com.marvelousanything.jhipster.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.marvelousanything.jhipster.domain.Recipe;
import com.marvelousanything.jhipster.repository.RecipeRepository;
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
 * Spring Data Elasticsearch repository for the {@link Recipe} entity.
 */
public interface RecipeSearchRepository extends ElasticsearchRepository<Recipe, Long>, RecipeSearchRepositoryInternal {}

interface RecipeSearchRepositoryInternal {
    Stream<Recipe> search(String query);

    Stream<Recipe> search(Query query);

    void index(Recipe entity);
}

class RecipeSearchRepositoryInternalImpl implements RecipeSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final RecipeRepository repository;

    RecipeSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, RecipeRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Recipe> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<Recipe> search(Query query) {
        return elasticsearchTemplate.search(query, Recipe.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Recipe entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
