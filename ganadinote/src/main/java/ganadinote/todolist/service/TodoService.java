package ganadinote.todolist.service;

import ganadinote.todolist.domain.TodoDTO;
import java.util.List;

public interface TodoService {

    /**
     * 특정 회원의 모든 할 일 목록을 조회합니다.
     * @param mbrCd 조회할 회원의 ID
     * @return 할 일 목록
     */
    List<TodoDTO> getTodosByMbrCd(int mbrCd);
}