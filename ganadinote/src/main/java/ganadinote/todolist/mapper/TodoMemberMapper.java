package ganadinote.todolist.mapper;

import ganadinote.common.domain.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TodoMemberMapper {

    /**
     * 회원 ID(mbr_cd)로 특정 회원 1명의 정보를 조회합니다.
     * (todolist 기능에서 견주 프로필을 표시하기 위해 사용)
     */
    Member getMemberByCd(int mbrCd);
}