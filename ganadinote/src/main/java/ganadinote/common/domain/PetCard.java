package ganadinote.common.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PetCard {
	private Integer cardId;
    private Integer petCd;
    private String petName;
    private String petImageUrl;
    private String introduction;
    private List<String> tags;
}
