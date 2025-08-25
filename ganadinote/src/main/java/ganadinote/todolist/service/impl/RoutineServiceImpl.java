package ganadinote.todolist.service.impl;

import ganadinote.common.domain.Routine;
import ganadinote.todolist.mapper.RoutineMapper;
import ganadinote.todolist.service.RoutineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RoutineServiceImpl implements RoutineService {

    private final RoutineMapper routineMapper;

    @Autowired
    public RoutineServiceImpl(RoutineMapper routineMapper) {
        this.routineMapper = routineMapper;
    }

    @Override
    public List<Routine> getRoutinesByMbrCd(int mbrCd) {
        return routineMapper.getRoutinesByMbrCd(mbrCd);
    }

    @Override
    public void addRoutine(Routine routine) {
        routineMapper.addRoutine(routine);
    }
}