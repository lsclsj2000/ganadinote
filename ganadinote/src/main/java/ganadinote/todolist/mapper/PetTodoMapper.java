package ganadinote.todolist.mapper;

import ganadinote.common.domain.Pet;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
// [이름 통일] 인터페이스 이름을 PetTodoMapper로 합니다.
public interface PetTodoMapper { 
    List<Pet> getPetsByMbrCd(int mbrCd);
}