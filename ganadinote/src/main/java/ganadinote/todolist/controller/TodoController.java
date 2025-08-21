package ganadinote.todolist.controller;

import ganadinote.todolist.domain.TodoDTO;
import ganadinote.todolist.service.TodoService; // 우리가 만든 Service를 import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // 데이터를 View에 전달하기 위해 Model을 import
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List; // List를 import

@Controller
@RequestMapping("/todo")
public class TodoController {

    // Service 계층을 주입받기 위한 final 필드를 선언합니다.
    private final TodoService todoService;

    // 생성자 주입 방식으로 TodoService를 Spring으로부터 주입받습니다.
    @Autowired
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping("/list")
    public String showTodoListView(Model model) { // 파라미터로 Model 객체를 받습니다.
        // 1. Service를 호출하여 할 일 목록 데이터를 가져옵니다.
        //    (지금은 로그인 기능이 없으니, 테스트를 위해 회원 ID를 1로 고정합니다.)
        int memberId = 1;
        List<TodoDTO> todoList = todoService.getTodosByMbrCd(memberId);

        // 2. Model 객체에 조회된 데이터를 담아 View로 전달합니다.
        //    "todos" 라는 이름으로 todoList 데이터를 추가합니다.
        model.addAttribute("todos", todoList);

        // 3. 렌더링할 뷰의 이름을 반환합니다.
        return "todo/todoListView.html"; // 팀에서 정한대로 .html을 반환합니다.
    }

    @GetMapping("/addView")
    public String showAddTodoView() {
        return "todo/addTodoView.html";
    }
}