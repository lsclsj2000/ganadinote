package ganadinote.todolist.service;

import java.util.List;
import ganadinote.common.domain.Todo;

public interface TodoService {

    // 기존에 있던 메소드
    List<Todo> getTodosByMbrCd(int mbrCd);
    
    // [추가!] 새로운 할 일을 추가하는 기능 명세
    void addTodo(Todo todo);
}