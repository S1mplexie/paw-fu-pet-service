package com.example.petadoption.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.petadoption.entity.PetAdoption;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 宠物领养信息Mapper接口
 */
@Mapper
public interface PetMapper extends BaseMapper<PetAdoption> {

    @Update("UPDATE pet_adoption SET view_count = view_count + 1 WHERE pet_id = #{petId}")
    int incrementViewCount(@Param("petId") String petId);
}
