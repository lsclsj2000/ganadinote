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

    // [추가!] 루틴 ID를 기반으로 데이터를 삭제하는 메소드
    void deleteRoutine(Long routineCd);
    
 // [추가 1!] 루틴 ID로 특정 루틴 1건의 정보를 조회하는 메소드
    Routine getRoutineByCd(Long routineCd);
    
    // [추가 2!] 수정된 루틴 정보를 DB에 업데이트하는 메소드
    void updateRoutine(Routine routine);
}