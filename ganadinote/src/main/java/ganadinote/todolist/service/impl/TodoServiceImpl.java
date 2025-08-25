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
}