package com.msa1.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.msa1.dto.RecipeCreateDTO;
import com.msa1.dto.RecipeDTO;
import com.msa1.dto.RecipeUpdateDTO;
import com.msa1.model.Recipe;

@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class RecipeMapper {

    @Mapping(target = "author", source = "authorId")
    public abstract Recipe map(RecipeCreateDTO dto);

    @Mapping(source = "author.id", target = "authorId")
    public abstract RecipeDTO map(Recipe model);

    @Mapping(target = "author", source = "authorId")
    public abstract void update(RecipeUpdateDTO dto, @MappingTarget Recipe model);
}
