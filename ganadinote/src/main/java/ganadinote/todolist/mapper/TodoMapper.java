package ganadinote.todolist.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import ganadinote.common.domain.Todo;

@Mapper
public interface TodoMapper {

    // 기존에 있던 메소드
    List<Todo> getTodosByMbrCd(int mbrCd);
    
    // [추가!] 새로운 할 일을 DB에 삽입하는 메소드 선언
    void addTodo(Todo todo);
    
 // [추가!] 할 일 ID를 기반으로 데이터를 삭제하는 메소드
    void deleteTodo(Long todoCd);
    
 // [추가 1!] 할 일 ID로 특정 할 일 1건의 정보를 조회하는 메소드
    Todo getTodoByCd(Long todoCd);
    
    // [추가 2!] 수정된 할 일 정보를 DB에 업데이트하는 메소드
    void updateTodo(Todo todo);
    
    // [추가!] 특정 루틴으로 오늘 생성된 할 일이 있는지 확인 (개수 반환)
    int countTodoByRoutineCdAndDate(@Param("routineCd") Long routineCd, @Param("today") LocalDate today);
    
 // [추가!] 여러 개의 할 일을 한 번에 삽입하는 메소드
    void addTodos(List<Todo> todos);
}