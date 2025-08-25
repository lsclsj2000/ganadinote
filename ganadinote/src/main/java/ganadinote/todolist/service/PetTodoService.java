package ganadinote.todolist.service;

import ganadinote.common.domain.Pet;
import java.util.List;

// [이름 변경] PetService -> PetTodoService
public interface PetTodoService { 
    List<Pet> getPetsByMbrCd(int mbrCd);
}