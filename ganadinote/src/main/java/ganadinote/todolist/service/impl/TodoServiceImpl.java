package ganadinote.todolist.service.impl;

import ganadinote.todolist.domain.TodoDTO;
import ganadinote.todolist.mapper.TodoMapper;
import ganadinote.todolist.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // 이 클래스가 비즈니스 로직을 담당하는 '서비스'임을 Spring에게 알려줍니다.
public class TodoServiceImpl implements TodoService {

    // final로 선언하여 불변성을 보장하고, 생성자를 통해 주입받습니다.
    private final TodoMapper todoMapper;

    // 생성자 주입(Constructor Injection) 방식.
    // Spring이 시작될 때 자동으로 TodoMapper 타입의 객체(Bean)를 여기에 주입해줍니다.
    @Autowired
    public TodoServiceImpl(TodoMapper todoMapper) {
        this.todoMapper = todoMapper;
    }

    // TodoService 인터페이스의 메소드를 실제로 구현(Override)합니다.
    @Override
    public List<TodoDTO> getTodosByMbrCd(int mbrCd) {
        // 지금은 특별한 비즈니스 로직이 없으므로,
        // Mapper에게 받은 데이터를 그대로 컨트롤러에게 전달합니다.
        return todoMapper.getTodosByMbrCd(mbrCd);
    }
}