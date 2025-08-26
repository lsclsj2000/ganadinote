package ganadinote.todolist.service;

import java.util.List;
import ganadinote.common.domain.Todo;

public interface TodoService {

    // 기존에 있던 메소드
    List<Todo> getTodosByMbrCd(int mbrCd);
    
    // [추가!] 새로운 할 일을 추가하는 기능 명세
    void addTodo(Todo todo);
    
 // [추가!] 할 일을 삭제하는 기능 명세
    void deleteTodo(Long todoCd);
    
    // [추가 1!] 할 일 ID로 특정 할 일 1건의 정보를 조회하는 기능 명세
    Todo getTodoByCd(Long todoCd);

    // [추가 2!] 수정된 할 일 정보를 업데이트하는 기능 명세
    void updateTodo(Todo todo);
}