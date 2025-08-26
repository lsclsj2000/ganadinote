package ganadinote.todolist.service.impl;

import ganadinote.common.domain.Routine;
import ganadinote.common.domain.Todo;
import ganadinote.todolist.mapper.RoutineMapper;
import ganadinote.todolist.mapper.TodoMapper;
import ganadinote.todolist.service.RoutineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoutineServiceImpl implements RoutineService {

    private final RoutineMapper routineMapper;
    private final TodoMapper todoMapper;

    @Autowired
    public RoutineServiceImpl(RoutineMapper routineMapper, TodoMapper todoMapper) {
        this.routineMapper = routineMapper;
        this.todoMapper = todoMapper;
    }

    @Override
    public List<Routine> getRoutinesByMbrCd(int mbrCd) {
        return routineMapper.getRoutinesByMbrCd(mbrCd);
    }

    @Override
    @Transactional
    public void addRoutineAndTodos(Routine routine) {
        routineMapper.addRoutine(routine);

        List<Todo> todosToCreate = new ArrayList<>();
        LocalDate currentDate = routine.getRoutineStartDate();
        
        while (!currentDate.isAfter(routine.getRoutineEndDate())) {
            if (isRoutineApplicableOn(routine, currentDate)) {
                Todo newTodo = new Todo();
                newTodo.setMbrCd(routine.getMbrCd());
                newTodo.setPetCd(routine.getPetCd());
                newTodo.setRoutineCd(routine.getRoutineCd());
                newTodo.setTodoTitle(routine.getRoutineTitle());
                
                LocalDateTime scheduledDt;
                if (routine.getRoutineTimeOfDay() != null) {
                    scheduledDt = currentDate.atTime(routine.getRoutineTimeOfDay());
                } else {
                    scheduledDt = currentDate.atStartOfDay();
                }
                newTodo.setTodoScheduledDt(scheduledDt);
                newTodo.setTodoIsCompleted(false);
                
                todosToCreate.add(newTodo);
            }
            currentDate = currentDate.plusDays(1);
        }

        if (!todosToCreate.isEmpty()) {
            todoMapper.addTodos(todosToCreate);
        }
    }

    // ======================================================================
    // ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ 이 부분이 핵심 수정사항입니다 ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼
    // ======================================================================

    /**
     * 특정 날짜(date)가 루틴(routine)의 반복 규칙에 해당하는지 확인하는 보조 메소드
     */
    private boolean isRoutineApplicableOn(Routine routine, LocalDate date) {
        switch (routine.getRoutineRepeatType()) {
            case "DAILY":
                return true;
            case "WEEKLY":
                // [수정!] 시작일의 요일 대신, DTO에 저장된 'routineDayOfWeek' 값과 비교합니다.
                // date.getDayOfWeek().getValue()는 월요일(1) ~ 일요일(7)을 반환합니다.
                return routine.getRoutineDayOfWeek() != null &&
                       date.getDayOfWeek().getValue() == routine.getRoutineDayOfWeek();
            case "MONTHLY":
                // [수정!] 시작일의 날짜 대신, DTO에 저장된 'routineDayOfMonth' 값과 비교합니다.
                return routine.getRoutineDayOfMonth() != null &&
                       date.getDayOfMonth() == routine.getRoutineDayOfMonth();
            default:
                return false;
        }
    }
    
    // [추가!] deleteRoutine 메소드의 실제 구현
    @Override
    @Transactional // 두 개의 삭제 작업이 모두 성공하거나 모두 실패하도록 보장
    public void deleteRoutine(Long routineCd) {
        // 1. 먼저 자식 테이블인 todo에서 관련 할 일들을 모두 삭제합니다.
        todoMapper.deleteTodosByRoutineCd(routineCd);
        
        // 2. 그 다음, 부모인 routine 테이블에서 해당 루틴을 삭제합니다.
        routineMapper.deleteRoutine(routineCd);
    }
    
 // [추가 1!] getRoutineByCd 메소드 구현
    @Override
    public Routine getRoutineByCd(Long routineCd) {
        return routineMapper.getRoutineByCd(routineCd);
    }
    
    // [추가 2!] updateRoutineAndTodos 메소드의 실제 구현 (핵심!)
    @Override
    @Transactional // 모든 DB 작업이 하나의 단위로 묶입니다.
    public void updateRoutineAndTodos(Routine routine) {
        // 1. 먼저, 이 루틴으로 인해 생성되었던 기존의 모든 할 일들을 삭제합니다.
        todoMapper.deleteTodosByRoutineCd(routine.getRoutineCd());
        
        // 2. 루틴 테이블의 정보를 새로운 내용으로 업데이트합니다.
        routineMapper.updateRoutine(routine);

        // 3. 'addRoutineAndTodos'에서 사용했던 할 일 일괄 생성 로직을 그대로 재활용합니다.
        List<Todo> todosToCreate = new ArrayList<>();
        LocalDate currentDate = routine.getRoutineStartDate();
        
        while (!currentDate.isAfter(routine.getRoutineEndDate())) {
            if (isRoutineApplicableOn(routine, currentDate)) {
                Todo newTodo = new Todo();
                newTodo.setMbrCd(routine.getMbrCd());
                newTodo.setPetCd(routine.getPetCd());
                newTodo.setRoutineCd(routine.getRoutineCd());
                newTodo.setTodoTitle(routine.getRoutineTitle());
                
                LocalDateTime scheduledDt;
                if (routine.getRoutineTimeOfDay() != null) {
                    scheduledDt = currentDate.atTime(routine.getRoutineTimeOfDay());
                } else {
                    scheduledDt = currentDate.atStartOfDay();
                }
                newTodo.setTodoScheduledDt(scheduledDt);
                newTodo.setTodoIsCompleted(false);
                
                todosToCreate.add(newTodo);
            }
            currentDate = currentDate.plusDays(1);
        }

        // 4. 새로 생성할 할 일이 있다면, 한꺼번에 DB에 삽입합니다.
        if (!todosToCreate.isEmpty()) {
            todoMapper.addTodos(todosToCreate);
        }
    }
}