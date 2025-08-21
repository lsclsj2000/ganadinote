package ganadinote.todolist.domain;

import java.time.LocalDateTime;

// 데이터베이스의 'todo' 테이블과 1:1로 대응되는 데이터 전송 객체입니다.
public class TodoDTO {

    private Long todoCd;
    private int mbrCd;
    private int petCd;
    private Long routineCd;
    private String todoTitle;
    private LocalDateTime todoScheduledDt;
    private boolean todoIsCompleted;
    private LocalDateTime regYmdt;
    private LocalDateTime mdfcnYmdt;

    // Getter와 Setter (Lombok을 사용하면 생략 가능하지만, 지금은 직접 만듭니다)
    // ... (이전에 제공했던 Getter/Setter 코드와 동일)
    public Long getTodoCd() { return todoCd; }
    public void setTodoCd(Long todoCd) { this.todoCd = todoCd; }
    public int getMbrCd() { return mbrCd; }
    public void setMbrCd(int mbrCd) { this.mbrCd = mbrCd; }
    public int getPetCd() { return petCd; }
    public void setPetCd(int petCd) { this.petCd = petCd; }
    public Long getRoutineCd() { return routineCd; }
    public void setRoutineCd(Long routineCd) { this.routineCd = routineCd; }
    public String getTodoTitle() { return todoTitle; }
    public void setTodoTitle(String todoTitle) { this.todoTitle = todoTitle; }
    public LocalDateTime getTodoScheduledDt() { return todoScheduledDt; }
    public void setTodoScheduledDt(LocalDateTime todoScheduledDt) { this.todoScheduledDt = todoScheduledDt; }
    public boolean isTodoIsCompleted() { return todoIsCompleted; }
    public void setTodoIsCompleted(boolean todoIsCompleted) { this.todoIsCompleted = todoIsCompleted; }
    public LocalDateTime getRegYmdt() { return regYmdt; }
    public void setRegYmdt(LocalDateTime regYmdt) { this.regYmdt = regYmdt; }
    public LocalDateTime getMdfcnYmdt() { return mdfcnYmdt; }
    public void setMdfcnYmdt(LocalDateTime mdfcnYmdt) { this.mdfcnYmdt = mdfcnYmdt; }
}