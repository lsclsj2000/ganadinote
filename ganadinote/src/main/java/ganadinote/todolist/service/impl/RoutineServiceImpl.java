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
}