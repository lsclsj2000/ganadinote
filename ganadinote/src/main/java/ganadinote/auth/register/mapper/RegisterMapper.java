package ganadinote.auth.register.mapper;

import org.apache.ibatis.annotations.Mapper;

import ganadinote.common.domain.Member;

@Mapper
public interface RegisterMapper {
	void insertMember(Member member);
    boolean existsByMbrEmail(String mbrEmail);
    boolean existsByMbrNknm(String mbrNknm);
}
