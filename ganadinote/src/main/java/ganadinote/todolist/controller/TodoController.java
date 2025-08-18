package ganadinote.todolist.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TodoController {

    @GetMapping("/todoListView")
    public String showTodoList() {
        return "todo/todoListView";
    }

    @GetMapping("/addTodoView")
    public String showCreateTodoPage() {
        return "todo/addTodoView";
    }
}