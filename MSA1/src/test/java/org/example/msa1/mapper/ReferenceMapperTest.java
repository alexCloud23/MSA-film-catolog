package org.example.msa1.mapper;

import jakarta.persistence.EntityManager;
import org.example.msa1.model.Film;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReferenceMapperTest {

    @Mock
    private EntityManager entityManager;

    private ReferenceMapper referenceMapper;

    @BeforeEach
    void setUp() {
        referenceMapper = new ReferenceMapper();
        ReflectionTestUtils.setField(referenceMapper, "entityManager", entityManager);
    }

    @Test
    void toReference_shouldReturnNull_whenIdIsNull() {
        assertNull(referenceMapper.toReference(Film.class, null));

        verifyNoInteractions(entityManager);
    }

    @Test
    void toReference_shouldReturnReference_whenIdIsPresent() {
        Film reference = new Film();
        when(entityManager.getReference(Film.class, 1L)).thenReturn(reference);

        Film result = referenceMapper.toReference(Film.class, 1L);

        assertSame(reference, result);
        verify(entityManager, times(1)).getReference(Film.class, 1L);
    }
}
