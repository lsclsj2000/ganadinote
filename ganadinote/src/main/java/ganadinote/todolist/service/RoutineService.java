package ganadinote.todolist.service;

import ganadinote.common.domain.Routine;
import java.util.List;

public interface RoutineService {

    /**
     * 특정 회원의 모든 루틴 목록을 조회합니다.
     * @param mbrCd 조회할 회원의 ID
     * @return 루틴 목록
     */
    List<Routine> getRoutinesByMbrCd(int mbrCd);

    /**
     * 새로운 루틴을 추가합니다.
     * @param routine 추가할 루틴 정보
     */
    void addRoutine(Routine routine);
}