package com.marvelousanything.jhipster.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.marvelousanything.jhipster.domain.Instruction;
import com.marvelousanything.jhipster.repository.InstructionRepository;
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
 * Spring Data Elasticsearch repository for the {@link Instruction} entity.
 */
public interface InstructionSearchRepository extends ElasticsearchRepository<Instruction, Long>, InstructionSearchRepositoryInternal {}

interface InstructionSearchRepositoryInternal {
    Stream<Instruction> search(String query);

    Stream<Instruction> search(Query query);

    void index(Instruction entity);
}

class InstructionSearchRepositoryInternalImpl implements InstructionSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final InstructionRepository repository;

    InstructionSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, InstructionRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Instruction> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<Instruction> search(Query query) {
        return elasticsearchTemplate.search(query, Instruction.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Instruction entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
