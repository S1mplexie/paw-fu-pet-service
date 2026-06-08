package com.example.petadoption.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.petadoption.entity.PetAdoption;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 宠物领养信息Mapper接口
 */
@Mapper
public interface PetMapper extends BaseMapper<PetAdoption> {

}
