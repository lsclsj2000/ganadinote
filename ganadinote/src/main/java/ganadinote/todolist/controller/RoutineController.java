package ganadinote.todolist.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ganadinote.common.domain.Pet;
import ganadinote.common.domain.Routine;
import ganadinote.todolist.service.PetTodoService;
import ganadinote.todolist.service.RoutineService;

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
    
    // [추가!] /routine/delete 경로의 POST 요청을 처리하는 삭제 메소드
    @PostMapping("/delete")
    public String deleteRoutine(@RequestParam("routineCd") Long routineCd) {
        // Service를 호출하여 해당 루틴과 관련된 모든 할 일을 함께 삭제합니다.
        routineService.deleteRoutine(routineCd);
        
        // 처리가 끝나면 루틴 목록 페이지로 다시 이동(redirect)시킵니다.
        return "redirect:/routine/list";
    }
    
 // [추가!] /routine/update 경로의 POST 요청을 처리하는 수정 메소드
    @PostMapping("/update")
    public String updateRoutine(Routine routine) { // 폼 데이터를 Routine 객체에 자동 매핑
        // 로그인 기능 구현 전까지는 mbrCd를 직접 설정해줍니다.
        routine.setMbrCd(1); 
        
        // 루틴을 수정하고, 관련된 미래 할 일을 모두 재생성하는 서비스를 호출합니다.
        routineService.updateRoutineAndTodos(routine);
        
        // 처리가 끝나면 루틴 목록 페이지로 다시 이동합니다.
        return "redirect:/routine/list";
    }

} 