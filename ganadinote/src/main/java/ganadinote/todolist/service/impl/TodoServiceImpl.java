package ganadinote.todolist.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ganadinote.common.domain.Todo;
import ganadinote.todolist.mapper.TodoMapper;
import ganadinote.todolist.service.TodoService;

@Service
public class TodoServiceImpl implements TodoService {

    private final TodoMapper todoMapper;

    @Autowired
    public TodoServiceImpl(TodoMapper todoMapper) {
        this.todoMapper = todoMapper;
    }

    @Override
    public List<Todo> getTodosByMbrCd(int mbrCd) {
        return todoMapper.getTodosByMbrCd(mbrCd);
    }
    
    // [추가!] addTodo 메소드의 실제 구현
    @Override
    public void addTodo(Todo todo) {
        todoMapper.addTodo(todo);
    }
    
 // [추가!] deleteTodo 메소드의 실제 구현
    @Override
    public void deleteTodo(Long todoCd) {
        // 나중에 여기에 "삭제 권한이 있는 사용자인가?" 같은
        // 비즈니스 로직을 추가할 수 있습니다.
        todoMapper.deleteTodo(todoCd);
    }
    
 // [추가 1!] getTodoByCd 메소드의 실제 구현
    @Override
    public Todo getTodoByCd(Long todoCd) {
        return todoMapper.getTodoByCd(todoCd);
    }
    
    // [추가 2!] updateTodo 메소드의 실제 구현
    @Override
    public void updateTodo(Todo todo) {
        // 나중에 여기에 "수정 권한이 있는 사용자인가?" 같은
        // 비즈니스 로직을 추가할 수 있습니다.
        todoMapper.updateTodo(todo);
    }
}