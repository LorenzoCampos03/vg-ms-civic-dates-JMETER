package pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.sigei.civicDates.domain.model.Event;
import pe.edu.vallegrande.sigei.civicDates.domain.port.out.EventRepository;
import pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.mapper.EventPersistenceMapper;
import pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.repository.R2dbcEventRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class EventPersistenceAdapter implements EventRepository {

    private final R2dbcEventRepository r2dbcEventRepository;
    private final EventPersistenceMapper mapper;

    @Override
    public Mono<Event> save(Event event) {
        return r2dbcEventRepository.save(mapper.toEntity(event))
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Event> findById(Long id) {
        return r2dbcEventRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Event> findAll() {
        return r2dbcEventRepository.findAll()
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Event> findByInstitutionId(String institutionId) {
        return r2dbcEventRepository.findByInstitutionId(institutionId)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Event> findByStatus(String status) {
        return r2dbcEventRepository.findByStatus(status)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return r2dbcEventRepository.deleteById(id);
    }

    @Override
    public Mono<Event> update(Event event) {
        return r2dbcEventRepository.save(mapper.toEntity(event))
                .map(mapper::toDomain);
    }
}
