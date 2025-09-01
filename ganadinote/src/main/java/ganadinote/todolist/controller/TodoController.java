package ganadinote.todolist.controller;

import ganadinote.common.domain.Member;
import ganadinote.common.domain.Pet;
import ganadinote.common.domain.Todo;
import ganadinote.common.util.TokenUtils;
import ganadinote.todolist.service.PetTodoService;
import ganadinote.todolist.service.TodoMemberService; // TodoMemberService import
import ganadinote.todolist.service.TodoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/todo")
public class TodoController {

    private final TodoService todoService;
    private final PetTodoService petTodoService;
    private final TodoMemberService todoMemberService; // [수정!] MemberService -> TodoMemberService

    // [수정!] 생성자에서 TodoMemberService를 주입받도록 변경합니다.
    @Autowired
    public TodoController(TodoService todoService, PetTodoService petTodoService, TodoMemberService todoMemberService) {
        this.todoService = todoService;
        this.petTodoService = petTodoService;
        this.todoMemberService = todoMemberService; // [수정!] 초기화 코드 추가
    }

    @GetMapping("/list")
    public String showTodoListView(Model model) {
        String mbrCd = TokenUtils.getMbrCd();
        if (mbrCd == null) {
            return "redirect:/login";
        }
        int memberId = Integer.parseInt(mbrCd);
        
        // [수정!] memberService -> todoMemberService
        Member loginMember = todoMemberService.getMemberByCd(memberId);

        List<Todo> todoList = todoService.getTodosByMbrCd(memberId);
        List<Pet> petList = petTodoService.getPetsByMbrCd(memberId);

        model.addAttribute("todos", todoList);
        model.addAttribute("pets", petList);
        model.addAttribute("member", loginMember);
        
        return "todo/todoListView.html";
    }
    
    // (이하 addView, add, delete, update 메소드는 변경 사항 없습니다.)
    @GetMapping("/addView")
    public String showAddTodoView(Model model) {
        String mbrCd = TokenUtils.getMbrCd();
        if (mbrCd == null) {
            return "redirect:/login"; 
        }
        int memberId = Integer.parseInt(mbrCd);

        List<Pet> petList = petTodoService.getPetsByMbrCd(memberId);
        model.addAttribute("pets", petList);

        return "todo/addTodoView.html";
    }

	@PostMapping("/add")
	public String addTodo(@RequestParam("todoTitle") String todoTitle, @RequestParam("petCd") int petCd,
			@RequestParam("todoDate") String todoDate,
			@RequestParam(value = "todoTime", required = false) String todoTime) {
		String mbrCd = TokenUtils.getMbrCd();
		if (mbrCd == null) {
			return "redirect:/login";
		}
		int memberId = Integer.parseInt(mbrCd);
		Todo todo = new Todo();
		todo.setTodoTitle(todoTitle);
		todo.setPetCd(petCd);
		todo.setMbrCd(memberId);
		LocalDate date = LocalDate.parse(todoDate);
		LocalDateTime scheduledDt;
		if (todoTime != null && !todoTime.isEmpty()) {
			LocalTime time = LocalTime.parse(todoTime);
			scheduledDt = LocalDateTime.of(date, time);
		} else {
			scheduledDt = date.atStartOfDay();
		}
		todo.setTodoScheduledDt(scheduledDt);
		todoService.addTodo(todo);
		return "redirect:/todo/list";
	}

	@PostMapping("/delete")
	public String deleteTodo(@RequestParam("todoCd") Long todoCd) {
		String mbrCd = TokenUtils.getMbrCd();
		if (mbrCd == null) {
			return "redirect:/login";
		}
		Todo todo = todoService.getTodoByCd(todoCd);
		if (todo == null || todo.getMbrCd() != Integer.parseInt(mbrCd)) {
			return "redirect:/error";
		}
		todoService.deleteTodo(todoCd);
		return "redirect:/todo/list";
	}

	@PostMapping("/update")
	public String updateTodo(@RequestParam("todoCd") Long todoCd, @RequestParam("todoTitle") String todoTitle,
			@RequestParam("petCd") int petCd, @RequestParam("todoDate") String todoDate,
			@RequestParam(value = "todoTime", required = false) String todoTime,
			@RequestParam(value = "isCompleted", required = false) boolean isCompleted) {
		Todo todo = todoService.getTodoByCd(todoCd);
		String mbrCd = TokenUtils.getMbrCd();
		if (mbrCd == null || todo.getMbrCd() != Integer.parseInt(mbrCd)) {
			 return "redirect:/error"; 
		}
		todo.setTodoTitle(todoTitle);
		todo.setPetCd(petCd);
		todo.setTodoIsCompleted(isCompleted);
		LocalDate date = LocalDate.parse(todoDate);
		LocalDateTime scheduledDt;
		if (todoTime != null && !todoTime.isEmpty()) {
			LocalTime time = LocalTime.parse(todoTime);
			scheduledDt = LocalDateTime.of(date, time);
		} else {
			scheduledDt = date.atStartOfDay();
		}
		todo.setTodoScheduledDt(scheduledDt);
		todoService.updateTodo(todo);
		return "redirect:/todo/list";
	}
}