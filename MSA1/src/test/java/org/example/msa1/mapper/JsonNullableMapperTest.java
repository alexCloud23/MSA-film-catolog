package org.example.msa1.mapper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonNullableMapperTest {

    @Test
    void mapperImpl_shouldBeInstantiable() {
        JsonNullableMapper mapper = new JsonNullableMapperImpl();

        assertNotNull(mapper);
        assertInstanceOf(JsonNullableMapper.class, mapper);
    }
}
