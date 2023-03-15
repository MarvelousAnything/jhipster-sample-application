package com.marvelousanything.jhipster.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.marvelousanything.jhipster.domain.MeasuringUnit;
import com.marvelousanything.jhipster.repository.MeasuringUnitRepository;
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
 * Spring Data Elasticsearch repository for the {@link MeasuringUnit} entity.
 */
public interface MeasuringUnitSearchRepository
    extends ElasticsearchRepository<MeasuringUnit, Long>, MeasuringUnitSearchRepositoryInternal {}

interface MeasuringUnitSearchRepositoryInternal {
    Stream<MeasuringUnit> search(String query);

    Stream<MeasuringUnit> search(Query query);

    void index(MeasuringUnit entity);
}

class MeasuringUnitSearchRepositoryInternalImpl implements MeasuringUnitSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final MeasuringUnitRepository repository;

    MeasuringUnitSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, MeasuringUnitRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<MeasuringUnit> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<MeasuringUnit> search(Query query) {
        return elasticsearchTemplate.search(query, MeasuringUnit.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(MeasuringUnit entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
