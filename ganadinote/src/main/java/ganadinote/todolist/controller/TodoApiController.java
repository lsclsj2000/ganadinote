package ganadinote.todolist.controller;

import ganadinote.todolist.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController // 이 컨트롤러는 데이터를 반환하는 API 컨트롤러임을 명시
@RequestMapping("/api/todo") // API 주소는 /api/ 로 시작하는 것이 관례
public class TodoApiController {

    private final TodoService todoService;

    @Autowired
    public TodoApiController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping("/toggleComplete")
    public ResponseEntity<?> toggleComplete(@RequestParam("todoCd") Long todoCd) {
        try {
            todoService.toggleTodoCompleted(todoCd);
            // 성공 시, "ok"라는 문자열과 함께 200 OK 상태 코드를 반환
            return ResponseEntity.ok("ok"); 
        } catch (Exception e) {
            // 실패 시, 에러 메시지와 함께 500 Internal Server Error 상태 코드를 반환
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}