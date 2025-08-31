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
import ganadinote.common.util.TokenUtils; // [추가!] TokenUtils를 import 합니다.
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

    @GetMapping("/list")
    public String showRoutineListView(Model model) {
        // [수정!] TokenUtils를 사용하여 현재 로그인한 회원 ID를 가져옵니다.
        String mbrCd = TokenUtils.getMbrCd();
        if (mbrCd == null) {
            return "redirect:/login"; // 비로그인 시 로그인 페이지로
        }
        int memberId = Integer.parseInt(mbrCd);
        
        List<Routine> routineList = routineService.getRoutinesByMbrCd(memberId);
        List<Pet> petList = petTodoService.getPetsByMbrCd(memberId);

        model.addAttribute("routines", routineList);
        model.addAttribute("pets", petList);
        
        return "routine/routineListView.html";
    }

    @GetMapping("/addView")
    public String showAddRoutineView(Model model) {
        // [수정!] TokenUtils를 사용하여 현재 로그인한 회원 ID를 가져옵니다.
        String mbrCd = TokenUtils.getMbrCd();
        if (mbrCd == null) {
            return "redirect:/login";
        }
        int memberId = Integer.parseInt(mbrCd);

        List<Pet> petList = petTodoService.getPetsByMbrCd(memberId);
        model.addAttribute("pets", petList);
        return "routine/addRoutineView.html";
    }

    @PostMapping("/add")
    public String addRoutine(Routine routine) {
        // [수정!] TokenUtils를 사용하여 현재 사용자 ID를 DTO에 설정합니다.
        String mbrCd = TokenUtils.getMbrCd();
        if (mbrCd == null) {
            return "redirect:/login";
        }
        routine.setMbrCd(Integer.parseInt(mbrCd));

        routineService.addRoutineAndTodos(routine); 
        
        return "redirect:/routine/list";
    }
    
    @PostMapping("/delete")
    public String deleteRoutine(@RequestParam("routineCd") Long routineCd) {
        // (보안 강화) 이 루틴이 정말 로그인한 사용자의 것인지 확인하는 로직 추가 권장
        String mbrCd = TokenUtils.getMbrCd();
        if (mbrCd == null) {
            return "redirect:/login";
        }
        
        Routine routine = routineService.getRoutineByCd(routineCd);
        Pet pet = petTodoService.getPetByCd(routine.getPetCd()); // Pet 정보 조회
        if (routine == null || pet.getMbrCd() != Integer.parseInt(mbrCd)) {
             return "redirect:/error"; // 내 루틴이 아니면 에러 페이지로
        }
        
        routineService.deleteRoutine(routineCd);
        return "redirect:/routine/list";
    }
    
    @PostMapping("/update")
    public String updateRoutine(Routine routine) {
        // [수정!] TokenUtils를 사용하여 현재 사용자 ID를 DTO에 설정합니다.
        String mbrCd = TokenUtils.getMbrCd();
        if (mbrCd == null) {
            return "redirect:/login";
        }
        routine.setMbrCd(Integer.parseInt(mbrCd));
        
        // (보안 강화) 이 루틴이 정말 로그인한 사용자의 것인지 확인하는 로직 추가 권장
        Routine originalRoutine = routineService.getRoutineByCd(routine.getRoutineCd());
        Pet pet = petTodoService.getPetByCd(originalRoutine.getPetCd());
        if (originalRoutine == null || pet.getMbrCd() != Integer.parseInt(mbrCd)) {
             return "redirect:/error";
        }

        routineService.updateRoutineAndTodos(routine);
        
        return "redirect:/routine/list";
    }
}