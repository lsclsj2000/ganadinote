package ganadinote.auth.login.mapper;

import ganadinote.common.domain.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LoginMapper {
    Member findByMbrEmail(@Param("mbrEmail") String mbrEmail);
}