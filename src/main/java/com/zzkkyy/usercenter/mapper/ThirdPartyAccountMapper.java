package com.zzkkyy.usercenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzkkyy.usercenter.model.domain.ThirdPartyAccount;
import org.apache.ibatis.annotations.Mapper;

/**
 * 第三方账号Mapper
 */
@Mapper
public interface ThirdPartyAccountMapper extends BaseMapper<ThirdPartyAccount> {
}
