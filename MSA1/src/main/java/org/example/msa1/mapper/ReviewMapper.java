package org.example.msa1.mapper;

import org.example.msa1.dto.ReviewCreateDTO;
import org.example.msa1.dto.ReviewDTO;
import org.example.msa1.dto.ReviewUpdateDTO;
import org.example.msa1.model.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = ReferenceMapper.class)
public interface ReviewMapper {

    Review toEntity(ReviewCreateDTO dto);

    @Mapping(target = "filmId", expression = "java(entity.getFilm() != null ? entity.getFilm().getId() : null)")
    @Mapping(target = "authorId", expression = "java(entity.getAuthor() != null ? entity.getAuthor().getId() : null)")
    ReviewDTO toDto(Review entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "film", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(ReviewUpdateDTO dto, @MappingTarget Review entity);
}