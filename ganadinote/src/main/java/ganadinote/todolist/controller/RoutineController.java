package ganadinote.todolist.controller;

import ganadinote.common.domain.Pet;
import ganadinote.common.domain.Routine;
import ganadinote.todolist.service.PetTodoService;
import ganadinote.todolist.service.RoutineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/routine") // "/routine"으로 시작하는 모든 요청은 이 컨트롤러가 담당
public class RoutineController {

    private final RoutineService routineService;
    private final PetTodoService petTodoService;

    @Autowired
    public RoutineController(RoutineService routineService, PetTodoService petTodoService) {
        this.routineService = routineService;
        this.petTodoService = petTodoService;
    }

    // 1. 루틴 목록 페이지 보여주기
    @GetMapping("/list")
    public String showRoutineListView(Model model) {
        int memberId = 1; // 임시 회원 ID
        List<Routine> routineList = routineService.getRoutinesByMbrCd(memberId);
        model.addAttribute("routines", routineList);
        return "routine/routineListView.html";
    }

    // 2. 루틴 추가 페이지 보여주기
    @GetMapping("/addView")
    public String showAddRoutineView(Model model) {
        int memberId = 1; // 임시 회원 ID
        List<Pet> petList = petTodoService.getPetsByMbrCd(memberId);
        model.addAttribute("pets", petList);
        return "routine/addRoutineView.html";
    }

    // 3. 새로운 루틴 등록 처리
    @PostMapping("/add")
    public String addRoutine(Routine routine) { // 폼 데이터를 Routine 객체에 자동으로 매핑
        // 로그인 기능이 없으므로, 현재 사용자는 1번 회원이라고 가정합니다.
        // DTO에 mbrCd 필드가 있다면 여기에 값을 설정할 수 있습니다.
        // routine.setMbrCd(1); 
        
        routineService.addRoutine(routine);
        return "redirect:/routine/list";
    }
}