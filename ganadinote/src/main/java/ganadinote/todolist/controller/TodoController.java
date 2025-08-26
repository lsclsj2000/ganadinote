package ganadinote.todolist.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping; // [추가!] PostMapping import
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam; // [추가!] RequestParam import

import ganadinote.common.domain.Pet;
import ganadinote.common.domain.Todo;
import ganadinote.todolist.service.PetTodoService;
import ganadinote.todolist.service.TodoService;

@Controller
@RequestMapping("/todo")
public class TodoController {

	private final TodoService todoService;
	private final PetTodoService petTodoService;

	@Autowired
	public TodoController(TodoService todoService, PetTodoService petTodoService) {
		this.todoService = todoService;
		this.petTodoService = petTodoService;
	}

	@GetMapping("/list")
	public String showTodoListView(Model model) {
		int memberId = 1;


		// 이제 목록을 조회하면, 방금 자동으로 생성된 할 일까지 모두 포함됩니다.
		List<Todo> todoList = todoService.getTodosByMbrCd(memberId);
		List<Pet> petList = petTodoService.getPetsByMbrCd(memberId);

		model.addAttribute("todos", todoList);
		model.addAttribute("pets", petList);

		return "todo/todoListView.html";
	}

	@GetMapping("/addView")
	public String showAddTodoView(Model model) {
		// ... (이전과 동일)
		int memberId = 1;
		List<Pet> petList = petTodoService.getPetsByMbrCd(memberId);
		model.addAttribute("pets", petList);
		return "todo/addTodoView.html";
	}

	// ======================================================================
	// ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ 이 메소드가 핵심입니다 ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼
	// ======================================================================

	/**
	 * '할 일 추가' 폼에서 전송된 데이터를 받아서 처리하는 메소드입니다. @PostMapping("/add") 어노테이션을 통해
	 * /todo/add 경로의 POST 요청만 받습니다.
	 */
	@PostMapping("/add")
	public String addTodo(@RequestParam("todoTitle") String todoTitle, @RequestParam("petCd") int petCd,
			@RequestParam("todoDate") String todoDate,
			@RequestParam(value = "todoTime", required = false) String todoTime) {
		Todo todo = new Todo();
		todo.setTodoTitle(todoTitle);
		todo.setPetCd(petCd);
		todo.setMbrCd(1); // 임시로 1번 회원으로 고정

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

		// 처리가 끝나면 목록 페이지로 리다이렉트합니다.
		return "redirect:/todo/list";
	}

	// [추가!] /todo/delete 경로의 POST 요청을 처리하는 삭제 메소드
	@PostMapping("/delete")
	public String deleteTodo(@RequestParam("todoCd") Long todoCd) {
		// Service를 호출하여 해당 ID의 할 일을 삭제합니다.
		todoService.deleteTodo(todoCd);

		// 처리가 끝나면 목록 페이지로 다시 이동(redirect)시킵니다.
		return "redirect:/todo/list";
	}

	// [추가!] /todo/update 경로의 POST 요청을 처리하는 수정 메소드
	@PostMapping("/update")
	public String updateTodo(@RequestParam("todoCd") Long todoCd, @RequestParam("todoTitle") String todoTitle,
			@RequestParam("petCd") int petCd, @RequestParam("todoDate") String todoDate,
			@RequestParam(value = "todoTime", required = false) String todoTime,
			@RequestParam(value = "isCompleted", required = false) boolean isCompleted) {
		// 1. 기존 할 일 정보를 불러옵니다.
		Todo todo = todoService.getTodoByCd(todoCd);

		// 2. 폼에서 받은 데이터로 값을 업데이트합니다.
		todo.setTodoTitle(todoTitle);
		todo.setPetCd(petCd);
		todo.setTodoIsCompleted(isCompleted);

		// 날짜와 시간을 합쳐서 업데이트합니다.
		LocalDate date = LocalDate.parse(todoDate);
		LocalDateTime scheduledDt;
		if (todoTime != null && !todoTime.isEmpty()) {
			LocalTime time = LocalTime.parse(todoTime);
			scheduledDt = LocalDateTime.of(date, time);
		} else {
			scheduledDt = date.atStartOfDay();
		}
		todo.setTodoScheduledDt(scheduledDt);

		// 3. Service를 호출하여 DB에 업데이트합니다.
		todoService.updateTodo(todo);

		// 4. 처리가 끝나면 목록 페이지로 다시 이동합니다.
		return "redirect:/todo/list";
	}

}