package ganadinote.main.domain;

import ganadinote.common.domain.Pet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PetWithNicknameDTO extends Pet {
    private String mbrNknm;
}