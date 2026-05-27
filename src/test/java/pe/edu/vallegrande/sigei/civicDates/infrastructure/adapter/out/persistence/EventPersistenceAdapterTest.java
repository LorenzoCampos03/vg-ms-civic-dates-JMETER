package pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.vallegrande.sigei.civicDates.domain.model.Event;
import pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.entity.EventEntity;
import pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.mapper.EventPersistenceMapper;
import pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.repository.R2dbcEventRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventPersistenceAdapterTest {

    @Mock
    private R2dbcEventRepository r2dbcEventRepository;

    @Mock
    private EventPersistenceMapper mapper;

    @InjectMocks
    private EventPersistenceAdapter adapter;

    @Test
    void save_shouldMapAndPersist() {
        Event event = Event.builder().id(1L).title("a").build();
        EventEntity entity = EventEntity.builder().id(1L).title("a").build();

        when(mapper.toEntity(event)).thenReturn(entity);
        when(r2dbcEventRepository.save(entity)).thenReturn(Mono.just(entity));
        when(mapper.toDomain(entity)).thenReturn(event);

        StepVerifier.create(adapter.save(event))
                .expectNext(event)
                .verifyComplete();
    }

    @Test
    void findMethods_shouldDelegateToRepository() {
        Event event = Event.builder().id(2L).title("b").build();
        EventEntity entity = EventEntity.builder().id(2L).title("b").build();

        when(r2dbcEventRepository.findById(2L)).thenReturn(Mono.just(entity));
        when(r2dbcEventRepository.findAll()).thenReturn(Flux.just(entity));
        when(r2dbcEventRepository.findByInstitutionId("INST")).thenReturn(Flux.just(entity));
        when(r2dbcEventRepository.findByStatus("ACTIVE")).thenReturn(Flux.just(entity));
        when(mapper.toDomain(any(EventEntity.class))).thenReturn(event);

        StepVerifier.create(adapter.findById(2L)).expectNext(event).verifyComplete();
        StepVerifier.create(adapter.findAll()).expectNext(event).verifyComplete();
        StepVerifier.create(adapter.findByInstitutionId("INST")).expectNext(event).verifyComplete();
        StepVerifier.create(adapter.findByStatus("ACTIVE")).expectNext(event).verifyComplete();
    }

    @Test
    void updateAndDelete_shouldDelegate() {
        Event event = Event.builder().id(5L).title("c").build();
        EventEntity entity = EventEntity.builder().id(5L).title("c").build();

        when(mapper.toEntity(event)).thenReturn(entity);
        when(r2dbcEventRepository.save(entity)).thenReturn(Mono.just(entity));
        when(mapper.toDomain(entity)).thenReturn(event);
        when(r2dbcEventRepository.deleteById(5L)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.update(event)).expectNext(event).verifyComplete();
        StepVerifier.create(adapter.deleteById(5L)).verifyComplete();

        verify(r2dbcEventRepository).deleteById(eq(5L));
    }
}
