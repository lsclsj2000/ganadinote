package ganadinote.todolist.mapper;

import ganadinote.common.domain.Routine;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface RoutineMapper {
    
    // 특정 회원의 모든 루틴 조회 (mbrCd 기준)
    List<Routine> getRoutinesByMbrCd(int mbrCd);

    // 새로운 루틴 추가
    void addRoutine(Routine routine);

    // (수정/삭제는 다음 단계에서 추가)
}