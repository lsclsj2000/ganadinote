package ganadinote.todolist.service.impl;

import ganadinote.common.domain.Pet;
import ganadinote.todolist.mapper.PetTodoMapper; // import 변경
import ganadinote.todolist.service.PetTodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PetTodoServiceImpl implements PetTodoService { 

    private final PetTodoMapper petTodoMapper; // [타입 변경]

    @Autowired
    public PetTodoServiceImpl(PetTodoMapper petTodoMapper) { // [타입 변경]
        this.petTodoMapper = petTodoMapper;
    }

    @Override
    public List<Pet> getPetsByMbrCd(int mbrCd) {
        return petTodoMapper.getPetsByMbrCd(mbrCd); // [호출 대상 변경]
    }
}