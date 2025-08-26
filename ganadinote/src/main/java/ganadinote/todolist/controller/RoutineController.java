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
import java.util.List;

@Controller
@RequestMapping("/routine")
public class RoutineController {

    private final RoutineService routineService;
    private final PetTodoService petTodoService;

    @Autowired
    public RoutineController(RoutineService routineService, PetTodoService petTodoService) {
        this.routineService = routineService;
        this.petTodoService = petTodoService;
    }

    // [수정!] 루틴 목록뿐만 아니라, 펫 목록도 함께 조회하여 전달합니다.
    @GetMapping("/list")
    public String showRoutineListView(Model model) {
        int memberId = 1; // 임시 회원 ID
        
        List<Routine> routineList = routineService.getRoutinesByMbrCd(memberId);
        List<Pet> petList = petTodoService.getPetsByMbrCd(memberId); // 펫 목록 조회 추가

        model.addAttribute("routines", routineList);
        model.addAttribute("pets", petList); // 모델에 펫 목록 추가
        
        return "routine/routineListView.html";
    }

    // 2. 루틴 추가 페이지 보여주기 (변경 없음)
    @GetMapping("/addView")
    public String showAddRoutineView(Model model) {
        int memberId = 1; // 임시 회원 ID
        List<Pet> petList = petTodoService.getPetsByMbrCd(memberId);
        model.addAttribute("pets", petList);
        return "routine/addRoutineView.html";
    }

    // 3. 새로운 루틴 등록 처리 (변경 없음)
    @PostMapping("/add")
    public String addRoutine(Routine routine) {
        // RoutineService에서 할 일 일괄 생성을 처리합니다.
        routineService.addRoutineAndTodos(routine); 
        
        return "redirect:/routine/list";
    }

} // [수정!] 빠졌던 클래스 닫는 괄호를 추가했습니다.