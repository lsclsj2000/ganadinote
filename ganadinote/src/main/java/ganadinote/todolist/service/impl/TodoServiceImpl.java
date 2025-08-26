package ganadinote.todolist.service.impl;

import ganadinote.common.domain.Todo;
import ganadinote.todolist.mapper.TodoMapper;
import ganadinote.todolist.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoServiceImpl implements TodoService {

    private final TodoMapper todoMapper;

    // [수정!] 생성자에서 RoutineMapper 주입 부분을 제거합니다.
    @Autowired
    public TodoServiceImpl(TodoMapper todoMapper) {
        this.todoMapper = todoMapper;
    }

    @Override
    public List<Todo> getTodosByMbrCd(int mbrCd) {
        return todoMapper.getTodosByMbrCd(mbrCd);
    }

    @Override
    public void addTodo(Todo todo) {
        todoMapper.addTodo(todo);
    }

    @Override
    public void deleteTodo(Long todoCd) {
        todoMapper.deleteTodo(todoCd);
    }

    @Override
    public Todo getTodoByCd(Long todoCd) {
        return todoMapper.getTodoByCd(todoCd);
    }

    @Override
    public void updateTodo(Todo todo) {
        todoMapper.updateTodo(todo);
    }

}